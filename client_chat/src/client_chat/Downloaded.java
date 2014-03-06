package client_chat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.text.BadLocationException;

public class Downloaded {

	// private List<Pair<Pair<String, Integer>, JPanel>> fileReferences = new
	// LinkedList<>();

	private Map<String, JPanel> fileReferences = new HashMap<>();

	private final JPanel pnl_main;
	private final JFrame frame;
	private JButton clearbutton = null;
	private boolean background_color = true;
	private Color LIGHT_LIGHT_GRAY = new Color(243, 243, 243);
	private Color ALMOST_WHITE = new Color(251, 251, 251);
	private Dimension frameSize = new Dimension(400, 85);

	public Downloaded() throws BadLocationException {
		frame = new JFrame();
		frame.setSize(frameSize);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setTitle("Downloads");
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(
				new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		pnl_main = new JPanel();
		pnl_main.setBackground(Color.WHITE);
		frame.getContentPane().add(pnl_main);
		BoxLayout a = new BoxLayout(pnl_main, BoxLayout.Y_AXIS);
		pnl_main.setLayout(a);
		pnl_main.setAlignmentX(Component.LEFT_ALIGNMENT);

		frame.add(Box.createVerticalStrut(10));
		JPanel btnpnl = new JPanel();
		btnpnl.setLocation(10, 10);
		btnpnl.setLayout(null);

		clearbutton = new JButton("clear");
		clearbutton.setSize(clearbutton.getPreferredSize());
		clearbutton.setMaximumSize(clearbutton.getPreferredSize());
		clearbutton.setLocation(10, 0);
		btnpnl.add(clearbutton);
		btnpnl.setSize(btnpnl.getPreferredSize());
		btnpnl.setPreferredSize(new Dimension(30, 30));
		frame.add(btnpnl);

		clearbutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clear();

			}
		});

		frame.setBackground(Color.WHITE);

	}

	private JPanel buildFilePanel(String fileName, int max) {

		JPanel container = new JPanel();
		container.setAlignmentX(Component.LEFT_ALIGNMENT);
		container.setMaximumSize(new Dimension(400, 55));
		container.setLayout(null);
		container.setName("container");
		if (background_color == false) {
			container.setBackground(LIGHT_LIGHT_GRAY);
		} else {
			container.setBackground(ALMOST_WHITE);
		}
		background_color = !background_color;

		JLabel fileicon = new JLabel();
		fileicon.setIcon(new ImageIcon("resources/file.png"));
		fileicon.setLocation(5, 10);
		fileicon.setSize(fileicon.getPreferredSize());
		fileicon.setName("fileicon");
		container.add(fileicon);

		JLabel filename = new JLabel(fileName);
		filename.setLocation(50, 15);
		filename.setSize(filename.getPreferredSize());
		filename.setName("filename");
		container.add(filename);

		JProgressBar progressbar = new JProgressBar(0, max);
		progressbar.setStringPainted(true);
		progressbar.setLocation(50, 35);
		progressbar.setSize(200, 15);

		progressbar.setName("progressbar");

		container.add(progressbar);

		container.setPreferredSize(new Dimension(400, 55));

		return container;
	}

	public static class FileReference {
		public FileReference() {

		}
	}

	public boolean updateProgressBar(String user, int id, int percentage) {
		JPanel pnl = null;

		synchronized (this) {
			String key = user + id;
			pnl = fileReferences.get(key);
		}
		if (pnl != null) {
			for (Component ccommp : pnl.getComponents()) {
				if (ccommp.getName() == "progressbar") {
					JProgressBar bar = (JProgressBar) ccommp;

					bar.setValue(bar.getValue() + percentage);
				}

			}
			return true;
		}

		return false;

	}

	public synchronized void addFile(String user, int id, String name, int max) {

		String key = user + id;
		JPanel local_pnl = buildFilePanel(name, max);
		fileReferences.put(key, local_pnl);
		// fileReferences.add(new Pair<>(new Pair<>(user, id), local_pnl));
		pnl_main.add(local_pnl);
		resizeFrame();
	}

	public boolean showFrame(boolean show) {
		if (fileReferences.size() == 0) {
			JFrame emptyframe=new JFrame();
			emptyframe.setSize(new Dimension(350,200));
			emptyframe.setLocationRelativeTo(null);
			emptyframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			emptyframe.setVisible(true);
			JOptionPane.showMessageDialog(emptyframe,"No file to show!",
					"Error!",JOptionPane.ERROR_MESSAGE);
			emptyframe.dispose();
			return false;
		} else {
			frame.setVisible(show);
			return true;
		}
	}

	private void resizeFrame() {

		if (fileReferences.size() == 0) {
			frame.setSize(100, 100);
		} else {
			frame.pack();
			frame.setSize(new Dimension(pnl_main.getSize().width, frame
					.getSize().height));
		}
	}

	private synchronized void clear() {

		int i = 0;

		for (Container a : fileReferences.values()) {
			for (Component child : a.getComponents()) {
				if (child.getName() == "progressbar") {
					JProgressBar progress = (JProgressBar) child;
					if (progress.getValue() == progress.getMaximum()) {
						pnl_main.remove(a);
						fileReferences.remove(a);
					}
				}
			}
		}

		// redraw_colors();

		resizeFrame();
	}

	public boolean isVisible() {
		return frame.isVisible();
	}

}
