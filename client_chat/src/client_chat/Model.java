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
	// List<Client> client = new ArrayList<>();
	Server server;
	int cont = 0;

	public Model() {

	}

	public void sendMessage(String message, int index) {

		if (message != "") {
			// check if i'm the server in this chat or the client
			// if (server.containIp("/158.148.28.231")) {
			// server.sendMessage(message);
			// } else {
			// client.get(index).sendMessage(message);
			// }
			if (client.containsKey(index)) {
				client.get(index).sendMessage(message);
			} else {
				server.sendMessage(message);
			}
		}
	}

	public void connectToServer(int index, JTextArea chat) {

		if (chat != null) {

			try {
				if (cont == 0) {
					client.put(index, new Client("localhost", chat));
				} else {
					client.put(index, new Client("localhost", chat));
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			cont++;
		}
	}

	public void attachViewObserver(ViewObserver controller) {
		// server will be created at start of programm and pending some clients

		if (!new File("ServerKey.jks").exists()) {
			createKeyStore("Server", "ServerKey", "password");

		}

		if (!new File("ClientKey.jks").exists()) {
			createKeyStore("Client", "ClientKey", "changeit");
		}

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
		
		sockethandler=new WebsocketHandler();
		/* Attemp connection to web service */
		try {
			
			
			this.server.controller.showMessageMain("Attempting connection to channel..");
			client.connectToServer(sockethandler,null, new URI(
					"ws://localhost:8080/ServerWebSocket/websocket"));
			
			/*
			client.connectToServer(WebsocketHandler.class, new URI(
					"ws://localhost:8080/ServerWebSocket/websocket"));
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
			// creo un file bat
			FileOutputStream output = new FileOutputStream(name
					+ "Certificate.bat");
			DataOutputStream stdout = new DataOutputStream(output);

			// codice per la creazione di un certificato
			stdout.write("@echo off\n".getBytes());
			stdout.write("cd ".getBytes());
			stdout.write(System.getProperty("java.home").getBytes());
			stdout.write("\n".getBytes());
			stdout.write(("(echo francesco cozzolino & echo cozzo & echo cozzo & echo misano "
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

			Runtime.getRuntime().exec(name + "Certificate.bat").waitFor();

		} catch (IOException e) {

			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*this method is needed to be referenced by the controller.*/
	@Override
	public WebsocketHandler getSocketHandler() {		
		return sockethandler;
	}
}
