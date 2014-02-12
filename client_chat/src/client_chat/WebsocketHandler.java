package client_chat;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.grizzly.http.server.Request;

import client_chat.ChatMessage.Type;

@ClientEndpoint(encoders = { MessageEncoder.class }, decoders = { MessageDecoder.class })
public class WebsocketHandler {
	static Session ClientSession;
	private static Controller controller;
	private static String DEBUG_NICKNAME="Lux"+Math.random();
	/**
	 * Method run at the successful connection beetween Client & Server
	 * 
	 * @throws EncodeException
	 */
	@OnOpen
	public void onOpen(Session session) throws IOException, EncodeException {
		// view.writeText("Connected to Channel : \"Main\"",0);

		controller.showMessageMain("Connected!");
		System.out.println("Sending my INITIALIZE message..");

		ClientSession = session;
		ChatMessage Message = new ChatMessage("hello", Type.INITIALIZE);
		Message.getAdditionalParams().setNickname(DEBUG_NICKNAME);
		Message.getAdditionalParams().SetVisibility(true);
		SendMex(Message); /* send my request of connection to the server */

		System.out.println("Sent!");
	}

	/**
	 * Method run at the receiving of a message from the server
	 * @throws EncodeException 
	 * @throws IOException 
	 * 
	 */
	@OnMessage
	public void onMessage(ChatMessage message, Session session) throws IOException, EncodeException {

		if (message.getType() == Type.USERLIST) {

			System.out
					.println("Server has responded! Number of current clients except me: "
							+ (message.getAdditionalParams().getUsersList()
									.size()));

			for (String user : message.getAdditionalParams().getUsersList())
				controller.appendUser(user);

			/*
			 * System.out.println("USERLIST MESSAGE TYPE, USERS:" +
			 * message.getAdditionalParams().getUsersList().get(0) + "\n" +
			 * message.getAdditionalParams().getUsersList().get(1));
			 */
		}

		if (message.getType() == Type.TEXT) {

			controller.showMessageMain(message.getAdditionalParams()
					.getNickname() + " : " + message.getMessage());
		}

		if (message.getType() == Type.NEWUSER) {

			controller.appendUser(message.getAdditionalParams().getNickname());
		}

		if (message.getType() == Type.USERDISCONNECTED) {

			controller.removeUser(message.getAdditionalParams().getNickname());
		}

		if (message.getType() == Type.REQUESTEDPRIVATECHAT) {

			String surl="http://vallentinsource.com/globalip.php";
			URL url=new URL(surl);
			InputStreamReader inpstrmread=new InputStreamReader(url.openStream());
			BufferedReader reader=new BufferedReader(inpstrmread);			
			String ip=reader.readLine();
			/*## GOT IP (store to private static variable?) ##*/
			
			System.out.println("MINE IP IS "+ip);
			
			//BISOGNA CONTROLLARE SE QUESTI JOPTIONPANE NON FERMANO IL THREAD.
			String senderNick=message.getAdditionalParams().getNickname();
			int choice=WebsocketHandler.controller.buildChoiceMessageBox("User "+senderNick+
					"wants to have a private chat with you", "Private Chat",
					new Object[]{"Yes","No Way!"},JOptionPane.WARNING_MESSAGE);			
			
			ChatMessage msg;
			if(choice == 0){
				msg=new ChatMessage("Yes",Type.YESPRIVATECHAT);
				msg.getAdditionalParams().setNickname(senderNick);		
				msg.getAdditionalParams().setIP(ip);
				/*##START LISTENING SERVER##*/
			}
			else{
				msg=new ChatMessage("No",Type.NOPRIVATECHAT);				
			}
			SendMex(msg);
		}
		
		if(message.getType() == Type.YESPRIVATECHAT){
			String iptoconnect=message.getAdditionalParams().getIP(); /*##IP TO CONNECT##*/
			System.out.println("IP TO CONNECT:"+message.getAdditionalParams().getIP());
			/*##CONNECT TO IP##*/
			
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
		System.out
				.println("Connection dropped, you might be kicked from the server");
		// latch.countDown();
	}

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

	public static void setController(Controller c) {
		controller = c;
	}
}
