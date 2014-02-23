package client_chat;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.websocket.EncodeException;

public interface ViewObserver {

	void commandSendMessage(/* String message */);

	void commandCloseTab(ActionEvent e);

	void commandCreateTab();

	void commandCreateTab(String ip, String keystore);

	void commandReceiveMessage(String message, String title);

	void commandCloseAll();

	void showMessageMain(String Message);

	void notifyClosing() throws IOException, EncodeException;

	void notifyChatUser() throws IOException, EncodeException;

	void notifyFileUser(File file);

	void notifySendFileUser() throws IOException, EncodeException;

	public int buildChoiceMessageBox(Controller.MessageBoxReason reason,
			String... optsender);

}
