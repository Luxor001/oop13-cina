package client_chat;

import java.io.IOException;

import client_chat.Model.connectionResult;

public class Application {

    private static SplashScreen splash;
    private static WebsocketHandler web;

    public static void main(String[] args) throws IOException {
	draw();
	//	web=new WebsocketHandler();
	//	splash=new SplashScreen();
    }

    public static void chat_initialization() throws IOException {

	new Thread() {
	    public void run() {

		connectionResult result = null;
		int userchoice = 0;
		try {
		    result = new WebsocketHandler().AttemptConnection();
		} catch (IOException e) {
		}

		/* connects to webserver */

		while (result == connectionResult.TIMEOUT && userchoice == 0) {

		    /*	userchoice = c.buildChoiceMessageBox(
		    			"Chat Channel is not responding," + "\nconnection failed",
		    			"Connection Failed", new Object[] { "Reconnect",
		    					"Quit to Main" }, JOptionPane.ERROR_MESSAGE);*/
		    System.out.print("Canale non visibile");

		    if (userchoice == 0)
			try {
			    result = web.AttemptConnection();
			} catch (IOException e) {
			}
		    if (userchoice == 1) {

		    }
		    /* splashscreen, needs to be implemented */
		}

		if (result == connectionResult.OK) {

		    //DISABLE ALL OTHER COMPONENTS..SHOW A CIRCLE LOADING

		    try {

			draw();
		    } catch (IOException e) {
		    }
		}

	    };
	}.start();

    }

    public static void draw() throws IOException {

	Model m = new Model();
	Controller c = new Controller();
	View v = new View();
	c.setView(v);
	c.setModel(m);

	WebsocketHandler.setController(c);

	synchronized (WebsocketHandler.class) {
	    WebsocketHandler.class.notify();
	}

	int a = 0;
	//splash.disposeFrame();
    }

}
