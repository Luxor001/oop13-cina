package client_chat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.JTextArea;

/*N.B. queste sono classi di prova, create per verificare la fattibilit� del progetto,
 per questo motivo sono presenti indirizzi ip,porte,percorsi assoluti inseriti in modo manuale
 dal programmatore*/

public class Client implements Runnable {
	SSLSocketFactory sslSocketFactory = null;
	SSLSocket sslSocket = null;
	ObjectOutputStream OOS = null;
	ObjectInputStream OIS = null;
	BufferedReader br = null;
	Thread t = null;
	String str = "";
	List<JTextArea> chat = new ArrayList<>();

	public Client() throws IOException, ClassNotFoundException {

		String path = "I:\\java\\eclipse\\client\\keystore.jks";
		char[] passphrase = "changeit".toCharArray();
		KeyStore keystore;
		TrustManagerFactory tmf;
		SSLContext context;
		TrustManager[] trustManagers;

		try {
			// indico il tipo della chiave
			keystore = KeyStore.getInstance("jks");
			// carico la chiave
			keystore.load(new FileInputStream(path), passphrase);

			// creo un'istanza che utilizza l'algoritmo "sunx509"
			KeyManagerFactory clientKeyManager = KeyManagerFactory
					.getInstance("SunX509");
			clientKeyManager.init(keystore, passphrase);

			// ottengo la chiave pubblica
			KeyStore serverPub = KeyStore.getInstance("jks");
			// successivamente verr� inviata dal webserver
			serverPub.load(new FileInputStream(
					"I:\\java\\eclipse\\server\\keystore.jks"), "password"
					.toCharArray());

			tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(serverPub);

			context = SSLContext.getInstance("SSL");
			trustManagers = tmf.getTrustManagers();
			context.init(clientKeyManager.getKeyManagers(), trustManagers,
					SecureRandom.getInstance("SHA1PRNG"));
			sslSocketFactory = context.getSocketFactory();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// stabilisco la connessione con il server
		sslSocket = (SSLSocket) sslSocketFactory.createSocket("192.168.1.100",
				9999);
		sslSocket.startHandshake();

		System.out.println("** Sono connesso con il server **");
		System.out.println("IP: " + sslSocket.getInetAddress());
		System.out.println("Porta: " + sslSocket.getPort());

		// inizializzo gli stream che mi permetteranno di inviare e ricevere i
		// mess ->
		OOS = new ObjectOutputStream(sslSocket.getOutputStream());
		OIS = new ObjectInputStream(sslSocket.getInputStream());

		t = new Thread(this);
		t.start();

	}

	public void run() {

		while (sslSocket.isConnected()) {
			// leggo quello che mi arriva dal server
			try {
				while ((str = (String) OIS.readObject()) != null) {
					chat.get(0).append("Server : " + str + "\n");
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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

	public void addChat(JTextArea chat) {
		this.chat.add(chat);
	}
}