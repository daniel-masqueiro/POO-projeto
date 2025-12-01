package objects;

import pt.iscte.poo.game.Room;

public class Buoy extends MovableObject {

	public Buoy(Room room) {
		// Room, isSolid=true, isSupport=true, isHeavy=false
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
}