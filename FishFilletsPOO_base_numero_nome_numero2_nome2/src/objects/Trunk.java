package objects;

import pt.iscte.poo.game.Room;

public class Trunk extends GameObject{
	public Trunk(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "trunk";
	}

	@Override
	public int getLayer() {
		return 1;
	}
}
