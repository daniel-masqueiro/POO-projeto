package objects;

import pt.iscte.poo.game.Room;

public abstract class MovableObject extends MovableElement{

	private boolean isHeavy;
	
	public MovableObject(Room room,boolean isHeavy) {
		super(room);
		this.isHeavy=isHeavy;
	}
	
	public boolean isheavy() {
		return isHeavy;
	}
	
	
}
