package endpoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import endpoint.ChatMessage.Param;
import endpoint.ChatMessage.Type;
import endpoint.User.State;


/**
 * main class similar to websockethandler.
 * It handles all the incoming (and outgoing) messages from client
 * to server and viceversa.
 * @author Stefano Belli
 * */

@ServerEndpoint(  
		value = "/websocket",
		encoders = { MessageEncoder.class }, 
		decoders = { MessageDecoder.class }
)
public class MyServerEndpoint implements ServletContextListener{ //http://mjtoolbox.wordpress.com/2013/06/20/websocket-chat-application-using-jee-7-with-glassfish-4-0/
	
	//private static final ArrayList<User> UsersList=new ArrayList<User>();
	private static final Map<Session,User> usersList=new HashMap<Session,User>();
	private int TIMEOUT_SECONDS=45;
	
	
	
	/**
	 * Method run at the successful connection beetween Client & Server
	 * 
	 * @throws EncodeException
	 * @throws InterruptedException
	 */
	@OnOpen
	public void onOpen(Session newSession){
	}
	
	

	/**
	 * Method run during the closing attempt of connection of the Client
	 * In this architecture (server side) when a user is disconnecting 
	 * a broadcast message of this disconnection is sent to all users.
	 * @throws EncodeException
	 * @throws IOException
	 * 
	 */
	@OnClose
	public void onClose(Session clientsession) {

		User user = searchUser(clientsession);
		if (user != null) {
			usersList.remove(user.GetSession());
			ChatMessage cmessage = new ChatMessage("disconnecting",
					Type.USERDISCONNECTED);
			ChatMessage.Param addp = new Param();
			addp.setNickname(user.GetNickname());
			cmessage.setAdditionalParams(addp);

			try {
				sendGlobalMex(cmessage);
			} catch (Exception e) {
			}

		}
	}


	/**
	 * Method run at the receiving of a message from the server
	 * 
	 * @throws EncodeException
	 * @throws IOException
	 * @throws InterruptedException
	 * 
	 */	
	@OnMessage
	public void onMessage(ChatMessage message, Session clientsession) throws IOException,
			EncodeException {

		if(message.getType() == ChatMessage.Type.INITIALIZE){ /*connection request from client*/
			String UserNickname=message.getAdditionalParams().getNickname();
			Boolean UserVisibility=message.getAdditionalParams().getVisibility();
						
			
			boolean nickavaible=checkAvailabilityNickname(UserNickname);
			
			if (!nickavaible) {
				sendMex(new ChatMessage("unavaiable", Type.NICKNAMEUNAVAIABLE),
						clientsession);
				return;
			} else {
				sendMex(new ChatMessage("granted", Type.CONNECTIONGRANTED),
						clientsession);

			}
			
			
			if(UserVisibility.equals(true)){ /*alerts other clients of new user*/
				
				ChatMessage newmessage;
				newmessage=new ChatMessage("new user", Type.NEWUSER);
				newmessage.getAdditionalParams().setNickname(UserNickname);
				sendGlobalMex(newmessage);				
			}

			clientsession.setMaxIdleTimeout(TIMEOUT_SECONDS*1000);
			if(UserVisibility)
				usersList.put(clientsession,new User(UserNickname,State.VISIBLE, clientsession));
			else
				usersList.put(clientsession,new User(UserNickname,State.INVISIBLE, clientsession));			
		}
		
		
		if(message.getType() == Type.USERLIST){
			sendUserList(clientsession);
		}
		
		/*a user is disconnecting: need to tell it to other clients*/
		if(message.getType() == Type.DISCONNECTING){ 
			User disconnUser=searchUser(clientsession);
			removeUser(disconnUser);
			
			ChatMessage cmessage=new ChatMessage("disconnecting",Type.USERDISCONNECTED);
			ChatMessage.Param addp=new Param();
			addp.setNickname(disconnUser.GetNickname());
			cmessage.setAdditionalParams(addp);
			
			sendGlobalMex(cmessage);
			
		}
		if(message.getType() == Type.TEXT){
			Param addp=new Param();
			addp.setNickname(searchUser(clientsession).GetNickname());
			message.setAdditionalParams(addp);
			sendBroadcast(message,clientsession);
		}
		
		if(message.getType() == Type.REQUESTPRIVATECHAT
				| message.getType() == Type.REQUESTSENDFILE){
			String SenderNickname=searchUser(clientsession).GetNickname();	/*Sender*/
			User Targetusr=searchUser(message.getAdditionalParams().getNickname()); /*Target*/
			
			ChatMessage cmessage;
			if(message.getType() == Type.REQUESTSENDFILE){
				cmessage=new ChatMessage("respond",Type.REQUESTEDSENDFILE);
				cmessage.getAdditionalParams().setFileName(
						message.getAdditionalParams().getFileName());
			}
			else{
				cmessage=new ChatMessage("respond",Type.REQUESTEDPRIVATECHAT);
			}
			
			cmessage.getAdditionalParams().setNickname(SenderNickname);
			sendMex(cmessage, Targetusr.GetSession());
		}
		
		if(message.getType() == Type.YESPRIVATECHAT ||
				message.getType() == Type.YESSENDFILE){
			
			User agreedinguser=searchUser(clientsession); /*SENDER*/
			User Targetusr=searchUser(message.getAdditionalParams().getNickname()); /*Target*/
			
			
			ChatMessage cmessage=new ChatMessage("respond",message.getType());
			cmessage.getAdditionalParams().setNickname(agreedinguser.GetNickname());
			cmessage.getAdditionalParams().setIP(message.getAdditionalParams().getIP());
			cmessage.getAdditionalParams().setSSLPort(message.getAdditionalParams().getSSLPort());
			cmessage.getAdditionalParams().setKEYPort(message.getAdditionalParams().getKEYPort());
			

			if(message.getType() == ChatMessage.Type.YESSENDFILE){
				cmessage.getAdditionalParams().setFileName(
						message.getAdditionalParams().getFileName());
			}
			sendMex(cmessage,Targetusr.GetSession());
		}			
		if(message.getType() == Type.NOPRIVATECHAT || 
				message.getType() == Type.NOSENDFILE){			
			User agreedinguser=searchUser(clientsession); /*SENDER*/
			User Targetusr=searchUser(message.getAdditionalParams().getNickname()); /*Target*/			
			
			ChatMessage cmessage=new ChatMessage("respond",message.getType());
			cmessage.getAdditionalParams().setNickname(agreedinguser.GetNickname());
			sendMex(cmessage,Targetusr.GetSession());
		}			
	}
	
