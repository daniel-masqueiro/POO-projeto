package objects;

import pt.iscte.poo.game.Room;

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
		return "bigFishLeft";
	}

	@Override
	public int getLayer() {
		return 1;
	}
}
