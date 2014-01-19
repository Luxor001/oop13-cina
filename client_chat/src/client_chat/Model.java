package client_chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;

public class Model implements ModelInterface/* , Runnable */{

	// List<Thread> clientThread = new ArrayList<>();
	List<client> client = new ArrayList<>();
	Server server;

	public Model() {

	}

	public void sendMessage(String message) {

		if (message != "") {
			// check if i'm the server in this chat or the client
			if (server.containIp("/82.57.179.140")) {
				server.sendMessage(message);
			} else {
				client.get(0).sendMessage(message);
			}
		}
	}

	public void connectToServer(JTextArea chat) {

		if (chat != null) {

			try {
				client.add(new client());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			client.get(client.size() - 1).addChat(chat);
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
	/*
	 * public void run() {
	 * 
	 * try { client.add(new Client()); } catch (ClassNotFoundException e) {
	 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } }
	 */
	
	
}
