package objects;

import pt.iscte.poo.game.Room;

public class Crab extends GameCharacter implements Dangerous {

	public Crab(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "krab";
	}

	@Override
	public int getLayer() {
		return 2;
	}

	@Override
	public boolean isSmall() {
		return true;
	}

	@Override
	public int getPushLimit() {
		return 0;
	}

	@Override
	public boolean canPushHeavy() {
		return false;
	}

	@Override
	public int getSupportLimit() {
		return 0;
	}

	@Override
	public boolean canSupportHeavy() {
		return false;
	}

	@Override
	public boolean isEnemy() {
		return true;
	}

	@Override
	public boolean isLethalTo(GameCharacter character) {
		return character instanceof SmallFish;
	}
}