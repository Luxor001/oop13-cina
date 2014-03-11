package client_chat;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.websocket.EncodeException;

import client_chat.ChatMessage.Type;
import client_chat.View.sfx;

/**
 * Can send commands to the model to update data structure and can send commands
 * to change the view
 * 
 * @author Francesco Cozzolino
 * @author Stefano Belli
 * 
 */
public class Controller implements ViewObserver {

	private static final String USER_DISCONNECTED = "the user may be "
			+ "disconnected or a new user has just login with the same nickname"
			+ ".in this case,if you want chat with this user ,send a new "
			+ "request of private chat";
	private ViewInterface view = null;
	private ModelInterface model = null;
	private Object lockNotification = new Object();

	public enum MessageBoxReason {
		REQUEST_PRIVATE_CHAT, REQUEST_RECEIVE_FILE, ALERT_CLOSING_WINDOW, FILESIZELIMIT
	}

	/**
	 * 
	 * @param view
	 * 
	 * @author Francesco Cozzolino
	 */
	public void setView(ViewInterface view) {
		this.view = view;
		this.view.attachViewObserver(this);
	}

	/**
	 * 
	 * @param model
	 * 
	 * @author Francesco Cozzolino
	 */
	public void setModel(ModelInterface model) {
		this.model = model;
		this.model.attachViewObserver(this);

	}

	/**
	 * invokes method that permit to show frame of downloads
	 * 
	 * @author Francesco Cozzolino
	 */
	public void commandShowDownloads() {
		model.showDownloads();
	}

	/**
	 * Invokes method that permit to show frame of preferences
	 * 
	 * @author Francesco Cozzolino
	 */
	public void commandShowPreferences() {
		model.showPreferences();
	}

	/**
	 * Sends message to a specific user or to everybody
	 * 
	 * @param message
	 *            message to send
	 * @param name
	 *            name of receiver
	 * 
	 * @author Francesco Cozzolino
	 * @author Stefano Belli
	 */
	public void commandSendMessage(String message, String name) {

		if (this.view.getTabIndex() == 0) {

			try {
				WebsocketHandler.getWebSocketHandler().SendMex(
						new ChatMessage(message, Type.TEXT));
			} catch (Exception e) {

				e.printStackTrace();
			}
		} else {

			String ip = exist(name);
			if (ip != null) {
				model.sendMessage(message, name);
			} else {
				commandReceiveMessage(USER_DISCONNECTED, name);
			}

		}

	}

	/**
	 * Sends file to a specific user
	 * 
	 * @param path
	 *            path of file
	 * @param name
	 *            name of receiver
	 * 
	 * @author Francesco Cozzolino
	 */
	public void sendFile(String path, String name) {
		String ip = exist(name);
		if (ip != null) {
			model.sendFile(path, name);
		} else {
			commandReceiveMessage(USER_DISCONNECTED, name);
		}

	}

	/**
	 * @author Francesco Cozzolino
	 */
	public synchronized void commandCloseTab(ActionEvent e) {
		view.closeTab(e);
	}

	/**
	 * Creates a chat window and establishes a connection (if necessary)
	 * 
	 * @param ip
	 *            ip address of user you want to talk
	 * @param port
	 *            port to connect to the specific ip
	 * @param name
	 *            name of user you want to talk
	 * 
	 * @author Francesco Cozzolino
	 * 
	 */
	public synchronized void commandCreateTab(String ip, int port, String name,
			String keyStore) {

		if (!model.isConnect(ip, port)) {
			this.model.connectToServer(ip, port, name, keyStore);
		}

		this.view.createTab(name);

	}

	/**
	 * Shows messages received from other users
	 * 
	 * @author Francesco Cozzolino
	 */
	public synchronized void commandReceiveMessage(String message, String title) {
		this.view.showMessage(message, title);
	}

	/**
	 * @author Francesco Cozzolino
	 */
	public void commandRemoveUser(String name) {
		this.model.removeNickName(name);
	}

