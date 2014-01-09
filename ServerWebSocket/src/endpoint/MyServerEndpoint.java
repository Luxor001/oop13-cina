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
	public void onOpen(Session aClientSession) throws IOException, EncodeException { // ## METODO CHIAMATO QUANDO UN CLIENT SI CONNETTE ##
				
		clientSessions.add(aClientSession);

		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		aClientSession.getBasicRemote().sendObject(new ChatMessage("PROVA", Type.INITIALIZE));
	}
	
	@OnClose
	public void onClose(Session aClientSession) {
		clientSessions.remove(aClientSession);
	}

	
	@OnMessage
	public void onMessage(String message, Session client) throws IOException,
			EncodeException {

		
		// send data to all connected clients (including caller)
		for (Session clientSession : clientSessions) {
//			if (message.equals("Open Sesame")) {
//
			//JsonObjectBuilder builder;
			
			
			//				JsonObjectBuilder builder = Json.createObjectBuilder();
//				builder.add("person",
//						Json.createObjectBuilder().add("firstName", "Michael")
//								.add("lastName", "Jo"));
//				JsonObject result = builder.build();
//				// StringWriter sw = new StringWriter();
//				// try(JsonWriter writer = Json.createWriter(sw))
//				// {
//				// writer.writeObject(result);
//				// }
//				//
//				message = result.toString();
//
			
//			}
			
		
			//clientSession.getBasicRemote().sendText(message);
			System.out.print("OnMessageServer:"+message);

		}
	}

	

	
	@OnError
	public void onError(Session aclientSession, Throwable aThrowable) {
		System.out.println("Error : " + aclientSession);

	}
		
	
	

	@Override
	public void contextInitialized(ServletContextEvent sce) { //Launched at the web app starting time.
        System.out.println("INIZIOOOOOOOOOOOOOOOOOOOOOOOOO");
		UsersList=new ArrayList<User>();
	}  
	

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

	
}