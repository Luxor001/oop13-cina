package client_chat;

import java.io.File;

public interface ModelInterface {

	void attachViewObserver(ViewObserver controller);

	void sendMessage(String message, String name);

	void showDownloads();

	void sendFile(File file, String name);

	void addNickName(String name, String ip);

	void removeNickName(String name);

	void addIp(String ip, String name);

	void closeAll();

	String exist(String name);

	String existIp(String ip);

	boolean isConnect(String ip);

	void closeClient(String name);

	void closeServer(String ip);

	void connectToServer(String ip, String name, String keyStore);

	WebsocketHandler getSocketHandler();
}
