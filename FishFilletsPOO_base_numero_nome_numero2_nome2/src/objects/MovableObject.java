package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import objects.HoledWall; 

public abstract class MovableObject extends MovableElement implements Heavy {

	private boolean isHeavy;
	
	public MovableObject(Room room, boolean isSolid, boolean isSupport, boolean isHeavy) {
		super(room, isSolid, isSupport);
		this.isHeavy = isHeavy;
	}
	
	@Override
	public boolean isHeavy() { return isHeavy; }

	// --- SOBRESCRITA LIMPA: Objetos móveis têm prioridade ---
	@Override
	public int getPriority() {
		return 1;
	}
	// --------------------------------------------------------

	public boolean canBeMovedBy(GameCharacter actor) {
		if (this.isHeavy() && !actor.canPushHeavy()) return false;
		return true;
	}

	private boolean canPushChain(GameCharacter actor, Direction dir, GameEngine engine) {
		int count = 1;
		Point2D nextPos = getPosition().plus(dir.asVector());
		
		while (true) {
			GameObject obj = engine.getObjectAt(nextPos);
			if (obj == null || !(obj instanceof MovableObject)) {
				break;
			}
			count++;
			nextPos = nextPos.plus(dir.asVector());
		}
		
		return count <= actor.getPushLimit(dir);
	}
	
	@Override
	public void processGravity(GameEngine engine) {
		if (isHeavy()) {
			Point2D posBelow = getPosition().plus(Direction.DOWN.asVector());
			GameObject objBelow = engine.getObjectAt(posBelow);
			
			if (objBelow instanceof Trunk) {
				engine.getCurrentRoom().removeObject(objBelow);
				move(Direction.DOWN.asVector()); 
				return;
			}
		}
		super.processGravity(engine);
	}

	@Override
	public boolean interact(GameCharacter actor, Direction dir, GameEngine engine) {
		if (!canBeMovedBy(actor)) return false;
		if (!canPushChain(actor, dir, engine)) return false;

		Point2D posBehind = getPosition().plus(dir.asVector());
		GameObject objBehind = engine.getObjectAt(posBehind);

		if (objBehind == null || !objBehind.isSolid()) {
			this.move(dir.asVector());
			return true;
		}

		if (objBehind instanceof HoledWall && this.isSmall()) {
			this.move(dir.asVector());
			return true;
		}

		if (objBehind.interact(actor, dir, engine)) {
			this.move(dir.asVector());
			return true;
		}

		return false;
	}
}