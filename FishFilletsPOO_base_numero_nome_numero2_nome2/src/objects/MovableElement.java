package objects;

import java.util.Random;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class MovableElement extends GameObject{
	
	public MovableElement(Room room) {
		super(room);
	}
	
	public void move(Vector2D dir) {
		Random rand = new Random();
		Point2D destination = new Point2D(rand.nextInt(10), rand.nextInt(10)); 
		setPosition(destination);		
	}

	
}
