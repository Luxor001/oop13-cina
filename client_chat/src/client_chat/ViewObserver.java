package client_chat;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.websocket.EncodeException;

public interface ViewObserver {

	void commandSendMessage(String message, String name);

	void commandShowDownloads();

	void commandShowPreferences();

	void commandCloseTab(ActionEvent e);

	void commandCreateTab(String ip, int port, String name, String keystore);

	void commandReceiveMessage(String message, String title);

	void commandRemoveUser(String name);

	void commandRefusedChat(String name);

	void commandCloseAll();

	void showMessageMain(String Message);

	void notifyClosing() throws IOException, EncodeException;

	void notifyChatUser(String name) throws IOException, EncodeException;

	void notifyChatUserIp(String ip, int keyPort, int sslPort);

	void notifySendFileUser(String path, String name) throws IOException,
			EncodeException;

	public int buildChoiceMessageBox(Controller.MessageBoxReason reason,
			String... optsender);

	public void closeChat() throws IOException, EncodeException;

}
