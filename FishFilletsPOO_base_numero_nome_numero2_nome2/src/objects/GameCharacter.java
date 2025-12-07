package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public abstract class GameCharacter extends MovableElement implements Solid, PushAgent {

	protected Direction facingDirection = Direction.LEFT;
	protected boolean isDead = false;
	
	public GameCharacter(Room room) { super(room, true, true); }

	@Override
	public int getLayer() { return 2; }

	public void setFacingDirection(Direction dir) {
		if (dir == Direction.LEFT || dir == Direction.RIGHT) {
			this.facingDirection = dir;
		}
	}

	public void setFishDeath(boolean isDead) { this.isDead = isDead; }
	public boolean isDead() { return isDead; }
	public boolean isEnemy() { return false; }

	@Override
	public void processGravity(GameEngine engine) { }

	public void validateCrushing(GameEngine engine) {
		int itemsAbove = 0;
		int heavyCount = 0;
		
		Point2D currentPos = getPosition().plus(Direction.UP.asVector());
		
		while (true) {
			GameObject obj = engine.getObjectAt(currentPos);
			if (obj == null || !obj.isSolid() || !(obj instanceof MovableElement)) break;
			itemsAbove++;
			if (obj instanceof Heavy && ((Heavy)obj).isHeavy()) heavyCount++;
			currentPos = currentPos.plus(Direction.UP.asVector());
		}
		
		boolean dies = false;
		if (heavyCount > 0 && !canSupportHeavy()) dies = true;
		if (canSupportHeavy() && heavyCount > 1) dies = true;
		if (itemsAbove > getSupportLimit()) dies = true;
		
		if (dies) {
			setFishDeath(true);
			engine.triggerGameOver(getName() + " foi esmagado!");
		}
	}
}