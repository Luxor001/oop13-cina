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

	public synchronized void sendMessage(String message, String name) {

		if (message != "") {
			if (!server.sendMessage(message, name)) {
				if (!client.sendMessage(message, name)) {
					connectToServer(peopleChat.get(name).substring(1),
							System.getProperty("user.dir") + "/" + name
									+ "ServerKey.jks");
					client.sendMessage(message, name);
				}
			}
		}
	}

	public void sendFile(File file, String name) {
		if (!server.sendFile(file, name)) {
			if (!client.sendFile(file, name)) {
				connectToServer(peopleChat.get(name).substring(1),
						System.getProperty("user.dir") + "/" + name
								+ "ServerKey.jks");
				client.sendFile(file, name);
			}
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
		try {
			client.close();
			server.close();
		} catch (Exception e) {

		}
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

			String path = System.getProperty("user.dir") + "/" + name;
			String nameCertificate;
			String confirm = "";
			// creo un file bat
			FileOutputStream output;
			DataOutputStream stdout;

			if (System.getProperty("os.name").contains("Windows")) {
				nameCertificate = path + "Certificate.bat";
				confirm = "si";
			} else {
				nameCertificate = path + "Certificate.sh";
				confirm = "s";
			}

			output = new FileOutputStream(nameCertificate);
			stdout = new DataOutputStream(output);
			// codice per la creazione di un certificato
			stdout.write("@echo off\n".getBytes());
			stdout.write("cd ".getBytes());
			stdout.write(System.getProperty("java.home").getBytes());
			stdout.write("\n".getBytes());

			if (System.getProperty("os.name").contains("Windows")) {
				stdout.write(("(echo francesco cozzolino & echo cozzo & echo cozzo & echo misano adriatico "
						+ "& echo rn & echo it & echo si) | keytool -genkey -alias "
						+ alias
						+ " -keyalg RSA"
						+ " -keypass "
						+ password
						+ " -storepass " + password + " -keystore " + path + "Key.jks\n")
						.getBytes());
			} else {
				stdout.write(("(echo "
						+ confirm
						+ " & echo cozzo & echo cozzo & echo misano adriatico "
						+ "& echo rn & echo it & echo francesco cozzolino) | keytool -genkey -alias "
						+ alias + " -keyalg RSA" + " -keypass " + password
						+ " -storepass " + password + " -keystore " + path + "Key.jks\n")
						.getBytes());
			}

			stdout.write(("keytool -export -alias " + alias + " -storepass "
					+ password + " -file " + path
					+ "Certificate.cer -keystore " + path + "Key.jks\n")
					.getBytes());

			stdout.write("echo on\n".getBytes());

			stdout.close();
			output.close();
			if (System.getProperty("os.name").contains("Windows")) {
				Runtime.getRuntime().exec(nameCertificate).waitFor();
			} else {
				File a=new File(nameCertificate);
				a.setExecutable(true);
				
				Runtime.getRuntime().exec(
						new String[] { "/bin/sh", "-c", nameCertificate }).waitFor();
			}

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
