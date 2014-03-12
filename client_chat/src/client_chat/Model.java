package client_chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
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

/**
 * Modifies data's structure when methods of this class are invoked from
 * Controll class
 * 
 * 
 * @author Francesco Cozzolino
 * @author Stefano Belli
 */
public class Model implements ModelInterface {

	public enum connectionResult {
		OK,
		TIMEOUT, 
		BAD_URI
	}

	private Downloaded download;
	private ManageClient client;
	private Server server;
	private KeyStoreServer keyStoreServer;
	private Map<String, String> peopleChat = new HashMap<>();
	private Map<String, String> peopleIp = new HashMap<>();
	private Object lockPeopleChat = new Object();
	private Object lockPeopleIp = new Object();

	/**
	 * Sends message to the user with specified name.If is the first message
	 * sended to an user,or is the first message sended after tiemout
	 * connection, before to send message, tries to connected
	 * 
	 * @param message
	 * @param name
	 *            user's name of receiver
	 * 
	 * @author Francesco Cozzolino
	 */
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

				client.sendMessage(message, name);

			}
		}
	}

	/**
	 * Sends file to the user with specified name.If is the first file sended to
	 * an user,or is the first file sended after tiemout connection, before to
	 * send file, tries to connected
	 * 
	 * @param path
	 *            path of file to send
	 * @param name
	 *            user's name of receiver
	 * 
	 * @author Francesco Cozzolino
	 */
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

				client.sendFile(path, name);

			}
		}
	}

	/**
	 * Show the JFrame of downloads
	 * 
	 * @author Stefano Belli
	 */
	public void showDownloads() {

		if (!download.isVisible()) {
			download.showFrame(true);
		}
	}

	/**
	 * Show the JFrame of preferences
	 * 
	 * @author Stefano Belli
	 */
	public void showPreferences() {
		@SuppressWarnings("unused")
		Prefs p = new Prefs();
	}

	/**
	 * 
	 * Adds name and some information of user to the historical chat of users
	 * 
	 * @param nickName
	 * @param ip
	 * @param port
	 *            router's port
	 * 
	 * @author Francesco Cozzolino
	 */
	public void addNickName(String nickName, String ip, int port) {
		synchronized (lockPeopleChat) {
			peopleChat.put(nickName, ip + ":" + port);
			removeIp(ip, port);
		}
	}

	/**
	 * Removes name of user to the historical chat of users
	 * 
	 * @param name
	 * 
	 * @author Francesco Cozzolino
	 */
	public void removeNickName(String name) {
		synchronized (lockPeopleChat) {
			peopleChat.remove(name);
		}
		deleteFile(new File(System.getProperty("user.dir") + "/" + name
				+ "ServerKey.jks"));
	}

	/**
	 * 
	 * Adds ip address and router's port of user to the historical chat of users
	 * 
	 * @param ip
	 * @param port
	 * @param name
	 * 
	 * @author Francesco Cozzolino
	 */
	public void addIp(String ip, int port, String name) {
		synchronized (lockPeopleIp) {
			peopleIp.put(ip + ":" + port, name);

		}
	}

	/**
	 * Removes ip address and router's port of user to the historical chat of
	 * users
	 * 
	 * 
	 * @param ip
	 * @param port
	 * 
	 * @author Francesco Cozzolino
	 */
	public void removeIp(String ip, int port) {
		synchronized (lockPeopleIp) {
			peopleIp.remove(ip + ":" + port);
		}
	}

	/**
	 * Tries to connect to the user's server. Established the connection it's
	 * possible to sends/receives messages/files. For establishing a connection
	 * is necessary specify user's ip you want to chat,his router's port,his
	 * nickname and name of his keystore file
	 * 
	 * 
	 * @param ip
	 *            ip address of server
	 * @param port
	 *            router's port of server
	 * @param name
	 *            name of user who want to chat
	 * 
	 * 
	 * @author Francesco Cozzolino
	 */
	public synchronized void connectToServer(String ip, int port, String name,
			String keyStore) {

		try {
			client.addClient(ip, port, name, keyStore);

		} catch (IOException e) {
		}

	}

	/**
	 * 
	 * 
	 * @return Downloaded object
	 * @see Downloaded
	 * 
	 * @author Francesco Cozzolino
	 */
	public Downloaded getDownloaded() {
		return download;
	}

	/**
	 * Returns ip address of user. If the name doesn't exist return null
	 * 
	 * @param name
	 *            name of user who want to search
	 * 
	 * @return ip address of user, null if the name doesn't exist
	 * @author Francesco Cozzolino
	 */
	public String exist(String name) {
		synchronized (lockPeopleChat) {
			return peopleChat.get(name);
		}
	}

	/**
	 * Check if an user with a specific ip and router's port exist
	 * 
	 * @param ip
	 * @param port
	 * 
	 * @return name of user or string value "exist", null if the ip address
	 *         doesn't exist
	 * 
	 * @author Francesco Cozzolino
	 */
	public String existIp(String ip, int port) {
		synchronized (lockPeopleChat) {
			if (peopleChat.containsValue(ip + ":" + port)) {
				return "exist";
			}

			return peopleIp.get(ip + ":" + port);
		}
	}

	/**
	 * Checks if an ip address is connected with you
	 * 
	 * @param ip
	 * @param port
	 * 
	 * @author Francesco Cozzolino
	 */
	public boolean isConnect(String ip, int port) {
		return server.isConnect(ip + ":" + port)
				|| client.isConnect(ip + ":" + port);
	}

	/**
	 * Close all connection
	 * 
	 * @author Francesco Cozzolino
	 */
	public void closeAll() {
		try {
			keyStoreServer.close();
			client.close();
			server.close();
		} catch (Exception e) {

		}

	}

	/**
	 * Removes a client from the list of connected clients
	 * 
	 * @param ip
	 * 
	 * @author Francesco Cozzolino
	 */
	public void closeClient(String ip) {
		server.closeClient(ip);
	}

	/**
	 * Close a connection with the server
	 * 
	 * @param name
	 *            user's name of the server
	 * 
	 * @author Francesco Cozzolino
	 */
	public void closeServer(String name) {
		client.closeServer(name);
	}

	/**
	 * Deletes a file
	 * 
	 * @param file
	 *            file to delete
	 * 
	 * @author Francesco Cozzolino
	 */
	private void deleteFile(File file) {

		if (file.exists()) {
			file.delete();
		}

	}

	/**
	 * Receive a ViewObserver object and create the server for chat with other
	 * users and exchange keystores. Deletes keystores previously created or
	 * received
	 * 
	 * @param controller
	 * @see Controller
	 * 
	 * @author Francesco Cozzolino
	 */
	public void attachViewObserver(ViewObserver controller) {

		String path = System.getProperty("user.dir");
		String[] list = new File(path).list();
		String serverPsw;
		String clientPsw;

		try {
			download = new Downloaded();
		} catch (BadLocationException e1) {
		} catch (Exception e) {
		}

		for (int i = 0; i < list.length; i++) {
			if (list[i].endsWith(".jks") || list[i].endsWith(".bat")
					|| list[i].endsWith(".sh") || list[i].endsWith(".cer")) {

				deleteFile(new File(path + "/" + list[i]));

			}
		}

		serverPsw = createKeyStore(User.getNickName(), "ServerKey");

		clientPsw = createKeyStore(User.getNickName(), "ClientKey");

		try {
			keyStoreServer = new KeyStoreServer(controller);
			server = new Server(controller, this, serverPsw);
			client = new ManageClient(controller, this, clientPsw);

		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
		}

	}

	/**
	 * Creates keystore necessary to chat with other users
	 * 
	 * @param name
	 *            your nickname
	 * @param alias
	 *            name to give to your keystore
	 * 
	 * @return password of Keystore
	 * 
	 * 
	 * @author Francesco Cozzolino
	 */
	private String createKeyStore(String name, String alias) {

		String character = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXx"
				+ "YyZz1234567890@";

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
			output = new FileOutputStream(certificate);

			stdout = new DataOutputStream(output);

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

	/**
	 * Check if config/conf.config file exists and, if not, build it with
	 * default indentation.
	 * In addition, it checks also if the ports entered are valid or not,
	 * and shows an error message otherwise.
	 * 
	 * @author Stefano Belli
	 */
	public void setSocketPorts() throws IOException {

		File file = new File("config/config.conf");
		if (!file.exists()) {

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

		} else {

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
