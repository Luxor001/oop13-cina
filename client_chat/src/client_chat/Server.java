package client_chat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server implements Runnable {

	private SSLServerSocket sslServerSocket;
	private SSLServerSocketFactory sslServerSocketFactory = null;
	// list of clients connected to server
	private List<MessageFromClient> client = new ArrayList<>();
	private ViewObserver controller;
	private ModelInterface model;

	public Server(ViewObserver controller, ModelInterface model)
			throws IOException, ClassNotFoundException {

		this.controller = controller;
		this.model = model;

		try {

			KeyStore serverKeys = KeyStore.getInstance("JKS");
			serverKeys.load(new FileInputStream(System.getProperty("user.name")
					+ "ServerKey.jks"), null);

			KeyManagerFactory serverKeyManager = KeyManagerFactory
					.getInstance("SunX509");

			serverKeyManager.init(serverKeys, "password".toCharArray());

			// creo il socket utilizzando il protocollo SSl
			SSLContext ssl = SSLContext.getInstance("SSL");
			ssl.init(serverKeyManager.getKeyManagers(), null,
					SecureRandom.getInstance("SHA1PRNG"));

			sslServerSocketFactory = ssl.getServerSocketFactory();

		} catch (Exception e) {
			e.printStackTrace();
		}
		// creo il server
		sslServerSocket = (SSLServerSocket) sslServerSocketFactory
				.createServerSocket(9999);
		sslServerSocket.setNeedClientAuth(false);
		new Thread(this).start();

	}

	public void run() {

		System.out.println("In attesa di client...");

		while (true) {
			try {

				client.add(new MessageFromClient((SSLSocket) sslServerSocket
						.accept(), controller, model));

				client.get(client.size() - 1).start();

				// SEND FILE
				/*
				 * client.get(client.size() - 1).sendMessage("Paperino"); File
				 * file = new File( "F:\\Radioactive.mp3"); long fileSize =
				 * file.length(); long completed = 0; int step = 150000; long
				 * size = fileSize; System.out.println("File sending");
				 * FileInputStream fileStream = new FileInputStream(file);
				 * byte[] buffer = new byte[step]; while (completed < fileSize)
				 * { size -= step; int oldstep=step; if (size < 0) { step =
				 * (int) (size + step); } fileStream.read(buffer);
				 * client.get(client.size() - 1).sendMessage(buffer, step);
				 * completed += oldstep; }
				 * 
				 * fileStream.close(); System.out.println("File Send");
				 */
				// SEND FILE
			} catch (IOException e1) {
			}
		}

	}

	public synchronized boolean sendMessage(String message, String name) {

		for (int i = 0; i < client.size(); i++) {
			if (!client.get(i).isClosed() && client.get(i).isConnected()
					&& client.get(i).getNameClient().equals(name)) {
				try {
					client.get(i).sendMessage(message);
					return true;
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		return false;
	}

	public boolean isConnect(String ip) {

		for (int i = 0; i < client.size(); i++) {
			if (client.get(i).getIp().equals(ip)) {
				return (!client.get(i).isClosed())
						&& client.get(i).isConnected();

			}
		}

		return false;
	}

	public synchronized void close() {
		while (client.size() > 0) {
			try {
				client.get(client.size() - 1).sendMessage(null);
				client.remove(client.size() - 1);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		try {
			sslServerSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void closeClient(String name) {
		for (int i = 0; i < client.size(); i++) {
			if (client.get(i).getNameClient().equals(name)) {
				if (client.get(i).isClosed()) {
					client.remove(i);
					return;
				}

			}
		}
	}

	private static class MessageFromClient extends Thread {
		private ViewObserver controller;
		private ModelInterface model;
		private ObjectInputStream ois = null;
		private ObjectOutputStream oos = null;
		private SSLSocket sslSocket;
		private String str = null;
		private String nameServer = System.getProperty("user.name");
		private String nameClient = null;
		private String ip = "";
		private boolean close = false;

		public MessageFromClient(SSLSocket sslSocket, ViewObserver controller,
				ModelInterface model) {
			this.controller = controller;
			this.sslSocket = sslSocket;
			this.model = model;
			try {
				ois = new ObjectInputStream(sslSocket.getInputStream());
				oos = new ObjectOutputStream(sslSocket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {

			System.out.println("** Un client si è connesso **");
			System.out.println("IP: " + sslSocket.getInetAddress());
			System.out.println("Porta: " + sslSocket.getPort());

			try {
				// LOGOUT
				oos.writeObject(nameServer);
				oos.flush();
				// LOGUT
				nameClient = (String) ois.readObject();
				ip = sslSocket.getInetAddress().toString();
				model.addNickName(nameClient, sslSocket.getInetAddress()
						.toString());
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}

			// leggo quello che mi arriva dal client
			try {

				while ((str = (String) ois.readObject()) != null) {
					controller.commandReceiveMessage(nameClient + " : " + str,
							nameClient);
				}

				if (!close) {
					sendMessage(null);
				}
				oos.close();
				ois.close();
				sslSocket.close();
				if (!close) {
					model.closeClient(nameClient);
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}

		}

		public synchronized void sendMessage(Object message) throws IOException {

			if (!close) {

				// SEND FILE
				/*
				 * oos.writeObject(false); oos.flush();
				 */
				// SEND FILE
				oos.writeObject(message);
				oos.flush();
			}

			if (message == null) {
				close = true;
			}
		}

		public synchronized void sendMessage(byte[] message, int step)
				throws IOException {

			if (!close) {

				oos.writeObject(true);
				oos.flush();
				oos.writeInt(step);
				oos.flush();
				oos.write(message, 0, 150000);
				oos.flush();
			}

			if (message == null) {
				close = true;
			}
		}

		public String getNameClient() {
			return nameClient;
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

	}

}