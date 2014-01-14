package client_chat;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

public interface ViewInterface {

	void attachViewObserver(ViewObserver controller);

	String sendMessage();

	void closeTab(ActionEvent e);

	JTextArea createTab();
}