	/**
	 * @author Francesco Cozzolino
	 */
	public void commandRefusedChat(String name) {
		commandRemoveUser(name);
		showMessageMain(name + " has refused the request of private chat");
	}

	/**
	 * Close all socket
	 * 
	 * @author Francesco Cozzolino
	 */
	public void commandCloseAll() {

		this.model.closeAll();
	}

	/**
	 * 
	 * @param name
	 *            name of user
	 * @return ip address of user or null if doesn't exist
	 */
	public String exist(String name) {

		return model.exist(name);
	}

	/**
	 * 
	 * @param ip
	 *            ip address
	 * @param port
	 *            router's port
	 * @return "exist", null if doesn't exist ip address or "pending" if a
	 *         request of chat it's already sended
	 */
	public String existIp(String ip, int port) {

		return model.existIp(ip, port);
	}

	/**
	 * Writes directly in the "main" chat.
	 * @param
	 * 		 the plain text message to show.
	 * @author Stefano Belli
	 */
	public synchronized void showMessageMain(String Message) {
		this.view.showMessageMain(Message);
	}

	/**
	 * calls the correspective view class method to append a 
	 * user in the JList of users.
	 * @param the user to append
	 * 
	 * @author Stefano Belli
	 * @see View
	 */
	public void appendUser(String user) {
		this.view.appendUser(user);
	}

	/**
	 * calls the correspective view class method to remove a 
	 * user in the JList of users.
	 * @param the user to remove
	 * @return
	 * 
	 * @author Stefano Belli
	 */
	public boolean removeUser(String user) {
		return this.view.removeUser(user);
	}

	/**
	 * Method called from the view class to notify that a closing attempt
	 * of the chat has been invoked.
	 * Here the closing handshake with the server is sent.
	 * @author Stefano Belli
	 */
	public void notifyClosing() throws IOException, EncodeException {
		WebsocketHandler.getWebSocketHandler().SendMex(
				new ChatMessage("Closing", Type.DISCONNECTING));
	}

	/**
	 * Creates a standard JOptionPane based on a MessageBoxReason message.
	 * @param 
	 * @param Optional informations like the nickname of the sender, etc.
	 * @author Stefano Belli
	 */
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
			message = "Are you sure you to close cryptochat?\n all opened "
					+ "connections will be lost!";
			title = "Are you sure?";
			options = new Object[] { "Yes", "No Way!" };
			iconType = JOptionPane.WARNING_MESSAGE;
			break;
		}
		case FILESIZELIMIT: {
			message = "File size is too big! Limit is 25MB!";
			title = "Error!";
			options = new Object[] { "Ok" };
			iconType = JOptionPane.ERROR_MESSAGE;
		}
		}

		return view.buildChoiceMessageBox(message, title, options, iconType);
	}

	/**
	 * Sends to the user a request of private chat
	 * 
	 * @param name
	 *            name of user you want to talk
	 * 
	 * @throws IOException
	 * @throws EncodeException
	 * 
	 * @author Stefano Belli
	 * @author Francesco Cozzolino
	 */
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

	/**
	 * Sends to the user a request of private file
	 * 
	 * @param ip
	 *            ip address
	 * @param keyPort
	 *            port for exchange keystore (if necessary)
	 * @param sslPort
	 *            port that establishes connection to the specific ip (if
	 *            necessary)
	 * 
	 * @author Francesco Cozzolino
	 * 
	 */
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
						showMessageMain("The user has refused the request of "
								+ "private chat or an invalid ip address has "
								+ "been entered");
					}
				} else {
					model.removeIp(ip, sslPort);
				}
			}
		}
	}

	/**
	 * Sends to the user a request of private chat
	 * 
	 * @param path
	 *            path of file
	 * @param name
	 *            name of receiver
	 * 
	 * @author Stefano Belli
	 * @author Francesco Cozzolino
	 */
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

	/**
	 * close the application.
	 * Handles all the closing handshakes of the connection.
	 * @author Stefano Belli
	 */
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