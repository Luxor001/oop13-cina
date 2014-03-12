package client_chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Create a SSl socket and attempts to connect to the server
 * 
 * 
 * @author Francesco Cozzolino
 * 
 */
public class Client extends SendReceiveFile implements Runnable {

	private SSLSocketFactory sslSocketFactory = null;
	private SSLSocket sslSocket = null;
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	private ViewObserver controller;
	private String ip;
	private int port;
	private String nameClient = User.getNickName();
	private String nameServer = null;
	private boolean resetTime = false;
	private boolean stop = false;
	private Downloaded download;
	private CountDownLatch latch = new CountDownLatch(1);
	private Object lock = new Object();
	private Object lockAll = new Object();
	private int id = 0;
	private Model model;

	/**
	 * loads your own keystore and server's keystore you want to connect.Starts
	 * a new thread that tries to connect to the server
	 * 
	 * 
	 * @param ip
	 *            ip address of server
	 * @param port
	 *            router's port of server
	 * @param name
	 *            user's name of server side
	 * @param password
	 *            password of your keystore
	 * @param controller
	 * @param model
	 * @param keyStore
	 *            name of your keystore
	 * @throws IOException
	 *             if the keystore doesn't exist or the password is wrong
	 * 
	 * @see Controller
	 * @see Model
	 */
	public Client(String ip, int port, String name, String password,
			ViewObserver controller, Model model, String keyStore)
			throws IOException {

		String path = System.getProperty("user.dir") + "/" + nameClient
				+ "ClientKey.jks";
		KeyStore keystore;
		TrustManagerFactory tmf;
		SSLContext context;
		TrustManager[] trustManagers;

		this.model = model;
		this.ip = ip;
		this.port = port;
		this.controller = controller;
		nameServer = name;

		try {
			keystore = KeyStore.getInstance("jks");
			keystore.load(new FileInputStream(path), null);

			KeyManagerFactory clientKeyManager = KeyManagerFactory
					.getInstance("SunX509");
			clientKeyManager.init(keystore, password.toCharArray());

			KeyStore serverPub = KeyStore.getInstance("jks");

			serverPub.load(new FileInputStream(keyStore), null);

			tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(serverPub);

			context = SSLContext.getInstance("SSL");
			trustManagers = tmf.getTrustManagers();
			context.init(clientKeyManager.getKeyManagers(), trustManagers,
					SecureRandom.getInstance("SHA1PRNG"));
			sslSocketFactory = context.getSocketFactory();

			new Thread(this).start();

			latch.await();

		} catch (KeyStoreException e) {
		} catch (NoSuchAlgorithmException e) {
		} catch (CertificateException e) {
		} catch (KeyManagementException e) {
		} catch (UnrecoverableKeyException e) {
		} catch (InterruptedException e) {
		}

	}

