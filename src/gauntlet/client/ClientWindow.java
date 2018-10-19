package gauntlet.client;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;

import gauntlet.client.datastructs.SimpleGameData;
import gauntlet.client.datastructs.SimplePlayerData;
import gauntlet.client.modules.AbstractModule;
import gauntlet.client.modules.GameModule;
import gauntlet.client.modules.PleaseWaitModule;
import gauntlet.client.modules.SelectUnitTypeModule;
import mgs2.shared.GameStage;
import mgs2.shared.Statics;
import ssmith.awt.BufferedImageCache;
import ssmith.awt.BufferedImageCache.ShadeType;

public final class ClientWindow extends JFrame implements KeyListener, WindowListener, MouseListener, MouseMotionListener {

	public enum Stage {
		SELECT_UNIT_TYPE, PLEASE_WAIT, PLAY_GAME;
	}

	private BufferStrategy BS;
	public boolean keys[] = new boolean[525]; // What keys are currently being held down.
	private BufferedImageCache images;
	public MessageBox msg_box = new MessageBox();
	public ClientMain main;
	public Point last_mouse_pos = new Point();
	public GameModule game_module;
	private AbstractModule module;
	public String str_time = "", str_respawn_time = "";

	public static Font font_large, font_normal, font_small;

	public ClientWindow(ClientMain _main) throws IOException {
		super();

		main = _main;

		this.setTitle(Statics.TITLE);
		this.addKeyListener(this);
		this.addWindowListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		this.setSize(Statics.WINDOW_SIZE);
		this.setResizable(false);
		this.setVisible(true);
		this.createBufferStrategy(2);
		BS = this.getBufferStrategy();
		images = new BufferedImageCache(this);

		game_module = new GameModule(main, this); 
		this.setModule(Stage.PLEASE_WAIT);

		Font font = null;
		String FONT = "hemihead.ttf";
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("./client_data/fonts/" + FONT));
		} catch (Exception e) {
			ClassLoader cl = ClientMain.class.getClassLoader();
			InputStream is = cl.getResourceAsStream("client_data/fonts/" + FONT);
			if (is != null) {
				try {
					font = Font.createFont(Font.TRUETYPE_FONT, is);
				} catch (FontFormatException e2) {
					e2.printStackTrace();
				}
			}
		}
		if (font != null) {
			font_large = font.deriveFont(48f);
			font_normal = font.deriveFont(25f);
			font_small = font.deriveFont(22f);
		}

	}


	public void setMap(byte mapw, byte maph) {
		game_module.sprite_grid = new ClientSpriteGrid(mapw, maph);

	}


	public void gameLoop(long interpol) throws IOException {
		Graphics g = BS.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, Statics.WINDOW_SIZE.width, Statics.WINDOW_SIZE.height);

		module.gameLoop(g, interpol);

		g.setColor(Color.white);
		g.setFont(font_normal);

		this.msg_box.paint(g, 20, Statics.WINDOW_SIZE.height - 80);

		if (keys[KeyEvent.VK_Q]) {
			// Draw player details
			g.setFont(font_small);
			g.setColor(Color.yellow);
			if (main.game_data != null) {
				SimpleGameData game_data = main.game_data;
				g.drawString("Game #" + game_data.id, 220, 90);
				int y_pos = 120;
				//for (byte side = 1 ; side<=game_data.getNumSides(); side++) {
					//g.drawString("SIDE " + side, 220, y_pos);
					y_pos += 30;
					for (SimplePlayerData playerdata : game_data.players)  {
						g.drawString(playerdata.name + " Score:" + playerdata.score + " Wins:" + playerdata.victories + "  Ping:" + playerdata.ping, 220, y_pos);
						y_pos += 30;
						//num++;
					}
				//}
			} else {
				this.msg_box.addText("No game data to show"); 
			}
		}
		if (keys[KeyEvent.VK_COMMA]) {
			keys[KeyEvent.VK_COMMA] = false;
			// Unit type selection
			if (this.game_module.getPlayer().getHealth() <= 0) {
				this.setModule(Stage.SELECT_UNIT_TYPE);
			} else if (this.main.game_data != null && this.main.game_data.game_stage != GameStage.STARTED) {
				this.setModule(Stage.SELECT_UNIT_TYPE);
			} else {
				this.msg_box.addText("You can only change unit when dead or prior to start");
			}
		}
		if (keys[KeyEvent.VK_C]) {
			keys[KeyEvent.VK_C] = false;
			if (main.chat.isVisible() == false) {
				main.chat.setVisible(true);
				main.chat.setLocation(this.getX() + this.getWidth(), this.getY());
				main.chat.setSize(300, 400);
				//main.chat.pack();
			} else {
				main.chat.setVisible(false);
			}
		}

		if (Statics.DEBUG || Statics.STRICT || Statics.DEBUG_GFX) {
			g.drawString("DEBUG/STRICT MODE!", 300, 600);
		}
		BS.show();

	}


	public void closeWindow() {
		this.setVisible(false);
	}

	public void keyPressed(KeyEvent e) {
		try {
			keys[e.getKeyCode()] = true;
			//p("Key p:" + e.getKeyCode());
		} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();
		}
	}


	@Override
	public void paint(Graphics g) {
		// DO nothing
	}


	public void keyReleased(KeyEvent e) {
		try {
			keys[e.getKeyCode()] = false;
		} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();
		}
	}


	public void keyTyped(KeyEvent e) {
		//p("Key t:" + e.getKeyCode());
	}


	public void setModule(Stage stage) throws IOException {
		switch (stage) {
		case SELECT_UNIT_TYPE:
			this.module = new SelectUnitTypeModule(main, this);
			break;
		case PLEASE_WAIT:
			this.module = new PleaseWaitModule(main, this);
			break;
		case PLAY_GAME:
			this.module = this.game_module;
			break;
		default:
			throw new IllegalArgumentException("Unknown stage: " + stage.name());
		}
	}


	/*	public Image getImage(String filename, ShadeType shade) throws FileNotFoundException {
	return this.images.getImage("./data/images/" + filename, Statics.SQ_SIZE, Statics.SQ_SIZE, shade);
}
	 */

	public Image getImage(String filename) {
		return this.images.getImage("./client_data/images/" + filename, Statics.SQ_SIZE, Statics.SQ_SIZE, ShadeType.NONE);
	}


	/*	public Image getImage(String filename, int w, int h, ShadeType shade) {
	return this.images.getImage("./data/images/" + filename, w, h, shade);
}
	 */

	public Image getImage(String filename, int w, int h) {
		return this.images.getImage("./client_data/images/" + filename, w, h, ShadeType.NONE);
	}


	public static void p(String s) {
		System.out.println(s);
	}


	public Point getLastMousePos() {
		return this.last_mouse_pos;
	}


	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		main.stopNow();

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
	public void mouseDragged(MouseEvent e) {
		this.last_mouse_pos.x = e.getX();
		this.last_mouse_pos.y = e.getY();
		//ClientMain.p("last_mouse_pos: " + last_mouse_pos.x + "," + last_mouse_pos.y);

	}


	@Override
	public void mouseMoved(MouseEvent e) {
		this.last_mouse_pos.x = e.getX();
		this.last_mouse_pos.y = e.getY();
		//ClientMain.p("last_mouse_pos: " + last_mouse_pos.x + "," + last_mouse_pos.y);
	}


	@Override
	public void mouseClicked(MouseEvent e) {

	}


	@Override
	public void mousePressed(MouseEvent e) {
		this.last_mouse_pos.x = e.getX();
		this.last_mouse_pos.y = e.getY();
		//ClientMain.p(("last_mouse_pos: " + last_mouse_pos.x + "," + last_mouse_pos.y));
		module.mousePressed(e);
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		module.mouseReleased(e);
	}


	@Override
	public void mouseEntered(MouseEvent e) {

	}


	@Override
	public void mouseExited(MouseEvent e) {

	}


}
