package client_chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 * Creates a SSl socket server.To accept a client you need to have router's port
 * open.
 * 
 * @author Francesco Cozzolino
 * 
 */
public class Server {

	private SSLServerSocket sslServerSocket;
	private List<MessageFromToClient> client = new ArrayList<>();

	/**
	 * 
	 * Loads the keystore and creates the server;will be listening on the
	 * specified port in .ini file and starts a new thread that accepts the
	 * clients that have server's keystore.
	 * 
	 * @param controller
	 * @param model
	 * @param password
	 *            password of keystore
	 * @throws IOException
	 *             if the keystore doesn't exist or the password is wrong
	 * @see Controller
	 * @see Model
	 */
	public Server(final ViewObserver controller, final Model model,
			String password) throws IOException, ClassNotFoundException {

		SSLServerSocketFactory sslServerSocketFactory = null;

		try {

			KeyStore serverKeys = KeyStore.getInstance("JKS");
			serverKeys.load(new FileInputStream(System.getProperty("user.dir")
					+ "/" + User.getNickName() + "ServerKey.jks"), null);

			KeyManagerFactory serverKeyManager = KeyManagerFactory
					.getInstance("SunX509");

			serverKeyManager.init(serverKeys, password.toCharArray());

			SSLContext ssl = SSLContext.getInstance("SSL");
			ssl.init(serverKeyManager.getKeyManagers(), null,
					SecureRandom.getInstance("SHA1PRNG"));

			sslServerSocketFactory = ssl.getServerSocketFactory();

		} catch (Exception e) {
			e.printStackTrace();
		}

		sslServerSocket = (SSLServerSocket) sslServerSocketFactory
				.createServerSocket(User.getPortSSL());
		sslServerSocket.setNeedClientAuth(false);

		class Accept implements Runnable {

			public void run() {

				while (true) {
					try {

						client.add(new MessageFromToClient(
								(SSLSocket) sslServerSocket.accept(),
								controller, model));

						new Thread(client.get(client.size() - 1)).start();

					} catch (IOException e1) {
					}
				}

			}
		}
		new Thread(new Accept()).start();

	}

	/**
	 * Send a message to the client with the specified name
	 * 
	 * @param message
	 * @param name
	 *            name of client
	 * @return true if the client exist and is connected,false otherwise
	 */
	public synchronized boolean sendMessage(String message, String name) {

		for (int i = 0; i < client.size(); i++) {
			if (!client.get(i).isClosed() && client.get(i).isConnected()
					&& client.get(i).getNameClient().equals(name)) {
				client.get(i).sendMessage(message);
				return true;

			}
		}

		return false;
	}

