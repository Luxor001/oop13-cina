package client_chat;

import java.io.IOException;

import javax.swing.JOptionPane;

import client_chat.Model.connectionResult;

public class Application {

	public static void main(String[] args) throws IOException {

		Controller c = new Controller();
		View v = new View();
		Model m = new Model();
		c.setView(v);
		c.setModel(m);

		WebsocketHandler.setController(c);

		connectionResult result;
		int userchoice = 0;
		result = m.AttemptConnection(); /* connects to webserver */

		while (result == connectionResult.TIMEOUT && userchoice == 0) {

			userchoice = v.buildChoiceMessageBox(
					"Chat Channel is not responding," + "\nconnection failed",
					"Connection Failed", new Object[] { "Reconnect",
							"Quit to Main" }, JOptionPane.ERROR_MESSAGE);

			if (userchoice == 0) /* retry */
				result = m.AttemptConnection();
			if (userchoice == 1)
				;
			/* splashscreen, needs to be implemented */
		}

	}
}
