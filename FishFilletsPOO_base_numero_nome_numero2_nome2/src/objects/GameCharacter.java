package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public abstract class GameCharacter extends MovableElement implements Solid, PushAgent {

	protected Direction facingDirection = Direction.LEFT;
	protected boolean isDead = false;
	

	public GameCharacter(Room room) {
		super(room, true, true); // Sólido=true, Suporte=false
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

	public void setFishDeath(boolean isDead) {
		this.isDead = isDead;
	}
	
	public boolean isDead() {
		return isDead;
	}
	
}