package client_chat;

import java.util.ArrayList;

/*
 * @(#)ChatMessage.java        1.0 02/12/2013
 *
 * Belli Stefano 0000652935, Cozzolino Francesco 0000xxxxxxx
 * 
 */

/* Class that describes the actual messages sent from (and to) the users
 * The below class is then converted in json format by MessageEncoder.java class
 * Please note that this class is equal to the server correspective.
 */
public class ChatMessage {

	enum Type {
		INITIALIZE, /* Used to Registry a new user to the channel */
		TEXT, /* Duh. */		 
		NEWUSER, /* Needed to tell to server there's a new boy in town */
		USERDISCONNECTED,
		USERLIST,
		DISCONNECTING,
		REQUESTPRIVATECHAT,
		REQUESTEDPRIVATECHAT,
		YESPRIVATECHAT,
		NOPRIVATECHAT,
		NICKNAMEUNAVAIABLE
	}

	private Param additionalParams;// =new Param();
	private Type messageType;
	private String message;

	public ChatMessage() {
	}

	public ChatMessage(String message, Type MessageType) {
		this.message = message;
		this.messageType = MessageType;
	}

	public ChatMessage(String message, Type MessageType, Param additionalParams) {
		this.message = message;
		this.messageType = MessageType;
		this.additionalParams = additionalParams;
	}

	public void setType(Type MessageType) {
		this.messageType = MessageType;
	}

	public Type getType() {
		return messageType;
	}

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

	/**
	 * Return message
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set message
	 * 
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/*
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
		public Param() {

		}

		/*
		 * ricoradrsi di specificare che il nickname in questi parametri
		 * addizionali è usato SOLO per il primo avvio. Dopo la faccenda dei
		 * nickname sarà gestita SOLO dal webserver: sarà lui a inserirlo, per
		 * un motivo di sicurezza.
		 */
		public void setNickname(String Nick) {
			nickname = Nick;
		}

		public String getNickname() {
			return nickname;
		}

		public void SetVisibility(Boolean visible) {
			visibility = visible;
			;
		}
		
		public void setIP(String ip){
			this.ip=ip;
		}		
		
		
		public Boolean getVisibility() {
			return visibility;
		}
		

		public ArrayList<String> getUsersList() {
			return usersList;
		}

		public void appendUser(String UserNickname) {
			usersList.add(UserNickname);
		}
		
		public String getIP(){
			return ip;
		}
	}
}