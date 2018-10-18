package mgs2.shared;

public final class ImageCodes {

	// Player codes
	public static final int SIDE_1 = 1;
	public static final int SIDE_2 = 2;
	public static final int SIDE_3 = 3;
	public static final int SIDE_4 = 4;
	
	
	// Texture types
	public static final int TEX_INTERIOR1 = 1;
	public static final int TEX_MOONROCK = 2;
	public static final int TEX_CORRIDOR1 = 3;
	public static final int TEX_INTERIOR2 = 4;
	public static final int TEX_RUBBLE = 5;
	public static final int TEX_PATH = 6;
	public static final int TEX_GRASS = 7;
	public static final int TEX_ROAD = 8;
	public static final int TEX_MUD = 9;
	public static final int TEX_WOODEN_FLOOR = 10;
	public static final int TEX_WATER = 11;
	public static final int TEX_BEACH = 12;
	public static final int TEX_SAND1 = 13;
	public static final int TEX_SAND2 = 14;
	public static final int TEX_WOODEN_PLANKS = 15;
	public static final int TEX_MOONBASE_BLUE = 16;
	public static final int TEX_CORRUGATED_WALL = 17;
	public static final int TEX_SNOW = 24;
	public static final int TEX_SPACEWALL = 25;
	public static final int TEX_STONE_TILES = 26;
	public static final int TEX_CARPET1 = 28;
	public static final int TEX_WOODEN_FLOOR2 = 29;
	public static final int TEX_ALIEN_SKIN = 50;
	public static final int TEX_BULKHEAD = 51;
	public static final int TEX_SPACESHIP_WALL = 52;
	public static final int TEX_TELEPORTER = 53;
	public static final int TEX_METAL_FLOOR5 = 55;
	public static final int TEX_METAL_FLOOR6 = 56;
	public static final int TEX_METAL_FLOOR15 = 57;
	public static final int TEX_FLOORHATCH = 59; // New
	public static final int TEX_METAL_FLOOR41 = 60;
	public static final int TEX_LAB_FLOOR1 = 61;
	public static final int TEX_LAB_FLOOR2 = 62;
	public static final int TEX_ESCAPE_HATCH = 68;
	public static final int TEX_BRICKS = 69;
	public static final int TEX_RUBBLE_2 = 72;
	public static final int TEX_RUBBLE_3 = 73;
	public static final int TEX_RUBBLE_4 = 74;
	public static final int TEX_CELLS3 = 75;
	public static final int TEX_ALIEN_PURPLE = 76;
	public static final int TEX_ALIEN_GREEN = 77; // Not used - only in Android
	public static final int TEX_CARVED_SANDSTONE = 78;
	public static final int TEX_ALIEN_COLONY = 93;
	public static final int MEDIKIT = 94;
	//public static final int AMMO_PACK = 95;
	public static final int TEX_DOOR = 96;
	public static final int IMG_LASER_BOLT = 97; 
	public static final int CORPSE_SIDE_1 = 100;
	public static final int CORPSE_SIDE_2 = 101;
	public static final int SHADOW_TOP_TRI = 102;
	public static final int SHADOW_RIGHT_TRI = 103;
	public static final int SHADOW_SQUARE = 104;
	public static final int SHADOW_L_SHAPE= 105;

	public static final int WARRIOR_E = 106;
	public static final int WARRIOR_SE = 107;
	public static final int WARRIOR_S = 108;
	public static final int WARRIOR_SW = 109;
	public static final int WARRIOR_W = 110;
	public static final int WARRIOR_NW = 111;
	public static final int WARRIOR_N = 112;
	public static final int WARRIOR_NE = 113;
	
	public static final int WIZARD_E = 114;
	public static final int WIZARD_SE = 115;
	public static final int WIZARD_S = 116;
	public static final int WIZARD_SW = 117;
	public static final int WIZARD_W = 118;
	public static final int WIZARD_NW = 119;
	public static final int WIZARD_N = 120;
	public static final int WIZARD_NE = 121;
	
	public static final int ZOMBIE_E = 122;
	public static final int ZOMBIE_SE = 123;
	public static final int ZOMBIE_S = 124;
	public static final int ZOMBIE_SW = 125;
	public static final int ZOMBIE_W = 126;
	public static final int ZOMBIE_NW = 127;
	public static final int ZOMBIE_N = 128;
	public static final int ZOMBIE_NE = 129;
	
