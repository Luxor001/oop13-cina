package client_chat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Prefs {
	
	private JFrame frame;
	private static String TITLE = "Preferences";
	private static Point frameSize = new Point(370, 230);
	private JLabel lbl_download_loc=new JLabel("   Download Directory:");
	private JCheckBox chk_sounds;
	private JTextArea txt_defaultdownloadloc;
	private JTextArea txt_defaultnick;
	private JCheckBox chk_visibility;
	public enum PrefType{
		DEFAULTNICKNAME,
		DEFAULTPATH,
		DEFAULTVISIBILITY,
		DEFAULTSOUNDS
	}
	Preferences prefs=Preferences.userRoot();
	public Prefs(){
		frame=new JFrame();
		frame = new JFrame();
		frame.setTitle(TITLE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(frameSize.x, frameSize.y);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null); /* centers jframe on screen. */
		
		
		
		JPanel pnl_main=new JPanel();
		pnl_main.setAlignmentY(Component.TOP_ALIGNMENT);
		pnl_main.setLayout(new BoxLayout(pnl_main, BoxLayout.Y_AXIS));
		frame.getContentPane().add(pnl_main);
		pnl_main.add(Box.createVerticalStrut(25));
		
		lbl_download_loc.setAlignmentX(0.1f);
		lbl_download_loc.setMinimumSize(lbl_download_loc.getPreferredSize());
		lbl_download_loc.setMinimumSize(new Dimension(200,20));
		pnl_main.add(lbl_download_loc);		
		pnl_main.add(Box.createVerticalStrut(3));
		
		JPanel pnl_loc=new JPanel();
		pnl_loc.setLayout(new BoxLayout(pnl_loc, BoxLayout.X_AXIS));
		pnl_loc.setMaximumSize(new Dimension(300,20));
		pnl_loc.setMinimumSize(new Dimension(400,20));
		pnl_loc.setAlignmentX(0f);
		
		String dfaddress=prefs.get(
				Prefs.PrefType.DEFAULTPATH.toString(), "address..");		
		txt_defaultdownloadloc=new JTextArea(dfaddress);
		txt_defaultdownloadloc.setMinimumSize(new Dimension(300,15));
		txt_defaultdownloadloc.setEnabled(false);
		pnl_loc.add(txt_defaultdownloadloc);		
		pnl_loc.add(Box.createHorizontalStrut(10));
		final JButton btn_directory=new JButton("Browse..");
		btn_directory.setPreferredSize(new Dimension(80,20));
		btn_directory.setMargin(new Insets(5, 3, 5, 3));
		pnl_loc.add(btn_directory);
		
		
		btn_directory.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc=new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal=fc.showDialog(btn_directory, "Select");

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					prefs.put(Prefs.PrefType.DEFAULTPATH.toString(),
							fc.getSelectedFile().toString());
					txt_defaultdownloadloc.setText(fc.getSelectedFile().toString());
				}
			}
		});
		
		pnl_main.add(pnl_loc);
		pnl_main.add(Box.createVerticalStrut(15));
		
		
		

		JPanel pnl_sounds=new JPanel();
		pnl_sounds.setLayout(new BoxLayout(pnl_sounds, BoxLayout.X_AXIS));
		pnl_sounds.setMaximumSize(new Dimension(300,20));
		pnl_sounds.setMinimumSize(new Dimension(400,20));
		pnl_sounds.setAlignmentX(0f);
		JLabel lbl_sounds=new JLabel("Enable/Disable sounds");
		pnl_sounds.add(lbl_sounds);
		chk_sounds=new JCheckBox();
		boolean dfsounds=prefs.getBoolean(
				Prefs.PrefType.DEFAULTSOUNDS.toString(), true);	
		chk_sounds.setSelected(dfsounds);
		pnl_sounds.add(chk_sounds);
		
		pnl_main.add(pnl_sounds);
		pnl_main.add(Box.createVerticalStrut(15));
		
		


		JPanel pnl_defaultnick=new JPanel();
		pnl_defaultnick.setLayout(new BoxLayout(pnl_defaultnick, BoxLayout.X_AXIS));
		pnl_defaultnick.setMaximumSize(new Dimension(300,20));
		pnl_defaultnick.setMinimumSize(new Dimension(400,20));
		pnl_defaultnick.setAlignmentX(0f);
		

		JLabel lbl_defaultnick=new JLabel("Default Nickname:");
		pnl_defaultnick.add(lbl_defaultnick);

		pnl_defaultnick.add(Box.createHorizontalStrut(5));
		
		txt_defaultnick = new JTextArea();
		txt_defaultnick.setMinimumSize(new Dimension(100, 20));		
		txt_defaultnick.setMaximumSize(new Dimension(100, 20));

		dfaddress=prefs.get(
				Prefs.PrefType.DEFAULTNICKNAME.toString(), WebsocketHandler.NICKNAME);		
		txt_defaultnick.setText(dfaddress);
		pnl_defaultnick.add(txt_defaultnick);
		
		pnl_main.add(pnl_defaultnick);
		pnl_main.add(Box.createVerticalStrut(15));
		

		
		

		JPanel pnl_defaultvisibility=new JPanel();
		pnl_defaultvisibility.setLayout(new BoxLayout(pnl_defaultvisibility, BoxLayout.X_AXIS));
		pnl_defaultvisibility.setMaximumSize(new Dimension(300,20));
		pnl_defaultvisibility.setMinimumSize(new Dimension(400,20));
		pnl_defaultvisibility.setAlignmentX(0f);
		JLabel lbl_defaultvisibility=new JLabel("Visible at Login");
		pnl_defaultvisibility.add(lbl_defaultvisibility);
		chk_visibility=new JCheckBox();
		boolean dfvisibility=prefs.getBoolean(
				Prefs.PrefType.DEFAULTVISIBILITY.toString(), true);	
		chk_visibility.setSelected(dfvisibility);
		pnl_defaultvisibility.add(chk_visibility);
		
		pnl_main.add(pnl_defaultvisibility);
		
		prefs.put("value", "2");
		
		frame.addWindowListener(new CustomWindowAdapter(frame));
		frame.setVisible(true);		
	}
	
	class CustomWindowAdapter extends WindowAdapter {

		JFrame window = null;

		CustomWindowAdapter(JFrame window) {
			this.window = window;
		}

		// implement windowClosing method
		public void windowClosing(WindowEvent e) {
			
			prefs.put(Prefs.PrefType.DEFAULTNICKNAME.toString(),
					txt_defaultnick.getText());
			prefs.put(Prefs.PrefType.DEFAULTPATH.toString(),
					txt_defaultdownloadloc.getText());
			prefs.putBoolean(Prefs.PrefType.DEFAULTSOUNDS.toString(),
					chk_sounds.isSelected());
			prefs.putBoolean(Prefs.PrefType.DEFAULTVISIBILITY.toString(),
					chk_visibility.isSelected());	
			
			
		}
	}
}

/*
 * Preferences pref=Preferences.systemRoot();
 *  String dfaddress=prefs.get( Preferences.PrefType.DEFAULTPATH.toString(), "address..");*/
