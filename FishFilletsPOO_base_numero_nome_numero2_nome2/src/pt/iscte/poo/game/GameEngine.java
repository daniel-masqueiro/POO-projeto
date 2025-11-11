package pt.iscte.poo.game;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import objects.SmallFish;
import objects.SteelHorizontal;
import objects.SteelVertical;
import objects.Trunk;
import objects.Wall;
import objects.BigFish;
import objects.GameCharacter;
import objects.GameObject;
import objects.HoledWall;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class GameEngine implements Observer {

	private Map<String, Room> rooms;
	private Room currentRoom;
	private int lastTickProcessed = 0;
	private boolean isSmallFishTurn;

	public GameEngine() {
		rooms = new HashMap<String, Room>();
		loadGame();
		currentRoom = rooms.get("room0.txt");
		updateGUI();
		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);
		this.isSmallFishTurn = true;
	}

	public String isSmallFishTurn() {
		if (isSmallFishTurn)
			return "SmallFish turn";
		return "BigFish turn";

	}

	private void loadGame() {
		File[] files = new File("./rooms").listFiles();
		for (File f : files) {
			rooms.put(f.getName(), Room.readRoom(f, this));
		}
	}

	@Override
	public void update(Observed source) {

		if (ImageGUI.getInstance().wasKeyPressed()) {
			int k = ImageGUI.getInstance().keyPressed();
			if (k == KeyEvent.VK_SPACE) {
				isSmallFishTurn = !isSmallFishTurn;
			} else {
				Direction dir = Direction.directionFor(k);
				
				if (dir != null) {
					
					GameCharacter activeFish;
					if (isSmallFishTurn) {
						activeFish = SmallFish.getInstance();
					} else {
						activeFish = BigFish.getInstance();
					}

					Point2D targetPos = activeFish.getPosition().plus(dir.asVector());
					if (isMoveValid(targetPos)) {
						activeFish.move(dir.asVector());
					}
				}
			}
		}
		int t = ImageGUI.getInstance().getTicks();
		while (lastTickProcessed < t) {
			processTick();
		}
		ImageGUI.getInstance().update();
		ImageGUI.getInstance().setStatusMessage(isSmallFishTurn());
	}

	private boolean isMoveValid(Point2D targetPos) {

		List<GameObject> allObjects = currentRoom.getObjects();

		for (GameObject obj : allObjects) {
			if (!obj.getPosition().equals(targetPos)) {
				continue;
			}
			if (obj instanceof Wall || obj instanceof SteelHorizontal || obj instanceof SteelVertical || obj instanceof GameCharacter) {
				return false;
			}
			
			if(!isSmallFishTurn) {
				if (obj instanceof HoledWall || obj instanceof Trunk ) {
					return false;
				}
			}
		}
		return true;
	}

	private void processTick() {
		lastTickProcessed++;
	}

	public void updateGUI() {
		if (currentRoom != null) {
			ImageGUI.getInstance().clearImages();
			ImageGUI.getInstance().addImages(currentRoom.getObjects());
		}
	}

}
