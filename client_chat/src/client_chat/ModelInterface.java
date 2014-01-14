package client_chat;

import javax.swing.JTextArea;

public interface ModelInterface {

	void showMessage();

	void sendMessage(String message);

	void connectToServer(JTextArea chat);
}
