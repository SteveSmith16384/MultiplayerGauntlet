package mgs2.client.windows;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ssmith.awt.AWTFunctions;
import mgs2.client.ClientMain;
import mgs2.shared.Statics;

public class StartGameOptions extends JFrame implements ActionListener, WindowListener {

	private static final String FILENAME = "user.properties";
	private static final String SERVER = "server";
	private static final String NAME = "name";
	private static final String SOUND_ON = "sound_on";

	private ClientMain main;
	public boolean OKClicked = false;

	//private JTextField txt_server = new JTextField();
	private JComboBox txt_server = new JComboBox(new DefaultComboBoxModel(new String[] {"178.62.91.22", "127.0.0.1"}));
	private JTextField txt_name = new JTextField();
	private JCheckBox cb_sound = new JCheckBox();
	private JCheckBox cb_bot = new JCheckBox();

	public StartGameOptions(ClientMain _main) {
		super();

		main = _main;

		txt_server.setEditable(true);
		this.setResizable(false);

		this.setTitle(Statics.TITLE + " Options");
		this.setLayout(new GridLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(5, 2));

		panel.add(new JLabel("Version"));
		panel.add(new JLabel(Statics.CODE_VERSION + "/" + Statics.COMMS_VERSION));

		panel.add(new JLabel("Server"));
		panel.add(txt_server);
		//panel.add(new JLabel("Port"));
		//panel.add(txt_port);
		panel.add(new JLabel("Your Name"));
		panel.add(txt_name);
		panel.add(new JLabel("Sound On"));
		panel.add(cb_sound);
		if (Statics.DEBUG) {
			panel.add(new JLabel("Bot"));
			panel.add(cb_bot);
		}

		JButton submit = new JButton("Connect");
		panel.add(submit);
		submit.addActionListener(this);

		SwingUtilities.getRootPane(this).setDefaultButton(submit);

		this.add(panel);

		//this.pack();
		this.setSize(300, 150);
		AWTFunctions.CentreWindow(this);

		try {
			Properties props = new Properties();
			File f = new File(FILENAME);
			if (f.exists()) {
				InputStream inStream = new FileInputStream( f );
				props.load(inStream);
				inStream.close();
				this.txt_server.getModel().setSelectedItem(props.getProperty(SERVER));
				this.txt_name.setText(props.getProperty(NAME));
				this.cb_sound.setSelected(props.getProperty(SOUND_ON).equalsIgnoreCase("true"));
			} else {
				this.txt_server.getModel().setSelectedItem("178.62.91.22");

			}
		}
		catch (Exception e ) {
			e.printStackTrace();
		}

		this.addWindowListener(this);

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.txt_name.getText().length() > 0) {
		OKClicked = true;
		saveProperties();
		this.setVisible(false);
		synchronized (this) {
			notify();
		}
		} else {
			JOptionPane.showMessageDialog(this, "Please enter a name");
		}
	}


	public void saveProperties() {
		try {
			Properties props = new Properties();
			props.setProperty(SERVER, (String)this.txt_server.getSelectedItem());
			props.setProperty(NAME, this.txt_name.getText());
			props.setProperty(SOUND_ON, (this.cb_sound.isSelected()?"true":"false"));
			File f = new File(FILENAME);
			OutputStream out = new FileOutputStream( f );
			props.store(out, "User properties");
			out.close();
		}
		catch (Exception e ) {
			e.printStackTrace();
		}
	}


	public String getServer() {
		return (String)this.txt_server.getSelectedItem();
	}


	public String getName() {
		return this.txt_name.getText().trim();
	}


	public boolean isBot() {
		return this.cb_bot.isSelected();
	}


	public boolean isMute() {
		return this.cb_sound.isSelected() == false;
	}


	@Override
	public void windowActivated(WindowEvent arg0) {

	}


	@Override
	public void windowClosed(WindowEvent arg0) {
	}


	@Override
	public void windowClosing(WindowEvent arg0) {
		synchronized (this) {
			notify();
		}		
	}


	@Override
	public void windowDeactivated(WindowEvent arg0) {

	}


	@Override
	public void windowDeiconified(WindowEvent arg0) {

	}


	@Override
	public void windowIconified(WindowEvent arg0) {

	}


	@Override
	public void windowOpened(WindowEvent arg0) {

	}


}
