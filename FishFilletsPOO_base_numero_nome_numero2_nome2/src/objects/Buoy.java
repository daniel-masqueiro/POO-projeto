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
		// Regra: Peixe pequeno não consegue empurrar a boia para baixo
		if (actor.isSmall() && dir == Direction.DOWN) {
			return false;
		}
		return super.interact(actor, dir, engine);
	}

	@Override
	public void processGravity(GameEngine engine) {
	    Point2D posAbove = getPosition().plus(Direction.UP.asVector());
	    GameObject objAbove = engine.getObjectAt(posAbove);

	    // LÓGICA POLIMÓRFICA:
	    // Se tiver um objeto em cima, só me comporto como "pedra" (cair) 
	    // se esse objeto NÃO flutuar. 
	    // (Taça, Pedra, Bomba -> floats() é false -> eu caio)
	    // (Outra Boia -> floats() é true -> eu não caio)
	    if (objAbove instanceof MovableObject && !((MovableElement) objAbove).floats()) {
	        super.processGravity(engine);
	    } else {
	        // Lógica de flutuar (só sobe se não tiver nada sólido em cima)
	        if (objAbove == null || !objAbove.isSolid()) {
	            if (ImageGUI.getInstance().isWithinBounds(posAbove)) {
	                move(Direction.UP.asVector());
	            }
	        }
	        // Se objAbove for uma Boia (Solid e floats=true), entra aqui no 'else',
	        // falha no check '!objAbove.isSolid()', e fica parada (comportamento correto).
	    }
	}
	@Override
	public boolean floats() {
	    return true; // A Boia é o único objeto (por enquanto) que declara flutuar
	}
}