package client_chat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

/*N.B. queste sono classi di prova, create per verificare la fattibilità del progetto,
 per questo motivo sono presenti indirizzi ip,porte,percorsi assoluti inseriti in modo manuale
 dal programmatore*/

public class server implements Runnable {

	SSLServerSocket sslServerSocket = null;
	SSLServerSocketFactory sslServerSocketFactory = null;
	SSLSocket sslSocket = null;
	ObjectOutputStream OOS = null;
	ObjectInputStream OIS = null;
	BufferedReader br = null;
	Thread t = null;
	String str = null;

	public server() throws IOException, ClassNotFoundException {

		try {

			KeyStore serverKeys = KeyStore.getInstance("JKS");
			serverKeys.load(new FileInputStream(
					"I:\\java\\eclipse\\server\\keystore.jks"), "password"
					.toCharArray());
			KeyManagerFactory serverKeyManager = KeyManagerFactory
					.getInstance("SunX509");

			serverKeyManager.init(serverKeys, "password".toCharArray());

			KeyStore clientPub = KeyStore.getInstance("JKS");
			clientPub.load(new FileInputStream(
					"I:\\java\\eclipse\\client\\keystore.jks"), "changeit"
					.toCharArray());
			TrustManagerFactory trustManager = TrustManagerFactory
					.getInstance("SunX509");
			trustManager.init(clientPub);

			// creo il socket utilizzando il protocollo SSl
			SSLContext ssl = SSLContext.getInstance("SSL");
			ssl.init(serverKeyManager.getKeyManagers(),
					trustManager.getTrustManagers(),
					SecureRandom.getInstance("SHA1PRNG"));
			sslServerSocketFactory = ssl.getServerSocketFactory();

		} catch (Exception e) {
			e.printStackTrace();
		}
		// creo il server
		SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory
				.createServerSocket(9999);
		sslServerSocket.setNeedClientAuth(true);
		System.out.println("In attesa di client...");

		sslSocket = (SSLSocket) sslServerSocket.accept();

		System.out.println("** Un client si è connesso **");
		System.out.println("IP: " + sslSocket.getInetAddress());
		System.out.println("Porta: " + sslSocket.getPort());
		String str = "";
		// inizializzo gli stream che mi permetteranno di inviare e ricevere i
		// mess
		OOS = new ObjectOutputStream(sslSocket.getOutputStream());
		OIS = new ObjectInputStream(sslSocket.getInputStream());

		br = new BufferedReader(new InputStreamReader(System.in));

		t = new Thread(this);
		t.start();

		while (sslSocket.isConnected()) {
			// leggo quello che mi arriva dal client
			while ((str = (String) OIS.readObject()) != null) {
				System.out.println("Client: " + str);
			}
		}
	}

	public void run() {
		while (sslSocket.isConnected()) {
			try {
				// leggo tutto quello che viene premuto sulla tastiera
				while ((str = br.readLine()) != null) {
					// invio
					OOS.writeObject(str);
					OOS.flush();
				}
			} catch (IOException e) {
				System.out
						.println("** Il client potrebbe essersi disconnesso! **");
			}
		}
	}

	public static void main(String[] Args) throws Exception, IOException,
			ClassNotFoundException {

		new server();
	}
}