package endpoint;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Init;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import endpoint.ChatMessage.Param;
import endpoint.ChatMessage.Type;




@ServerEndpoint(   //per provare la chat,C:\glassfish4\glassfish\domains\domain1\eclipseApps\WebSocketJEE7Demo
		value = "/websocket",
		encoders = { MessageEncoder.class }, 
		decoders = { MessageDecoder.class }
)
public class MyServerEndpoint implements ServletContextListener{ //http://mjtoolbox.wordpress.com/2013/06/20/websocket-chat-application-using-jee-7-with-glassfish-4-0/

	
	
	private static final Set<Session> clientSessions = Collections
			.synchronizedSet(new HashSet<Session>());	
	private ArrayList<User> UsersList;
	
	
	@OnOpen
	public void onOpen(Session newSession) throws IOException, EncodeException { // ## METODO CHIAMATO QUANDO UN CLIENT SI CONNETTE ##
				
		clientSessions.add(newSession);
	/*	ChatMessage Message=new ChatMessage("PROVA", Type.INITIALIZE);
		Message.appendAdditionalParams("Nickname", "Lux");
		SendMex(Message, newSession);*/
	}
	
	@OnClose
	public void onClose(Session aClientSession) {
		clientSessions.remove(aClientSession);
	}

	
	@OnMessage
	public void onMessage(ChatMessage message, Session client) throws IOException,
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
			messagetoclient.getAdditionalParams().appendUser("Tizio");
			messagetoclient.getAdditionalParams().appendUser("Caio");
			messagetoclient.getAdditionalParams().appendUser("Sempronio");
			
			SendMex(messagetoclient, client);			
		}		
	}

	

	
	@OnError
	public void onError(Session aclientSession, Throwable aThrowable) {
		System.out.println("Error : " + aclientSession);

	}
		
	
	

	@Override
	public void contextInitialized(ServletContextEvent sce) { //Launched at the web app starting time.
        System.out.println("#### Inizializing Server for first launch.. ####");
		UsersList=new ArrayList<User>();
	}  
	

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}
	
	 public void SendMex(ChatMessage Mex,Session currsession) 
			 throws IOException, EncodeException{    
	    	currsession.getBasicRemote().sendObject(Mex);
	}
	 
	 public void SendGlobalMex(ChatMessage Mex) throws IOException, EncodeException{
		 for (Session clientSession : clientSessions) {
			 SendMex(Mex,clientSession);		 
		 }
	 }
	
}