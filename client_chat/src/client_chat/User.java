package client_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class User {

	private static String NICKNAME = "";
	private static String IPUSER = null;
	private static int PORTKEYSTORE = 0;
	private static int PORTSSL = 0;
	private static Preferences prefs=Preferences.userRoot();
	
	private enum PrefType {
		DEFAULTNICKNAME, DEFAULTPATH, DEFAULTVISIBILITY, DEFAULTSOUNDS
	}

	
	public static void setNickName(String name) {
		NICKNAME = name;
	}

	public static void setPortKeyStore(int port) {
		PORTKEYSTORE = port;
	}

	public static void setPortSSL(int port) {
		PORTSSL = port;
	}

	public static String getNickName() {
		return NICKNAME;
	}

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

	public static int getPortKeyStore() {
		return PORTKEYSTORE;
	}

	public static int getPortSSL() {
		return PORTSSL;
	}
	
	
	
	public static String getStoredNickname(){
		return prefs.get(
				PrefType.DEFAULTNICKNAME.toString(),
				System.getProperty("user.name"));
	}
	public static void setStoredNickname(String nick){
		prefs.put(
				PrefType.DEFAULTNICKNAME.toString(), nick);
	}
	
	public static boolean getStoredVisibility(){
		return prefs.getBoolean(
				PrefType.DEFAULTVISIBILITY.toString(), true);
	}
	public static void setStoredVisibility(boolean choice){
		prefs.putBoolean(
				PrefType.DEFAULTVISIBILITY.toString(), true);
	}
	
	public static boolean getStoredSounds(){
		return prefs.getBoolean(
				PrefType.DEFAULTSOUNDS.toString(), true);
	}
	public static void setStoredSounds(boolean choice){
		prefs.putBoolean(
				PrefType.DEFAULTSOUNDS.toString(), true);
	}
	
	public static String getStoredPath(){
		return prefs.get(
				PrefType.DEFAULTPATH.toString(), 
				System.getProperty("user.dir"));
	}
	public static void setStoredPath(String path){
		prefs.put(
				PrefType.DEFAULTPATH.toString(), path);
	}
	
}
