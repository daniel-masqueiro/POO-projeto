package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Vector2D;

public abstract class MovableElement extends GameObject {
	
	public MovableElement(Room room, boolean isSolid, boolean isSupport) {
		super(room, isSolid, isSupport);
	}
	
	public void move(Vector2D dir) {
		setPosition(super.getPosition().plus(dir));		
	}
	
	public boolean isSmall() {
        return false;
    }
}