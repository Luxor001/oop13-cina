package client_chat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

public class Model implements ModelInterface {

	public enum connectionResult {
		OK, TIMEOUT, BAD_URI
	}

	private Downloaded download;
	private ManageClient client;
	private Server server;
	private KeyStoreServer keyStoreServer;
	private Map<String, String> peopleChat = new HashMap<>();
	private Map<String, String> peopleIp = new HashMap<>();
	private WebsocketHandler sockethandler;
	private Object lockPeopleChat = new Object();
	private Object lockPeopleIp = new Object();

	public void sendMessage(String message, String name) {

		if (!server.sendMessage(message, name)) {
			if (!client.sendMessage(message, name)) {
				String ip;
				synchronized (lockPeopleChat) {
					ip = peopleChat.get(name);
				}
				String[] ipPort = ip.split(":");
				connectToServer(ipPort[0], Integer.parseInt(ipPort[1]), name,
						System.getProperty("user.dir") + "/" + name
								+ "ServerKey.jks");
				/*
				 * try { Thread.sleep(1000); } catch (InterruptedException e) {
				 * e.printStackTrace(); }
				 */
				client.sendMessage(message, name);

			}
		}
	}

	public void sendFile(String path, String name) {

		if (!server.sendFile(path, name)) {
			if (!client.sendFile(path, name)) {
				String ip;
				synchronized (lockPeopleChat) {
					ip = peopleChat.get(name);
				}
				String[] ipPort = ip.split(":");
				connectToServer(ipPort[0], Integer.parseInt(ipPort[1]), name,
						System.getProperty("user.dir") + "/" + name
								+ "ServerKey.jks");
				/*
				 * try { Thread.sleep(1000); } catch (InterruptedException e) {
				 * e.printStackTrace(); }
				 */
				client.sendFile(path, name);

			}
		}
	}

	public void showDownloads() {

		if (!download.isVisible()) {
			download.showFrame(true);
		}
	}

	public void showPreferences() {
		@SuppressWarnings("unused")
		Prefs p = new Prefs();
	}

	public void addNickName(String nickName, String ip, int port) {
		synchronized (lockPeopleChat) {
			peopleChat.put(nickName, ip + ":" + port);
			removeIp(ip, port);
		}
	}

	public void removeNickName(String name) {
		synchronized (lockPeopleChat) {
			peopleChat.remove(name);
		}
		deleteFile(new File(System.getProperty("user.dir") + "/" + name
				+ "ServerKey.jks"));
	}

	public void addIp(String ip, int port, String name) {
		synchronized (lockPeopleIp) {
			peopleIp.put(ip + ":" + port, name);

		}
	}

	public void removeIp(String ip, int port) {
		synchronized (lockPeopleIp) {
			peopleIp.remove(ip + ":" + port);
		}
	}

