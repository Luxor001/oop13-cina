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

/*N.B. queste sono classi di prova, create per verificare la fattibilit� del progetto,
 per questo motivo sono presenti indirizzi ip,porte,percorsi assoluti inseriti in modo manuale
 dal programmatore*/

public class Client implements Runnable {
	private SSLSocketFactory sslSocketFactory = null;
	private SSLSocket sslSocket = null;
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	private String str = "";
	private ViewObserver controller;
	private String ip;
	private String nameClient = WebsocketHandler.DEBUG_NICKNAME;
	private String nameServer = null;
	private boolean resetTime = false;
	private boolean stop = false;
	private Downloaded download;
	private CountDownLatch latch = new CountDownLatch(1);
	private Object lock = new Object();
	private Object lockFile = new Object();
	private int id = 0;
	private Model model;

	public Client(String ip, String name, ViewObserver controller, Model model,
			String keyStore) throws IOException, ClassNotFoundException {

		String path = System.getProperty("user.dir") + "/" + nameClient
				+ "ClientKey.jks";
		char[] passphrase = "changeit".toCharArray();
		KeyStore keystore;
		TrustManagerFactory tmf;
		SSLContext context;
		TrustManager[] trustManagers;

		this.model = model;
		this.ip = ip;
		this.controller = controller;
		nameServer = name;

		try {
			// indico il tipo della chiave
			keystore = KeyStore.getInstance("jks");
			// carico la chiave
			keystore.load(new FileInputStream(path), null);

			// creo un'istanza che utilizza l'algoritmo "sunx509"
			KeyManagerFactory clientKeyManager = KeyManagerFactory
					.getInstance("SunX509");
			clientKeyManager.init(keystore, passphrase);

			// ottengo la chiave pubblica
			KeyStore serverPub = KeyStore.getInstance("jks");
			// successivamente verr� inviata dal webserver

			serverPub.load(new FileInputStream(keyStore), null);

			tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(serverPub);

			context = SSLContext.getInstance("SSL");
			trustManagers = tmf.getTrustManagers();
			context.init(clientKeyManager.getKeyManagers(), trustManagers,
					SecureRandom.getInstance("SHA1PRNG"));
			sslSocketFactory = context.getSocketFactory();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		}

		// stabilisco la connessione con il server
		new Thread(this).start();

	}

	public void run() {

		synchronized (lock) {

			try {
				sslSocket = (SSLSocket) sslSocketFactory.createSocket(ip, 9999);
				sslSocket.startHandshake();
			} catch (IOException e) {
				model.removeNickName(nameServer);
			}

			System.out.println("** Sono connesso con il server **");
			System.out.println("IP: " + sslSocket.getInetAddress());
			System.out.println("Porta: " + sslSocket.getPort());
			// inizializzo gli stream che mi permetteranno di inviare e ricevere
			// i
			// mess ->

			try {
				oos = new ObjectOutputStream(sslSocket.getOutputStream());
				ois = new ObjectInputStream(sslSocket.getInputStream());

				download = model.getDownloaded();
				oos.writeObject(false);
				oos.flush();
				oos.writeObject(nameClient);
				oos.flush();
				model.addNickName(nameServer, sslSocket.getInetAddress()
						.toString().substring(1));

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

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
						sendMessage(null);
						timeout = true;
					}
				}
			}

