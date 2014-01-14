package client_chat;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;

public class Controller implements ViewObserver, Runnable {

	private ViewInterface view;
	private ModelInterface model;

	List<Thread> clientThread = new ArrayList<>();
	List<Client> client = new ArrayList<>();
	Server server;

	public Controller() {
		try {
			server = new Server();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setView(ViewInterface view) {
		this.view = view;
		this.view.attachViewObserver(this);
		// server.run();
		// t.run();
	}

	public void setModel(ModelInterface model) {
		this.model = model;
	}

	public void commandSendMessage(String message) {
		// view.sendMessage();

		if (server.containIp("/127.0.0.1")) {
			server.sendMessage(message);
		} else {
			client.get(0).sendMessage(message);
		}
	}

	public void commandCloseTab(ActionEvent e) {
		view.closeTab(e);
	}

	public void commandCreateTab(JTextArea chat) {
		// view.createTab();
		if (server.containIp("/127.0.0.1")) {
			server.addChat(chat);
		} else {
			clientThread.add(new Thread(this));
			clientThread.get(clientThread.size() - 1).run();

			client.get(client.size() - 1).addChat(chat);
		}
	}

	public void run() {

		try {
			// server = new Server();
			client.add(new Client());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
