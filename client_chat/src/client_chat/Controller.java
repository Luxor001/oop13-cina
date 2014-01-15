package client_chat;

import java.awt.event.ActionEvent;

public class Controller implements ViewObserver {

	private ViewInterface view;
	private ModelInterface model;

	public Controller() {
	}

	public void setView(ViewInterface view) {
		this.view = view;
		this.view.attachViewObserver(this);
	}

	public void setModel(ModelInterface model) {
		this.model = model;
		this.model.attachViewObserver(this);
	}

	public void commandSendMessage() {
		this.model.sendMessage((this.view.sendMessage()));
	}

	public void commandCloseTab(ActionEvent e) {
		view.closeTab(e);
	}

	public void commandCreateTab() {
		this.model.connectToServer(view.createTab(view.getTitle()));
	}

	public void commandReceiveMessage(String message, String title) {

		this.view.showMessage(message, title);
	}

}
