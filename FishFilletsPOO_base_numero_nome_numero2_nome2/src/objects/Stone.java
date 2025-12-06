package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Stone extends MovableObject {

	private boolean hasMoved = false;

	public Stone(Room room) {
		super(room, true, true, true); // Pesado
	}

	@Override
	public String getName() { return "stone"; }

	@Override
	public int getLayer() { return 3; }
	
	public boolean hasMoved() { return hasMoved; }
	
	public void setMoved(boolean moved) { this.hasMoved = moved; }

	@Override
	public boolean interact(GameCharacter actor, Direction dir, GameEngine engine) {
		// Tenta empurrar usando a lógica da classe pai (MovableObject)
		boolean moved = super.interact(actor, dir, engine);
		
		// Se moveu e ainda não tinha movido, liberta o caranguejo
		if (moved && !hasMoved) {
			hasMoved = true;
			spawnCrab(engine);
		}
		return moved;
	}

	private void spawnCrab(GameEngine engine) {
		// Spawn na posição acima da pedra
		Point2D spawnPos = getPosition().plus(Direction.UP.asVector());
		
		if (engine.getObjectAt(spawnPos) == null) {
			Crab crab = new Crab(getRoom());
			crab.setPosition(spawnPos);
			getRoom().addObject(crab);
		}
	}
}