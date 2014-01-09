package client_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NotCryptedClient implements Runnable {

	public Socket s = null;
	public ObjectOutputStream OOS = null;
	public ObjectInputStream OIS = null;
	public BufferedReader br = null;
	public Thread t = null;
	public String str = "";

	public NotCryptedClient() throws IOException, ClassNotFoundException {

		// mi connetto all'ip sulla porta 9999
		s = new Socket("192.168.1.100", 9999);
		System.out.println("** Sono connesso con il server **");
		System.out.println("IP: " + s.getInetAddress());
		System.out.println("Porta: " + s.getPort());

		// inizializzo gli stream che mi permetteranno di inviare e ricevere i
		// messaggi
		OOS = new ObjectOutputStream(s.getOutputStream());
		OIS = new ObjectInputStream(s.getInputStream());

		// leggo l'input della tastiera
		br = new BufferedReader(new InputStreamReader(System.in));

		t = new Thread(this);
		t.start();

		while (s.isConnected()) {
			// leggo quello che mi arriva dal server
			while ((str = (String) OIS.readObject()) != null) {
				System.out.println("Server: " + str);
			}
		}
	}

	public void run() {
		while (s.isConnected()) {
			try {
				// leggo tutto quello che viene premuto sulla tastiera
				while ((str = br.readLine()) != null) {
					// invio messaggio al server
					OOS.writeObject(str);
					OOS.flush();
				}
			} catch (IOException e) {
				System.out
						.println("** Il server potrebbe essersi disconnesso! **");
			}
		}
	}

	public static void main(String[] Args) throws IOException,
			ClassNotFoundException {
		new NotCryptedClient();
	}
}
