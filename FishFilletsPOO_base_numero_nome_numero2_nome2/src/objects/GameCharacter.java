package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public abstract class GameCharacter extends MovableElement {

	protected Direction facingDirection = Direction.LEFT;
	protected boolean isDead = false;

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

	public void setFishDeath(boolean isDead) {
		this.isDead = isDead;
	}
	public boolean isDead() {
		return isDead;
	}

}