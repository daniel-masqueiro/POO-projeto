package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;

public abstract class GameObject implements ImageTile, Solid, Support {
	
	private Point2D position;
	private Room room;
	private boolean isSolid;
	private boolean isSupport;
	
	public GameObject(Room room, boolean isSolid, boolean isSupport) {
		this.room = room;
		this.isSolid = isSolid;
		this.isSupport = isSupport;
	}
	public GameObject(Point2D position, Room room, boolean isSolid, boolean isSupport) {
		this.position = position;
		this.room = room;
		this.isSolid = isSolid;
		this.isSupport = isSupport;
	}
	@Override
	public boolean isSolid() {
		return isSolid;
	}

	@Override
	public boolean isSupport() {
		return isSupport;
	}

	public void setPosition(int i, int j) {
		position = new Point2D(i, j);
	}
	
	public void setPosition(Point2D position) {
		this.position = position;
	}

	@Override
	public Point2D getPosition() {
		return position;
	}
	
	public Room getRoom() {
		return room;
	}
	
	public void setRoom(Room room) {
		this.room = room;
	}
}