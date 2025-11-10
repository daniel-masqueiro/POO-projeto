package objects;

import java.util.Random;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class GameCharacter extends MovableElement {
	
	public GameCharacter(Room room) {
		super(room);
	}
	
	@Override
	public int getLayer() {
		return 2;
	}
	
}