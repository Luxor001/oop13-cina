package client_chat;

import java.awt.event.ActionEvent;

public interface ViewObserver {

	void commandSendMessage();

	void commandCloseTab(ActionEvent e);

	void commandCreateTab();
}
