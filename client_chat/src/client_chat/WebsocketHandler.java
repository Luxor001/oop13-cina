package client_chat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
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
	// public static String NICKNAME = System.getProperty("user.name");// "Lux"
	// +
	// Math.random();
	public static boolean VISIBLE_FLAG = true;
	public static boolean RESET_FLAG_DELETE_ME = false;
	private long PING_INTERVAL_SECONDS = 15;
	public static String UNABLE_VISIBLE_MESSAGE = "You can't perform this operation"
			+ " while you are invisible: please login as visible and repeat"
			+ " this operation";

	public final static Object monitor = 1;
	public Timer timer;

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

		System.out.println("Sending my INITIALIZE message..");

		ClientSession = session;
		if (RESET_FLAG_DELETE_ME) {
			ChatMessage message = new ChatMessage("reset", Type.RESETFLAG);
			SendMex(message);
			RESET_FLAG_DELETE_ME = false;
		} else {

			ChatMessage Message = new ChatMessage("hello", Type.INITIALIZE);
			Message.getAdditionalParams().setNickname(User.getNickName());
			// Message.getAdditionalParams().setNickname(NICKNAME);
			Message.getAdditionalParams().SetVisibility(VISIBLE_FLAG);
			SendMex(Message); /* send my request of connection to the server */

			System.out.println("Sent!");

			timer = new Timer();
			timer.schedule(new PingTimer(), PING_INTERVAL_SECONDS * 1000,
					PING_INTERVAL_SECONDS * 1000);

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

			final String ip = User.getIp();

			final String senderNick = message.getAdditionalParams()
					.getNickname();

			new Thread() {

				public void run() {

					int choice = WebsocketHandler.controller
							.buildChoiceMessageBox(
									MessageBoxReason.REQUEST_PRIVATE_CHAT,
									senderNick);

					ChatMessage msg;
					if (choice == 0) {
						msg = new ChatMessage("Yes", Type.YESPRIVATECHAT);
						msg.getAdditionalParams().setNickname(senderNick);
						msg.getAdditionalParams().setIP(ip);
						msg.getAdditionalParams().setSSLPort(
								User.getPortSSL() + "");
						msg.getAdditionalParams().setKEYPort(
								User.getPortKeyStore() + "");
					} else {
						msg = new ChatMessage("No", Type.NOPRIVATECHAT);
						msg.getAdditionalParams().setNickname(senderNick);
					}
					try {
						SendMex(msg);
					} catch (Exception e) {
					}
				};

			}.start();

		}

		if (message.getType() == Type.YESPRIVATECHAT) {

			System.out.println("RECEIVED PORT SSL:"
					+ message.getAdditionalParams().getSSLPort());
			System.out.println("RECEIVED PORT KEY:"
					+ message.getAdditionalParams().getKEYPort());
			/* IP to connect on private chat */
			String iptoconnect = message.getAdditionalParams().getIP();
			String portKey = message.getAdditionalParams().getKEYPort();
			String name = Client.ObtainKeyStore(iptoconnect,
					Integer.parseInt(portKey), "Web Server");

			if (name != null) {
				String port = message.getAdditionalParams().getKEYPort();
				controller.commandCreateTab(iptoconnect,
						Integer.parseInt(port), name,
						System.getProperty("user.dir") + "/" + name
								+ "ServerKey.jks");
			} else {
				controller.commandRefusedChat(message.getAdditionalParams()
						.getNickname());
			}

		}

		if (message.getType() == Type.YESSENDFILE) {

			System.out.println("RECEIVED PORT SSL:"
					+ message.getAdditionalParams().getSSLPort());
			System.out.println("RECEIVED PORT KEY:"
					+ message.getAdditionalParams().getKEYPort());
			System.out.println(message.getAdditionalParams().getFileName());
			int a = 0;
			String ip = message.getAdditionalParams().getIP();
			String filename = message.getAdditionalParams().getFileName();
			String name = message.getAdditionalParams().getNickname();

			if (controller.exist(name) == null) {
				String portKey = message.getAdditionalParams().getKEYPort();
				name = Client.ObtainKeyStore(ip, Integer.parseInt(portKey),
						"Web Server");
				if (name != null) {
					String iptoconnect = message.getAdditionalParams().getIP();
					String port = message.getAdditionalParams().getKEYPort();
					controller.commandCreateTab(iptoconnect,
							Integer.parseInt(port), name,
							System.getProperty("user.dir") + "/" + name
									+ "ServerKey.jks");
				} else {
					controller.showMessageMain("Error during send of file");
				}
			}

			if (name != null) {
				controller.sendFile(filename, name);
			}

		}

		if (message.getType() == Type.NOPRIVATECHAT) {
			System.out.println("NO RECEIVED");
			controller.commandRefusedChat(message.getAdditionalParams()
					.getNickname());
		}

		if (message.getType() == Type.REQUESTEDSENDFILE) {

			final String ip = User.getIp();
			final String senderNick = message.getAdditionalParams()
					.getNickname();
			final String path = message.getAdditionalParams().getFileName();
			final String fileName = path.substring(path.lastIndexOf("\\") + 1);

			new Thread() {

				public void run() {

					int choice = WebsocketHandler.controller
							.buildChoiceMessageBox(
									MessageBoxReason.REQUEST_RECEIVE_FILE,
									senderNick, fileName);

					ChatMessage msg;
					if (choice == 0) {
						msg = new ChatMessage("Yes", Type.YESSENDFILE);
						msg.getAdditionalParams().setNickname(senderNick);
						msg.getAdditionalParams().setIP(ip);
						msg.getAdditionalParams().setFileName(path);
						msg.getAdditionalParams().setSSLPort(
								User.getPortSSL() + "");
						msg.getAdditionalParams().setKEYPort(
								User.getPortKeyStore() + "");
					} else {
						msg = new ChatMessage("No", Type.NOSENDFILE);
						msg.getAdditionalParams().setNickname(senderNick);
					}
					try {
						SendMex(msg);
					} catch (Exception e) {
					}

				}

			}.start();

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

		/* closing connection from the SERVER */
		if (closeReason.equals(CloseReason.CloseCodes.CLOSED_ABNORMALLY)) {
			controller.closeChat();
			System.out
					.println("Connection dropped, you might be kicked from the server");
		}

		/* closing connection from the client */
		if (closeReason.equals(CloseReason.CloseCodes.GOING_AWAY)) {

		}
		// latch.countDown();
	}

	public void SendMex(ChatMessage Mex) throws IOException, EncodeException {
		if (!VISIBLE_FLAG) {
			if (Mex.getType() == Type.REQUESTSENDFILE
					|| Mex.getType() == Type.REQUESTPRIVATECHAT
					|| Mex.getType() == Type.TEXT) {
				controller.showMessageMain(UNABLE_VISIBLE_MESSAGE);
				return;
			}
		}
		ClientSession.getBasicRemote().sendObject(Mex);
	}

	public boolean isConnected() {
		if (ClientSession != null) {
			return ClientSession.isOpen();
		} else {
			return false;
		}
	}

	public void closeConnection() {
		try {
			ClientSession.close(new CloseReason(
					CloseReason.CloseCodes.GOING_AWAY, "normal"));
		} catch (IOException e) {
		}
	}

	public static void setController(Controller c) {
		controller = c;
	}

	/*
	 * public String getIP() throws IOException {
	 * 
	 * String surl = "http://vallentinsource.com/globalip.php"; URL url = new
	 * URL(surl); InputStreamReader inpstrmread = new
	 * InputStreamReader(url.openStream()); BufferedReader reader = new
	 * BufferedReader(inpstrmread); String ip = reader.readLine(); return ip; }
	 */

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
					"ws://192.168.1.16:8080/ServerWebSocket/websocket"));
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

	public class PingTimer extends TimerTask {

		@Override
		public void run() {
			try {
				if (WebsocketHandler.getWebSocketHandler().isConnected()) {
					WebsocketHandler.getWebSocketHandler().SendMex(
							new ChatMessage("alive", ChatMessage.Type.PING));

					System.out.println("PING!");
				} else {
					System.out.println("Abort!");
					this.cancel();

				}
			} catch (Exception e) {
			}
		}
	}
}
