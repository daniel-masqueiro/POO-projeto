package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public class SmallFish extends GameCharacter {

	private static SmallFish sf = new SmallFish(null);
	
	private SmallFish(Room room) { super(room); }
	public static SmallFish getInstance() { return sf; }
	
	@Override
	public String getName() {
		if(isDead) return "deadfish";
		if (facingDirection == Direction.RIGHT) return "smallFishRight";
		return "smallFishLeft";
	}

	@Override
	public int getLayer() {
		return 2;
	}
	
	@Override
	public int getPushLimit() { 
		return 1;
	}

	@Override
	public boolean canPushHeavy() { 
		return false;
	}

	@Override
	public int getSupportLimit() { 
		return 1;
	}

	@Override
	public boolean canSupportHeavy() { 
		return false;
	}
	@Override
    public boolean isSmall() {
        return true;
    }
}