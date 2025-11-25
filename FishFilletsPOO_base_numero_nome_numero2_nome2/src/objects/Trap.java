package objects;
import pt.iscte.poo.game.Room;

public class Trap extends MovableObject {
	public Trap(Room room) {
		// isSolid=FALSE, isSupport=true, isHeavy=true
		super(room, false, true, true);
	}
	@Override
	public String getName() { return "trap"; }	
	@Override
	public int getLayer() { return 1; }
}