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

		if (!ImageGUI.getInstance().isWithinBounds(targetPos))
			return;

		GameObject target = engine.getObjectAt(targetPos);

		if (target == null) {
			move(dir.asVector());
		} else {
			if (target.interact(this, dir, engine)) {
				this.move(dir.asVector());
			}
		}
	}

	@Override
	public boolean interact(GameCharacter actor, Direction dir, GameEngine engine) {
		if (isLethalTo(actor)) {
			actor.setFishDeath(true);
			engine.triggerGameOver("O Peixe Pequeno foi apanhado pelo caranguejo!");
			return true;
		}
		if (actor instanceof BigFish) {
			engine.getCurrentRoom().removeObject(this);
			return true;
		}
		return false;
	}

	@Override
	public void processGravity(GameEngine engine) {
		if (!isSupported()) {
			move(Direction.DOWN.asVector());
		}
	}
}