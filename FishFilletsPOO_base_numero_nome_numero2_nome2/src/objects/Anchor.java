package objects;

import pt.iscte.poo.game.Room;

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
}