	/**
	 * Send a file to the client with the specified name
	 * 
	 * @param path
	 *            path of file to send
	 * @param name
	 *            name of client
	 * @return true if the client exist and is connected,false otherwise
	 */
	public synchronized boolean sendFile(String path, String name) {
		for (int i = 0; i < client.size(); i++) {

			if (!client.get(i).isClosed() && client.get(i).isConnected()
					&& client.get(i).getNameClient().equals(name)) {

				class SendFile extends Thread {
					private String path;
					private MessageFromToClient client;

					public SendFile(String path, MessageFromToClient client) {
						this.path = path;
						this.client = client;
					}

					public void run() {
						try {
							client.sendFile(path);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				new SendFile(path, client.get(i)).start();

				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the connection state of the client with the specific ip address
	 * 
	 * @param ip
	 *            Address of client
	 * @return true if the client was successfully connected to the server
	 */
	public synchronized boolean isConnect(String ip) {

		for (int i = 0; i < client.size(); i++) {
			if (client.get(i).getIp().substring(1).equals(ip)) {
				return (!client.get(i).isClosed())
						&& client.get(i).isConnected();

			}
		}

		return false;
	}

	/**
	 * Close the server
	 */
	public synchronized void close() {
		while (client.size() > 0) {
			client.get(client.size() - 1).sendMessage(null);
			client.remove(client.size() - 1);

		}

		try {
			sslServerSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes a client from the list of connected clients
	 * 
	 * @param ip
	 */
	public synchronized void closeClient(String ip) {
		for (int i = 0; i < client.size(); i++) {
			String[] split = client.get(i).getIp().split(":");
			if (split[0].equals(ip)) {
				client.remove(i);
				return;

			}
		}
	}

	/**
	 * This class permits to sends/receives messages/files from/to other clients
	 * that connect to the server
	 * 
	 * @author Francesco Cozzolino
	 * 
	 */
	private static class MessageFromToClient extends SendReceiveFile implements
			Runnable {

		private ViewObserver controller;
		private Model model;
		private ObjectInputStream ois = null;
		private ObjectOutputStream oos = null;
		private SSLSocket sslSocket;
		private String nameServer = User.getNickName();
		private String nameClient = null;
		private String ip = "";
		private int id = 0;
		private int port;
		private Object lock = new Object();
		private boolean close = false;
		private Downloaded download;

		/**
		 * 
		 * @param sslSocket
		 *            client connected to the server
		 * @param controller
		 * @param model
		 * 
		 * @see Controller
		 * @see Model
		 */
		public MessageFromToClient(SSLSocket sslSocket,
				ViewObserver controller, Model model) {
			this.controller = controller;
			this.sslSocket = sslSocket;
			this.model = model;
			try {
				ip = sslSocket.getInetAddress().toString();
				ois = new ObjectInputStream(sslSocket.getInputStream());
				oos = new ObjectOutputStream(sslSocket.getOutputStream());
				download = model.getDownloaded();
			} catch (IOException e) {
				try {
					sslSocket.close();
				} catch (IOException e1) {
				}
				model.closeClient(ip);
			}
		}

		/**
		 * Thread listens input stream of client and show messages or downlaod
		 * file that come from him
		 */
		public void run() {
			try {

				Object o;
				Map<Integer, DownloadFile> file = new HashMap<>();

				nameClient = (String) ois.readObject();
				port = ois.readInt();

				model.addNickName(nameClient, sslSocket.getInetAddress()
						.toString().substring(1), port);

				while ((o = ois.readObject()) != null) {
					if (o instanceof ManagementFiles) {

						receiveFile(o, nameClient + nameServer, ois, download,
								file);

					} else {
						controller.commandReceiveMessage(nameClient + " : "
								+ (String) o, nameClient);
					}
				}

				if (!close) {
					model.closeClient(ip);

					sendMessage(null);
				}
				oos.close();
				ois.close();
				sslSocket.close();

			} catch (Exception e) {
				try {
					oos.close();
					ois.close();
					sslSocket.close();
					model.closeClient(ip);
				} catch (IOException e1) {
				}

			}

		}

		/**
		 * Sends file to the client
		 * 
		 * @param path
		 *            path of file
		 * @throws IOException
		 *             if it's impossible to send byte to the client or read
		 *             byte from the file
		 */
		public void sendFile(String path) throws IOException {

			File file = new File(path);
			ManagementFiles managementFile;

			controller.commandReceiveMessage("File sending", nameClient);
			synchronized (lock) {
				id++;

				managementFile = new ManagementFiles(file.getName(), id,
						(int) file.length());
			}

			super.sendFile(file, nameClient, managementFile, download);
			controller.commandReceiveMessage("File sent", nameClient);

		}

		/**
		 * Sends message to the client
		 * 
		 * @param message
		 *            message to send
		 */
		public synchronized void sendMessage(String message) {

			if (!close) {
				try {
					oos.writeObject(message);
					oos.flush();
				} catch (IOException e) {
					try {
						oos.close();
						ois.close();
						sslSocket.close();
						model.closeClient(ip);
					} catch (IOException e1) {
					}
				}

			}

			if (message == null) {
				close = true;
			}
		}

		/**
		 * 
		 * Sends chunk of bytes to the client
		 * 
		 * @param file
		 * @param message
		 *            bytes to send
		 * @param step
		 *            how many bytes to send
		 * 
		 * @see ManagementFiles
		 */
		public synchronized void sendMessage(ManagementFiles file,
				byte[] message, int step) {

			if (!close) {

				try {
					oos.writeObject(file);
					oos.flush();

					oos.writeInt(step);
					oos.flush();
					oos.write(message, 0, step);
					oos.flush();
				} catch (IOException e) {
					try {
						oos.close();
						ois.close();
						sslSocket.close();
						model.closeClient(ip);
					} catch (IOException e1) {
					}
				}

			}

			if (message == null) {
				close = true;
			}
		}

		public String getNameClient() {
			return nameClient;
		}

		/**
		 * 
		 * @return ip address and port of client
		 */
		public String getIp() {
			return ip + ":" + port;
		}

		/**
		 * 
		 * Returns the connection state of the client with the specific ip
		 * address
		 * 
		 * @return true if the client was successfully connected to the server
		 * 
		 */
		public boolean isConnected() {
			if (sslSocket == null) {
				return false;
			}
			return sslSocket.isConnected();
		}

		/**
		 * Returns the closed state of client
		 * 
		 * @return true if the client is closed
		 */
		public boolean isClosed() {
			if (sslSocket == null) {
				return true;
			}
			return sslSocket.isClosed();
		}

	}

}