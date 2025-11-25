package objects;
import pt.iscte.poo.game.Room;

public class Anchor extends MovableObject {
	public Anchor(Room room) {
		// Sólido=true, Suporta=true, Pesado=true
		super(room, true, true, true);
	}
	@Override
	public String getName() { return "anchor"; }	
	@Override
	public int getLayer() { return 3; }
}