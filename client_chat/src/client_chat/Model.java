package client_chat;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTextArea;

public class Model implements ModelInterface {

    public enum connectionResult {
	OK, TIMEOUT, BAD_URI
    }

    ManageClient client;
    Server server;

    public void sendMessage(String message, int index, String name) {

	if (message != "") {

	    //CHANGE
	    if (server != null) {
		if (!server.sendMessage(message, name)) {
		    client.sendMessage(message, name);
		}
	    } else
		client.sendMessage(message, name);
	}
    }

    public synchronized void connectToServer(JTextArea chat, int index,
	    String ip) {

	if (!new File("ClientKey.jks").exists()) {
	    createKeyStore("Client", "ClientKey", "changeit");
	}

	try {
	    client.addClient(ip);
	} catch (ClassNotFoundException e) {

	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    public void closeAll() {

	new Thread() {
	    public void run() {
		client.close();
	    }
	}.start();

	server.close();
    }

    public void closeClient(String name) {
	server.closeClient(name);
    }

    public void attachViewObserver(ViewObserver controller) {

	if (!new File("ServerKey.jks").exists()) {
	    createKeyStore("Server", "ServerKey", "password");
	}

	// server will be created at start of programm and pending some clients

	try {
	    server = new Server(controller);
	    client = new ManageClient(controller);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    public WebsocketHandler sockethandler;

    private void createKeyStore(String name, String alias, String password) {
	try {

	    String path = System.getProperty("user.dir") + "\\" + name;
	    String nameCertificate;
	    // creo un file bat
	    FileOutputStream output;
	    DataOutputStream stdout;

	    if (System.getProperty("os.name").contains("Windows")) {
		nameCertificate = name + "Certificate.bat";
	    } else {
		nameCertificate = name + "Certificate.sh";

	    }

	    output = new FileOutputStream(nameCertificate);
	    stdout = new DataOutputStream(output);
	    // codice per la creazione di un certificato
	    stdout.write("@echo off\n".getBytes());
	    stdout.write("cd ".getBytes());
	    stdout.write(System.getProperty("java.home").getBytes());
	    stdout.write("\n".getBytes());
	    stdout.write(("(echo francesco cozzolino & echo cozzo & echo cozzo & echo misano adriatico "
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

	    Runtime.getRuntime().exec(nameCertificate).waitFor();

	} catch (IOException e) {

	    e.printStackTrace();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    public WebsocketHandler getSocketHandler() {
	return sockethandler;
    }
}