	public synchronized void connectToServer(String ip, int port, String name,
			String keyStore) {

		try {
			client.addClient(ip, port, name, keyStore);

		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Downloaded getDownloaded() {
		return download;
	}

	public String exist(String name) {
		synchronized (lockPeopleChat) {
			return peopleChat.get(name);
		}
	}

	public String existIp(String ip, int port) {
		synchronized (lockPeopleChat) {
			if (peopleChat.containsValue(ip + ":" + port)) {
				return "exist";
			}

			return peopleIp.get(ip + ":" + port);
		}
	}

	public boolean isConnect(String ip, int port) {
		return server.isConnect(ip + ":" + port)
				|| client.isConnect(ip + ":" + port);
	}

	public void closeAll() {
		try {
			keyStoreServer.close();
			client.close();
			server.close();
		} catch (Exception e) {

		}

	}

	public void closeClient(String ip) {
		server.closeClient(ip);
	}

	public void closeServer(String name) {
		client.closeServer(name);
	}

	private void deleteFile(File file) {

		if (file.exists()) {
			file.delete();
		}

	}

	public void attachViewObserver(ViewObserver controller) {

		try {
			download = new Downloaded();
		} catch (BadLocationException e1) {
		} catch (Exception e) {
			e.printStackTrace();
		}

		String path = System.getProperty("user.dir");

		String[] list = new File(path).list();
		for (int i = 0; i < list.length; i++) {
			if (list[i].endsWith(".jks") || list[i].endsWith(".bat")
					|| list[i].endsWith(".sh") || list[i].endsWith(".cer")) {

				deleteFile(new File(path + "/" + list[i]));
			}
		}

		String serverPsw = createKeyStore(User.getNickName(), "ServerKey");

		String clientPsw = createKeyStore(User.getNickName(), "ClientKey");

		// server will be created at start of programm and pending some clients
		try {
			keyStoreServer = new KeyStoreServer(controller);
			server = new Server(controller, this, serverPsw);
			client = new ManageClient(controller, this, clientPsw);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String createKeyStore(String name, String alias) {

		String character = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890@";

		String language = Locale.getDefault().getLanguage();
		String password = "";
		String concatenate = "";
		File certificate;
		String path = System.getProperty("user.dir") + "/" + name + alias;
		String nameCertificate;
		String confirm = "";
		// creo un file bat
		FileOutputStream output;
		DataOutputStream stdout;

		for (int i = 0; i < 8; i++) {
			int index = (int) (Math.random() * character.length());
			password += character.charAt(index);
		}

		if (System.getProperty("os.name").contains("Windows")) {
			nameCertificate = path + "Certificate.bat";
			concatenate = " & ";
			if (language.equals("it")) {
				confirm = "si";
			} else {
				confirm = "yes";
			}
		} else {
			nameCertificate = path + "Certificate.sh";
			concatenate = " && ";
			if (language.equals("it")) {
				confirm = "s";
			} else {
				confirm = "y";
			}
		}

		try {
			certificate = new File(nameCertificate);
			// certificate.setExecutable(true);
			output = new FileOutputStream(certificate);

			stdout = new DataOutputStream(output);
			// codice per la creazione di un certificato

			stdout.write("cd ".getBytes());
			stdout.write(System.getProperty("java.home").getBytes());
			stdout.write("\n".getBytes());

			stdout.write(("(echo " + name + concatenate + "echo " + name
					+ concatenate + "echo " + name + concatenate + "echo "
					+ concatenate + "echo " + concatenate + "echo "
					+ concatenate + "echo " + confirm
					+ ") | keytool -genkey -alias " + alias + " -keyalg RSA"
					+ " -keypass " + password + " -storepass " + password
					+ " -keystore " + path + ".jks\n").getBytes());

			stdout.write(("keytool -export -alias " + alias + " -storepass "
					+ password + " -file " + path
					+ "Certificate.cer -keystore " + path + "Key.jks\n")
					.getBytes());
			stdout.close();
			output.close();
			if (System.getProperty("os.name").contains("Windows")) {
				Runtime.getRuntime().exec(nameCertificate).waitFor();
			} else {

				certificate.setExecutable(true);
				Runtime.getRuntime()
						.exec(new String[] { "/bin/sh", "-c", nameCertificate })
						.waitFor();
			}

		} catch (IOException e) {

			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return password;
	}

	public WebsocketHandler getSocketHandler() {
		return sockethandler;
	}

	public void setSocketPorts() throws IOException {

		File file = new File("config/config.conf");
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		BufferedReader dis = null;
		if (!file.exists()) {

			/* if config directory doesn't exist, create it */
			if (!new File("config").exists()) {
				new File("config").mkdir();
			}
			FileWriter outFile = new FileWriter(file);
			PrintWriter out = new PrintWriter(outFile);

			out.println("#SET SOCKET PORTS AFTER THE ':'");
			out.println("#YOU SHOULD OBIVOUSLY OPEN THEM ON YOUR ROUTER SETTINGS");
			out.println("SSLPort:9999");
			out.println("KEYPort:9998");
			out.close();
			JOptionPane
					.showMessageDialog(
							new JFrame(),
							"Socket Ports are not set.\n "
									+ "Please edit file Config/config.conf accordingly");
			WebsocketHandler.getWebSocketHandler().closeConnection();

		} else {/* file exist, open it up */

			try {
				FileReader freader = new FileReader(file);
				BufferedReader reader = new BufferedReader(freader);

				String line;
				while ((line = reader.readLine()) != null) {

					String[] split;
					if (line.contains("SSLPort")) {
						split = line.split(":");
						int ssl = Integer.parseInt(split[1]);
						User.setPortSSL(ssl);
					}
					if (line.contains("KEYPort")) {
						split = line.split(":");
						int key = Integer.parseInt(split[1]);
						User.setPortKeyStore(key);
					}
				}
				freader.close();
				reader.close();
			} catch (Exception e) {

				JOptionPane.showMessageDialog(new JFrame(),
						"An error occurred. \n"
								+ "Your config file may be corrupt");
				WebsocketHandler.getWebSocketHandler().closeConnection();
				System.exit(0);

			}
		}
	}
}
