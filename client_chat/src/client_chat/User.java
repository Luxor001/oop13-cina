package client_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.prefs.Preferences;

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

}
