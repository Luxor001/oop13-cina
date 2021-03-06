package mainChat;

import additionalFrames.Downloaded;
/**
 * The model interface for define a data structure for a chat. The class that is
 * interested to developing a data structure for a chat implements this
 * interface
 * 
 * @author Francesco Cozzolino
 * @author Stefano Belli
 */
public interface ModelInterface {

	void attachViewObserver(ViewObserver controller);

	void sendMessage(String message, String name);

	void showDownloads();

	void showPreferences();

	void sendFile(String path, String name);

	void addNickName(String name, String ip, int port);

	void removeNickName(String name);

	void addIp(String ip, int port, String name);

	void removeIp(String ip, int port);

	void closeAll();

	String exist(String name);

	String existIp(String ip, int port);

	boolean isConnect(String ip, int port);

	void closeClient(String name);

	void closeServer(String name);

	void connectToServer(String ip, int port, String name, String keyStore);

	Downloaded getDownloaded();
}
