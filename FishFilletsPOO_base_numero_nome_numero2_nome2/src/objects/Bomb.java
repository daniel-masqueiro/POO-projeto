package objects;

import java.util.ArrayList;
import java.util.List;
import pt.iscte.poo.game.GameEngine;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Bomb extends MovableObject {

	public Bomb(Room room) {
		super(room, true, true, false); // Leve
	}
	@Override
	public String getName() { return "bomb"; }	
	@Override
	public int getLayer() { return 3; }
	
	@Override
	public void processGravity(GameEngine engine) {
		if (!isSupported()) {
			move(Direction.DOWN.asVector());
			
			if (isSupported()) {
				Point2D posBelow = getPosition().plus(Direction.DOWN.asVector());
				GameObject support = engine.getObjectAt(posBelow);
				
				if (support instanceof GameCharacter) {
					return; 
				}
				
				explode(engine);
			}
		}
	}

	private void explode(GameEngine engine) {
		Point2D center = getPosition();
		List<Point2D> zone = center.getNeighbourhoodPoints();
		zone.add(center);

		List<GameObject> toRemove = new ArrayList<>();
		boolean fishDied = false;
		String deadFishName = "";

		for (GameObject obj : new ArrayList<>(getRoom().getObjects())) {
			if (zone.contains(obj.getPosition())) {
				if (obj instanceof GameCharacter) {
					((GameCharacter) obj).setFishDeath(true);
					fishDied = true;
					deadFishName = obj.getName();
				} else {
					toRemove.add(obj);
				}
			}
		}

		for (GameObject obj : toRemove) getRoom().removeObject(obj);
		
		for (Point2D p : zone) {
			if (engine.getObjectAt(p) == null) {
				Water w = new Water(getRoom());
				w.setPosition(p);
				getRoom().addObject(w);
			}
		}

		engine.updateGUI();

		if (fishDied) {
			engine.triggerGameOver("A bomba explodiu o " + deadFishName + "!");
		}
	}
}