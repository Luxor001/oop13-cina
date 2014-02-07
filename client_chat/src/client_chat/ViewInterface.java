package client_chat;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

public interface ViewInterface {

	void attachViewObserver(ViewObserver controller);

	public String sendMessage();

	void closeTab(ActionEvent e);

	String getTitle();

	void showMessage(String message, String title);

	JTextArea createTab(String title);

	void writeText(String msg, int tab);

	int getTabIndex();
	
	public void showMessageMain(String Message);
	
	public int buildChoiceMessageBox(String Message, String title, Object[] options,
			int IconType);
	
	public void appendUser(String user);
	
	public boolean removeUser(String user);
}
