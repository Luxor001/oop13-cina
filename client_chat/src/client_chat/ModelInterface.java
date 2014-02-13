package client_chat;

import javax.swing.JTextArea;

public interface ModelInterface {

	void attachViewObserver(ViewObserver controller);

	void sendMessage(String message, int index, String name);

	void closeAll();

	void connectToServer(JTextArea chat, int index, String ip);

	WebsocketHandler getSocketHandler();
}
