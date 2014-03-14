package webSocket;

import java.util.ArrayList;

/** Class that describes the "Object" messages sent from (and to) the users
 * The below class is then converted in json format by MessageEncoder.java class 
 * Please note that this class is equal to the server correspective.
 * @author Stefano Belli
 */
public class ChatMessage {

	/** 
	 * Type ofChatMessage. Needed to quickly identify a chatmessage.
	 * */
	public enum Type {
		INITIALIZE, /* Used to Registry a new user to the channel */
		CONNECTIONGRANTED, 
		TEXT,
		NEWUSER, /* Needed to tell to server there's a new boy in town */
		USERDISCONNECTED,
		USERLIST, 
		DISCONNECTING, 
		REQUESTPRIVATECHAT,
		REQUESTEDPRIVATECHAT,
		YESPRIVATECHAT, 
		NOPRIVATECHAT, 
		NICKNAMEUNAVAIABLE, 
		REQUESTSENDFILE, 
		REQUESTEDSENDFILE, 
		YESSENDFILE,
		NOSENDFILE,
		RESETFLAG, 
		PING
	}

	
	private Param additionalParams;
	private Type messageType;
	private String message;

	public ChatMessage() {
	}

	public ChatMessage(String message, Type messageType) {
		this.message = message;
		this.messageType = messageType;
	}

	public ChatMessage(String message, Type messageType, Param additionalParams) {
		this.message = message;
		this.messageType = messageType;
		this.additionalParams = additionalParams;
	}

	public void setType(Type messageType) {
		this.messageType = messageType;
	}

	public Type getType() {
		return messageType;
	}

	/**
	 * If no additionalParams were already added, a new (blank) one will be
	 * created on the istance of this class
	 * @return the Param istance of this object
	 * */
	public Param getAdditionalParams() {
		if (additionalParams == null)
			additionalParams = new Param();
		return additionalParams;
	}

	public void setAdditionalParams(Param params) {
		this.additionalParams = params;
	}

	public boolean isParamSet() {
		if (additionalParams == null)
			return false;
		else
			return true;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * This inner class exist to encapsulate all the optional parameters of a
	 * message for e.g. the users list sent by a "User List" request, or a
	 * nickname notification on connection. It has been created to get a smaller
	 * size of data packets sent: in fact, the Param class is completely
	 * optional and not necessarily needed.
	 */
	public static class Param {
		private String nickname;
		private Boolean visibility;
		private ArrayList<String> usersList = new ArrayList<String>();
		private String ip;
		private String fileName;
		private String SSLPort;
		private String KEYPort;

		
		public void setNickname(String nick) {
			nickname = nick;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getNickname() {
			return nickname;
		}

		public void SetVisibility(Boolean visible) {
			visibility = visible;

		}

		public void setIP(String ip) {
			this.ip = ip;
		}

		public Boolean getVisibility() {
			return visibility;
		}

		public ArrayList<String> getUsersList() {
			return usersList;
		}

		/**
		 * Append a user in the usersList field, needed for a 
		 * USERSLIST Chatmessage Type.
		 * */
		public void appendUser(String userNickname) {
			usersList.add(userNickname);
		}

		public String getIP() {
			return ip;
		}

		public String getFileName() {
			return fileName;
		}

		public void setSSLPort(String port) {
			this.SSLPort = port;
		}

		public String getSSLPort() {
			return SSLPort;
		}

		public void setKEYPort(String port) {
			this.KEYPort = port;
		}

		public String getKEYPort() {
			return KEYPort;
		}
	}
}