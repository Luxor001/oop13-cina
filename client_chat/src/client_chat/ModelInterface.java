package client_chat;

public interface ModelInterface {

	void attachViewObserver(ViewObserver controller);

	void sendMessage(String message, String name);

	void addNickName(String name, String ip);

	void closeAll();

	String exist(String name);

	boolean isConnect(String ip);

	void closeClient(String name);

	void closeServer(String ip);

	void connectToServer(String ip, String keyStore);

	WebsocketHandler getSocketHandler();
}