	public static final int CLERIC_E = 130;
	public static final int CLERIC_SE = 131;
	public static final int CLERIC_S = 132;
	public static final int CLERIC_SW = 133;
	public static final int CLERIC_W = 134;
	public static final int CLERIC_NW = 135;
	public static final int CLERIC_N = 136;
	public static final int CLERIC_NE = 137;
	
	public static final int RUBBLE = 138;
	public static final int RUBBLE_RED = 139;
	public static final int RUBBLE_YELLOW = 140;
	public static final int RUBBLE_WHITE = 141;
	public static final int DAMAGED_FLOOR = 142;
	public static final int DAMAGED_FLOOR2 = 143;
	public static final int GRILL = 144;
	public static final int CRACK1 = 145;
	public static final int CRACK2 = 146;
	public static final int WEED1 = 147;
	public static final int WEED2 = 148;
	public static final int DAMAGED_FLOOR3 = 149;
	public static final int DAMAGED_FLOOR4 = 150;
	public static final int DAMAGED_FLOOR5 = 151;
	public static final int DAMAGED_FLOOR6 = 152;
	public static final int DAMAGED_FLOOR7 = 153;
	public static final int SCROLL = 154;

	public static final int ZOMBIE_MONSTER_GEN = 155;
	public static final int GHOST_MONSTER_GEN = 156;
	
	public static final int GHOST_E = 157;
	public static final int GHOST_SE = 158;
	public static final int GHOST_S = 159;
	public static final int GHOST_SW = 160;
	public static final int GHOST_W = 161;
	public static final int GHOST_NW = 162;
	public static final int GHOST_N = 163;
	public static final int GHOST_NE = 164;
	
	public static final int STAIRS_DOWN = 165;
	// Doors
	//public static final byte DOOR_NS = 1;
	//public static final byte DOOR_EW = 2;


	public ImageCodes() {
	}

