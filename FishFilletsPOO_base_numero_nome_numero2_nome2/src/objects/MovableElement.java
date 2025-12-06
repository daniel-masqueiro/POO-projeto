package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class MovableElement extends GameObject {
	
	public MovableElement(Room room, boolean isSolid, boolean isSupport) {
		super(room, isSolid, isSupport);
	}
	
	public void move(Vector2D dir) {
		setPosition(super.getPosition().plus(dir));		
	}
	
	public boolean isSmall() { return false; }

	public void processGravity(GameEngine engine) {
		if (!isSupported()) {
			move(Direction.DOWN.asVector());
		}
	}

	public boolean isSupported() {
		Point2D posBelow = getPosition().plus(Direction.DOWN.asVector());
		for (GameObject other : getRoom().getObjects()) {
			if (other.getPosition().equals(posBelow)) {
				if (other instanceof Support && ((Support) other).isSupport()) {
					
					// --- USO DA INTERFACE PARA GRAVIDADE ---
					// Se o objeto de baixo deixa-me passar (ex: HoledWall deixa passar SmallObject),
					// então ele NÃO serve de chão. Eu caio para dentro dele.
					if (other instanceof Transpassable && ((Transpassable) other).isPassableFor(this)) {
						return false;
					}
					// ---------------------------------------
					
					return true;
				}
			}
		}
		return false;
	}
}