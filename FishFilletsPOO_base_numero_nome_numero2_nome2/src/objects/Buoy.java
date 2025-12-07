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
	public String getName() {
		return "buoy";
	}

	@Override
	public int getLayer() {
		return 3;
	}

	@Override
	public boolean interact(GameCharacter actor, Direction dir, GameEngine engine) {
		if (actor.isSmall() && dir == Direction.DOWN) {
			return false;
		}
		return super.interact(actor, dir, engine);
	}

	@Override
	public void processGravity(GameEngine engine) {
		// Se tiver peso algures na pilha acima, a boia tem de afundar
		if (hasWeightAbove(engine)) {
			
			// 1. Guardar onde a boia está ANTES de cair
			Point2D originalPos = getPosition();
			
			// 2. Tentar mover a boia para baixo (comportamento de objeto pesado)
			super.processGravity(engine); 
			
			// 3. Se a boia mudou de sítio (caíu), temos de puxar tudo o que está em cima
			// para preencher o "buraco" deixado na originalPos imediatamente.
			if (!getPosition().equals(originalPos)) {
				pullChainDown(engine, originalPos);
			}
			
		} else {
			// Lógica de flutuar (só sobe se não tiver nada sólido em cima)
			Point2D posAbove = getPosition().plus(Direction.UP.asVector());
			GameObject objAbove = engine.getObjectAt(posAbove);
			
			if (objAbove == null || !objAbove.isSolid()) {
				if (ImageGUI.getInstance().isWithinBounds(posAbove)) {
					move(Direction.UP.asVector());
				}
			}
		}
	}

	// Método que sobe a pilha e puxa todos os objetos móveis um nível para baixo
	private void pullChainDown(GameEngine engine, Point2D holePosition) {
		Point2D currentHole = holePosition;
		Point2D nextPosUp = currentHole.plus(Direction.UP.asVector());
		
		GameObject objAbove = engine.getObjectAt(nextPosUp);
		
		// Enquanto houver objetos móveis para cima (Âncoras, Pedras, outras Boias...)
		while (objAbove instanceof MovableElement) {
			// Puxa o objeto para baixo, para tapar o buraco
			((MovableElement) objAbove).move(Direction.DOWN.asVector());
			
			// O "buraco" sobe para onde o objeto estava
			currentHole = nextPosUp;
			nextPosUp = currentHole.plus(Direction.UP.asVector());
			
			// Vê o que está em cima desse para continuar o ciclo
			objAbove = engine.getObjectAt(nextPosUp);
		}
	}
	
	private boolean hasWeightAbove(GameEngine engine) {
		Point2D currentPos = getPosition().plus(Direction.UP.asVector());
		
		while (true) {
			GameObject obj = engine.getObjectAt(currentPos);
			if (obj == null || !obj.isSolid()) return false;
			if (!(obj instanceof MovableElement)) return false; 
			if (!((MovableElement) obj).floats()) {
				return true;
			}
			currentPos = currentPos.plus(Direction.UP.asVector());
		}
	}
	@Override
	public boolean floats() {
	    return true;
	}
}