package client;
/*
 * @(#)ClientExecutor.java        1.0 02/12/2013
 *
 * Belli Stefano 0000652935, Cozzolino Francesco 0000xxxxxxx
 * 
 */

import java.awt.BorderLayout;
import java.awt.TrayIcon.MessageType;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;


/* Classe Client che si occupa di gestire le interazioni con l'utente e di
 * gestire la comunicazione WebSocket con il Web Service.
 * 
 */


/* Those 3 initial lines are crucial!
 * As Described by the below classes, they handles the messages sent (and received)
 * from users and convert them back in JSON format.
 * This makes possible to send complex object trough the websocket protocol */
@ClientEndpoint(
	  encoders = { MessageEncoder.class }, 
	  decoders = { MessageDecoder.class }
)
public class ClientExecutor {

	 private static  CountDownLatch latch; 	 
	 static Session ClientSession;
	    
	 
	/**
	 * Method run at the successful connection beetween Client & Server
	 * @throws EncodeException 
	 */
	@OnOpen
	public void onOpen(Session session) throws IOException, EncodeException { 		
		Listmodel.addElement("Connected!");
		ClientSession = session;	
		SendMex(new ChatMessage("Visiblity:VisibileNick:Lux",ChatMessage.Type.INITIALIZE));
	}
	
	
	/**
	 * Method run at the receiving of a message from the server
	 * 
	 */	 
	@OnMessage
	public void onMessage(ChatMessage message, Session session) { 		
		Listmodel.addElement("SERVER SAYS:" + message);
	}

	
	/**
	 * Method run during the closing attempt of connection of the Client
	 * 
	 */	 
	@OnClose
	public void onClose(Session session, CloseReason closeReason) { 		
		latch.countDown();		
	}
	    
	
	/**
	 * Method run by the entry-point of the application.
	 * @throws IOException 
	 * 
	 */	 	
	public void Start() throws IOException { 

		DisegnaGUI();           /* Draws the User GUI and handles her events */
		latch = new CountDownLatch(1);
		ClientManager client = null ;
		try{
		 client = ClientManager.createClient();
		}
		catch(Exception e){e.printStackTrace();
		}
		/* Attemp connection to web service */
		try {
			Listmodel.addElement("Trying to connect..");
			client.connectToServer(ClientExecutor.class, new URI(
					"ws://localhost:8080/ServerWebSocket/websocket"));

			latch.await();

		} catch (DeploymentException | URISyntaxException
				| InterruptedException e) {
			throw new RuntimeException(e);

		}
	}
	    

    
    public void SendMex(ChatMessage Mex) throws IOException, EncodeException{    
    	ClientSession.getBasicRemote().sendObject(Mex);
    }
    
	    
	    
	    JList listbox; 
	    static DefaultListModel Listmodel;
	    JTextArea Input;
  	 JFrame frame;
	    public void DisegnaGUI(){
	    	frame=new JFrame();
	    	frame.setTitle("CLIENT");
	    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	frame.setSize(600,1000);
	    	
	    	JPanel topPanel = new JPanel();
			topPanel.setLayout( new BorderLayout() );
			frame.getContentPane().add( topPanel );

			Listmodel=new DefaultListModel();
	    	 listbox=new JList(Listmodel);

			
			topPanel.add( listbox, BorderLayout.NORTH );
			
			Input=new JTextArea();
	    	topPanel.add(Input,BorderLayout.SOUTH);
	        
	    	Input.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent arg0) {}
				
				@Override
				public void keyReleased(KeyEvent arg0) {}
				
				@Override
				public void keyPressed(KeyEvent arg0) {
					int a=arg0.getKeyCode();
					if(arg0.getKeyCode() == KeyEvent.VK_ENTER){
						try {
							SendMex(new ChatMessage(Input.getText(),
									ChatMessage.Type.TEXT));							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (EncodeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						Input.setText("");
					}
					
				}
			});
	    	
	    	frame.setVisible(true);	    	
	    }
	    	    
	    /*
	    @OnMessage 
	    public String onMessage(String message, Session session) { //method run at the receiving of a message from the server

	    	Listmodel.addElement("SERVER SAYS:"+message);
	    	return "";
	        // same as above
	    }
	 */
	    
}