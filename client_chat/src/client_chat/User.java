package client_chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class offers static methods for set/get user information like as
 * nickname, router's port and other preferences
 * 
 * 
 * @author Francesco Cozzolino
 * @author Stefano Belli
 * 
 */
public class User {

	private static String NICKNAME = "";
	private static boolean VISIBILITY = true;
	private static String IPUSER = null;
	private static int PORTKEYSTORE = 0;
	private static int PORTSSL = 0;
	private static String WEBSERVER_IP;
	private static Preferences prefs = Preferences.userRoot();

	private enum PrefType {
		DEFAULTNICKNAME, DEFAULTPATH, DEFAULTVISIBILITY, DEFAULTSOUNDS
	}

	/**
	 * 
	 * 
	 * @param name
	 * 
	 * @author Francesco Cozzolino
	 */
	public static void setNickName(String name) {
		NICKNAME = name;
	}

	/**
	 * 
	 * @param port
	 * 
	 * @author Francesco Cozzolino
	 */
	public static void setPortKeyStore(int port) {
		PORTKEYSTORE = port;
	}

	/**
	 * 
	 * @param vis
	 * 
	 * @author Stefano Belli
	 */
	public static void setVisibility(boolean vis) {
		VISIBILITY = vis;
	}

	/**
	 * 
	 * @return
	 * 
	 * @author Stefano Belli
	 */
	public static boolean getVisibility() {
		return VISIBILITY;
	}

	/**
	 * 
	 * @param port
	 * 
	 * @author Francesco Cozzolino
	 */
	public static void setPortSSL(int port) {
		PORTSSL = port;
	}

	/**
	 * 
	 * @return your nickname used on the current session
	 * 
	 * @author Francesco Cozzolino
	 */
	public static String getNickName() {
		return NICKNAME;
	}

	/**
	 * Return your ip address
	 * 
	 * @return your Ip address
	 * 
	 * @author Francesco Cozzolino
	 */
	public static String getIp() {
		if (IPUSER == null) {
			String surl = "http://vallentinsource.com/globalip.php";
			URL url;
			try {
				url = new URL(surl);
				InputStreamReader inpstrmread = new InputStreamReader(
						url.openStream());
				BufferedReader reader = new BufferedReader(inpstrmread);
				IPUSER = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return IPUSER;
	}

	/**
	 * 
	 * @return router's port used for create server for exchange keystore
	 * 
	 * @author Francesco Cozzolino
	 */
	public static int getPortKeyStore() {
		return PORTKEYSTORE;
	}

	/**
	 * 
	 * @return router's port used for create server for chat with other users
	 * 
	 * @author Francesco Cozzolino
	 */
	public static int getPortSSL() {
		return PORTSSL;
	}

	/**
	 * 
	 * 
	 * @return
	 * 
	 * @author Stefano Belli
	 */
	public static String getStoredNickname() {
		return prefs.get(PrefType.DEFAULTNICKNAME.toString(),
				System.getProperty("user.name"));
	}

	/**
	 * 
	 * @param nick
	 * 
	 * @author Stefano Belli
	 */
	public static void setStoredNickname(String nick) {
		prefs.put(PrefType.DEFAULTNICKNAME.toString(), nick);
	}

	/**
	 * 
	 * 
	 * @return
	 * 
	 * @author Stefano Belli
	 */
	public static boolean getStoredVisibility() {
		return prefs.getBoolean(PrefType.DEFAULTVISIBILITY.toString(), true);
	}

	/**
	 * 
	 * 
	 * @param choice
	 * 
	 * @author Stefano Belli
	 */
	public static void setStoredVisibility(boolean choice) {
		prefs.putBoolean(PrefType.DEFAULTVISIBILITY.toString(), true);
	}

	/**
	 * 
	 * 
	 * @return
	 * 
	 * @author Stefano Belli
	 */
	public static boolean getStoredSounds() {
		return prefs.getBoolean(PrefType.DEFAULTSOUNDS.toString(), true);
	}

	/**
	 * 
	 * @param choice
	 * 
	 * @author Stefano Belli
	 */
	public static void setStoredSounds(boolean choice) {
		prefs.putBoolean(PrefType.DEFAULTSOUNDS.toString(), true);
	}

	/**
	 * 
	 * 
	 * @return
	 * 
	 * @author Stefano Belli
	 */
	public static String getStoredPath() {
		return prefs.get(PrefType.DEFAULTPATH.toString(),
				System.getProperty("user.dir"));
	}

	/**
	 * 
	 * 
	 * @param path
	 * 
	 * @author Stefano Belli
	 */
	public static void setStoredPath(String path) {
		prefs.put(PrefType.DEFAULTPATH.toString(), path);
	}
	
	public static String getWebServerIP() {
		return WEBSERVER_IP;
	}
	
	
	/**
	 * Check if config/conf.config file exists and, if not, build it with
	 * default indentation.
	 * In addition, it checks also if the ports and ip entered are valid or not,
	 * and shows an error message otherwise.
	 * 
	 * @author Stefano Belli
	 */
	public static void loadConfigFile() throws IOException {

		
		File file = new File("config/config.conf");
		if (!file.exists()) {

			if (!new File("config").exists()) {
				new File("config").mkdir();
			}
			FileWriter outFile = new FileWriter(file);
			PrintWriter out = new PrintWriter(outFile);

			out.println("#SET SOCKET PORTS AFTER THE ':'");
			out.println("#YOU SHOULD OBIVOUSLY OPEN THEM ON YOUR ROUTER SETTINGS");
			out.println("WebServer Ip:localhost");
			out.println("SSLPort:9999");
			out.println("KEYPort:9998");
			out.close();
			JOptionPane.showMessageDialog(new JFrame(),"Socket Ports are not set.\n "
				+ "Please edit file Config/config.conf accordingly.");
			System.exit(0);

		} else {

			try {
				FileReader freader = new FileReader(file);
				BufferedReader reader = new BufferedReader(freader);

				String line;
				while ((line = reader.readLine()) != null) {

					String[] split;
					if (line.contains("WebServer Ip")) {
						split = line.split(":");
						String ip = split[1];
						WEBSERVER_IP = ip;
					}
					if (line.contains("SSLPort")) {
						split = line.split(":");
						int ssl = Integer.parseInt(split[1]);
						PORTSSL=ssl;
					}
					if (line.contains("KEYPort")) {
						split = line.split(":");
						int key = Integer.parseInt(split[1]);
						PORTKEYSTORE=key;
					}
				}
				freader.close();
				reader.close();
			} catch (Exception e) {

				JOptionPane.showMessageDialog(new JFrame(),
						"An error occurred. \nYour config file may be corrupt");
				WebsocketHandler.getWebSocketHandler().closeConnection();
				System.exit(0);
			}
		}
	}

}
