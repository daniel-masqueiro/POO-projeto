package objects;

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
		if (isDead) {
			return "deadfish";
		}
		if (facingDirection == Direction.RIGHT) {
			return "bigFishRight";
		}
		return "bigFishLeft";
	}

	@Override
	public int getLayer() {
		return 2;
	}

}
