package client_chat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

/*N.B. queste sono classi di prova, create per verificare la fattibilit� del progetto,
 per questo motivo sono presenti indirizzi ip,porte,percorsi assoluti inseriti in modo manuale
 dal programmatore*/

public class Server implements Runnable {

	SSLServerSocket sslServerSocket;
	SSLServerSocketFactory sslServerSocketFactory = null;
	SSLSocket sslSocket = null;
	ObjectOutputStream OOS = null;
	ObjectInputStream OIS = null;
	BufferedReader br = null;
	Thread t = null;
	String str = null;
	ViewObserver controller;

	public Server(ViewObserver controller) throws IOException,
			ClassNotFoundException {

		this.controller = controller;
		try {

			KeyStore serverKeys = KeyStore.getInstance("JKS");
			serverKeys.load(new FileInputStream("ServerKey.jks"),
					"password".toCharArray());
			KeyManagerFactory serverKeyManager = KeyManagerFactory
					.getInstance("SunX509");

			serverKeyManager.init(serverKeys, "password".toCharArray());

			KeyStore clientPub = KeyStore.getInstance("JKS");
			clientPub.load(new FileInputStream("ClientKey.jks"),
					"changeit".toCharArray());
			TrustManagerFactory trustManager = TrustManagerFactory
					.getInstance("SunX509");
			trustManager.init(clientPub);

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

		t = new Thread(this);
		t.start();

	}

	public void run() {

		System.out.println("In attesa di client...");

		try {
			sslSocket = (SSLSocket) sslServerSocket.accept();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("** Un client si � connesso **");
		System.out.println("IP: " + sslSocket.getInetAddress());
		System.out.println("Porta: " + sslSocket.getPort());
		// inizializzo gli stream che mi permetteranno di inviare e ricevere
		// i
		// mess
		try {
			OOS = new ObjectOutputStream(sslSocket.getOutputStream());
			OIS = new ObjectInputStream(sslSocket.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (sslSocket.isConnected()) {
			// leggo quello che mi arriva dal client
			try {
				while ((str = (String) OIS.readObject()) != null) {

					String stringa = "Client : " + str + "\n";
					controller.commandReceiveMessage(stringa, "Client");

					// chat.get(0).append("Client : " + str + "\n");

				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMessage(String message) {
		if (sslSocket.isConnected()) {
			try {
				OOS.writeObject(message);
				OOS.flush();
			} catch (IOException e) {
				System.out
						.println("** Il client potrebbe essersi disconnesso! **");
			}
		}

	}

	/*
	 * public void addChat(JTextArea chat) { this.chat.add(chat); }
	 */
	public boolean containIp(String ip) {
		if (sslSocket != null) {
			return sslSocket.getInetAddress().toString().equals(ip);
		}
		return false;
	}

}