package client_chat;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Application {

	public static void main(String[] args) throws IOException {

		Controller c = new Controller();
		View v = new View();
		Model m = new Model();
		c.setView(v);
		c.setModel(m);

		WebsocketHandler.setController(c);
		int result=m.AttemptConnection(); /*connects to webserver*/
		
		if(result == -1){
			int choice=v.buildChoiceMessageBox("Chat Channel is not responding,\nconnection failed","Connection Failed",
					new Object[]{"Reconnect", "Quit to Main"},
					JOptionPane.ERROR_MESSAGE);
			
			if(choice == 0){
				System.out.println("Recconect");
				/*Attempt reconnection*/
			}
		}
	}
}
