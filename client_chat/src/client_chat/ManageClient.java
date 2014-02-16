package client_chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageClient {

    List<Client> client = new ArrayList<>();
    ViewObserver controller;

    public ManageClient(ViewObserver controller) {
	this.controller = controller;
    }

    public boolean addClient(String ip) throws ClassNotFoundException,
	    IOException {

	for (Client c : client) {
	    if (c.getIp().equals(ip)) {
		return false;
	    }
	}

	client.add(new Client(ip, controller));
	return true;
    }

    public boolean sendMessage(String message, String name) {

	for (Client c : client) {
	    if (c.getNameServer().equals(name)) {
		c.sendMessage(message);
		return true;
	    }
	}
	return false;
    }

    public void close() {
	for (Client c : client) {
	    c.close();
	}
    }
}
