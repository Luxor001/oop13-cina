package client_chat;

import java.awt.event.ActionEvent;

public interface ViewObserver {

	void commandSendMessage(/* String message */);

	void commandCloseTab(ActionEvent e);

	void commandCreateTab(/* JTextArea chat */);

	void commandReceiveMessage(String message, String title);
	// void commandShowMessage();
	public void showMessageMain(String Message);
}
