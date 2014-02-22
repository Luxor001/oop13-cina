package client_chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

public class Credits {

	private Dimension FRAME_SIZE=new Dimension(400,300);
	private String FRAME_TITLE="About CriptoChat";
	private String ABOUT_TXT="CryptoChat allows you to have private, encrypted SSL chat with other users."
			+ "\nAll the private informations (such as IP and private informations) are NOT \nstored in our main server,\n allowing you "
			+ "to have also an anonimous public chat.\n\nRemember: you and you are the real responsable of your privacy.\nDon't share "
			+ "your personal informations with anyone in public chat!";	
	private String CREDITS_TXT="CryptoChat© Version 1.0 Precise Fat Cat\n\n"
			+ "Copyright© Stefano Belli & Francesco Cozzolino,\nAll right reserved.";
	
	public Credits(){
	
		JFrame frame=new JFrame();
		frame.setSize(FRAME_SIZE);
		frame.setTitle(FRAME_TITLE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		JPanel pnl_main=new JPanel();
		pnl_main.setSize(FRAME_SIZE);
		frame.add(pnl_main);
		
		BorderLayout brd_layout=new BorderLayout();
		
		pnl_main.setLayout(brd_layout);
		JTextArea txt_credits=new JTextArea();
		

		txt_credits.setText(CREDITS_TXT);
		
		txt_credits.setBorder(new EmptyBorder( 16, 16, 16, 16 ));
		
		pnl_main.add(txt_credits,BorderLayout.NORTH);
		txt_credits.validate();
		
		
		frame.setResizable(false);
		frame.setVisible(true);
		
	}
	
	
}
