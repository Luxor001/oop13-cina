package client_chat;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.websocket.EncodeException;

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

	public void commandSendMessage() {

		if (this.view.getTabIndex() == 0) {/*
											 * if user wants to send a message
											 * on general chat
											 */
			try {
				this.model.getSocketHandler().SendMex(
						new ChatMessage(this.view.sendMessage(), Type.TEXT));
			} catch (Exception e) {

				e.printStackTrace();
			}
		} else { /* instead user wants to write on a private chat.. */
			this.model.sendMessage((this.view.sendMessage()),
					+this.view.getTabIndex(), this.view.getTabName());
		}

	}

	public synchronized void commandCloseTab(ActionEvent e) {
		view.closeTab(e);
	}

	public synchronized void commandCreateTab() {
		this.model.connectToServer(view.createTab(view.getTitle()),
				this.view.getTabIndex(), "localhost");
	}

	public synchronized void commandCreateTab(String ip) {
		this.model.connectToServer(view.createTab(view.getTitle()),
				this.view.getTabIndex(), ip);
	}

	public synchronized void commandReceiveMessage(String message, String title) {
		this.view.showMessage(message, title);
	}

	public void commandCloseAll() {

		this.model.closeAll();
	}

	public void showMessageMain(String Message) {
		this.view.showMessageMain(Message);
	}

	public void appendUser(String user) {
		this.view.appendUser(user);
	}

	public boolean removeUser(String user) {
		return this.view.removeUser(user);
	}

	public void notifyClosing() throws IOException, EncodeException {
		this.model.getSocketHandler().SendMex(
				new ChatMessage("Closing", Type.DISCONNECTING));
	}

	public int buildChoiceMessageBox(String Message, String title,
			Object[] options, int IconType) {
		return view.buildChoiceMessageBox(Message, title, options, IconType);
	}

	public void notifyChatUser() throws IOException, EncodeException {
		ChatMessage message = new ChatMessage("Connect to",
				Type.REQUESTPRIVATECHAT);
		ChatMessage.Param params = new ChatMessage.Param();
		params.setNickname(view.getTitle());
		message.setAdditionalParams(params);
		this.model.getSocketHandler().SendMex(message);
	}

}