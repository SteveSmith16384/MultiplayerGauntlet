package mgs2.client;

import java.io.FileNotFoundException;

import mgs2.client.sprites.AbstractClientSprite;
import mgs2.client.sprites.OtherSprite;
import mgs2.shared.AbstractGameObject;
import mgs2.shared.AbstractGameObject.Type;
import mgs2.shared.AbstractSpriteGrid;
import mgs2.shared.ImageCodes;
import mgs2.shared.Statics;

public final class ClientSpriteGrid extends AbstractSpriteGrid {

	public ClientSpriteGrid(byte w, byte h) {
		super(w, h);

	}


	public void checkWalls(ClientWindow window) throws FileNotFoundException {
		for (int y = 0; y < objects[0].length-1; y++) {
			for (int x = 0; x < objects.length; x++) {
				if (objects[x][y] != null) {
					// Shade walls
					AbstractClientSprite wall = (AbstractClientSprite)this.getType(x, y, Type.WALL);
					if (wall != null) {
						AbstractGameObject wall2 = this.getType(x, y+1, Type.WALL);
						if (wall2 == null) {
							wall.img = window.getImage(ImageCodes.GetFilename(wall.image_code));//, ShadeType.BOTTOM_HALF);
						}
					}
					// Add shadows
					AbstractGameObject floor = this.getType(x, y, Type.FLOOR);
					if (floor != null) {
						AbstractGameObject wall_left = this.getType(x-1, y, Type.WALL);
						AbstractGameObject wall_left_bottom = this.getType(x-1, y+1, Type.WALL);
						AbstractGameObject wall_bottom = this.getType(x, y+1, Type.WALL);
						
						int checksum = 0;
						checksum += (wall_left != null ? 1 : 0);
						checksum += (wall_left_bottom != null ? 2 : 0);
						checksum += (wall_bottom != null ? 4 : 0);
						
						if (checksum == 1) { // Left only
							OtherSprite shad1 = new OtherSprite(window, -1, "Shadow", Type.SCENERY, x*Statics.SQ_SIZE, y*Statics.SQ_SIZE, Statics.SQ_SIZE/2, Statics.SQ_SIZE, AbstractClientSprite.DRAW_LEVEL_SHADOW, false, false, ImageCodes.SHADOW_RIGHT_TRI, 0, (byte)0, false);
							objects[x][y].add(shad1);
						} else if (checksum == 2) { // left-bottom only
							OtherSprite shad2 = new OtherSprite(window, -1, "Shadow", Type.SCENERY, x*Statics.SQ_SIZE, y*Statics.SQ_SIZE, Statics.SQ_SIZE/2, Statics.SQ_SIZE/2, AbstractClientSprite.DRAW_LEVEL_SHADOW, false, false, ImageCodes.SHADOW_SQUARE, 0, (byte)0, false);
							shad2.draw_off_y = Statics.SQ_SIZE/2;
							objects[x][y].add(shad2);
						} else if (checksum == 4) { // bottom only
							OtherSprite shad2 = new OtherSprite(window, -1, "Shadow", Type.SCENERY, x*Statics.SQ_SIZE, y*Statics.SQ_SIZE, Statics.SQ_SIZE, Statics.SQ_SIZE/2, AbstractClientSprite.DRAW_LEVEL_SHADOW, false, false, ImageCodes.SHADOW_TOP_TRI, 0, (byte)0, false);
							shad2.draw_off_y = Statics.SQ_SIZE/2;
							objects[x][y].add(shad2);
						} else if (checksum == 3) { // left and left-bottom
							OtherSprite shad2 = new OtherSprite(window, -1, "Shadow", Type.SCENERY, x*Statics.SQ_SIZE, y*Statics.SQ_SIZE, Statics.SQ_SIZE/2, Statics.SQ_SIZE, AbstractClientSprite.DRAW_LEVEL_SHADOW, false, false, ImageCodes.SHADOW_SQUARE, 0, (byte)0, false);
							objects[x][y].add(shad2);
						} else if (checksum == 5 || checksum == 7) { // left and bottom
							OtherSprite shad2 = new OtherSprite(window, -1, "Shadow", Type.SCENERY, x*Statics.SQ_SIZE, y*Statics.SQ_SIZE, Statics.SQ_SIZE, Statics.SQ_SIZE, AbstractClientSprite.DRAW_LEVEL_SHADOW, false, false, ImageCodes.SHADOW_L_SHAPE, 0, (byte)0, false);
							objects[x][y].add(shad2);
						} else if (checksum == 6) { // left-bottom and bottom
							OtherSprite shad2 = new OtherSprite(window, -1, "Shadow", Type.SCENERY, x*Statics.SQ_SIZE, y*Statics.SQ_SIZE, Statics.SQ_SIZE, Statics.SQ_SIZE/2, AbstractClientSprite.DRAW_LEVEL_SHADOW, false, false, ImageCodes.SHADOW_SQUARE, 0, (byte)0, false);
							shad2.draw_off_y = Statics.SQ_SIZE/2;
							objects[x][y].add(shad2);
						}
					}
				}
			}
		}
	}



}
