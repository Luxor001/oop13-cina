package client_chat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageClient {

	List<Client> client = new ArrayList<>();
	ViewObserver controller;
	ModelInterface model;

	public ManageClient(ViewObserver controller, ModelInterface model) {
		this.controller = controller;
		this.model = model;
	}

	public boolean addClient(String ip, String keyStore)
			throws ClassNotFoundException, IOException {

		for (Client c : client) {
			if (c.getIp().equals(ip)) {
				return false;
			}
		}

		client.add(new Client(ip, controller, model, keyStore));
		return true;
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

	public boolean sendMessage(String message, String name) {

		if (client != null) {
			for (Client c : client) {
				if (c.getNameServer().equals(name)) {
					c.sendMessage(message);
					return true;
				}
			}
		}
		return false;
	}

	public boolean sendFile(File file, String name) {
		if (client != null) {
			for (Client c : client) {
				if (c.getNameServer().equals(name)) {
					try {
						c.sendFile(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				}
			}
		}
		return false;
	}

	public void close() {

		if (client != null) {
			for (Client c : client) {
				c.close();
			}
		}
	}

	public void closeServer(String ip) {
		for (int i = 0; i < client.size(); i++) {
			if (client.get(i).getIp().equals(ip)) {
				client.remove(i);
				return;
			}
		}
	}
}
