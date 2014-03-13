package client_chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Permits to manage one or more client connected to different server
 * 
 * @author Francesco Cozzolino
 */
public class ManageClient {

	private List<Client> client = new ArrayList<>();
	private ViewObserver controller;
	private ModelInterface model;
	private String password;

	/**
	 * 
	 * @param controller
	 * @param model
	 * @param password
	 *            password of keystore
	 * 
	 * @see ViewObserver
	 * @see ModelInterface
	 */
	public ManageClient(ViewObserver controller, ModelInterface model, String password) {
		this.controller = controller;
		this.model = model;
		this.password = password;
	}

	/**
	 * 
	 * Creates and adds a client to the list of clients
	 * 
	 * @param ip
	 *            ip address of server who want to connect
	 * @param port
	 *            router's port of server
	 * @param name
	 *            user's name who want to chat
	 * @param keyStore
	 *            name of server's keystore
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void addClient(String ip, int port, String name, String keyStore)
			throws IOException {

		for (int i = 0; i < client.size(); i++) {
			if (client.get(i).getIp().equals(ip)) {
				return;
			}
		}
		client.add(new Client(ip, port, name, password, controller, model,
				keyStore));

	}

	/**
	 * Returns the connection state
	 * 
	 * @return true if the server was successfully connected to the server
	 */
	public synchronized boolean isConnect(String ip) {

		for (int i = 0; i < client.size(); i++) {
			if (client.get(i).getIp().equals(ip)) {
				return (!client.get(i).isClosed())
						&& client.get(i).isConnected();

			}
		}

		return false;
	}

	/**
	 * Tries to send a message to the server with the specified name
	 * 
	 * @param message
	 * @param name
	 *            user's name of server side
	 * @return true if the server exist and is connected,false otherwise
	 */
	public synchronized boolean sendMessage(String message, String name) {

		if (client != null) {
			for (Client c : client) {
				if (c.getNameServer().equals(name)) {
					try {
						c.sendMessage(message);
					} catch (IOException e) {
					}
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Tries to send a file to the server with the specified name
	 * 
	 * @param path
	 *            path of file to send
	 * @param name
	 *            user's name of server side
	 * @return true if the client exist and is connected,false otherwise
	 */
	public synchronized boolean sendFile(final String path, String name) {
		if (client != null) {
			for (Client c : client) {
				if (c.getNameServer().equals(name)) {

					final Client clientTmp = c;

					new Thread() {
						public void run() {
							clientTmp.sendFile(path);

						}
					}.start();

					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Closes all connections with the servers
	 */
	public synchronized void close() {
		if (client != null) {
			for (Client c : client) {
				c.close();
			}
		}
	}

	/**
	 * Closes a specific server
	 * 
	 * @param name
	 *            user's name of server side
	 */
	public synchronized void closeServer(String name) {
		for (int i = 0; i < client.size(); i++) {

			if (client.get(i).getNameServer().equals(name)) {
				client.remove(i);
				return;
			}
		}

	}
}
