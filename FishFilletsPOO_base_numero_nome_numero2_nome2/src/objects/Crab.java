package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Crab extends GameCharacter implements Dangerous {

	public Crab(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "krab";
	}

	@Override
	public int getLayer() {
		return 2;
	}

	@Override
	public boolean isSmall() {
		return true;
	}

	@Override
	public boolean isEnemy() {
		return true;
	}

	@Override
	public int getPushLimit(Direction dir) {
		return 0;
	}

	@Override
	public boolean canPushHeavy() {
		return false;
	}

	@Override
	public int getSupportLimit() {
		return 0;
	}

	@Override
	public boolean canSupportHeavy() {
		return false;
	}

	@Override
	public boolean isLethalTo(GameCharacter character) {
		return character instanceof SmallFish;
	}

	public void moveRandomly(GameEngine engine) {
	    Direction dir = Math.random() < 0.5 ? Direction.LEFT : Direction.RIGHT;
	    Point2D targetPos = getPosition().plus(dir.asVector());

	    if (!ImageGUI.getInstance().isWithinBounds(targetPos)) return;

	    GameObject target = engine.getObjectAt(targetPos);

	    if (target == null) {
	        move(dir.asVector());
	    } else {
	        target.interact(this, dir, engine);
	    }
	}
	@Override
	public boolean interact(GameCharacter actor, Direction dir, GameEngine engine) {
	    // Cenário 1: O Peixe Pequeno tenta entrar na casa do Caranguejo
	    if (isLethalTo(actor)) {
	        actor.setFishDeath(true);
	        engine.triggerGameOver("O Peixe Pequeno foi apanhado pelo caranguejo!");
	        return true; // Retorna true para permitir o movimento visual (sobreposição) antes do reset
	    }
	    
	    // Cenário 2: O Peixe Grande tenta entrar na casa do Caranguejo
	    // O requisito diz: "é morto se tocar no peixe grande"
	    if (actor instanceof BigFish) {
	        engine.getCurrentRoom().removeObject(this); // O caranguejo morre/desaparece
	        return true; // O Peixe Grande ocupa o lugar onde estava o caranguejo
	    }
	    
	    // Default: Funciona como parede para outros objetos
	    return false;
	}
}