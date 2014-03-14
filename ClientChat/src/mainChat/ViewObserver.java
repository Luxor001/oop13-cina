package mainChat;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.websocket.EncodeException;

/**
 * The observer interface for control the view and data structure for a chat.
 * The class that is interested to manage the GUI and data structure of a chat
 * implements this interface
 * 
 * @author Francesco Cozzolino
 * @author Stefano Belli
 */
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
	
	public void setView(ViewInterface view);
	
	public void setModel(ModelInterface view);

}
