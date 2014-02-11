package client_chat;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.websocket.EncodeException;

public interface ViewObserver {

	void commandSendMessage(/* String message */);

	void commandCloseTab(ActionEvent e);

	void commandCreateTab(/* JTextArea chat */);

	void commandReceiveMessage(String message, String title);

	// void commandShowMessage();
	public void showMessageMain(String Message);

	void notifyClosing() throws IOException, EncodeException;
	
	void notifyChatUser() throws IOException, EncodeException;
}
