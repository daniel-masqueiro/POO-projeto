package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public class HoledWall extends GameObject implements Transpassable {

	public HoledWall(Room room) {
		super(room, true, true);
	}

	@Override
	public String getName() { return "holedWall"; }

	@Override
	public int getLayer() { return 0; }
	
	@Override
	public boolean isPassableFor(GameObject obj) {
		if (obj instanceof MovableElement) {
			return ((MovableElement) obj).isSmall();
		}
		return false;
	}

	@Override
	public boolean interact(GameCharacter actor, Direction dir, GameEngine engine) {
		return isPassableFor(actor);
	}
}