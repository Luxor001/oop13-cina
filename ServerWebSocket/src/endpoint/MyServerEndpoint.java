package endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
	private ArrayList<User> UsersList=new ArrayList<User>();
	
	
	@OnOpen
	public void onOpen(Session newSession) throws IOException, EncodeException { // ## METODO CHIAMATO QUANDO UN CLIENT SI CONNETTE ##
				
	//	clientSessions.add(newSession);
	/*	ChatMessage Message=new ChatMessage("PROVA", Type.INITIALIZE);
		Message.appendAdditionalParams("Nickname", "Lux");
		SendMex(Message, newSession);*/
	}
	
	@OnClose
	public void onClose(Session clientsession) {
		User user=SearchUser(clientsession);
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
				SendGlobalMex(newmessage);				
			}
			
			/* send users list to the new client*/
			ChatMessage messagetoclient=new ChatMessage("Users List",Type.USERLIST); 
			for(User cUser:UsersList) //ESCLUDERE INVISIBILI!
				messagetoclient.getAdditionalParams().appendUser(cUser.GetNickname());
			SendMex(messagetoclient, clientsession);			
			
			UsersList.add(new User(UserNickname,State.VISIBLE, clientsession));			
		}
		
		if(message.getType() == Type.USERLIST){
			ChatMessage messagetoclient=new ChatMessage("Users List",Type.USERLIST);
			for(User cUser:UsersList)
				messagetoclient.getAdditionalParams().appendUser(cUser.GetNickname());
			
			SendMex(messagetoclient, clientsession);						
		}
		
		if(message.getType() == Type.TEXT)
			SendGlobalMex(message);
	}

	

	
	@OnError
	public void onError(Session clientsession, Throwable aThrowable) {
		System.out.println("Error : " + clientsession);

	}
		
	
	

	@Override
	public void contextInitialized(ServletContextEvent sce) { //Launched at the web app starting time.
        System.out.println("#### Inizializing Server for first launch.. ####");
		UsersList=new ArrayList<User>();
        System.out.println("#### Initialized.. ####");		
	}  
	

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}
	
	 public void SendMex(ChatMessage Mex,Session clientsession) 
			 throws IOException, EncodeException{    
		 	clientsession.getBasicRemote().sendObject(Mex);
	}
	 
	 public void SendGlobalMex(ChatMessage Mex) throws IOException, EncodeException{
		 for (User cUser: UsersList) {
			 SendMex(Mex,cUser.GetSession());		 
		 }
	 }
	 
	 public User SearchUser(Session session){
		 
		 for(User cUser:UsersList){
			 if(cUser.GetSession().equals(session))
				 return cUser;
		 }
		 return null;
		 
	 }
	
}