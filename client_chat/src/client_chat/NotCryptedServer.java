package client_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NotCryptedServer implements Runnable {

	ServerSocket ss = null;
	Socket s = null;
	ObjectOutputStream OOS = null;
	ObjectInputStream OIS = null;
	BufferedReader br = null;
	Thread t = null;
	String str = null;

	public NotCryptedServer() throws IOException, ClassNotFoundException {

		// il server "riceverà" i client sulla porta 9999
		ss = new ServerSocket(9999);
		System.out.println("In attesa di client...");

		// stabilisce la connessione con il client
		s = ss.accept();
		System.out.println("** Un client si è connesso **");
		System.out.println("IP: " + s.getInetAddress());
		System.out.println("Porta: " + s.getPort());
		String str = "";

		// inizializzo gli stream che mi permetteranno di inviare e ricevere i
		// messaggi
		OOS = new ObjectOutputStream(s.getOutputStream());
		OIS = new ObjectInputStream(s.getInputStream());

		// leggo l'input della tastiera
		br = new BufferedReader(new InputStreamReader(System.in));

		t = new Thread(this);
		t.start();

		while (s.isConnected()) {
			// leggo quello che mi arriva dal client
			while ((str = (String) OIS.readObject()) != null) {
				System.out.println("Client: " + str);
			}
		}
	}

	public void run() {
		while (s.isConnected()) {
			try {
				// leggo tutto quello che viene premuto sulla tastiera
				while ((str = br.readLine()) != null) {
					// invio il messaggio al client
					OOS.writeObject(str);
					OOS.flush(); // pulisco il buffer
				}
			} catch (IOException e) {
				System.out
						.println("** Il client potrebbe essersi disconnesso! **");
			}
		}
	}

	public static void main(String[] Args) throws Exception, IOException,
			ClassNotFoundException {

		new NotCryptedServer();
	}

}
