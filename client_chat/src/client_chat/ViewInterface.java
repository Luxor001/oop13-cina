package client_chat;

import java.awt.event.ActionEvent;
import java.io.IOException;

import client_chat.View.sfx;

/**
 * The view interface for define a GUI for a chat. The class that is interested
 * to developing a GUI for a chat implements this interface
 * 
 * 
 * 
 * @author Francesco Cozzolino
 * @author Stefano Belli
 * 
 */
public interface ViewInterface {

	void attachViewObserver(ViewObserver controller);

	String sendMessage();

	void showMessage(String message, String title);
	
	void showMessageMain(String Message);
	
	void createTab(String title);
	
	void closeTab(ActionEvent e);

	int getTabIndex();

	String getTabName();

	int buildChoiceMessageBox(String Message, String title, Object[] options,
			int IconType);

	
	void appendUser(String user);

	boolean removeUser(String user);

	public void playSound(sfx soundeffect);

	public void closeChat() throws IOException;
}
