package objects;

import pt.iscte.poo.game.Room;

public class Crab extends GameCharacter {

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
}