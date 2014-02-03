package client_chat;

import java.io.IOException;

public class Application {

	public static void main(String[] args) throws IOException {

		Controller c = new Controller();
		View v = new View();
		Model m = new Model();
		c.setView(v);
		c.setModel(m);

		WebsocketHandler.setController(c);
		m.Start(); /*connects to webserver*/
		
	}
}
