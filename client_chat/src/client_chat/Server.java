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

	SSLServerSocket sslServerSocket;
	SSLServerSocketFactory sslServerSocketFactory = null;

	// list of clients connected to server
	List<SSLSocket> sslSocket = new ArrayList<>();
	List<ObjectOutputStream> oos = new ArrayList<>();
	Message m;
	String str = null;

	public Server(ViewObserver controller) throws IOException,
			ClassNotFoundException {

		m = new Message(controller);
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

				sslSocket.add((SSLSocket) sslServerSocket.accept());
				System.out.println("** Un client si è connesso **");
				System.out.println("IP: "
						+ sslSocket.get(sslSocket.size() - 1).getInetAddress());
				System.out.println("Porta: "
						+ sslSocket.get(sslSocket.size() - 1).getPort());

				oos.add(new ObjectOutputStream(sslSocket.get(
						sslSocket.size() - 1).getOutputStream()));

				new Thread(new MessageFromClient(
						sslSocket.get(sslSocket.size() - 1), m)).start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	public void sendMessage(String message, String ip) {

		for (int i = 0; i < sslSocket.size(); i++) {
			if (sslSocket.get(i).getInetAddress().toString().equals(ip)
					&& sslSocket.get(i).isConnected()) {
				try {
					oos.get(i).writeObject(message);
					oos.get(i).flush();
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	private static class MessageFromClient implements Runnable {
		Message m;
		ObjectInputStream ois = null;
		SSLSocket sslSocket;
		String str = null;

		public MessageFromClient(SSLSocket sslSocket, Message m) {
			this.m = m;
			this.sslSocket = sslSocket;
			try {
				ois = new ObjectInputStream(sslSocket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			while (sslSocket.isConnected()) {
				// leggo quello che mi arriva dal client
				try {
					while ((str = (String) ois.readObject()) != null) {
						m.receiveMessage(str);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static class Message {
		ViewObserver controller;

		public Message(ViewObserver controller) {
			this.controller = controller;
		}

		private synchronized void receiveMessage(String stringa) {
			controller.commandReceiveMessage(stringa, stringa.split(" :")[0]);
		}
	}

}