package client_chat;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.websocket.EncodeException;

import client_chat.ChatMessage.Type;
import client_chat.View.sfx;

public class Controller implements ViewObserver {

	private ViewInterface view = null;
	private ModelInterface model = null;
	private Object lockNotification = new Object();

	public enum MessageBoxReason {
		REQUEST_PRIVATE_CHAT, REQUEST_RECEIVE_FILE, ALERT_CLOSING_WINDOW,
		FILESIZELIMIT
	}

	public void setView(ViewInterface view) {
		this.view = view;
		this.view.attachViewObserver(this);
	}

	public void setModel(ModelInterface model) throws IOException {
		this.model = model;
		this.model.attachViewObserver(this);

	}

	public void commandShowDownloads() {
		model.showDownloads();
	}

	public void commandShowPreferences() {
		model.showPreferences();
	}

	public void commandSendMessage(String message, String name) {

		if (this.view.getTabIndex() == 0) {
			/*
			 * if user wants to send a message on general chat
			 */
			try {
				WebsocketHandler.getWebSocketHandler().SendMex(
						new ChatMessage(message, Type.TEXT));
			} catch (Exception e) {

				e.printStackTrace();
			}
		} else { /* instead user wants to write on a private chat.. */

			String ip = exist(name);
			if (ip != null) {
				model.sendMessage(message, name);
			} else {
				commandReceiveMessage(
						"the user may be disconnected or a new user has just login with the same nickname."
								+ "in this case, if you want chat with this user ,send a new request of private chat",
						name);
			}

		}

	}

	public void sendFile(String path, String name) {
		String ip = exist(name);
		if (ip != null) {
			model.sendFile(path, name);
		} else {
			commandReceiveMessage(
					"the user may be disconnected or a new user has just login with the same nickname."
							+ "in this case, if you want chat with this user ,send a new request of private chat",
					name);
		}

	}

	public synchronized void commandCloseTab(ActionEvent e) {
		view.closeTab(e);
	}

	public synchronized void commandCreateTab(String ip, int port, String name,
			String keyStore) {

		if (!model.isConnect(ip, port)) {
			this.model.connectToServer(ip, port, name, keyStore);
		}

		this.view.createTab(name);

	}

	public synchronized void commandReceiveMessage(String message, String title) {
		this.view.showMessage(message, title);
	}

	public void commandRemoveUser(String name) {
		this.model.removeNickName(name);
	}

	public void commandRefusedChat(String name) {
		commandRemoveUser(name);
		showMessageMain(name + " has refused the request of private chat");
	}

	public void commandCloseAll() {

		this.model.closeAll();
	}

	public String exist(String name) {

		return model.exist(name);
	}

	public String existIp(String ip, int port) {

		return model.existIp(ip, port);
	}

	public synchronized void showMessageMain(String Message) {
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

	public int buildChoiceMessageBox(MessageBoxReason reason,
			String... optsender) {

		String message = "";
		String title = "";
		Object[] options = null;
		int iconType = 0;

		switch (reason) {
		case REQUEST_PRIVATE_CHAT: {
			message = "User " + optsender[0]
					+ " wants to have a private chat with you";
			title = "Private Chat";
			options = new Object[] { "Yes", "No Way!" };
			iconType = JOptionPane.WARNING_MESSAGE;
			view.playSound(sfx.REQUEST);
			break;
		}

		case REQUEST_RECEIVE_FILE: {
			message = "User " + optsender[0] + " wants to send you file:"
					+ optsender[1];
			title = "Receiving File";
			options = new Object[] { "Yes", "No Way!" };
			iconType = JOptionPane.WARNING_MESSAGE;
			view.playSound(sfx.REQUEST);
			break;
		}

		case ALERT_CLOSING_WINDOW: {
			message = "Are you sure you to close cryptochat?\n all opened connections will be lost!";
			title = "Are you sure?";
			options = new Object[] { "Yes", "No Way!" };
			iconType = JOptionPane.WARNING_MESSAGE;
			break;
		}
		case FILESIZELIMIT:{
			message = "File size is too big! Limit is 25MB!";
			title = "Error!";
			options = new Object[] { "Ok"};
			iconType = JOptionPane.ERROR_MESSAGE;
		}
		}

		return view.buildChoiceMessageBox(message, title, options, iconType);
	}

	public void notifyChatUser(String name) throws IOException, EncodeException {

		synchronized (lockNotification) {
			String ip = exist(name);
			if (ip == null) {
				model.addNickName(name, "pending", 0);
				ChatMessage message = new ChatMessage("Connect to",
						Type.REQUESTPRIVATECHAT);
				ChatMessage.Param params = new ChatMessage.Param();
				params.setNickname(name);
				message.setAdditionalParams(params);
				WebsocketHandler.getWebSocketHandler().SendMex(message);
			} else {
				if (!ip.equals("pending:0")) {
					String[] ipPort = ip.split(":");
					commandCreateTab(ipPort[0], Integer.parseInt(ipPort[1]),
							name, name + "ServerKey.jks");
				}
			}
		}

	}

	public void notifyChatUserIp(String ip, int keyPort, int sslPort) {
		synchronized (lockNotification) {
			String name = existIp(ip, sslPort);

			if (name == null || name.equals("exist")) {
				model.addIp(ip, sslPort, "pending");
				if (!model.isConnect(ip, sslPort)) {
					name = Client.ObtainKeyStore(ip, keyPort, "user");

					if (name != null) {
						commandCreateTab(ip, sslPort, name, name
								+ "ServerKey.jks");
					} else {
						model.removeIp(ip, sslPort);
						showMessageMain("The user has refused the request of private chat or an invalid ip address has been entered");
					}
				} else {
					model.removeIp(ip, sslPort);
				}
			}
		}
	}

	public void notifySendFileUser(String path, String name)
			throws IOException, EncodeException {

		if (new File(path).length() <= 26214400) {

			ChatMessage message = new ChatMessage("Connect to",
					Type.REQUESTSENDFILE);
			ChatMessage.Param params = new ChatMessage.Param();
			params.setNickname(name);
			params.setFileName(path);
			message.setAdditionalParams(params);
			WebsocketHandler.getWebSocketHandler().SendMex(message);

		} else {
			buildChoiceMessageBox(MessageBoxReason.FILESIZELIMIT, "ok");
		}

	}

	public void closeChat() throws IOException, EncodeException {
		if (WebsocketHandler.getWebSocketHandler().isConnected()) {
			ChatMessage closingmessage = new ChatMessage("closing",
					Type.DISCONNECTING);
			WebsocketHandler.getWebSocketHandler().SendMex(closingmessage);
		}
		model.closeAll();
		view.closeChat();
		WebsocketHandler.getWebSocketHandler().closeConnection();
	}
}