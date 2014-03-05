package client_chat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Preferences {
	
	private JFrame frame;
	private static String TITLE = "Preferences";
	private static Point frameSize = new Point(400, 300);
	private JLabel lbl_download_loc=new JLabel("Download Directory:");
	public Preferences(){
		frame=new JFrame();
		frame = new JFrame();
		frame.setTitle(TITLE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(frameSize.x, frameSize.y);
		
		frame.setLocationRelativeTo(null); /* centers jframe on screen. */
		
		
		
		JPanel pnl_main=new JPanel();
		pnl_main.setAlignmentY(Component.TOP_ALIGNMENT);
//		pnl_main.setAlignmentX(0.5f);
		pnl_main.setLayout(new BoxLayout(pnl_main, BoxLayout.Y_AXIS));
		frame.getContentPane().add(pnl_main);
		pnl_main.add(Box.createVerticalStrut(25));
		
		lbl_download_loc.setAlignmentX(0.1f);
		pnl_main.add(lbl_download_loc);
		
		
		JPanel pnl_loc=new JPanel();
		pnl_loc.setLayout(null);
		pnl_loc.setSize(300,30);
		pnl_loc.setPreferredSize(new Dimension(300,30));
		pnl_loc.setBackground(Color.WHITE);
		pnl_loc.setMaximumSize(new Dimension(300,20));
		pnl_loc.setMinimumSize(new Dimension(300,20));
		pnl_loc.setAlignmentX(0f);
		
		pnl_main.add(pnl_loc);
	/*	JButton btear=new JButton("");
		pnl_main.add(btear);*/
		/*
		frame.getContentPane().add(Box.createVerticalStrut(25));
		JPanel pnl_filler=new JPanel();
		pnl_filler.setSize(25, frameSize.y);
		frame.getContentPane().add(pnl_filler);
		*/
		/*
		JButton btn1=new JButton("asdda");
		btn1.setAlignmentY(Component.TOP_ALIGNMENT);
		btn1.setAlignmentX(Component.LEFT_ALIGNMENT);
		JButton btn2=new JButton("asdda");
		btn2.setAlignmentX(Component.LEFT_ALIGNMENT);
		btn2.setAlignmentY(Component.TOP_ALIGNMENT);
		frame.getContentPane().add(btn1);
		frame.getContentPane().add(btn2);
		*/
		/*
		frame.getContentPane().add(Box.createVerticalStrut(10));
		
		JPanel left=new JPanel();
		BoxLayout bleft=new BoxLayout(left,BoxLayout.Y_AXIS);
		left.setLayout(bleft);
		left.add(new JButton("left"));
		
		frame.getContentPane().add(left);*/
		frame.setVisible(true);
		
	}
}
