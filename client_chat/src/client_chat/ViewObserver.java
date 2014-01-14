package client_chat;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

public interface ViewObserver {

	void commandSendMessage(String message);

	void commandCloseTab(ActionEvent e);

	void commandCreateTab(JTextArea chat);

	// void commandShowMessage();
}
