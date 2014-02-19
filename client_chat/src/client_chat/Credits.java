package client_chat;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class Credits {

	private Dimension FRAME_SIZE=new Dimension(400,300);
	private String FRAME_TITLE="About CriptoChat";
	private String CREDITS_TXT="CryptoChat\n \n Version 1.0\n\n(c) Copyright Stefano Belli & Francesco Cozzolino";	
	public Credits(){
	
		JFrame frame=new JFrame();
		frame.setSize(FRAME_SIZE);
		frame.setTitle(FRAME_TITLE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		JPanel pnl_main=new JPanel();
		BorderLayout brd_layout=new BorderLayout();
		
		
		pnl_main.setLayout(brd_layout);
		JTextArea txt_credits=new JTextArea(CREDITS_TXT);
		//txt_credits.setSize(txt_credits.getPreferredSize());
		txt_credits.setBorder(new EmptyBorder( 16, 16, 16, 16 ));
		pnl_main.add(txt_credits,BorderLayout.NORTH);
		
		frame.add(pnl_main);
		
		frame.setResizable(false);
		frame.setVisible(true);
		
	}
	
	
}
