package endpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.persistence.sessions.server.ClientSession;

import endpoint.ChatMessage.Param;
import endpoint.ChatMessage.Type;
import endpoint.User.State;




@ServerEndpoint(   //per provare la chat,C:\glassfish4\glassfish\domains\domain1\eclipseApps\WebSocketJEE7Demo
		value = "/websocket",
		encoders = { MessageEncoder.class }, 
		decoders = { MessageDecoder.class }
)
public class MyServerEndpoint implements ServletContextListener{ //http://mjtoolbox.wordpress.com/2013/06/20/websocket-chat-application-using-jee-7-with-glassfish-4-0/

	private static final Set<Session> clientSessions = Collections
			.synchronizedSet(new HashSet<Session>());	
	
	//private static final ArrayList<User> UsersList=new ArrayList<User>();
	private static final Map<Session,User> UsersList=new HashMap<Session,User>();
	
	@OnOpen
	public void onOpen(Session newSession) throws IOException, EncodeException { // ## METODO CHIAMATO QUANDO UN CLIENT SI CONNETTE ##
				
	//	clientSessions.add(newSession);
	/*	ChatMessage Message=new ChatMessage("PROVA", Type.INITIALIZE);
		Message.appendAdditionalParams("Nickname", "Lux");
		SendMex(Message, newSession);*/
		
	}
	
	@OnClose
	public void onClose(Session clientsession) {
		User user=searchUser(clientsession);
		if(user != null)
			UsersList.remove(user);
	}

	
	@OnMessage
	public void onMessage(ChatMessage message, Session clientsession) throws IOException,
			EncodeException {

		if(message.getType() == ChatMessage.Type.INITIALIZE){ /*connection request from client*/
			String UserNickname=message.getAdditionalParams().getNickname();
			Boolean UserVisibility=message.getAdditionalParams().getVisibility();
						
			if(UserVisibility.equals(true)){ /*alerts other clients of new user*/
				ChatMessage newmessage;
				newmessage=new ChatMessage("new user", Type.NEWUSER);
				newmessage.getAdditionalParams().setNickname(UserNickname);
				sendGlobalMex(newmessage);				
			}

			
			/* send users list to the new client */
			if (UsersList.size() != 0) {
				sendUserList(clientsession);				
			}
			
			
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
	 
	 public boolean removeUser(User usr){
		 
			User result=UsersList.remove(usr);
			if(result != null)
				return true;
			else
				return false;			
	 }
	 
	 public void sendUserList(Session target) throws IOException, EncodeException{
		 
		 ChatMessage messagetoclient = new ChatMessage("Users List",
					Type.USERLIST);
			for (User cUser : UsersList.values()) {
				if (cUser.GetState() == State.VISIBLE) {
					messagetoclient.getAdditionalParams().appendUser(
							cUser.GetNickname());
				}
			}
			sendMex(messagetoclient, target);
	 }
	
}