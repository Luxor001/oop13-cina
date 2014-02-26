package client_chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

	public Server(ViewObserver controller, Model model) throws IOException,
			ClassNotFoundException {

		this.controller = controller;
		this.model = model;

		try {

			KeyStore serverKeys = KeyStore.getInstance("JKS");
			serverKeys.load(new FileInputStream(System.getProperty("user.dir")
					+ "/" + WebsocketHandler.DEBUG_NICKNAME + "ServerKey.jks"),
					null);

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

			} catch (IOException e1) {
			}
		}

	}

	public boolean sendMessage(String message, String name) {

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

	public boolean sendFile(File file, String name) {
		for (int i = 0; i < client.size(); i++) {
			if (!client.get(i).isClosed() && client.get(i).isConnected()
					&& client.get(i).getNameClient().equals(name)) {
				try {
					client.get(i).sendFile(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
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
		private Model model;
		private ObjectInputStream ois = null;
		private ObjectOutputStream oos = null;
		private SSLSocket sslSocket;
		private String str = null;
		private String nameServer = WebsocketHandler.DEBUG_NICKNAME;
		private String nameClient = null;
		private String ip = "";
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
				// LOGOUT
				sendMessage(nameServer);
				// LOGUT
				ois.readObject();
				nameClient = (String) ois.readObject();
				ip = sslSocket.getInetAddress().toString();
				model.addNickName(nameClient, sslSocket.getInetAddress()
						.toString());
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}

			// leggo quello che mi arriva dal client
			try {

				Object o;
				byte[] buffer = new byte[150000];
				Map<String, FileOutputStream> file = new HashMap<>();

				while ((o = ois.readObject()) != null) {
					if (o instanceof Boolean) {
						if ((boolean) o) {
							int filesize = ois.readInt();
							int step = ois.readInt();
							String name = ois.readUTF();
							System.out.println("Bytes received from " + name
									+ " : " + step);
							ois.readFully(buffer, 0, step);
							FileOutputStream fileStream = file.get(name);

							if (fileStream == null) {
								download.addFile(name, filesize);
								fileStream = new FileOutputStream(new File(
										System.getProperty("user.dir") + "/"
												+ name));
							}

							download.updateProgressBar(name, step);
							fileStream.write(buffer, 0, step);
							file.put(name, fileStream);
							if (step < 1024) {
								System.out.println("File received " + name);
								fileStream.flush();
								fileStream.close();
								file.put(name, fileStream);
								file.remove(name);
							}

						} else {
							String message = null;
							if ((message = (String) ois.readObject()) != null) {
								controller.commandReceiveMessage(nameClient
										+ " : " + message, nameClient);
							} else {
								o = null;
							}
						}
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

		public void sendFile(File file) throws IOException {
			String name = file.getName();
			int step = 150000;
			long fileSize = file.length();
			FileInputStream fileStream = new FileInputStream(file);
			byte[] buffer = new byte[step];

			try {
				download.addFile(name, (int) fileSize);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("File sending");
			while (fileSize > 0) {
				fileSize -= step;

				if (fileSize < 0) {
					fileSize += step;
					step = (int) fileSize;
				}
				download.updateProgressBar(name, step);
				fileStream.read(buffer);
				sendMessage(buffer, (int) file.length(), step, name);
			}

			System.out.println("File sent");
			fileStream.close();
		}

		public synchronized void sendMessage(Object message) throws IOException {

			if (!close) {

				oos.writeObject(false);
				oos.flush();

				oos.writeObject(message);
				oos.flush();
			}

			if (message == null) {
				close = true;
			}
		}

		public synchronized void sendMessage(byte[] message, int filesize,
				int step, String name) throws IOException {

			if (!close) {

				oos.writeObject(true);
				oos.flush();
				oos.writeInt(filesize);
				oos.flush();
				oos.writeInt(step);
				oos.flush();
				oos.writeUTF(name);
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