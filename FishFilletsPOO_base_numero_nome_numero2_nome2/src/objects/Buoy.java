package objects;

import pt.iscte.poo.game.Room;

public class Buoy extends MovableObject implements Floatable {

	public Buoy(Room room) {
		super(room, true, true, false);
	}

	@Override
	public String getName() {
		return "buoy";
	}

	@Override
	public int getLayer() {
		return 3;
	}
	@Override
	public boolean triesToFloat() {
		return true;
	}
}