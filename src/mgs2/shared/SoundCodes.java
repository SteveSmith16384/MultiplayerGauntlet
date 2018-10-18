package mgs2.shared;

public class SoundCodes {

	public static final int DOOR = 1;
	public static final int LASER4 = 2;

	private SoundCodes() {

	}


	public static String GetFilename(int code) {
		switch (code) {
		case DOOR: return "door.wav";
		case LASER4: return "laser4.wav";
		default:
			System.err.println("No sound for code " + code);
			return "";
		} 
	}

}
