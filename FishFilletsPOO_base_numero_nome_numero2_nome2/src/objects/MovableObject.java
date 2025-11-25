package objects;

import pt.iscte.poo.game.Room;

public abstract class MovableObject extends MovableElement implements Heavy {

	private boolean isHeavy;
	public MovableObject(Room room, boolean isSolid, boolean isSupport, boolean isHeavy) {
		super(room, isSolid, isSupport);
		this.isHeavy = isHeavy;
	}
	
	@Override
	public boolean isHeavy() {
		return isHeavy;
	}
}