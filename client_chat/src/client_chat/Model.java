package client_chat;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.swing.JTextArea;
import javax.websocket.DeploymentException;

import org.glassfish.tyrus.client.ClientManager;

public class Model implements ModelInterface {

	public enum connectionResult {
		OK, TIMEOUT, BAD_URI
	}

	Map<Integer, Client> client = new HashMap<>();
	Map<String, String> clientConnectToServer = new HashMap<>();
	Server server;

	public void sendMessage(String message, int index, String name) {

		clientConnectToServer.put("Pippo", "/192.168.1.101");
		clientConnectToServer.put("Pluto", "/192.168.1.104");
		if (message != "") {
			if (client.containsKey(index)) {
				client.get(index).sendMessage(message);
			} else {
				server.sendMessage(message, clientConnectToServer.get(name));
			}
		}
	}

	public void connectToServer(JTextArea chat, int index) {

		if (!new File("ClientKey.jks").exists()) {
			createKeyStore("Client", "ClientKey", "changeit");
		}

		try {
			client.put(index, new Client("192.168.1.101", chat));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void attachViewObserver(ViewObserver controller) {

		if (!new File("ServerKey.jks").exists()) {
			createKeyStore("Server", "ServerKey", "password");
		}

		// server will be created at start of programm and pending some clients

		try {
			server = new Server(controller);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static CountDownLatch latch;
	public WebsocketHandler sockethandler;

	public connectionResult AttemptConnection() throws IOException {

		latch = new CountDownLatch(1);
		ClientManager client = null;
		try {
			client = ClientManager.createClient();
		} catch (Exception e) {
			e.printStackTrace();
		}

		sockethandler = new WebsocketHandler();
		/* Attemp connection to web service */
		try {

			client.connectToServer(sockethandler, null, new URI(
					"ws://localhost:8080/ServerWebSocket/websocket"));
			/*
			 * client.connectToServer(WebsocketHandler.class, new URI(
			 * "ws://localhost:8080/ServerWebSocket/websocket"));
			 */latch.await();

		} catch (DeploymentException | URISyntaxException
				| InterruptedException e) {

			if (e.getClass().isAssignableFrom(DeploymentException.class)) {
				return connectionResult.TIMEOUT;
			}
			if (e.getClass().isAssignableFrom(URISyntaxException.class)) {
				return connectionResult.BAD_URI;
			}
		}

		return connectionResult.OK;
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
