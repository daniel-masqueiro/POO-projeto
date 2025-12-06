package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public abstract class GameCharacter extends MovableElement implements Solid, PushAgent {

	protected Direction facingDirection = Direction.LEFT;
	protected boolean isDead = false;
	
	public GameCharacter(Room room) {
		super(room, true, true);
	}

	@Override
	public int getLayer() { return 2; }

	public void setFacingDirection(Direction dir) {
		if (dir == Direction.LEFT || dir == Direction.RIGHT) {
			this.facingDirection = dir;
		}
	}

	public void setFishDeath(boolean isDead) { this.isDead = isDead; }
	public boolean isDead() { return isDead; }
	public boolean isEnemy() { return false; }

	@Override
	public void processGravity(GameEngine engine) { }

	// CORREÇÃO: Lógica de morte por esmagamento (Stack Death)
	public void validateCrushing(GameEngine engine) {
		int itemsAbove = 0;
		int heavyCount = 0; // Contador de objetos pesados
		
		Point2D currentPos = getPosition().plus(Direction.UP.asVector());
		
		while (true) {
			GameObject obj = engine.getObjectAt(currentPos);
			
			if (obj == null || !obj.isSolid() || !(obj instanceof MovableElement)) {
				break;
			}
			
			itemsAbove++;
			if (obj instanceof Heavy && ((Heavy)obj).isHeavy()) {
				heavyCount++;
			}
			
			currentPos = currentPos.plus(Direction.UP.asVector());
		}
		
		boolean dies = false;
		
		// 1. Regra para quem NÃO suporta pesados (Peixe Pequeno)
		// Se tiver QUALQUER pesado em cima -> morre
		if (heavyCount > 0 && !canSupportHeavy()) {
			dies = true;
		}
		
		// 2. Regra para quem SUPORTA pesados (Peixe Grande)
		// "suporte de VÁRIOS objectos pesados provoca a sua morte" (vários > 1)
		if (canSupportHeavy() && heavyCount > 1) {
			dies = true;
		}

		// 3. Regra de quantidade total (Limite de items)
		if (itemsAbove > getSupportLimit()) {
			dies = true;
		}
		
		if (dies) {
			setFishDeath(true);
			engine.triggerGameOver(getName() + " foi esmagado!");
		}
	}
}