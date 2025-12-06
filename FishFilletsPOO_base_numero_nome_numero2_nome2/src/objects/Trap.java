package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public class Trap extends MovableObject implements Dangerous {

	public Trap(Room room) {
		super(room, true, true, true); // Sólido=false (peq. peixe passa), Suporta=true, Pesado=true
	}

	@Override
	public String getName() { return "trap"; }

	@Override
	public int getLayer() { return 1; }

	@Override
	public boolean isLethalTo(GameCharacter character) {
		return character instanceof BigFish;
	}

	@Override
	public boolean interact(GameCharacter actor, Direction dir, GameEngine engine) {
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