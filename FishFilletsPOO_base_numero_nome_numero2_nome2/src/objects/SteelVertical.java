package objects;

import pt.iscte.poo.game.Room;

public class SteelVertical extends GameObject {

	public SteelVertical(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "steelVertical";
	}

	@Override
	public int getLayer() {
		return 3;
	}
}
