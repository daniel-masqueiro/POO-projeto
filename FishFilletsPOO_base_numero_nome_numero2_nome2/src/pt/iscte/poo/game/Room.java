package pt.iscte.poo.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import objects.Water;
import objects.BigFish;
import objects.GameObject;
import objects.SmallFish;
import pt.iscte.poo.utils.Point2D;

public class Room {
	
	private List<GameObject> objects;
	private String roomName;
	private GameEngine engine;
	private Point2D smallFishStartingPosition;
	private Point2D bigFishStartingPosition;
	
	public Room() {
		objects = new ArrayList<GameObject>();
	}

	private void setName(String name) {
		roomName = name;
	}
	
	public String getName() {
		return roomName;
	}
	
	private void setEngine(GameEngine engine) {
		this.engine = engine;
	}

	public void addObject(GameObject obj) {
		objects.add(obj);
		engine.updateGUI();
	}
	
	public void removeObject(GameObject obj) {
		objects.remove(obj);
		engine.updateGUI();
	}
	
	public List<GameObject> getObjects() {
		return objects;
	}

	public void setSmallFishStartingPosition(Point2D heroStartingPosition) {
		this.smallFishStartingPosition = heroStartingPosition;
	}
	
	public Point2D getSmallFishStartingPosition() {
		return smallFishStartingPosition;
	}
	
	public void setBigFishStartingPosition(Point2D heroStartingPosition) {
		this.bigFishStartingPosition = heroStartingPosition;
	}
	
	public Point2D getBigFishStartingPosition() {
		return bigFishStartingPosition;
	}
	
	public static Room readRoom(File f, GameEngine engine) {
		Room r = new Room();
		r.setEngine(engine);
		r.setName(f.getName());
		
		GameObject water = new Water(r);
		water.setPosition(new Point2D(0, 0));
		r.addObject(water);
		
		GameObject bf = BigFish.getInstance();
		bf.setPosition(2, 2);
		r.addObject(bf);
		
		GameObject sf = SmallFish.getInstance();
		sf.setPosition(3, 3);
		r.addObject(sf);
		
		return r;
		
	}
	
}