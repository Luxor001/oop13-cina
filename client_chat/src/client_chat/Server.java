package client_chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private Model model;

	public Server(ViewObserver controller, Model model, String password)
			throws IOException, ClassNotFoundException {

		this.controller = controller;
		this.model = model;

		try {

			KeyStore serverKeys = KeyStore.getInstance("JKS");
			serverKeys.load(new FileInputStream(System.getProperty("user.dir")
					+ "/" + User.getNickName() + "ServerKey.jks"), null);

			KeyManagerFactory serverKeyManager = KeyManagerFactory
					.getInstance("SunX509");

			serverKeyManager.init(serverKeys, password.toCharArray());

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

				new Thread(client.get(client.size() - 1)).start();

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

	public synchronized boolean sendFile(String path, String name) {
		for (int i = 0; i < client.size(); i++) {

			if (!client.get(i).isClosed() && client.get(i).isConnected()
					&& client.get(i).getNameClient().equals(name)) {

				class SendFile extends Thread {
					private String path;
					private MessageFromClient client;

					public SendFile(String path, MessageFromClient client) {
						this.path = path;
						this.client = client;
					}

					public void run() {
						try {
							client.sendFile(path);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				new SendFile(path, client.get(i)).start();

				return true;
			}
		}

		return false;
	}

	public synchronized boolean isConnect(String ip) {

		for (int i = 0; i < client.size(); i++) {
			if (client.get(i).getIp().substring(1).equals(ip)) {
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

	private static class MessageFromClient extends SendReceiveFile implements
			Runnable {

		private ViewObserver controller;
		private Model model;
		private ObjectInputStream ois = null;
		private ObjectOutputStream oos = null;
		private SSLSocket sslSocket;
		private String nameServer = User.getNickName();
		private String nameClient = null;
		private String ip = "";
		private int id = 0;
		private int port;
		private Object lock = new Object();
		private boolean close = false;
		private Downloaded download;

		public MessageFromClient(SSLSocket sslSocket, ViewObserver controller,
				Model model) {
			this.controller = controller;
			this.sslSocket = sslSocket;
			this.model = model;
			try {
				ois = new ObjectInputStream(sslSocket.getInputStream());
				oos = new ObjectOutputStream(sslSocket.getOutputStream());
				download = model.getDownloaded();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {

			System.out.println("** Un client si � connesso **");
			System.out.println("IP: " + sslSocket.getInetAddress());
			System.out.println("Porta: " + sslSocket.getPort());

			try {

				nameClient = (String) ois.readObject();
				port = ois.readInt();
				ip = sslSocket.getInetAddress().toString();
				model.addNickName(nameClient, sslSocket.getInetAddress()
						.toString().substring(1), port);
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}

			// leggo quello che mi arriva dal client
			try {

				Object o;
				Map<Integer, DownloadFile> file = new HashMap<>();

				while ((o = ois.readObject()) != null) {
					if (o instanceof ManagementFiles) {

						receiveFile(o, nameClient, ois, download, file);

					} else {
						controller.commandReceiveMessage(nameClient + " : "
								+ (String) o, nameClient);
					}
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void sendFile(String path) throws IOException {

			File file = new File(path);
			ManagementFiles managementFile;

			controller.commandReceiveMessage("File sending", nameServer);
			synchronized (lock) {
				id++;

				managementFile = new ManagementFiles(file.getName(), id,
						(int) file.length());
			}

			super.sendFile(file, nameServer, managementFile, download);
			controller.commandReceiveMessage("File sent", nameServer);

		}

		public synchronized void sendMessage(Object message) throws IOException {

			if (!close && sslSocket.isConnected() && !sslSocket.isClosed()) {
				oos.writeObject(message);
				oos.flush();
			}

			if (message == null) {
				close = true;
			}
		}

		public synchronized void sendMessage(ManagementFiles file,
				byte[] message, int step) throws IOException {

			if (!close && sslSocket.isConnected() && !sslSocket.isClosed()) {

				oos.writeObject(file);
				oos.flush();

				oos.writeInt(step);
				oos.flush();
				oos.write(message, 0, step);
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
			return ip + ":" + port;
		}

		public boolean isConnected() {
			if (sslSocket == null) {
				return false;
			}
			return sslSocket.isConnected();
		}

		public boolean isClosed() {
			if (sslSocket == null) {
				return true;
			}
			return sslSocket.isClosed();
		}

	}

}