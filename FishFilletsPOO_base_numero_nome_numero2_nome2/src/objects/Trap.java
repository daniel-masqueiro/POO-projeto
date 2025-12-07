package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Trap extends MovableObject implements Dangerous, Transpassable {

	public Trap(Room room) {
		super(room, true, true, true);
	}

	@Override
	public String getName() { return "trap"; }

	@Override
	public int getLayer() { return 1; }

	@Override
	public boolean isLethalTo(GameCharacter character) {
		return character instanceof BigFish || character instanceof Crab;
	}

	@Override
	public boolean isPassableFor(GameObject obj) {
		return obj instanceof GameCharacter;
	}

	@Override
	public boolean interact(GameCharacter actor, Direction dir, GameEngine engine) {
		// 1. Verifica se alguém está a tentar ENTRAR diretamente (passos)
		if (isPassableFor(actor)) {
			Point2D actorsNextPosition = actor.getPosition().plus(dir.asVector());
			
			// Se o ator vai mesmo pisar a armadilha
			if (actorsNextPosition.equals(this.getPosition())) {
				if (isLethalTo(actor) || actor.isEnemy()) {
					if (actor.isEnemy()) {
						engine.getCurrentRoom().removeObject(actor);
					} else {
						actor.setFishDeath(true);
						engine.triggerGameOver("O " + actor.getName() + " morreu na armadilha!");
					}
					return true; 
				}
				return true; 
			}
		}
		return super.interact(actor, dir, engine);
	}
}