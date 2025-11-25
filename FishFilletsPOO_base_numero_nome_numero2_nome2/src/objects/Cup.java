package objects;
import pt.iscte.poo.game.Room;

public class Cup extends MovableObject {
	public Cup(Room room) {
		// isSolid=true, isSupport=true, isHeavy=FALSE
		super(room, true, true, false);
	}
	@Override
	public String getName() { return "cup"; }	
	@Override
	public int getLayer() { return 3; }
}