	/**
	 * Method run automatically if an exception is thrown.
	 * */
	@OnError
	public void onError(Session clientsession, Throwable aThrowable) {
		System.out.println("Error : " + clientsession);

	}


	/**
	 * Method run automatically on the initialization of the server.
	 * */
	@Override
	public void contextInitialized(ServletContextEvent sce) { //Launched at the web app starting time.
        System.out.println("#### Inizializing Server for first launch.. ####");
        System.out.println("#### Initialized.. ####");		
        
	}  
	


	/**
	 * Method run automatically on the shutting down of the server.
	 * */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}
	

	/**
	 * sends the given message to the target client session
	 * @param the message to be sent
	 * @param the target client session
	 * */	
	 public void sendMex(ChatMessage mex,Session clientsession) 
			 throws IOException, EncodeException{    
		 	clientsession.getBasicRemote().sendObject(mex);
	}
	 
	 /**
	 * sends the given message to all users
	 * @param the message to be sent
	 * */	
	 public void sendGlobalMex(ChatMessage mex) throws IOException, EncodeException{
		 for (User cUser:usersList.values()) {
			 sendMex(mex,cUser.GetSession());		 
		 }
	 }
	 

	 /**
	 * sends the given message to all users, excluding the given session 
	 * from the message
	 * @param the message to be sent
	 * @param the session to exclude from the message
	 * */	
	public void sendBroadcast(ChatMessage mex,Session exclude) throws IOException, EncodeException{
		 for (User cUser:usersList.values()) {
			 if(!cUser.GetSession().equals(exclude)){
				 sendMex(mex,cUser.GetSession());		 
			 }
		 }
	 }
	 
	 /**
		 * sends the given message to all users,excluding the given message
		 * and the visible users.
		 * @param the message to be sent
		 * @param the session to exclude from the message
		 * */	
	 public void sendGlobalMex(ChatMessage mex,Session exclude) throws IOException, EncodeException{
		 for (User cUser:usersList.values()) {
			 if(!cUser.GetSession().equals(exclude) &&
					 cUser.GetState() == State.VISIBLE){
					 sendMex(mex,cUser.GetSession());	
			 }
		 }
	 }
	 
	 /**
	  * Search the User object associated with the Session object.
	  * */
	 public User searchUser(Session session){		 
		 if(usersList.containsKey(session))
			 return usersList.get(session);
		 return null;		 
	 }
	 

	 /**
	  * Search the User object associated with his nickname.
	  * */
	 public User searchUser(String nick){
		 for(User usr:usersList.values()){
			 if(usr.GetNickname().equals(nick))
				 return usr;
		 }
		 return null;
	 }
	 

	 /**
	  * remove the user from the main map of users.
	  * */
	 public boolean removeUser(User usr){
		 
		 	/*session is the key of the map*/
			User result=usersList.remove(usr.GetSession());
			if(result != null)
				return true;
			else
				return false;			
	 }
	 

	 /**
	  * sends the users list to the given session, excluding the invisible
	  * users.
	  * */
	 public void sendUserList(Session target) throws IOException, EncodeException{
		 
		 ChatMessage messagetoclient = new ChatMessage("Users List",
					Type.USERLIST);
			for (User cUser : usersList.values()) {
				if (cUser.GetState() == State.VISIBLE && cUser.GetSession() != target) {
					messagetoclient.getAdditionalParams().appendUser(
							cUser.GetNickname());
				}
			}
			sendMex(messagetoclient, target);
	 }
	 

	 /**
	  * check if the given nickname is available in the server
	  * @param The nickname to check
	  * @return the availability of the nickname: TRUE if available, FALSE if not.
	  * */
	 public boolean checkAvailabilityNickname(String nick){
		 boolean result=true;
		for (User cUser : usersList.values()) {
			if (cUser.GetNickname().equals(nick))
				result=false;
		}
		return result;
	 }
	
}