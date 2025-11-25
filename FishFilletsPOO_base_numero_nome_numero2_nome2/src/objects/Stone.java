package objects;
import pt.iscte.poo.game.Room;

public class Stone extends MovableObject {
	public Stone(Room room) {
		// isSolid=true, isSupport=true, isHeavy=true
		super(room, true, true, true);
	}
	@Override
	public String getName() { return "stone"; }	
	@Override
	public int getLayer() { return 3; }
}