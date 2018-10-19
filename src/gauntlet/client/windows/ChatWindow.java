package gauntlet.client.windows;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import gauntlet.client.ClientMain;

public class ChatWindow extends JFrame implements KeyListener, WindowListener, FocusListener {
	
	private static final String ECH = "[Enter chat here]";
	private static final int MAX_LENGTH = 2000;
	
	private ClientMain main;
	private StringBuilder str = new StringBuilder();
	private JTextArea textarea = new JTextArea();
	private JTextField textbox = new JTextField(ECH);

	public ChatWindow(ClientMain _main) {
		super();

		main =_main;

		this.setTitle("TT Chat");//Statics.TITLE + " - Chat");
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(textarea), BorderLayout.CENTER);
		this.add(textbox, BorderLayout.SOUTH);
		this.setSize(300, 400);
		
		textarea.setLineWrap(true);
		textarea.setWrapStyleWord(true);
		textarea.setEditable(false);

		this.addWindowListener(this);
		textbox.addKeyListener(this);
		textbox.addFocusListener(this);
		
		textarea.setFont(main.window.font_small);
		textbox.setFont(main.window.font_small);
	}


	private void sendChat() {
		try {
			main.sendChat(this.textbox.getText());
			this.textbox.setText("");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}


	public void appendChat(String chat) {
		str.append(chat + "\n");
		
		while (str.length() > MAX_LENGTH) {
			int pos = str.indexOf("\n");
			str.delete(0, pos+1);
		}
		
		this.textarea.setText(str.toString());
		textarea.setCaretPosition(textarea.getDocument().getLength());

	}


	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == '\n') {//KeyEvent.VK_ENTER) {
			this.sendChat();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}


	@Override
	public void windowOpened(WindowEvent e) {
	}


	@Override
	public void windowClosing(WindowEvent e) {

	}


	@Override
	public void windowClosed(WindowEvent e) {

	}


	@Override
	public void windowIconified(WindowEvent e) {

	}


	@Override
	public void windowDeiconified(WindowEvent e) {

	}


	@Override
	public void windowActivated(WindowEvent e) {

	}


	@Override
	public void windowDeactivated(WindowEvent e) {

	}


	@Override
	public void focusGained(FocusEvent arg0) {
		if (this.textbox.getText().equalsIgnoreCase(ECH)) {
			this.textbox.setText("");
		}
		
	}


	@Override
	public void focusLost(FocusEvent e) {
		
	}

}
