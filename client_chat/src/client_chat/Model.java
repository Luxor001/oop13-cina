package client_chat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.JTextArea;
import javax.websocket.DeploymentException;

import org.glassfish.tyrus.client.ClientManager;

public class Model implements ModelInterface {

	List<Client> client = new ArrayList<>();
	Server server;
	int cont = 0;

	public Model() {

	}

	public void sendMessage(String message, int index) {

		if (message != "") {
			// check if i'm the server in this chat or the client
			if (server.containIp("/158.148.28.231")) {
				server.sendMessage(message);
			} else {
				client.get(index - 1).sendMessage(message);
			}
		}
	}

	public void connectToServer(JTextArea chat) {

		if (chat != null) {

			try {
				if (cont == 0) {
					client.add(new Client("localhost"));
				} else {
					client.add(new Client("localhost"));
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			client.get(client.size() - 1).addChat(chat);

			cont++;
		}
	}

	public void attachViewObserver(ViewObserver controller) {
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
	public void Start() throws IOException {

		latch = new CountDownLatch(1);
		ClientManager client = null;
		try {
			client = ClientManager.createClient();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* Attemp connection to web service */
		try {
			//view.writeText("Attempt to connect..",0);
			
			this.server.controller.showMessageMain("Attempting connection to channel..");
			client.connectToServer(WebsocketHandler.class, new URI(
					"ws://localhost:8080/ServerWebSocket/websocket"));
			latch.await();

		} catch (DeploymentException | URISyntaxException
				| InterruptedException e) {
			throw new RuntimeException(e);

		}
	}


}
