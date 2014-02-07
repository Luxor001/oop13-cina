package client_chat;

import java.awt.event.ActionEvent;
import java.io.IOException;

import client_chat.ChatMessage.Type;

public class Controller implements ViewObserver {

	private ViewInterface view;
	private ModelInterface model;

	public Controller() {
	}

	public void setView(ViewInterface view) {
		this.view = view;
		this.view.attachViewObserver(this);
	}

	public void setModel(ModelInterface model) throws IOException {
		this.model = model;
		this.model.attachViewObserver(this);

	}

	public void commandSendMessage(){

		if(this.view.getTabIndex() == 0){/*if user wants to send a message on general chat*/
			try {
				this.model.getSocketHandler().SendMex(
						new ChatMessage("Messaggio",Type.TEXT));
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		else{ /*instead user wants to write on a private chat..*/
			this.model.sendMessage((this.view.sendMessage()),
				+this.view.getTabIndex());
		}
	}

	public void commandCloseTab(ActionEvent e) {
		view.closeTab(e);
	}

	public void commandCreateTab() {
		this.model.connectToServer(this.view.getTabIndex(),
				view.createTab(view.getTitle()));
	}

	public void commandReceiveMessage(String message, String title) {
		this.view.showMessage(message, title);
	}

	public void showMessageMain(String Message) {
		this.view.showMessageMain(Message);
	}

}