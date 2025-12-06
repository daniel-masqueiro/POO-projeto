package objects;

import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public class HoledWall extends GameObject {

	public HoledWall(Room room) {
		super(room, true, true); // Sólido = true (GameEngine trata a exceção via interact)
	}

	@Override
	public String getName() { return "holedWall"; }

	@Override
	public int getLayer() { return 3; }
	
	@Override
	public boolean interact(GameCharacter actor, Direction dir, GameEngine engine) {
		// Regra: Só o peixe pequeno passa
		if (actor.isSmall()) {
			return true;
		}
		return false; // Bloqueia peixe grande ou outros objetos
	}
}