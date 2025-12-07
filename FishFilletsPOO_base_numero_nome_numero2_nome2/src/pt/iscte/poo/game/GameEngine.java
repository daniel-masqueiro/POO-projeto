package pt.iscte.poo.game;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import objects.BigFish;
import objects.Crab;
import objects.GameCharacter;
import objects.GameObject;
import objects.MovableElement;
import objects.MovableObject;
import objects.SmallFish;

import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class GameEngine implements Observer {

	private Map<String, Room> rooms;
	private Room currentRoom;
	private int lastTickProcessed;
	private boolean isSmallFishTurn;
	private int numberOfMoves;
	private String currentLevelFile;
	private int ticksAtLevelStart;
	private int numberFish;
	private int totalMovesRun;
	private int totalTimeRun;

	public GameEngine() {
		this.currentLevelFile = "room0.txt";
		this.rooms = new HashMap<>();
		this.totalMovesRun = 0;
		this.totalTimeRun = 0;
		loadGame();
		currentRoom = rooms.get(currentLevelFile);
		updateGUI();
		SmallFish.getInstance().setRoom(currentRoom);
		if (currentRoom.getSmallFishStartingPosition() != null) {
			SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());
		}
		BigFish.getInstance().setRoom(currentRoom);
		if (currentRoom.getBigFishStartingPosition() != null) {
			BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());
		}
		this.isSmallFishTurn = true;
		this.lastTickProcessed = 0;
		this.numberOfMoves = 0;
		this.ticksAtLevelStart = 0;
		this.numberFish = 2;
	}

	public String isSmallFishTurn() {
		return isSmallFishTurn ? "SmallFish turn" : "BigFish turn";
	}

	public Room getCurrentRoom() {
		return currentRoom;
	}

	public GameObject getObjectAt(Point2D p) {
		if (currentRoom == null)
			return null;
		GameObject movable = null;
		GameObject staticObj = null;

		for (GameObject obj : currentRoom.getObjects()) {
			if (obj.getPosition().equals(p) && obj.isSolid()) {
				if (obj instanceof MovableObject) {
					movable = obj;
				} else {
					staticObj = obj;
				}
			}
		}
		return movable != null ? movable : staticObj;
	}

	private void loadGame() {
		File[] files = new File("./rooms").listFiles();
		if (files != null) {
			for (File f : files) {
				rooms.put(f.getName(), Room.readRoom(f, this));
			}
		}
	}

	@Override
	public void update(Observed source) {
		if (ImageGUI.getInstance().wasKeyPressed()) {
			int k = ImageGUI.getInstance().keyPressed();

			if (k == KeyEvent.VK_R) {
				restartLevel();
				ImageGUI.getInstance().showMessage("Reinício", "Nível reiniciado");
				return;
			}

			if (k == KeyEvent.VK_SPACE && numberFish > 1) {
				isSmallFishTurn = !isSmallFishTurn;
			} else {
				Direction dir = Direction.directionFor(k);

				if (dir != null) {
					GameCharacter activeFish = isSmallFishTurn ? SmallFish.getInstance() : BigFish.getInstance();
					Point2D targetPos = activeFish.getPosition().plus(dir.asVector());
					if (isMoveValid(targetPos, dir)) {
						activeFish.setFacingDirection(dir);
						activeFish.move(dir.asVector());
						moveEnemies();

						if (!ImageGUI.getInstance().isWithinBounds(activeFish.getPosition())) {
							numberFish--;
							currentRoom.removeObject(activeFish);
							isSmallFishTurn = !isSmallFishTurn;
							if (numberFish == 0) {
								loadNextLevel();
							}
						}
						numberOfMoves++;
					}
				}
			}
		}

		int t = ImageGUI.getInstance().getTicks();
		while (lastTickProcessed < t) {
			processTick();
		}

		ImageGUI.getInstance().update();
		ImageGUI.getInstance().setStatusMessage(
				isSmallFishTurn() + " | time passed: " + ticksToTime() + " | number of moves made: " + numberOfMoves);
	}

	private boolean isMoveValid(Point2D targetPos, Direction dir) {
		GameCharacter activeFish = isSmallFishTurn ? SmallFish.getInstance() : BigFish.getInstance();

		List<GameObject> allObjects = new ArrayList<>();
		for (GameObject obj : currentRoom.getObjects()) {
			if (obj.getPosition().equals(targetPos) && obj.isSolid()) {
				allObjects.add(obj);
			}
		}

		if (allObjects.isEmpty())
			return true;

		for (GameObject obj : allObjects) {
			if (!(obj instanceof MovableObject)) {
				if (!obj.interact(activeFish, dir, this)) {
					return false;
				}
			}
		}

		boolean canPass = true;
		for (GameObject obj : allObjects) {
			if (obj instanceof MovableObject) {
				if (!obj.interact(activeFish, dir, this)) {
					canPass = false;
				}
			}
		}

		return canPass;
	}

	private void moveEnemies() {
		for (GameObject obj : new ArrayList<>(currentRoom.getObjects())) {
			if (obj instanceof Crab) {
				((Crab) obj).moveRandomly(this);
			}
		}
	}

	private void processTick() {
		lastTickProcessed++;
		List<GameObject> allObjects = new ArrayList<>(currentRoom.getObjects());

		for (GameObject obj : allObjects) {
			if (obj instanceof MovableElement) {
				((MovableElement) obj).processGravity(this);
			}
		}

		SmallFish.getInstance().validateCrushing(this);
		BigFish.getInstance().validateCrushing(this);
	}

	private void loadNextLevel() {
		totalMovesRun += numberOfMoves;
		totalTimeRun += (lastTickProcessed - ticksAtLevelStart) / 2;
		String numberStr = currentLevelFile.replaceAll("\\D+", "");
		int nextNum = Integer.parseInt(numberStr) + 1;
		String nextLevelFile = "room" + nextNum + ".txt";
		File nextFile = new File("rooms/" + nextLevelFile);

		if (nextFile.exists()) {
			currentLevelFile = nextLevelFile;
			restartLevel();
			ImageGUI.getInstance().showMessage("Nível Concluído!", "A carregar o nivel " + nextNum + "...");
		} else {
			handleVictory();
		}
	}

	private void handleVictory() {
		ImageGUI.getInstance().showMessage("VITÓRIA!", "Parabéns, completaste todos os níveis!");
		String name = ImageGUI.getInstance().askUser("Introduz o teu nome para o Highscore:");
		if (name == null || name.trim().isEmpty())
			name = "Anónimo";
		HighScore.registerScore(name, totalMovesRun, totalTimeRun);
		ImageGUI.getInstance().showMessage("Highscores", HighScore.getBoard());
		System.exit(0);
	}

	public void updateGUI() {
		if (currentRoom != null) {
			ImageGUI.getInstance().clearImages();
			ImageGUI.getInstance().addImages(currentRoom.getObjects());
		}
	}

	private String ticksToTime() {
		long ticks = lastTickProcessed - ticksAtLevelStart;
		long totalSeconds = ticks / 2;
		long minutes = totalSeconds / 60;
		long sec = totalSeconds % 60;
		return String.format("%dm%02ds", minutes, sec);
	}

	public void triggerGameOver(String message) {
		updateGUI();
		ImageGUI.getInstance().update();
		ImageGUI.getInstance().showMessage("Game Over", message);
		restartLevel();
	}

	private void restartLevel() {
		File file = new File("rooms/" + currentLevelFile);
		Room resetRoom = Room.readRoom(file, this);
		rooms.put(currentLevelFile, resetRoom);
		currentRoom = resetRoom;
		SmallFish.getInstance().setRoom(currentRoom);
		SmallFish.getInstance().setFishDeath(false);
		BigFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setFishDeath(false);
		this.isSmallFishTurn = true;
		this.lastTickProcessed = ImageGUI.getInstance().getTicks();
		this.numberOfMoves = 0;
		this.ticksAtLevelStart = lastTickProcessed;
		this.numberFish = 2;
		updateGUI();
		ImageGUI.getInstance().update();
	}
}