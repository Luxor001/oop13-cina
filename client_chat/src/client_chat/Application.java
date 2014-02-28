package client_chat;

import java.io.IOException;

import javax.swing.JOptionPane;

import client_chat.Model.connectionResult;

public class Application {

	private static SplashScreen splash;
	private static WebsocketHandler web;

	public static void main(String[] args) throws IOException {

		start();
	}

	public static void start() {
		splash = new SplashScreen();
	}

	public static void chat_initialization() throws IOException {

		web = WebsocketHandler.getWebSocketHandler();
		new Thread() {
			public void run() {

				connectionResult result = null;
				int userchoice = 0;

				do {

					splash.setVisibilityLoadingCircle(true);
					try {
						result = web.AttemptConnection();
					} catch (IOException e) {
					}

					if (result == connectionResult.TIMEOUT && userchoice == 0) {

						splash.setVisibilityLoadingCircle(false);
						userchoice = splash.buildChoiceMessageBox(
								"Chat Channel is not responding,"
										+ "\nconnection failed",
								"Connection Failed", new Object[] {
										"Reconnect", "Cancel" },
								JOptionPane.ERROR_MESSAGE);
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

	public static boolean granted = false;
	public static boolean loaded = false;

	public static void draw() throws IOException, InterruptedException {

		/* websockethandler has received response for INITIALIZE from server */
		synchronized (WebsocketHandler.monitor) {
			WebsocketHandler.monitor.wait();
			if (granted == false) {
				splash.nicknameInvalid();
				splash.setVisibilityLoadingCircle(false);
				return;

			}
		}

		splash.setVisibilityLoadingCircle(false);
		Model m = new Model();
		Controller c = new Controller();
		View v = new View();
		c.setView(v);
		c.setModel(m);

		WebsocketHandler.setController(c);

		/* chat window builded, websockethandler can now list the players. */
		synchronized (WebsocketHandler.monitor) {
			WebsocketHandler.monitor.notifyAll();
		}
		loaded = true;

		v.setVisible(true);

		splash.disposeFrame();

	}

}
