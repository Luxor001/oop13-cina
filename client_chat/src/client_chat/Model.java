package client_chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;

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

}
