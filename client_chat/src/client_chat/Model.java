package client_chat;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Model implements ModelInterface {

	public enum connectionResult {
		OK, TIMEOUT, BAD_URI
	}

	private ManageClient client;
	private Server server;
	private KeyStoreServer keyStoreServer;
	private Map<String, String> peopleChat = new HashMap<>();
	private WebsocketHandler sockethandler;

	public void sendMessage(String message, String name) {

		if (message != "") {

			// CHANGE
			// if (server != null) {
			if (!server.sendMessage(message, name)) {
				if (!client.sendMessage(message, name)) {
					connectToServer(peopleChat.get(name).substring(1),
							System.getProperty("user.dir") + "/" + name
									+ "ServerKey.jks");
					client.sendMessage(message, name);
				}
			}
			// } else if (!client.sendMessage(message, name)) {
			// connectToServer(peopleChat.get(name).substring(1), name
			// + "ServerKey.jks");
			// client.sendMessage(message, name);
			// }
		}
	}

	public synchronized void addNickName(String nickName, String ip) {
		peopleChat.put(nickName, ip);
	}

	public synchronized void connectToServer(String ip, String keyStore) {

		try {
			client.addClient(ip, keyStore);

		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String exist(String name) {
		return peopleChat.get(name);
	}

	public boolean isConnect(String ip) {
		return server.isConnect(ip) || client.isConnect(ip);
	}

	public void closeAll() {
		client.close();
		server.close();
	}

	public synchronized void closeClient(String name) {
		server.closeClient(name);
	}

	public synchronized void closeServer(String ip) {
		client.closeServer(ip);
	}

	public void attachViewObserver(ViewObserver controller) {

		if (!new File(System.getProperty("user.name") + "ServerKey.jks")
				.exists()) {
			createKeyStore(System.getProperty("user.name") + "Server",
					"ServerKey", "password");
		}

		if (!new File(System.getProperty("user.name") + "ClientKey.jks")
				.exists()) {
			createKeyStore(System.getProperty("user.name") + "Client",
					"ClientKey", "changeit");
		}

		// server will be created at start of programm and pending some clients
		try {
			keyStoreServer = new KeyStoreServer();
			server = new Server(controller, this);
			client = new ManageClient(controller, this);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void createKeyStore(String name, String alias, String password) {
		try {

			String path = System.getProperty("user.dir") + "\\" + name;
			String nameCertificate;
			// creo un file bat
			FileOutputStream output;
			DataOutputStream stdout;

			if (System.getProperty("os.name").contains("Windows")) {
				nameCertificate = name + "Certificate.bat";
			} else {
				nameCertificate = name + "Certificate.sh";

			}

			output = new FileOutputStream(nameCertificate);
			stdout = new DataOutputStream(output);
			// codice per la creazione di un certificato
			stdout.write("@echo off\n".getBytes());
			stdout.write("cd ".getBytes());
			stdout.write(System.getProperty("java.home").getBytes());
			stdout.write("\n".getBytes());
			stdout.write(("(echo francesco cozzolino & echo cozzo & echo cozzo & echo misano adriatico "
					+ "& echo rn & echo it & echo si) | keytool -genkey -alias "
					+ alias
					+ " -keyalg RSA"
					+ " -keypass "
					+ password
					+ " -storepass " + password + " -keystore " + path + "Key.jks\n")
					.getBytes());

			stdout.write(("keytool -export -alias " + alias + " -storepass "
					+ password + " -file " + path
					+ "Certificate.cer -keystore " + path + "Key.jks\n")
					.getBytes());

			stdout.write("echo on\n".getBytes());

			stdout.close();

			Runtime.getRuntime().exec(nameCertificate).waitFor();

		} catch (IOException e) {

			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public WebsocketHandler getSocketHandler() {
		return sockethandler;
	}
}
