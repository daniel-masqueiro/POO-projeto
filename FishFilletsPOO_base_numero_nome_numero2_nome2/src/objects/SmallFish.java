package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public class SmallFish extends GameCharacter implements Transpassable {

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
	public int getLayer() { return 2; }
	
	@Override
	public int getPushLimit(Direction dir) { 
		return 1;
	}

	@Override
	public boolean canPushHeavy() { return false; }
	@Override
	public int getSupportLimit() { return 1; }
	@Override
	public boolean canSupportHeavy() { return false; }
	
	@Override
	public boolean isSmall() { return true; }

	@Override
	public boolean interact(GameCharacter actor, Direction dir, GameEngine engine) {
		if (actor.isEnemy()) {
			setFishDeath(true);
			engine.triggerGameOver("O Peixe Pequeno foi apanhado!");
			return true;
		}
		return super.interact(actor, dir, engine);
	}
	@Override
    public boolean isPassableFor(GameObject obj) {
        if (obj instanceof Trap) {
            return true;
        }
        return false;
    }
}