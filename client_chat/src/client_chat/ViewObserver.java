package client_chat;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.websocket.EncodeException;

public interface ViewObserver {

	void commandSendMessage(/* String message */);

	void commandCloseTab(ActionEvent e);

	void commandCreateTab();

	void commandCreateTab(String ip);

	void commandReceiveMessage(String message, String title);

	void commandCloseAll();

	void showMessageMain(String Message);

	void notifyClosing() throws IOException, EncodeException;

	void notifyChatUser() throws IOException, EncodeException;

	public int buildChoiceMessageBox(String Message, String title,
			Object[] options, int IconType);
}
