package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Buoy extends MovableObject {

	public Buoy(Room room) {
		super(room, true, true, false); // Sólido, Suporta, Leve
	}

	@Override
	public String getName() { return "buoy"; }

	@Override
	public int getLayer() { return 3; }
	
	@Override
	public boolean interact(GameCharacter actor, Direction dir, GameEngine engine) {
		if (actor.isSmall() && dir == Direction.DOWN) {
			return false;
		}return super.interact(actor, dir, engine);
	}
	
	@Override
	public void processGravity(GameEngine engine) {
		Point2D posAbove = getPosition().plus(Direction.UP.asVector());
		GameObject objAbove = engine.getObjectAt(posAbove);
		
		if (objAbove == null || !objAbove.isSolid()) {
			if (ImageGUI.getInstance().isWithinBounds(posAbove)) {
				move(Direction.UP.asVector());
			}
		}
	}
}