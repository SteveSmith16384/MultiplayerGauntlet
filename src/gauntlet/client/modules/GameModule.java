package gauntlet.client.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.PriorityQueue;
import java.util.Queue;

import gauntlet.client.ClientMain;
import gauntlet.client.ClientSpriteGrid;
import gauntlet.client.ClientWindow;
import gauntlet.client.datastructs.SimpleGameData;
import gauntlet.client.gfx.DroppingText;
import gauntlet.client.sprites.AbstractClientSprite;
import gauntlet.client.sprites.PlayerSprite;
import mgs2.shared.AbstractGameObject;
import mgs2.shared.GameStage;
import mgs2.shared.Statics;
import mgs2.shared.UnitType;
import ssmith.util.Interval;

public final class GameModule extends AbstractModule {

	public ClientSpriteGrid sprite_grid;
	private PlayerSprite player;
	private Interval check_los_unseens_interval = new Interval(250);
	private Interval check_los_seens_interval = new Interval(1000);
	private DroppingText droptext;
	private Image background;
	private Point map_screen_pos = new Point();

	private ArrayList<Line2D> debug_lines = new ArrayList<Line2D>();

	public GameModule(ClientMain _main, ClientWindow window) {
		super(_main, window);

		//background = window.getImage("background_hills.jpg", Statics.WINDOW_SIZE.width, Statics.WINDOW_SIZE.height);
	}


	public void setPlayer(PlayerSprite _player) {
		player = _player;
	}


	public PlayerSprite getPlayer() {
		return this.player;
	}


	@Override
	public void gameLoop(Graphics g, long interpol) throws IOException {
		g.drawImage(background, 0, 0, window);
		if (this.player != null && player.getX() >= 0 && player.getY() >= 0) {
			if (player.getHealth() > 0) {
				if (window.keys[Statics.KEY_UP]) {
					player.off_y = -1;
				} else if (window.keys[Statics.KEY_DOWN]) {
					player.off_y = 1;
				} else {
					player.off_y = 0;
				}
				if (window.keys[Statics.KEY_LEFT]) {
					player.off_x = -1;
				} else if (window.keys[Statics.KEY_RIGHT]) {
					player.off_x = 1;
				} else {
					player.off_x = 0;
				}
			}

			map_screen_pos.x = (int)player.getX() - (Statics.WINDOW_SIZE.width/2);
			map_screen_pos.y = (int)player.getY() - (Statics.WINDOW_SIZE.height/2);

			// Adjust if elf
			if (this.main.unit_type == UnitType.ELF) {
				map_screen_pos.x += (this.window.last_mouse_pos.x - (Statics.WINDOW_SIZE.width/2));
				map_screen_pos.y += (this.window.last_mouse_pos.y - (Statics.WINDOW_SIZE.height/2));
			}

			int sx = map_screen_pos.x / Statics.SQ_SIZE;
			int sy = map_screen_pos.y / Statics.SQ_SIZE;
			int ex = (map_screen_pos.x+Statics.WINDOW_SIZE.width) / Statics.SQ_SIZE;
			int ey = (map_screen_pos.y+Statics.WINDOW_SIZE.height) / Statics.SQ_SIZE;

			if (sprite_grid != null) { // We might be being sent a new object before we've been told about the game, i.e. our new avatar (sent to all players)
				synchronized (sprite_grid) {
					try {
						// Process sprites
						Queue<AbstractGameObject> process_objs = this.sprite_grid.getProcessObjects();
						for (AbstractGameObject sprite : process_objs) {
							sprite.process(interpol);
						}
					} catch (ConcurrentModificationException ex2) {
						ex2.printStackTrace();
					}
				}

				// Draw sprites
				boolean check_unseens = check_los_unseens_interval.hitInterval();
				boolean check_seens = check_los_seens_interval.hitInterval();
				Queue<AbstractClientSprite> to_draw_on_top = new PriorityQueue<AbstractClientSprite>(10, sprite_grid);

				for (int y = sy; y <= ey; y++) {
					for (int x = sx; x <= ex; x++) {
						if (x >= 0 && x <sprite_grid.width && y>=0 && y<sprite_grid.height ) {
							try {
								Queue<AbstractGameObject> sprites_in_sq = sprite_grid.getSpritesAt(x, y);
								if (sprites_in_sq != null) {
									synchronized (sprites_in_sq) {
										for (AbstractGameObject sprite2 : sprites_in_sq) {
											AbstractClientSprite sprite = (AbstractClientSprite)sprite2;
											if (sprite.side != player.side) { // Check LoS
												if (main.game_data == null || main.game_data.game_stage != GameStage.POST_GAME) {
													if (sprite.visible == false && check_unseens) {
														sprite.visible = main.window.game_module.player.canSee(sprite);
													} else if (sprite.visible && check_seens) {
														sprite.visible = main.window.game_module.player.canSee(sprite);
													}
												} else {
													sprite.visible = true; 
												}
											}
											if (Statics.USE_LOS == false) {
												sprite.visible = true; 
											}
											if (sprite.visible) {
												int x2 = (int)(sprite.getX() - map_screen_pos.x);
												int y2 = (int)(sprite.getY() - map_screen_pos.y);
												if (sprite.draw_pri >= AbstractClientSprite.DRAW_LEVEL_POST_GRID) {
													if (Statics.STRICT) {
														if (to_draw_on_top.contains(sprite)) {
															//ClientMain.pe("Dupe sprite at " + sprite.getX() +"," + sprite.getY() + ":"+ sprite);
														}
													}
													to_draw_on_top.add(sprite);
												} else {
													sprite.draw(g, x2, y2);
												}
											}
										}
									}
								}
							} catch (java.lang.ArrayIndexOutOfBoundsException ex3) {
								ex3.printStackTrace();
							} catch (ConcurrentModificationException ex2) {
								ex2.printStackTrace();
							}
						}
					}
				}
				while (to_draw_on_top.size() > 0) {
					AbstractClientSprite sprite = to_draw_on_top.poll();
					int x2 = (int)(sprite.getX() - map_screen_pos.x);
					int y2 = (int)(sprite.getY() - map_screen_pos.y);
					if (sprite == player && this.main.unit_type == UnitType.ELF == false) {
						sprite.draw(g, (Statics.WINDOW_SIZE.width/2), (Statics.WINDOW_SIZE.height/2));
					} else {
						sprite.draw(g, x2, y2);
					}
				}
			}

			// Debug lines
			if (Statics.DEBUG_GFX) {
				g.setColor(Color.white);
				for (Line2D line : this.debug_lines) {
					g.drawLine((int)line.getX1()-map_screen_pos.x, (int)line.getY1()-map_screen_pos.y, (int)line.getX2()-map_screen_pos.x, (int)line.getY2()-map_screen_pos.y);
				}
			}

			g.setFont(window.font_normal);
			g.setColor(Color.white);
			//g.drawString("FPS: " + main.fps, 20, 80);
			if (this.main.unit_type != null) {
				g.drawString(this.main.unit_type.name(), 20, 110); 
			}
			if (main.game_data != null) {
				if (window.str_time.length() > 0) {
					g.drawString(window.str_time, 20, 50);
				}
				if (getPlayer() != null) {
					g.drawString("Health: " + getPlayer().getHealth(), 20, 140);
					if (this.main.game_data.game_stage == GameStage.STARTED) {
						if (getPlayer().getHealth() <= 0) {
							g.drawString(window.str_respawn_time, 20, 170);
						}
					}
					g.drawString(main.game_data.game_stage.getName(), 20, 200);
				}
			}

		}
		if (droptext != null) {
			this.droptext.paint(g, interpol);
		}
	}


