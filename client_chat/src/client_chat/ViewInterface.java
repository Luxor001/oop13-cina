package client_chat;

import java.awt.event.ActionEvent;

public interface ViewInterface {

	void attachViewObserver(ViewObserver controller);

	void sendMessage();

	void closeTab(ActionEvent e);

	void createTab();
}
