package client_chat;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import client_chat.ChatMessage.Type;


@ClientEndpoint(
		  encoders = { MessageEncoder.class }, 
		  decoders = { MessageDecoder.class }
	)
public class WebsocketHandler {
	static Session ClientSession;
	private static Controller controller;
	
	/**
	 * Method run at the successful connection beetween Client & Server
	 * 
	 * @throws EncodeException
	 */
	@OnOpen
	public void onOpen(Session session) throws IOException, EncodeException {
		//view.writeText("Connected to Channel : \"Main\"",0);

		controller.showMessageMain("Connected!");
		//controller.showMessageMain("Connected to WebServer!");
		//System.out.println("Connected to WebServer!");
		
		System.out.println("Sending my INITIALIZE message..");
		
		ClientSession = session;
		ChatMessage Message = new ChatMessage("hello", Type.INITIALIZE);
		Message.getAdditionalParams().setNickname("Lux");
		Message.getAdditionalParams().SetVisibility(true);
		SendMex(Message); /* send my request of connection to the server */

		System.out.println("Sent!");
	}

	/**
	 * Method run at the receiving of a message from the server
	 * 
	 */
	@OnMessage
	public void onMessage(ChatMessage message, Session session) {

		if (message.getType() == Type.USERLIST) {

			System.out.println("Server has responded! Number of current clients: "+(message.getAdditionalParams().getUsersList().size()+1));
/*			System.out.println("USERLIST MESSAGE TYPE, USERS:"
					+ message.getAdditionalParams().getUsersList().get(0)
					+ "\n"
					+ message.getAdditionalParams().getUsersList().get(1));*/
		}

		if (message.getType() == Type.TEXT) {
			System.out.println(message.getAdditionalParams().getNickname()
					+ " : " + message.getMessage());
		}

	}

	/**
	 * Method run during the closing attempt of connection of the Client
	 * 
	 * @throws EncodeException
	 * @throws IOException
	 * 
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason)
			throws IOException, EncodeException {
			//latch.countDown();
	}

	/**
	 * Method run by the entry-point of the application.
	 * 
	 * @throws IOException
	 * 
	 */
	

	public void SendMex(ChatMessage Mex) throws IOException, EncodeException {
		ClientSession.getBasicRemote().sendObject(Mex);
	}

	/* needed to intercept the action of closing window */
	class CustomWindowAdapter extends WindowAdapter {

		JFrame window = null;

		CustomWindowAdapter(JFrame window) {
			this.window = window;
		}

		// implement windowClosing method
		public void windowClosing(WindowEvent e) {
			try {
				ClientSession.close();
			} /* tells the server we're going out */
			catch (IOException e1) {
				e1.printStackTrace();
			}

			System.exit(0);
		}
	}
	
	public static void setController(Controller c){
		controller=c;
	}
}
