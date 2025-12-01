package objects;

import pt.iscte.poo.game.Room;

public class Crab extends MovableObject {

	public Crab(Room room) {
		// Room, isSolid=true, isSupport=true, isHeavy=false
		super(room, true, true, false);
	}

	@Override
	public String getName() {
		return "krab";
	}

	@Override
	public int getLayer() {
		return 2; 
	}
	
	@Override
    public boolean isSmall() {
        return true;
    }
}