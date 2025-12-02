package objects;

import pt.iscte.poo.game.Room;

public class Trap extends MovableObject implements Dangerous {

	public Trap(Room room) {
		super(room, false, true, true);
	}

	@Override
	public String getName() {
		return "trap";
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean isLethalTo(GameCharacter character) {
		return character instanceof BigFish;
	}
}