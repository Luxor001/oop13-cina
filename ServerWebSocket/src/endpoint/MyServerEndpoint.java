package endpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;

import endpoint.ChatMessage.Param;
import endpoint.ChatMessage.Type;
import endpoint.User.State;




@ServerEndpoint(   //per provare la chat,C:\glassfish4\glassfish\domains\domain1\eclipseApps\WebSocketJEE7Demo
		value = "/websocket",
		encoders = { MessageEncoder.class }, 
		decoders = { MessageDecoder.class }
)
public class MyServerEndpoint implements ServletContextListener{ //http://mjtoolbox.wordpress.com/2013/06/20/websocket-chat-application-using-jee-7-with-glassfish-4-0/
	
	//private static final ArrayList<User> UsersList=new ArrayList<User>();
	private static final Map<Session,User> UsersList=new HashMap<Session,User>();
	private int TIMEOUT_SECONDS=45;
	
	@OnOpen
	public void onOpen(Session newSession){ // ## METODO CHIAMATO QUANDO UN CLIENT SI CONNETTE ##
				
	//	clientSessions.add(newSession);
	/*	ChatMessage Message=new ChatMessage("PROVA", Type.INITIALIZE);
		Message.appendAdditionalParams("Nickname", "Lux");
		SendMex(Message, newSession);*/
		
	}
	
	@OnClose
	public void onClose(Session clientsession) {
		
		User user=searchUser(clientsession);
		
		if(user != null){				
			UsersList.remove(user.GetSession());
		}
	}

	@OnMessage
	public void onMessage(ChatMessage message, Session clientsession) throws IOException,
			EncodeException {

		/*RESET THE SERVER*/
		if(message.getType() == ChatMessage.Type.RESETFLAG){
			for(User a:UsersList.values()){
				a.GetSession().close();
				removeUser(a);				
			}			
		}
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
				UsersList.put(clientsession,new User(UserNickname,State.VISIBLE, clientsession));
			else
				UsersList.put(clientsession,new User(UserNickname,State.INVISIBLE, clientsession));			
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
			sendGlobalMex(message,clientsession);
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
		
		if(message.getType() == Type.YESPRIVATECHAT){
			
			User agreedinguser=searchUser(clientsession); /*SENDER*/
			User Targetusr=searchUser(message.getAdditionalParams().getNickname()); /*Target*/
			
			
			ChatMessage cmessage=new ChatMessage("respond",Type.YESPRIVATECHAT);
			cmessage.getAdditionalParams().setNickname(agreedinguser.GetNickname());
			cmessage.getAdditionalParams().setIP(message.getAdditionalParams().getIP());
			sendMex(cmessage,Targetusr.GetSession());
		}
		
			
	}

	

	
	@OnError
	public void onError(Session clientsession, Throwable aThrowable) {
		System.out.println("Error : " + clientsession);

	}
		
	
	

	@Override
	public void contextInitialized(ServletContextEvent sce) { //Launched at the web app starting time.
        System.out.println("#### Inizializing Server for first launch.. ####");
        System.out.println("#### Initialized.. ####");		
        
	}  
	

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}
	
	 public void sendMex(ChatMessage Mex,Session clientsession) 
			 throws IOException, EncodeException{    
		 	clientsession.getBasicRemote().sendObject(Mex);
	}
	 
	 public void sendGlobalMex(ChatMessage Mex) throws IOException, EncodeException{
		 for (User cUser:UsersList.values()) {
			 sendMex(Mex,cUser.GetSession());		 
		 }
	 }
	 
	 public void sendGlobalMex(ChatMessage Mex,Session exclude) throws IOException, EncodeException{
		 for (User cUser:UsersList.values()) {
			 if(!cUser.GetSession().equals(exclude) && 
					 cUser.GetState() == State.VISIBLE)
				 sendMex(Mex,cUser.GetSession());		 
		 }
	 }
	 
	 public User searchUser(Session session){		 
		 if(UsersList.containsKey(session))
			 return UsersList.get(session);
		 return null;		 
	 }
	 public User searchUser(String Nick){
		 for(User usr:UsersList.values()){
			 if(usr.GetNickname().equals(Nick))
				 return usr;
		 }
		 return null;
	 }
	 
	 public boolean removeUser(User usr){
		 
		 	/*session is the key of the map*/
			User result=UsersList.remove(usr.GetSession());
			if(result != null)
				return true;
			else
				return false;			
	 }
	 
	 public void sendUserList(Session target) throws IOException, EncodeException{
		 
		 ChatMessage messagetoclient = new ChatMessage("Users List",
					Type.USERLIST);
			for (User cUser : UsersList.values()) {
				if (cUser.GetState() == State.VISIBLE && cUser.GetSession() != target) {
					messagetoclient.getAdditionalParams().appendUser(
							cUser.GetNickname());
				}
			}
			sendMex(messagetoclient, target);
	 }
	 
	 public boolean checkAvailabilityNickname(String Nick){
		 boolean result=true;
		for (User cUser : UsersList.values()) {
			if (cUser.GetNickname().equals(Nick))
				result=false;
		}
		return result;
	 }
	
}