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
		if (isPassableFor(actor)) {
			Point2D actorsNextPosition = actor.getPosition().plus(dir.asVector());
			if (actorsNextPosition.equals(this.getPosition())) {
				if (isLethalTo(actor) || actor.isEnemy()) {
					if (actor.isEnemy()) {
						engine.getCurrentRoom().removeObject(actor);
					} else {
						String nomePeixe = (actor instanceof SmallFish) ? "Peixe Pequeno" : "Peixe Grande";
						actor.setFishDeath(true);
						engine.triggerGameOver("O " + nomePeixe + " morreu na armadilha!");
					}
					return true; 
				}
				return true; 
			}
		}
		return super.interact(actor, dir, engine);
	}
	@Override
    public void processGravity(GameEngine engine) {
        // Verificar o que está imediatamente abaixo
        Point2D posBelow = getPosition().plus(Direction.DOWN.asVector());
        
        for (GameObject obj : engine.getCurrentRoom().getObjects()) {
            if (obj.getPosition().equals(posBelow)) {
                // Se estiver um Peixe Grande em baixo, a armadilha esmaga-o
                if (obj instanceof BigFish) {
                    ((BigFish) obj).setFishDeath(true);
                    engine.triggerGameOver("O Peixe Grande foi esmagado pela armadilha!");
                    return;
                }
            }
        }
        
        // Se não matou ninguém, executa a gravidade normal (cair se não houver chão)
        super.processGravity(engine);
    }
}