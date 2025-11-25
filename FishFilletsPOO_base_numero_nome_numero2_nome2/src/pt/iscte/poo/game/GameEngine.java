package pt.iscte.poo.game;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import objects.BigFish;
import objects.GameCharacter;
import objects.GameObject;
import objects.Heavy;
import objects.HoledWall;
import objects.MovableObject;
import objects.SmallFish;
import objects.Solid;
import objects.Support;
import objects.Trap;
import objects.Trunk;
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

	public GameEngine() {
		this.currentLevelFile = "room0.txt";
		rooms = new HashMap<String, Room>();
		loadGame();
		currentRoom = rooms.get(currentLevelFile);
		updateGUI();
		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);
		this.isSmallFishTurn = true;
		this.lastTickProcessed = 0;
		this.numberOfMoves = 0;
		this.ticksAtLevelStart = 0;
	}

	public String isSmallFishTurn() {
		if (isSmallFishTurn)
			return "SmallFish turn";
		return "BigFish turn";
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
				return;
			}

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
					if (isMoveValid(targetPos, dir)) {
						activeFish.setFacingDirection(dir);
						activeFish.move(dir.asVector());
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
		ImageGUI.getInstance().setStatusMessage(isSmallFishTurn() + " | time passed: " + ticksToTime()
				+ " | number of moves made: " + String.valueOf(numberOfMoves));
	}

	private boolean isMoveValid(Point2D targetPos, Direction dir) {
		List<GameObject> allObjects = currentRoom.getObjects();

		for (GameObject obj : allObjects) {
			if (!obj.getPosition().equals(targetPos)) {
				continue;
			}

			if (obj instanceof GameCharacter) {
				return false;
			}

			if (obj instanceof Trap) {
				if (!isSmallFishTurn) {
					BigFish.getInstance().setFishDeath(true);
					BigFish.getInstance().move(dir.asVector());
					triggerGameOver("O peixe grande morreu na armadilha! Clica OK para voltar ao início.");
					return false;
				}
			}

			if (obj instanceof MovableObject) {
				return pushMovable((MovableObject) obj, dir);
			}

			if (obj instanceof Solid && ((Solid) obj).isSolid()) {
				if (obj instanceof HoledWall && isSmallFishTurn) {
					continue;
				}
				return false;
			}
		}
		return true;
	}

	private boolean pushMovable(MovableObject firstObj, Direction dir) {
		List<MovableObject> chain = new ArrayList<>();
		chain.add(firstObj);

		Point2D nextPos = firstObj.getPosition().plus(dir.asVector());
		MovableObject nextObj = getMovableObjectAt(nextPos);

		while (nextObj != null) {
			chain.add(nextObj);
			nextPos = nextObj.getPosition().plus(dir.asVector());
			nextObj = getMovableObjectAt(nextPos);
		}

		// CORREÇÃO: Verificar TODOS os objetos na posição final para encontrar bloqueios
		for (GameObject obj : currentRoom.getObjects()) {
			if (obj.getPosition().equals(nextPos)) {
				if (obj instanceof GameCharacter)
					return false;

				if (obj instanceof Solid && ((Solid) obj).isSolid()) {
					if (obj instanceof MovableObject)
						continue; // Ignora o objeto que estamos a empurrar (já está na chain)

					if (obj instanceof HoledWall && isSmallFishTurn) {
						continue;
					}
					return false; // Encontrou parede ou obstáculo sólido -> Bloqueia
				}
			}
		}

		GameCharacter activeFish = isSmallFishTurn ? SmallFish.getInstance() : BigFish.getInstance();

		// Regra: Se excede o limite, APENAS BLOQUEIA (return false), NÃO MATA.
		if (chain.size() > activeFish.getPushLimit()) {
			return false;
		}

		boolean hasHeavy = false;
		for (MovableObject o : chain) {
			if (o instanceof Heavy && ((Heavy) o).isHeavy()) {
				hasHeavy = true;
				break;
			}
		}

		if (hasHeavy && !activeFish.canPushHeavy()) {
			return false;
		}

		for (int i = chain.size() - 1; i >= 0; i--) {
			MovableObject obj = chain.get(i);
			obj.move(dir.asVector());
		}
		return true;
	}

	private boolean isSupported(MovableObject obj) {
		Point2D posBelow = obj.getPosition().plus(Direction.DOWN.asVector());
		for (GameObject other_obj : currentRoom.getObjects()) {
			if (other_obj.getPosition().equals(posBelow)) {
				if (other_obj instanceof Support && ((Support) other_obj).isSupport()) {
					return true;
				}
			}
		}
		return false;
	}

	private void processTick() {
		lastTickProcessed++;
		List<GameObject> allObjects = new ArrayList<>(currentRoom.getObjects());

		for (GameObject obj : allObjects) {
			if (obj instanceof MovableObject) {
				MovableObject m_obj = (MovableObject) obj;
				Point2D posBelow = m_obj.getPosition().plus(Direction.DOWN.asVector());

				if (SmallFish.getInstance().getPosition().equals(posBelow)) {
					if (m_obj instanceof Heavy && ((Heavy) m_obj).isHeavy()) {
						SmallFish.getInstance().setFishDeath(true);
						m_obj.move(Direction.DOWN.asVector());
						triggerGameOver("O peixe pequeno morreu esmagado! Clica OK para voltar ao início.");
						return;
					} else {
						continue;
					}
				}

				if (BigFish.getInstance().getPosition().equals(posBelow)) {
					continue;
				}

				Trunk trunkBelow = null;
				for (GameObject t : currentRoom.getObjects()) {
					if (t instanceof Trunk && t.getPosition().equals(posBelow)) {
						trunkBelow = (Trunk) t;
						break;
					}
				}

				if ((m_obj instanceof Heavy && ((Heavy) m_obj).isHeavy()) && trunkBelow != null) {
					currentRoom.getObjects().remove(trunkBelow);
					m_obj.move(Direction.DOWN.asVector());
					updateGUI();
					ImageGUI.getInstance().update();
					continue;
				}

				if (!isSupported(m_obj)) {
					m_obj.move(Direction.DOWN.asVector());
				}
			}
		}

		validateStackDeath(SmallFish.getInstance());
		validateStackDeath(BigFish.getInstance());
	}

	private void validateStackDeath(GameCharacter fish) {
		List<MovableObject> stack = new ArrayList<>();
		Point2D pos = fish.getPosition().plus(Direction.UP.asVector());
		while (true) {
			MovableObject obj = getMovableObjectAt(pos);
			if (obj == null)
				break;
			stack.add(obj);
			pos = pos.plus(Direction.UP.asVector());
		}

		if (stack.isEmpty())
			return;

		int heavyCount = 0;
		for (MovableObject o : stack) {
			if (o instanceof Heavy && ((Heavy) o).isHeavy())
				heavyCount++;
		}

		boolean dies = false;

		if (stack.size() > fish.getSupportLimit())
			dies = true;

		if (heavyCount > 0 && !fish.canSupportHeavy())
			dies = true;

		if (fish.canSupportHeavy()) {
			if (heavyCount > 1)
				dies = true;
			if (heavyCount == 1 && stack.size() > 1)
				dies = true;
		}

		if (dies) {
			fish.setFishDeath(true);
			triggerGameOver("O " + fish.getName() + " morreu esmagado pela carga!");
		}
	}

	public void updateGUI() {
		if (currentRoom != null) {
			ImageGUI.getInstance().clearImages();
			ImageGUI.getInstance().addImages(currentRoom.getObjects());
		}
	}

	private String ticksToTime() {
		long ticksPassadosNoNivel = lastTickProcessed - ticksAtLevelStart;
		long totalSeconds = ticksPassadosNoNivel / 2;
		long minutes = totalSeconds / 60;
		long remainingSeconds = totalSeconds % 60;
		return String.format("%dm%02ds", minutes, remainingSeconds);
	}

	private MovableObject getMovableObjectAt(Point2D p) {
		for (GameObject obj : currentRoom.getObjects()) {
			if (obj.getPosition().equals(p) && obj instanceof MovableObject && !(obj instanceof GameCharacter)) {
				return (MovableObject) obj;
			}
		}
		return null;
	}

	private void triggerGameOver(String message) {
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
		this.ticksAtLevelStart = this.lastTickProcessed;

		updateGUI();
		ImageGUI.getInstance().update();
		ImageGUI.getInstance().showMessage("Reinício", "Nível reiniciado");
	}
}