package client_chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;

public class Model implements ModelInterface, Runnable {

	List<Thread> clientThread = new ArrayList<>();
	List<Client> client = new ArrayList<>();
	Server server;

	public Model() {

		// server will be created at start of programm and pending some clients
		try {
			server = new Server();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showMessage() {

	}

	public void sendMessage(String message) {

		if (message != "") {
			// check if i'm the server in this chat or the client
			if (server.containIp("/192.168.1.101")) {
				server.sendMessage(message);
			} else {
				client.get(0).sendMessage(message);
			}
		}
	}

	public void connectToServer(JTextArea chat) {

		if (chat != null) {
			if (server.containIp("/192.168.1.101")) {
				// it's a proof, in the future will be removed
				server.addChat(chat);
			} else {
				// i try to connect to the server
				clientThread.add(new Thread(this));
				clientThread.get(clientThread.size() - 1).run();

				client.get(client.size() - 1).addChat(chat);
			}
		}
	}

	public void run() {

		try {
			client.add(new Client());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
