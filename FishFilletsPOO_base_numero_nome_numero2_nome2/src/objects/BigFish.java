package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public class BigFish extends GameCharacter {

	private static BigFish bf = new BigFish(null);

	private BigFish(Room room) {
		super(room);
	}

	public static BigFish getInstance() {
		return bf;
	}

	@Override
	public String getName() {
		if (isDead)
			return "deadfish";
		if (facingDirection == Direction.RIGHT)
			return "bigFishRight";
		return "bigFishLeft";
	}

	@Override
	public int getLayer() {
		return 2;
	}

	@Override
	public int getPushLimit(Direction dir) {
		if (dir == Direction.UP) {
			return 1;
		}
		return 10;
	}

	@Override
	public boolean canPushHeavy() {
		return true;
	}

	@Override
	public int getSupportLimit() {
		return 4;
	}

	@Override
	public boolean canSupportHeavy() {
		return true;
	}

	@Override
	public boolean interact(GameCharacter actor, Direction dir, GameEngine engine) {
		if (actor.isEnemy()) {
			engine.getCurrentRoom().removeObject(actor);
			return true;
		}
		return super.interact(actor, dir, engine);
	}
}