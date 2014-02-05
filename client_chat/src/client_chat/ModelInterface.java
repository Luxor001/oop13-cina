package client_chat;

import javax.swing.JTextArea;

public interface ModelInterface {

	void attachViewObserver(ViewObserver controller);

	void sendMessage(String message, int index);

	void connectToServer(int index, JTextArea chat);
}