	public static String GetFilename(int tex_code) {
		switch(tex_code) {
		case TEX_INTERIOR1:
			return "metalfloor1.jpg";
		case TEX_INTERIOR2:
			return "floor3.jpg";
		case TEX_MOONROCK:
			return "moonrock.png";
		case TEX_CORRIDOR1:
			return "corridor.jpg";
		case TEX_RUBBLE:
			return "rubble.png";
		case TEX_PATH:
			return "road1.png";
		case TEX_GRASS:
			//return "grass.png";
			return "grass.jpg";
		case TEX_ROAD:
			return "road2.png";
		case TEX_MUD:
			return "mud.png";
		case TEX_WOODEN_FLOOR:
			return "floor02.png";
		case TEX_WATER:
			return "water.png";
		case TEX_BEACH:
			return "beach.png";
		case TEX_SAND1:
			return "sand1.png";
		case TEX_SAND2:
			return "sand2.png";
		case TEX_WOODEN_PLANKS:
			return "wooden_planks_lr.jpg";
		case TEX_MOONBASE_BLUE:
			return "ufo2_03.png";
		case TEX_CORRUGATED_WALL:
			return "wall2.jpg";
		case TEX_SNOW:
			return "snow.jpg";
		case TEX_SPACEWALL:
			return "spacewall.png";
		case TEX_STONE_TILES:
			return "stone_tiles.jpg";
		case TEX_CARPET1:
			return "carpet006.jpg";
		case TEX_WOODEN_FLOOR2:
			return "wood_b_9.jpg";
		case TEX_ALIEN_SKIN:
			return "alienskin2.jpg";
		case TEX_BULKHEAD:
			return "bulkhead.jpg";
		case TEX_SPACESHIP_WALL:
			return "spaceship_wall.png";
		case TEX_TELEPORTER:
			return "teleporter.jpg";
		case TEX_METAL_FLOOR5:
			return "floor5.jpg";
		case TEX_METAL_FLOOR6:
			return "floor006.png";
		case TEX_METAL_FLOOR15:
			return "floor015.png";
		case TEX_FLOORHATCH:
			return "floorhatch.png";
		case TEX_METAL_FLOOR41:
			return "floor0041.png";
		case TEX_LAB_FLOOR1:
			return "lab_floor1.jpg";
		case TEX_LAB_FLOOR2:
			return "lab_floor2.png";
		case TEX_ESCAPE_HATCH:
			return "escape_hatch.jpg";
		case TEX_BRICKS:
			return "bricks.jpg";
		case TEX_DOOR:
			return "door_lr.png";
		case IMG_LASER_BOLT:
			return "laser_bolt.png";
		case MEDIKIT:
			return "medikit.png";
		case CORPSE_SIDE_1:
			return "corpse1.png";
		case CORPSE_SIDE_2:
			return "corpse2.png";
		case SHADOW_TOP_TRI: 
			return "shadow_top.png";
		case SHADOW_RIGHT_TRI: 
			return "shadow_right.png";
		case SHADOW_SQUARE: 
			return "shadow_square.png";
		case SHADOW_L_SHAPE: 
			return "shadow_l_shape.png";

		case WARRIOR_E:
			return "human_s1_e.png";
		case WARRIOR_SE:
			return "human_s1_se.png";
		case WARRIOR_S:
			return "human_s1_s.png";
		case WARRIOR_SW:
			return "human_s1_sw.png";
		case WARRIOR_W:
			return "human_s1_w.png";
		case WARRIOR_NW:
			return "human_s1_nw.png";
		case WARRIOR_N:
			return "human_s1_n.png";
		case WARRIOR_NE:
			return "human_s1_ne.png";
			
		case WIZARD_E:
			return "human_s2_e.png";
		case WIZARD_SE:
			return "human_s2_se.png";
		case WIZARD_S:
			return "human_s2_s.png";
		case WIZARD_SW:
			return "human_s2_sw.png";
		case WIZARD_W:
			return "human_s2_w.png";
		case WIZARD_NW:
			return "human_s2_nw.png";
		case WIZARD_N:
			return "human_s2_n.png";
		case WIZARD_NE:
			return "human_s2_ne.png";
			
		case CLERIC_E:
			return "human_s3_e.png";
		case CLERIC_SE:
			return "human_s3_se.png";
		case CLERIC_S:
			return "human_s3_s.png";
		case CLERIC_SW:
			return "human_s3_sw.png";
		case CLERIC_W:
			return "human_s3_w.png";
		case CLERIC_NW:
			return "human_s3_nw.png";
		case CLERIC_N:
			return "human_s3_n.png";
		case CLERIC_NE:
			return "human_s3_ne.png";
			
		case ZOMBIE_E:
			return "zombie_e.png";
		case ZOMBIE_SE:
			return "zombie_se.png";
		case ZOMBIE_S:
			return "zombie_s.png";
		case ZOMBIE_SW:
			return "zombie_sw.png";
		case ZOMBIE_W:
			return "zombie_w.png";
		case ZOMBIE_NW:
			return "zombie_nw.png";
		case ZOMBIE_N:
			return "zombie_n.png";
		case ZOMBIE_NE:
			return "zombie_ne.png";
			
		case GHOST_E:
			return "ghost_e.png";
		case GHOST_SE:
			return "ghost_se.png";
		case GHOST_S:
			return "ghost_s.png";
		case GHOST_SW:
			return "ghost_sw.png";
		case GHOST_W:
			return "ghost_w.png";
		case GHOST_NW:
			return "ghost_nw.png";
		case GHOST_N:
			return "ghost_n.png";
		case GHOST_NE:
			return "ghost_ne.png";
			
		case RUBBLE: return "";
		case RUBBLE_RED: return "";
		case RUBBLE_YELLOW: return "";
		case RUBBLE_WHITE: return "";
		case DAMAGED_FLOOR: return "damaged_floor.png";
		case DAMAGED_FLOOR2: return "damaged_floor2.png";
		case DAMAGED_FLOOR3: return "damaged_floor3.png";
		case DAMAGED_FLOOR4: return "damaged_floor4.png";
		case DAMAGED_FLOOR5: return "damaged_floor5.png";
		case DAMAGED_FLOOR6: return "damaged_floor6.png";
		case DAMAGED_FLOOR7: return "damaged_floor7.png";
		case GRILL: return "grill.png";
		case CRACK1: return "crack1.png";
		case CRACK2: return "crack2.png";
		case WEED1: return "";
		case WEED2: return "";

		case ZOMBIE_MONSTER_GEN: return "teleporter.jpg";
		case GHOST_MONSTER_GEN: return "teleporter.jpg";

		case STAIRS_DOWN: return "stairs_down.png";

		default:
			throw new RuntimeException("Unknown image code: " + tex_code);
			//System.err.println("Warning: Unknown texture code: " + tex_code);dddd
			//return "metalfloor1.jpg";
		}
	}


	public static int GetCorpseForSide(byte side) {
		switch (side) {
		case 1: return CORPSE_SIDE_1;
		case 2: return CORPSE_SIDE_2;
		default: throw new RuntimeException("Can't handle side " + side);
		}
	}
}
