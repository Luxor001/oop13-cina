package client_chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
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
	private String nameClient = System.getProperty("user.name");
	private String nameServer = null;
	private boolean resetTime = false;
	private boolean stop = false;
	private CountDownLatch latch = new CountDownLatch(1);
	ModelInterface model;

	public Client(String ip, ViewObserver controller, ModelInterface model,
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

		try {
			sslSocket = (SSLSocket) sslSocketFactory.createSocket(ip, 9999);
			sslSocket.startHandshake();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("** Sono connesso con il server **");
		System.out.println("IP: " + sslSocket.getInetAddress());
		System.out.println("Porta: " + sslSocket.getPort());
		// inizializzo gli stream che mi permetteranno di inviare e ricevere i
		// mess ->

		try {
			oos = new ObjectOutputStream(sslSocket.getOutputStream());
			ois = new ObjectInputStream(sslSocket.getInputStream());

			sendMessage(nameClient);
			ois.readObject();
			nameServer = (String) ois.readObject();
			model.addNickName(nameServer, sslSocket.getInetAddress().toString());

		} catch (IOException | ClassNotFoundException e1) {
			e1.printStackTrace();
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

			Object o;
			byte[] buffer = new byte[150000];
			Map<String, FileOutputStream> file = new HashMap<>();

			while ((o = ois.readObject()) != null) {
				if (o instanceof Boolean) {
					if ((boolean) o) {
						int step = ois.readInt();
						String name = ois.readUTF();
						ois.readFully(buffer, 0, step);
						System.out.println("Bytes received from " + name
								+ " : " + step);
						FileOutputStream fileStream = file.get(name);
						if (fileStream == null) {
							fileStream = new FileOutputStream(
									new File(System.getProperty("user.dir")
											+ "/" + name));
						}

						fileStream.write(buffer, 0, step);
						file.put(name, fileStream);
						if (step < 1024) {
							fileStream.flush();
							fileStream.close();
							file.put(name, fileStream);
							file.remove(name);
							System.out.println("File received " + name);
						}
					} else {
						controller.commandReceiveMessage(nameServer + " : "
								+ (String) ois.readObject(), nameServer);
					}
					resetTime = true;
				}
			}

			// LOGOUT
			// while ((str = (String) ois.readObject()) != null) {
			// controller.commandReceiveMessage(nameServer + " : " + str,
			// nameServer);
			// resetTime = true;
			// }
			// LOGOUT

			/*
			 * //SEND FILE int oldstep = 0; boolean isFile = false;
			 * 
			 * while ((o = ois.readObject()) != null) {
			 * 
			 * if (o instanceof Boolean) { if ((boolean) o) { int step =
			 * ois.readInt();
			 * 
			 * bytesRead = step; ois.readFully(buffer, 0, step);
			 * 
			 * //bytesRead = ois.read(buffer, 0, step); if (bytesRead >= 0) {
			 * 
			 * outStream.write(buffer, 0, bytesRead);
			 * 
			 * counter += bytesRead; System.out.println("total bytes read: " +
			 * counter); } if (bytesRead < 1024) { outStream.flush();
			 * outStream.close(); } oldstep = bytesRead; } else {
			 * chat.append(nameServer + " : " + (String) ois.readObject() +
			 * "\n"); } } }
			 */
			// SEND FILE
		} catch (IOException | ClassNotFoundException e) {
			// SEND FILE
			/*
			 * System.out.println(bytesRead); try { outStrbeam.close(); } catch
			 * (IOException e1) { e1.printStackTrace(); }
			 */
			// SEND FILE
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

	public void sendFile(File file) throws IOException {
		String name = file.getName();
		int step = 150000;
		long fileSize = file.length();
		FileInputStream fileStream = new FileInputStream(file);
		byte[] buffer = new byte[step];

		System.out.println("File sending");
		while (fileSize > 0) {
			fileSize -= step;

			if (fileSize < 0) {
				fileSize += step;
				step = (int) fileSize;
			}

			fileStream.read(buffer);
			sendMessage(buffer, step, name);
		}
		System.out.println("File sent");

		fileStream.close();
	}

	public synchronized void sendMessage(byte[] message, int step, String name) {
		if (sslSocket.isConnected() && !sslSocket.isClosed()) {
			try {
				oos.writeObject(true);
				oos.flush();
				oos.writeInt(step);
				oos.flush();
				oos.writeUTF(name);
				oos.flush();
				oos.write(message, 0, step);
				oos.flush();
				resetTime = true;
			} catch (IOException e) {
			}
		}
	}

	public synchronized void sendMessage(String message) {
		int cont = 0;

		while (cont < 5) {
			if (sslSocket.isConnected() && !sslSocket.isClosed()) {
				try {
					resetTime = true;
					oos.writeObject(false);
					oos.flush();
					oos.writeObject(message);
					oos.flush();
					cont = 6;
				} catch (IOException e) {
				}
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			cont++;
		}

		if (cont == 5) {
			controller.commandReceiveMessage("Messaggio non inviato,riprova",
					getNameServer());
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

	public String getIp() {
		return ip;
	}

	public boolean isConnected() {
		return sslSocket.isConnected();
	}

	public boolean isClosed() {
		return sslSocket.isClosed();
	}

	public String getNameServer() {
		return nameServer;
	}

}