	/**
	 * Thread tries to connect and listens input stream of server and show
	 * messages or downlaod file that come from him.if is inactive for a minute
	 * or more in both sending and receiving, the client will disconnect from
	 * the server automatically
	 */
	public void run() {

		class Timer extends Thread {
			private final static int TIMEOUT = 60000;
			private int sleep = 1000;
			private int currentTime = 0;
			private boolean timeout = false;

			public void run() {
				while (!timeout) {
					try {
						Thread.sleep(sleep);
					} catch (InterruptedException e) {
					}

					currentTime += sleep;

					if (resetTime) {
						resetTime = false;
						currentTime = 0;
					}
					if (currentTime > TIMEOUT || stop) {
						try {
							sendMessage(null);
						} catch (IOException e) {
						}
						timeout = true;
					}
				}
			}

			public boolean getStateTimeout() {
				return timeout;
			}
		}

		Timer t = null;
		Map<Integer, DownloadFile> file = null;

		try {
			synchronized (lockAll) {
				latch.countDown();
				sslSocket = (SSLSocket) sslSocketFactory.createSocket(ip, port);
				sslSocket.startHandshake();
				model.addNickName(nameServer, ip, port);

				oos = new ObjectOutputStream(sslSocket.getOutputStream());
				ois = new ObjectInputStream(sslSocket.getInputStream());

				download = model.getDownloaded();
				oos.writeObject(nameClient);
				oos.flush();
				oos.writeInt(User.getPortSSL());
				oos.flush();

			}
			t = new Timer();
			t.start();

			Object o;
			file = new HashMap<>();

			while ((o = ois.readObject()) != null) {
				if (o instanceof ManagementFiles) {

					receiveFile(o, nameServer, ois, download, file);
				} else {
					controller.commandReceiveMessage(nameServer + " : "
							+ (String) o, nameServer);
				}

				resetTime = true;
			}

		} catch (IOException | ClassNotFoundException e) {

		}

		try {

			sslSocket.close();
			oos.close();
			ois.close();

			latch.countDown();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (t != null) {
			if (!t.getStateTimeout()) {
				t.interrupt();
				try {
					sendMessage(null);
				} catch (IOException e) {
				}
			}
		}
		if (!stop) {
			model.closeServer(nameServer);
		}

	}

	/**
	 * Sends file to the server
	 * 
	 * @param path
	 *            path of file
	 */
	public void sendFile(String path) {

		File file = new File(path);
		ManagementFiles managementFile;

		controller.commandReceiveMessage("File sending", nameServer);
		synchronized (lock) {
			id++;

			managementFile = new ManagementFiles(file.getName(), id,
					(int) file.length());
		}

		try {
			super.sendFile(file, nameServer, managementFile, download);
			controller.commandReceiveMessage("File sent", nameServer);
		} catch (IOException e) {
			controller
					.commandReceiveMessage("Impossible send file", nameServer);
		}

	}

	/**
	 * 
	 * Sends chunk of bytes to the server
	 * 
	 * @param file
	 * @param message
	 *            bytes to send
	 * @param step
	 *            how many bytes to send
	 * 
	 * @see ManagementFiles
	 */
	public void sendMessage(ManagementFiles file, byte[] message, int step)
			throws IOException {
		synchronized (lockAll) {
			if (sslSocket.isConnected() && !sslSocket.isClosed()) {
				try {
					oos.writeObject(file);
					oos.flush();

					oos.writeInt(step);
					oos.flush();
					oos.write(message, 0, step);
					oos.flush();
				} catch (IOException e) {

					sslSocket.close();
					model.closeServer(nameServer);
					oos.close();
					ois.close();
				}

				resetTime = true;
			}
		}

	}

	/**
	 * Sends message to the client
	 * 
	 * @param message
	 *            message to send
	 */
	public void sendMessage(String message) throws IOException {

		synchronized (lockAll) {

			if (sslSocket.isConnected() && !sslSocket.isClosed()) {
				try {
					resetTime = true;
					oos.writeObject(message);
					oos.flush();
				} catch (IOException e) {
					controller.commandReceiveMessage("Impossible send message",
							nameServer);
					sslSocket.close();
					model.closeServer(nameServer);
					oos.close();
					ois.close();
				}

			}
		}

	}

	/**
	 * Close the client
	 */
	public void close() {
		stop = true;
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Exchange keystore with the server of user you want to chat
	 * 
	 * @param ip
	 *            ip address of user's server
	 * @param port
	 *            router's port of server
	 * @param sender
	 *            who sends request of chat. user or web server
	 * 
	 */
	public static String ObtainKeyStore(String ip, int port, String sender) {

		File file = new File(System.getProperty("user.dir") + "/"
				+ User.getNickName() + "ServerKey.jks");
		String name = "";
		try {

			Socket socket = new Socket(ip, port);

			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());

			oos.writeUTF(User.getNickName());
			oos.flush();
			oos.writeUTF(sender);
			oos.flush();
			name = ois.readUTF();

			if (name == null) {
				oos.close();
				ois.close();
				socket.close();
				return name;
			}

			oos.writeInt((int) file.length());
			oos.flush();
			int size = ois.readInt();

			FileInputStream fileStream = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			fileStream.read(buffer);
			oos.write(buffer);

			File receivedFile = new File(System.getProperty("user.dir") + "/"
					+ name + "ServerKey.jks");
			receivedFile.createNewFile();
			FileOutputStream outStream = new FileOutputStream(receivedFile);

			byte[] bufferReader = new byte[size];
			ois.readFully(bufferReader);
			outStream.write(bufferReader);
			oos.close();
			ois.close();
			socket.close();
			fileStream.close();
			outStream.close();

		} catch (IOException e) {
			return null;
		}

		return name;
	}

	/**
	 * 
	 * @return ip address and port of server
	 */
	public String getIp() {
		return ip + ":" + port;
	}

	/**
	 * 
	 * Returns the connection state
	 * 
	 * @return true if the server was successfully connected to the server
	 * 
	 */
	public boolean isConnected() {

		if (sslSocket == null) {
			return false;
		}
		return sslSocket.isConnected();
	}

	/**
	 * Returns the closed state of server
	 * 
	 * @return true if the client is closed
	 */

	public boolean isClosed() {
		if (sslSocket == null) {
			return true;
		}
		return sslSocket.isClosed();
	}

	public String getNameServer() {
		return nameServer;
	}

}