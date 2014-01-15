package client_chat;

public class Application {

	public static void main(String[] args) {

		Controller c = new Controller();
		View v = new View();
		Model m = new Model();
		c.setView(v);
		c.setModel(m);

	}
}
