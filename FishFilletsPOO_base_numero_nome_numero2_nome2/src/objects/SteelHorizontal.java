package objects;
import pt.iscte.poo.game.Room;

public class SteelHorizontal extends GameObject {
	public SteelHorizontal(Room room) {
		super(room, true, true); // Sólido, Suporta
	}
	@Override
	public String getName() { return "steelHorizontal"; }
	@Override
	public int getLayer() { return 3; }
}