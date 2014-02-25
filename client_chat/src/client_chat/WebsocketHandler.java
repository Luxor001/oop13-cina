package client_chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

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
import client_chat.Controller.MessageBoxReason;
import client_chat.Model.connectionResult;

@ClientEndpoint(encoders = { MessageEncoder.class }, decoders = { MessageDecoder.class })
public class WebsocketHandler {

	/* Let's make this a sigleton class, shall we? */
	private final static WebsocketHandler wshandler = new WebsocketHandler();

	static Session ClientSession;
	private static Controller controller;
	public static String DEBUG_NICKNAME = System.getProperty("user.name");// "Lux"
																			// +
																			// Math.random();
	public static boolean RESET_FLAG_DELETE_ME = false;

	public final static Object monitor = 1;

	private WebsocketHandler() {

	}

	public static WebsocketHandler getWebSocketHandler() {
		return wshandler;
	}

	/**
	 * Method run at the successful connection beetween Client & Server
	 * 
	 * @throws EncodeException
	 * @throws InterruptedException
	 */
	@OnOpen
	public void onOpen(Session session) throws IOException, EncodeException,
			InterruptedException {
		// view.writeText("Connected to Channel : \"Main\"",0);

		/*
		 * waiting for application class to draw the interface, or i will be
		 * unable to write "Connected" and other funny things..
		 */
		/*
		 * synchronized (WebsocketHandler.class) {
		 * WebsocketHandler.class.wait(); }
		 */

		/* controller.showMessageMain("Connected!"); */

		System.out.println("Sending my INITIALIZE message..");

		ClientSession = session;
		if (RESET_FLAG_DELETE_ME) {
			ChatMessage message = new ChatMessage("reset", Type.RESETFLAG);
			SendMex(message);
			RESET_FLAG_DELETE_ME = false;
		} else {

			ChatMessage Message = new ChatMessage("hello", Type.INITIALIZE);
			Message.getAdditionalParams().setNickname(DEBUG_NICKNAME);
			Message.getAdditionalParams().SetVisibility(true);
			SendMex(Message); /* send my request of connection to the server */

			System.out.println("Sent!");
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
	public void onMessage(ChatMessage message, Session session)
			throws IOException, EncodeException, InterruptedException {

		if (message.getType() == Type.NICKNAMEUNAVAIABLE) {
			/* server refused me to connect */
			synchronized (WebsocketHandler.monitor) {
				Application.granted = false;
				WebsocketHandler.monitor.notifyAll();
			}

		}

		if (message.getType() == Type.CONNECTIONGRANTED) {
			/* i got a grant from server: now i can load the chat window */
			synchronized (WebsocketHandler.monitor) {
				Application.granted = true;
				WebsocketHandler.monitor.notifyAll();
			}

			synchronized (WebsocketHandler.monitor) {
				System.out.println("Waiting");
				monitor.wait();
				System.out.println("escaped");
			}
			SendMex(new ChatMessage("userlist", Type.USERLIST));

		}

		/* the app is stil loading the main chat: discard any new message */
		if (Application.loaded == false) {

			return;
		}
		if (message.getType() == Type.USERLIST) {

			for (String user : message.getAdditionalParams().getUsersList())
				controller.appendUser(user);

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

			String surl = "http://vallentinsource.com/globalip.php";
			URL url = new URL(surl);
			InputStreamReader inpstrmread = new InputStreamReader(
					url.openStream());
			BufferedReader reader = new BufferedReader(inpstrmread);
			String ip = reader.readLine();
			/* ## GOT IP (store to private static variable?) ## */

			// BISOGNA CONTROLLARE SE QUESTI JOPTIONPANE NON FERMANO IL THREAD.
			String senderNick = message.getAdditionalParams().getNickname();
			int choice = WebsocketHandler.controller.buildChoiceMessageBox(
					MessageBoxReason.REQUEST_PRIVATE_CHAT, senderNick);

			ChatMessage msg;
			if (choice == 0) {
				msg = new ChatMessage("Yes", Type.YESPRIVATECHAT);
				msg.getAdditionalParams().setNickname(senderNick);
				msg.getAdditionalParams().setIP(ip);
			} else {
				msg = new ChatMessage("No", Type.NOPRIVATECHAT);
			}
			SendMex(msg);
		}

		if (message.getType() == Type.YESPRIVATECHAT) {
			/* IP to connect on private chat */
			String iptoconnect = message.getAdditionalParams().getIP();
			Socket socket = new Socket(iptoconnect, 9998);

			File file = new File(System.getProperty("user.dir") + "/"
					+ System.getProperty("user.name") + "ServerKey.jks");
			String name = "";
			try {

				ObjectOutputStream oos = new ObjectOutputStream(
						socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(
						socket.getInputStream());

				oos.writeUTF(System.getProperty("user.name"));
				oos.flush();
				name = ois.readUTF();
				FileInputStream fileStream = new FileInputStream(file);
				byte[] buffer = new byte[10240];
				fileStream.read(buffer);
				oos.write(buffer);
				File receivedFile = new File(System.getProperty("user.dir")
						+ "/" + name + "ServerKey.jks");
				receivedFile.createNewFile();
				FileOutputStream outStream = new FileOutputStream(receivedFile);

				byte[] bufferReader = new byte[10240];
				ois.readFully(bufferReader);
				outStream.write(bufferReader);
				oos.close();
				ois.close();
				socket.close();
				fileStream.close();
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			controller.commandCreateTab(iptoconnect,
					System.getProperty("user.dir") + "/" + name
							+ "ServerKey.jks");
		}

		if (message.getType() == Type.REQUESTEDSENDFILE) {

			String senderNick = message.getAdditionalParams().getNickname();
			String fileName = message.getAdditionalParams().getFileName();
			int choice = WebsocketHandler.controller
					.buildChoiceMessageBox(
							MessageBoxReason.REQUEST_RECEIVE_FILE, senderNick,
							fileName);

			System.out.println(choice);
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
		controller.closeChat();

		// latch.countDown();
	}

	public void SendMex(ChatMessage Mex) throws IOException, EncodeException {
		ClientSession.getBasicRemote().sendObject(Mex);
	}

	public static void setController(Controller c) {
		controller = c;
	}

	private static CountDownLatch latch;

	public synchronized connectionResult AttemptConnection() throws IOException {

		latch = new CountDownLatch(1);
		ClientManager client = null;
		try {
			client = ClientManager.createClient();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// sockethandler = new WebsocketHandler();
		/* Attemp connection to web service */
		try {

			client.connectToServer(this, null, new URI(
					"ws://79.32.190.112:8080/ServerWebSocket/websocket"));
			/*
			 * client.connectToServer(WebsocketHandler.class, new URI(
			 * "ws://localhost:8080/ServerWebSocket/websocket"));
			 */

		} catch (Exception e) {
			if (e.getClass().isAssignableFrom(DeploymentException.class)) {
				return connectionResult.TIMEOUT;
			}
			if (e.getClass().isAssignableFrom(URISyntaxException.class)) {
				return connectionResult.BAD_URI;
			}
		}
		return connectionResult.OK;
	}
}
