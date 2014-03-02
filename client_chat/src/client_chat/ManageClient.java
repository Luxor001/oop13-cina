package client_chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageClient {

	private List<Client> client = new ArrayList<>();
	private ViewObserver controller;
	private Model model;
	private Object lock = new Object();

	public ManageClient(ViewObserver controller, Model model) {
		this.controller = controller;
		this.model = model;
	}

	public void addClient(String ip, String name, String keyStore)
			throws ClassNotFoundException, IOException {
		synchronized (lock) {
			for (Client c : client) {
				if (c.getIp().equals(ip)) {
					return;
				}
			}

			client.add(new Client(ip, name, controller, model, keyStore));
		}

	}

	public synchronized boolean isConnect(String ip) {

		for (int i = 0; i < client.size(); i++) {
			if (client.get(i).getIp().substring(1).equals(ip)) {
				return (!client.get(i).isClosed())
						&& client.get(i).isConnected();

			}
		}

		return false;
	}

	public synchronized boolean sendMessage(String message, String name) {

		if (client != null) {
			for (Client c : client) {
				if (c.getNameServer().equals(name) && !c.isClosed()
						&& c.isConnected()) {
					c.sendMessage(message);
					return true;
				}
			}
		}
		return false;
	}

	public synchronized boolean sendFile(String path, String name) {
		if (client != null) {
			for (Client c : client) {
				if (c.getNameServer().equals(name) && !c.isClosed()
						&& c.isConnected()) {

					class SendFile extends Thread {
						String path;
						Client client;

						public SendFile(String path, Client client) {
							this.path = path;
							this.client = client;
						}

						public void run() {
							try {
								client.sendFile(path);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}

					new SendFile(path, c).start();
					return true;
				}
			}
		}
		return false;
	}

	public synchronized void close() {
		synchronized (lock) {
			if (client != null) {
				for (Client c : client) {
					c.close();
				}
			}
		}
	}

	public synchronized void closeServer(String ip) {
		synchronized (lock) {
			for (int i = 0; i < client.size(); i++) {
				if (client.get(i).getIp().equals(ip)) {
					client.remove(i);
					return;
				}
			}
		}
	}
}
