package mgs2.shared;


public class UnitTypeModifiers {

	public UnitTypeModifiers() {
	}
	
	
	public static final float GetSpeedMod(UnitType unit) {
		switch (unit) {
		case CLERIC:
			return 1.1f;
		case THEIF:
			return 1.3f;
		case WARRIOR:
			return 0.6f;
		case UNSET:
			throw new RuntimeException("Unit type not set");
		default:
			return 1;
		}
	}


	public static final int GetShotInterval(UnitType unit) {
		switch (unit) {
		case ELF:
			return 3000;
		case WARRIOR:
			return 300;
		case CLERIC:
			return 200;
		case UNSET:
			throw new RuntimeException("Unit type not set");
		default:
			return 500;
		}
	}


	public static final int GetBulletMaxDamage(UnitType unit) {
		switch (unit) {
		case CLERIC:
			return 10;
		case WARRIOR:
			return 20;
		case THEIF:
			return 40;
		case ELF:
			return 50;
		case UNSET:
			throw new RuntimeException("Unit type not set");
		default:
			return 15;
		}
	}


	public static final int GetBulletMinDamage(UnitType unit) {
		switch (unit) {
		case ELF:
			return 30;
		case THEIF:
			return 20;
		case UNSET:
			throw new RuntimeException("Unit type not set");
		default:
			return 5;
		}
	}


	public static final int GetBulletRange(UnitType unit) {
		switch (unit) {
		case ELF:
			return Statics.SQ_SIZE * 20;
		case THEIF:
		case CLERIC:
			return Statics.SQ_SIZE * 4;
		case UNSET:
			throw new RuntimeException("Unit type not set");
		default:
			return Statics.SQ_SIZE * 8;
		}
	}


	public static final int GetMaxHealth(UnitType unit) {
		int std_health = 100;
		if (Statics.DEBUG) {
			std_health = 10;
		}
		switch (unit) {
		case CLERIC:
		case THEIF:
			return (int)(std_health * 0.75f);
		case WARRIOR:
			return (int)(std_health * 1.5f);
		case UNSET:
			throw new RuntimeException("Unit type not set");
		default:
			return std_health;
		}
	}


	public static final float GetBulletThickness(UnitType unit) {
		switch (unit) {
		case CLERIC:
		case WARRIOR:
			return 6f;
		case ELF:
			return 3f;
		case UNSET:
			throw new RuntimeException("Unit type not set");
		default:
			return 3f;
		}
	}


}
