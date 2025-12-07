package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public class Anchor extends MovableObject {

	private boolean hasMovedHorizontally = false;

	public Anchor(Room room) {
		// Sólido=true, Suporta=true, Pesado=true
		super(room, true, true, true);
	}

	@Override
	public String getName() {
		return "anchor";
	}

	@Override
	public int getLayer() {
		return 3;
	}

	public boolean hasMovedHorizontally() {
		return hasMovedHorizontally;
	}

	public void setMovedHorizontally(boolean moved) {
		this.hasMovedHorizontally = moved;
	}
	@Override
	public boolean interact(GameCharacter actor, Direction dir, GameEngine engine) {
		if (dir == Direction.UP) {
            return false;
        }
		if ((dir == Direction.LEFT || dir == Direction.RIGHT) && hasMovedHorizontally) {
			return false;
		}
		boolean moved = super.interact(actor, dir, engine);
		if (moved && (dir == Direction.LEFT || dir == Direction.RIGHT)) {
			hasMovedHorizontally = true;
		}

		return moved;
	}
}