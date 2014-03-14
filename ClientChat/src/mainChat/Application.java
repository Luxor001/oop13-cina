package mainChat;

import preferences.User;
import additionalFrames.SplashScreen;
import webSocket.WebsocketHandler;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import webSocket.WebsocketHandler.connectionResult;

public class Application {

	private static SplashScreen splash;
	private static WebsocketHandler web;
	private static String programName = "Cryptochat";
	private static String ERROR_DUPLICATE_STRING = "Only one instance of "
			+ "Cryptochat\n can be run at time";
	public static boolean granted = false;
	public static boolean loaded = false;

	public static void main(String[] args) throws IOException {

		boolean alreadyRunning;

		try {
			JUnique.acquireLock(programName);
			alreadyRunning = false;
		} catch (AlreadyLockedException e) {
			alreadyRunning = true;
		}
		if (!alreadyRunning) {
			start(); // Start sequence here
		}else{

			JOptionPane.showMessageDialog(new JFrame(), ERROR_DUPLICATE_STRING,
					"Error!", JOptionPane.WARNING_MESSAGE);
		}

	}

	public static void start() throws IOException {
		User.loadConfigFile();
		splash = new SplashScreen();
	}

	public static void chat_initialization() throws IOException {
		splash.setFrameEnabled(false);
		web = WebsocketHandler.getWebSocketHandler();
		new Thread() {
			public void run() {

				connectionResult result = null;
				int userchoice = 0;
				splash.setVisibilityLoadingCircle(true);

				do {

					try {
						result = web.AttemptConnection();
					} catch (IOException e) {
					}

					if (result == connectionResult.TIMEOUT && userchoice == 0) {

						splash.setVisibilityLoadingCircle(false);
						
						userchoice = splash.buildChoiceMessageBox(
								"Chat Channel is not responding,\nconnection failed",
								"Connection Failed", new Object[] {"Reconnect", "Cancel" },
								JOptionPane.ERROR_MESSAGE);
						
						splash.setFrameEnabled(true);
					}
				} while ((result == connectionResult.TIMEOUT && userchoice == 0));

				if (result == connectionResult.OK) {
					try {
						draw();
					} catch (Exception e) {
					}
				}

			};
		}.start();

	}


	public static void draw() throws IOException, InterruptedException {

		/* websockethandler has received response for INITIALIZE from server
		 * let's build the GUI */
		synchronized (WebsocketHandler.monitor) {
			WebsocketHandler.monitor.wait();
			if (granted == false) { /* the server rejected the connection*/
				splash.nicknameInvalid();
				splash.setVisibilityLoadingCircle(false);
				splash.setFrameEnabled(true);
				return;

			}
		}
		splash.setVisibilityLoadingCircle(false);
		Model m = new Model();
		//m.getSocketPorts();
		Controller c = new Controller();
		View v = new View();
		c.setView(v);
		c.setModel(m);

		WebsocketHandler.setController(c);

		/* the GUI has been builded, websockethandler can now list the players. */
		synchronized (WebsocketHandler.monitor) {
			WebsocketHandler.monitor.notifyAll();
		}
		loaded = true;
		splash.setFrameEnabled(true);

		v.setVisible(true);

		splash.disposeFrame();

	}

}
