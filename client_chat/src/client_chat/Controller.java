package client_chat;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.websocket.EncodeException;

import client_chat.ChatMessage.Type;
import client_chat.View.sfx;

public class Controller implements ViewObserver {

	private ViewInterface view;
	private ModelInterface model;

	public enum MessageBoxReason{
		REQUEST_PRIVATE_CHAT,
		REQUEST_RECEIVE_FILE,
		ALERT_CLOSING_WINDOW
	}
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

		if (this.view.getTabIndex() == 0) {
			/*
			 * if user wants to send a message on general chat
			 */

			try {
				WebsocketHandler.getWebSocketHandler().SendMex(
						new ChatMessage(this.view.sendMessage(), Type.TEXT));
			} catch (Exception e) {

				e.printStackTrace();
			}
		} else { /* instead user wants to write on a private chat.. */
			this.model.sendMessage((this.view.sendMessage()),
					this.view.getTabName());
		}

	}

	public synchronized void commandCloseTab(ActionEvent e) {
		view.closeTab(e);
	}

	public synchronized void commandCreateTab() {
		if (!model.isConnect("/192.168.1.103")) {
			// this.model.connectToServer("192.168.1.103");
		}
		this.view.createTab(view.getTitle());
	}

	public synchronized void commandCreateTab(String ip, String keyStore) {

		if (!model.isConnect(ip)) {
			this.model.connectToServer(ip, keyStore);
		}
		this.view.createTab(view.getTitle());
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
		WebsocketHandler.getWebSocketHandler().SendMex(
				new ChatMessage("Closing", Type.DISCONNECTING));
	}

	public int buildChoiceMessageBox(MessageBoxReason reason,String ... optsender) {
		String message="";
		String title="";
		Object [] options=null;
		int iconType=0;
		
		switch (reason) {
		case REQUEST_PRIVATE_CHAT:{
			message="User " + optsender[0]
					+ " wants to have a private chat with you";
			title="Private Chat";
			options= new Object[] { "Yes", "No Way!" };
			iconType=JOptionPane.WARNING_MESSAGE;
			view.playSound(sfx.REQUEST);
			break;
		}
		
		
		case REQUEST_RECEIVE_FILE:{
			message="User " + optsender[0]
				+ " wants to send you file:"+optsender[1];	
			title="Receiving File";
			options= new Object[] { "Yes", "No Way!" };
			iconType=JOptionPane.WARNING_MESSAGE;
			view.playSound(sfx.REQUEST);
			break;			
		}

		case ALERT_CLOSING_WINDOW:{
			message="Are you sure you to close cryptochat?\n all opened connections will be lost!";
			title="Are you sure?";
			options= new Object[] { "Yes", "No Way!" };
			iconType=JOptionPane.WARNING_MESSAGE;
			break;			
		}
		}
		
		return view.buildChoiceMessageBox(message, title, options, iconType);
	}

	public synchronized void notifyChatUser() throws IOException,
			EncodeException {
		String ip = model.exist(view.getTitle());
		if (ip == null) {
			ChatMessage message = new ChatMessage("Connect to",
					Type.REQUESTPRIVATECHAT);
			ChatMessage.Param params = new ChatMessage.Param();
			params.setNickname(view.getTitle());
			message.setAdditionalParams(params);
			WebsocketHandler.getWebSocketHandler().SendMex(message);
		} else {
			commandCreateTab(ip, view.getTitle() + "ServerKey.jks");
		}
	}
	
	public synchronized void notifySendFileUser() throws IOException, EncodeException {

			ChatMessage message = new ChatMessage("Connect to",
					Type.REQUESTSENDFILE);
			ChatMessage.Param params = new ChatMessage.Param();
			params.setNickname(view.getTitle());
			params.setFileName("Path"); /*##INSERT PATH HERE FOR GOD SAKE##*/
			message.setAdditionalParams(params);
			WebsocketHandler.getWebSocketHandler().SendMex(message);

	}

}