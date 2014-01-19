package client_chat;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;

import client_chat.ChatMessage.Type;


@ClientEndpoint(
		  encoders = { MessageEncoder.class }, 
		  decoders = { MessageDecoder.class }
	)
public class Controller implements ViewObserver {

	private ViewInterface view;
	private ModelInterface model;

	public Controller(){
	}

	public void setView(ViewInterface view) {
		this.view = view;
		this.view.attachViewObserver(this);
	}

	public void setModel(ModelInterface model) throws IOException {
		this.model = model;
		this.model.attachViewObserver(this);

	}

	public void commandSendMessage() {
		this.model.sendMessage((this.view.sendMessage()));
	}

	public void commandCloseTab(ActionEvent e) {
		view.closeTab(e);
	}

	public void commandCreateTab() {
		this.model.connectToServer(view.createTab(view.getTitle()));
	}

	public void commandReceiveMessage(String message, String title) {

		this.view.showMessage(message, title);
	}
	
	
	
	
	
	private static CountDownLatch latch;
	static Session ClientSession;

	/**
	 * Method run at the successful connection beetween Client & Server
	 * 
	 * @throws EncodeException
	 */
	@OnOpen
	public void onOpen(Session session) throws IOException, EncodeException {
		//view.writeText("Connected to Channel : \"Main\"",0);
		System.out.println("Connected to WebServer!");
		
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
	@SuppressWarnings("unchecked")
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
		latch.countDown();
	}

	/**
	 * Method run by the entry-point of the application.
	 * 
	 * @throws IOException
	 * 
	 */
	public void Start() throws IOException {

		latch = new CountDownLatch(1);
		ClientManager client = null;
		try {
			client = ClientManager.createClient();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* Attemp connection to web service */
		try {
			//view.writeText("Attempt to connect..",0);
			System.out.println("Attempt connection to webserver...");
			client.connectToServer(Controller.class, new URI(
					"ws://localhost:8080/ServerWebSocket/websocket"));

			latch.await();

		} catch (DeploymentException | URISyntaxException
				| InterruptedException e) {
			throw new RuntimeException(e);

		}
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

}