			public boolean getStateTimeout() {
				return timeout;
			}
		}

		Timer t = new Timer();
		t.start();

		// leggo quello che mi arriva dal server
		try {

			boolean goOn = true;
			Object o;
			byte[] buffer = new byte[150000];
			Map<Pair<String, Integer>, Pair<FileOutputStream, String>> file = new HashMap<>();

			while (goOn) {
				o = ois.readObject();

				if (o instanceof Boolean) {
					if ((boolean) o) {
						int filesize = ois.readInt();
						int step = ois.readInt();

						Pair<String, Integer> key = (Pair<String, Integer>) ois
								.readObject();

						if (step != -1) {
							ois.readFully(buffer, 0, step);

							Pair<FileOutputStream, String> value = file
									.get(key);

							if (value == null) {

								String name = key.getFirst().substring(
										key.getFirst().lastIndexOf("\\") + 1);

								String newName = name;
								int i = 1;
								while (new File(System.getProperty("user.dir")
										+ "/" + newName).exists()) {

									String[] nameExtension = name.split("\\.");

									nameExtension[0] = nameExtension[0] + "("
											+ i + ")";
									newName = nameExtension[0] + "."
											+ nameExtension[1];
									i++;
								}

								download.addFile(new Pair<String, Integer>(
										newName, key.getSecond()), filesize);
								value = new Pair<FileOutputStream, String>(
										new FileOutputStream(new File(
												System.getProperty("user.dir")
														+ "/" + newName)),
										newName);
							}

							download.updateProgressBar(
									new Pair<String, Integer>(
											value.getSecond(), key.getSecond()),
									step);
							value.getFirst().write(buffer, 0, step);
							file.put(key, value);
						} else {
							System.out.println("File received "
									+ file.get(key).getSecond());
							file.get(key).getFirst().flush();
							file.get(key).getFirst().close();
							file.remove(key);
						}
						resetTime = true;
					} else {

						String message = null;
						if ((message = (String) ois.readObject()) != null) {
							controller.commandReceiveMessage(nameServer + " : "
									+ message, nameServer);
							resetTime = true;
						} else {
							goOn = false;
						}
					}

				}
			}

		} catch (IOException | ClassNotFoundException e) {

			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!t.getStateTimeout()) {
			t.interrupt();
			sendMessage(null);
		}
		try {
			oos.close();
			ois.close();
			sslSocket.close();

			if (!stop) {
				model.closeServer(getIp());
			}

			latch.countDown();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void sendFile(String path) throws IOException {
		File file = new File(path);
		String name = file.getName();
		int step = 150000;
		long fileSize = file.length();
		FileInputStream fileStream = new FileInputStream(file);
		byte[] buffer = new byte[step];
		Pair<String, Integer> pair;

		synchronized (lock) {
			id++;
			pair = new Pair<>(path, id);
			try {
				download.addFile(new Pair<String, Integer>(name, id),
						(int) fileSize);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("File sending");
		while (fileSize > 0) {
			fileSize -= step;

			if (fileSize < 0) {
				fileSize += step;
				step = (int) fileSize;
			}
			download.updateProgressBar(
					new Pair<String, Integer>(name, pair.getSecond()), step);
			fileStream.read(buffer);
			sendMessage(buffer, (int) file.length(), step, pair);
		}

		sendMessage(buffer, (int) file.length(), -1, pair);
		System.out.println("File sent");
		fileStream.close();
	}

	public void sendMessage(byte[] message, int filesize, int step,
			Pair<String, Integer> pair) {
		synchronized (lock) {
			if (sslSocket.isConnected() && !sslSocket.isClosed()) {
				try {
					oos.writeObject(true);
					oos.flush();
					oos.writeInt(filesize);
					oos.flush();
					oos.writeInt(step);
					oos.flush();
					oos.writeObject(pair);
					oos.flush();
					if (step != -1) {
						oos.write(message, 0, step);
						oos.flush();
					}
					resetTime = true;
				} catch (IOException e) {
				}
			}
		}
	}

	public void sendMessage(String message) {

		synchronized (lock) {
			if (sslSocket.isConnected() && !sslSocket.isClosed()) {
				try {
					resetTime = true;
					oos.writeObject(false);
					oos.flush();
					oos.writeObject(message);
					oos.flush();
				} catch (IOException e) {
				}

			}
		}
	}

	public void close() {
		stop = true;
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String ObtainKeyStore(String ip, String who) {

		File file = new File(System.getProperty("user.dir") + "/"
				+ WebsocketHandler.DEBUG_NICKNAME + "ServerKey.jks");
		String name = "";
		try {

			Socket socket = new Socket(ip, 9998);

			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());

			oos.writeUTF(WebsocketHandler.DEBUG_NICKNAME);
			oos.flush();
			oos.writeUTF(who);
			oos.flush();
			name = ois.readUTF();

			if (name == null) {
				oos.close();
				ois.close();
				socket.close();
				return name;
			}

			FileInputStream fileStream = new FileInputStream(file);
			byte[] buffer = new byte[10240];
			fileStream.read(buffer);
			oos.write(buffer);
			File receivedFile = new File(System.getProperty("user.dir") + "/"
					+ name + "ServerKey.jks");
			receivedFile.createNewFile();
			FileOutputStream outStream = new FileOutputStream(receivedFile);

			byte[] bufferReader = new byte[10240];
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

	public String getIp() {
		return ip;
	}

	public boolean isConnected() {

		if (sslSocket == null) {
			return false;
		}
		return sslSocket.isConnected();
	}

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