	public Point getRelativeMousePos() {
		Point p = window.getLastMousePos();
		return new Point(map_screen_pos.x + p.x, map_screen_pos.y+p.y);
	}




	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == e.BUTTON1) {
			player.startPerformingMainAction(true);
		} else if (e.getButton() == e.BUTTON2) {
			player.startPerformingSecondaryAction(true);
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == e.BUTTON1) {
			player.startPerformingMainAction(false);
		} else if (e.getButton() == e.BUTTON2) {
			player.startPerformingSecondaryAction(false);
		}
	}


	public void addDebugLine(Line2D rect) {
		if (Statics.DEBUG_GFX) {
			if (rect.getX1() <= 0 || rect.getY1() <= 0) {
				ClientMain.p("Toos small!");
			}
			this.debug_lines.add(rect);
			while (this.debug_lines.size() > 20) {
				this.debug_lines.remove(0);
			}
		}
	}


	public void addDropText(SimpleGameData game_data) {
		//GameStage stage = this.main.game_data.game_stage;
		switch (game_data.game_stage) {
		case POST_GAME:
			/*todo 
				this.droptext = new DroppingText(window, "text_you_have_won.png");
			*/
			break;
		case STARTED:
			this.droptext = new DroppingText(window, "text_game_started.png");
			break;
		case START_IMINENT:
			this.droptext = new DroppingText(window, "text_get_ready.png");
			break;
		case WAIT_FOR_PLAYERS:
			this.droptext = new DroppingText(window, "text_waiting_for_players.png");
			break;
		default:
			break;

		}
	}
}
