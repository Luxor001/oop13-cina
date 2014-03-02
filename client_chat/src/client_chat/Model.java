package client_chat;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

		if (message != "") {
			if (!server.sendMessage(message, name)) {
				if (!client.sendMessage(message, name)) {
					String ip;
					synchronized (lockPeopleChat) {
						ip = peopleChat.get(name);
					}
					if (ip != null) {
						connectToServer(ip, name,
								System.getProperty("user.dir") + "/" + name
										+ "ServerKey.jks");
						/*
						 * try { Thread.sleep(1000); } catch
						 * (InterruptedException e) { e.printStackTrace(); }
						 */
						client.sendMessage(message, name);
					}
				}
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
				if (ip != null) {
					connectToServer(ip, name, System.getProperty("user.dir")
							+ "/" + name + "ServerKey.jks");
					/*
					 * try { Thread.sleep(1000); } catch (InterruptedException
					 * e) { e.printStackTrace(); }
					 */
					client.sendFile(path, name);
				}
			}
		}
	}

	public void showDownloads() {

		if (!download.isVisible()) {
			download.showFrame(true);
		}
	}

	public void addNickName(String nickName, String ip) {
		synchronized (lockPeopleChat) {
			peopleChat.put(nickName, ip);
			removeIp(ip);
		}
	}

	public void removeNickName(String name) {
		synchronized (lockPeopleChat) {
			peopleChat.remove(name);
		}
		deleteFile(new File(System.getProperty("user.dir") + "/" + name
				+ "ServerKey.jks"));
	}

	public void addIp(String ip, String name) {
		synchronized (lockPeopleIp) {
			peopleIp.put(ip, name);

		}
	}

	public void removeIp(String ip) {
		synchronized (lockPeopleIp) {
			peopleIp.remove(ip);
		}
	}

	public synchronized void connectToServer(String ip, String name,
			String keyStore) {

		try {
			client.addClient(ip, name, keyStore);

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

	public String existIp(String ip) {
		synchronized (lockPeopleChat) {
			if (peopleChat.containsValue(ip)) {
				return "exist";
			}

			return peopleIp.get(ip);
		}
	}

	public boolean isConnect(String ip) {
		return server.isConnect(ip) || client.isConnect(ip);
	}

	public void closeAll() {
		try {
			keyStoreServer.close();
			client.close();
			server.close();
		} catch (Exception e) {

		}

	}

	public void closeClient(String name) {
		server.closeClient(name);
	}

	public void closeServer(String ip) {
		client.closeServer(ip);
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

		createKeyStore(WebsocketHandler.DEBUG_NICKNAME, "ServerKey", "password");

		createKeyStore(WebsocketHandler.DEBUG_NICKNAME, "ClientKey", "changeit");

		// server will be created at start of programm and pending some clients
		try {
			keyStoreServer = new KeyStoreServer(controller);
			server = new Server(controller, this);
			client = new ManageClient(controller, this);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void createKeyStore(String name, String alias, String password) {
		try {

			String language = Locale.getDefault().getLanguage();

			File certificate;
			String path = System.getProperty("user.dir") + "/" + name + alias;
			String nameCertificate;
			String confirm = "";
			// creo un file bat
			FileOutputStream output;
			DataOutputStream stdout;

			if (System.getProperty("os.name").contains("Windows")) {
				nameCertificate = path + "Certificate.bat";
				if (language.equals("it")) {
					confirm = "si";
				} else {
					confirm = "yes";
				}
			} else {
				nameCertificate = path + "Certificate.sh";
				if (language.equals("it")) {
					confirm = "s";
				} else {
					confirm = "y";
				}
			}

			certificate = new File(nameCertificate);
			// certificate.setExecutable(true);
			output = new FileOutputStream(certificate);

			stdout = new DataOutputStream(output);
			// codice per la creazione di un certificato

			stdout.write("cd ".getBytes());
			stdout.write(System.getProperty("java.home").getBytes());
			stdout.write("\n".getBytes());

			if (System.getProperty("os.name").contains("Windows")) {
				stdout.write(("(echo " + name + " & echo " + name + " & echo "
						+ name + " & echo " + "& echo  & echo  & echo "
						+ confirm + ") | keytool -genkey -alias " + alias
						+ " -keyalg RSA" + " -keypass " + password
						+ " -storepass " + password + " -keystore " + path + ".jks\n")
						.getBytes());
			} else {

				stdout.write(("(echo " + name + " && echo " + name
						+ " && echo " + name + " && echo "
						+ "&& echo  && echo  && echo " + confirm
						+ ") | keytool -genkey -alias " + alias
						+ " -keyalg RSA" + " -keypass " + password
						+ " -storepass " + password + " -keystore " + path + ".jks\n")
						.getBytes());

			}

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
	}

	public WebsocketHandler getSocketHandler() {
		return sockethandler;
	}
}
