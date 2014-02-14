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

		//SEND FILE
		/*		client.get(client.size() - 1).sendMessage("Paperino");
				File file = new File(
					"F:\\Radioactive.mp3");
				long fileSize = file.length();
				long completed = 0;
				int step = 150000;
				long size = fileSize;
				System.out.println("File sending");
				FileInputStream fileStream = new FileInputStream(file);
				byte[] buffer = new byte[step];
				while (completed < fileSize) {
				    size -= step;
				    int oldstep=step;
				    if (size < 0) {
					step = (int) (size + step);
				    }
				    fileStream.read(buffer);
				    client.get(client.size() - 1).sendMessage(buffer, step);
				    completed += oldstep;
				}
				
				fileStream.close();
				System.out.println("File Send");
		*/
		//SEND FILE
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

    public void close() {
	while (client.size() > 0) {
	    if (!client.get(client.size() - 1).isClosed()
		    && client.get(client.size() - 1).isConnected()) {
		try {
		    client.get(client.size() - 1).sendMessage(null);
		    client.remove(client.size() - 1);
		} catch (IOException e) {
		    e.printStackTrace();
		}

	    } else {
		client.remove(client.size() - 1);
	    }
	}
    }

    private static class MessageFromClient extends Thread {
	private ViewObserver controller;
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	private SSLSocket sslSocket;
	private String str = null;
	private String nameServer = System.getProperty("user.name");
	private String nameClient = null;
	private boolean close = false;

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
		//LOGOUT
		oos.writeObject(nameServer);
		oos.flush();
		//LOGUT
		nameClient = (String) ois.readObject();
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
	    } catch (IOException | ClassNotFoundException e) {
		e.printStackTrace();
	    }

	}

	public synchronized void sendMessage(Object message) throws IOException {

	    if (!close) {

		//SEND FILE
		/*oos.writeObject(false);
		oos.flush();*/
		//SEND FILE
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

	public boolean isConnected() {
	    return sslSocket.isConnected();
	}

	public boolean isClosed() {
	    return sslSocket.isClosed();
	}

    }

}