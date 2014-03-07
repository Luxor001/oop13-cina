package client_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class User {

	private static String NICKNAME = "";
	private static String IPUSER = null;
	private static int PORTKEYSTORE = 0;
	private static int PORTSSL = 0;

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
}
