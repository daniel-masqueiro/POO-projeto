package objects;

import java.util.Random;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class GameCharacter extends MovableElement {
	
	protected Direction facingDirection = Direction.LEFT;
	
	public GameCharacter(Room room) {
		super(room);
	}
	
	@Override
	public int getLayer() {
		return 2;
	}
	public void setFacingDirection(Direction dir) {
        if (dir == Direction.LEFT || dir == Direction.RIGHT) {
            this.facingDirection = dir;
        }
    }
	
}