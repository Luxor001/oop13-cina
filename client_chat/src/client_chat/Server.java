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

	public Server(ViewObserver controller) throws IOException,
			ClassNotFoundException {

		this.controller = controller;

		try {

			KeyStore serverKeys = KeyStore.getInstance("JKS");
			serverKeys.load(new FileInputStream("ServerKey.jks"), null);

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
						.accept(), controller));

				client.get(client.size() - 1).start();

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	public boolean sendMessage(String message, String name) {

		for (int i = 0; i < client.size(); i++) {
			if (client.get(i).getNameClient().equals(name)) {
				if (!client.get(i).isClosed() && client.get(i).isConnected()) {
					try {
						client.get(i).sendMessage(message);
						return true;
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					client.remove(i);
				}

			}
		}

		return false;
	}

	private static class MessageFromClient extends Thread {
		private ViewObserver controller;
		private ObjectInputStream ois = null;
		private ObjectOutputStream oos = null;
		private SSLSocket sslSocket;
		private String str = null;
		private String nameServer = System.getProperty("user.name");
		private String nameClient = null;

		public MessageFromClient(SSLSocket sslSocket, ViewObserver controller) {
			this.controller = controller;
			this.sslSocket = sslSocket;
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
				oos.writeObject(nameServer);
				oos.flush();
				nameClient = (String) ois.readObject();
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}

			// leggo quells che mi arriva dal client
			try {

				while ((str = (String) ois.readObject()) != null) {
					controller.commandReceiveMessage(nameClient + " : " + str,
							nameClient);
				}

				oos.writeObject(null);
				oos.close();
				ois.close();

				sslSocket.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}

		}

		public void sendMessage(String message) throws IOException {

			oos.writeObject(message);
			oos.flush();

		}

		public String getNameClient() {
			return nameClient;
		}

		public boolean isConnected() {
			return sslSocket.isConnected();
		}

		public boolean isClosed() {
			return sslSocket.isClosed();
		}

	}

}