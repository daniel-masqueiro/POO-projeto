package objects;
import pt.iscte.poo.game.Room;

public class Bomb extends MovableObject {
	public Bomb(Room room) {
		// Sólido=true, Suporta=true, Pesado=FALSE (Leve)
		super(room, true, true, false);
	}
	@Override
	public String getName() { return "bomb"; }	
	@Override
	public int getLayer() { return 3; }
	
	public int explosionRange() {
		return 1;
	}
}