package pt.iscte.poo.game;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import objects.Anchor;
import objects.BigFish;
import objects.Bomb;
import objects.Buoy;
import objects.Crab;
import objects.Dangerous;
import objects.Floatable;
import objects.GameCharacter;
import objects.GameObject;
import objects.Heavy;
import objects.HoledWall;
import objects.MovableElement;
import objects.MovableObject;
import objects.SmallFish;
import objects.Solid;
import objects.Stone;
import objects.Support;
import objects.Trap;
import objects.Trunk;
import objects.Water;

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

	public GameEngine() {
		this.currentLevelFile = "room0.txt";
		this.rooms = new HashMap<>();

		loadGame();
		currentRoom = rooms.get(currentLevelFile);

		updateGUI();

		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);

		this.isSmallFishTurn = true;
		this.lastTickProcessed = 0;
		this.numberOfMoves = 0;
		this.ticksAtLevelStart = 0;
		this.numberFish = 2;
	}

	public String isSmallFishTurn() {
		return isSmallFishTurn ? "SmallFish turn" : "BigFish turn";
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

						if (!ImageGUI.getInstance().isWithinBounds(activeFish.getPosition())) {
							numberFish--;
							currentRoom.removeObject(activeFish);
							isSmallFishTurn = !isSmallFishTurn;
							if (numberFish == 0) {
								loadNextLevel();
							}
						}
						moveEnemies();
						
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

	private void loadNextLevel() {
        // 1. Acumular estatísticas do nível que acabou de ser concluído
        totalMovesRun += numberOfMoves;
        totalTimeRun += (lastTickProcessed - ticksAtLevelStart) / 2; // /2 porque cada segundo são 2 ticks (aprox)

        String numberStr = currentLevelFile.replaceAll("\\D+", "");
        int nextNum = Integer.parseInt(numberStr) + 1;
        String nextLevelFile = "room" + nextNum + ".txt";

        File nextFile = new File("rooms/" + nextLevelFile);

        if (nextFile.exists()) {
            currentLevelFile = nextLevelFile;
            restartLevel();
            ImageGUI.getInstance().showMessage("Nível Concluído!", "A carregar o nivel " + nextNum + "...");
        } else {
            // FIM DO JOGO - VITÓRIA
            handleVictory();
        }
    }

    private void handleVictory() {
        ImageGUI.getInstance().showMessage("VITÓRIA!", "Parabéns, completaste todos os níveis!");
        
        // Pedir nome ao utilizador
        String name = ImageGUI.getInstance().askUser("Introduz o teu nome para o Highscore:");
        if (name == null || name.trim().isEmpty()) {
            name = "Anónimo";
        }

        // Registar Score
        HighScoreManager manager = new HighScoreManager();
        manager.addScore(name, totalMovesRun, totalTimeRun);

        // Mostrar Tabela
        ImageGUI.getInstance().showMessage("Highscores", manager.getHighScoresBoard());
        
        // Opcional: Fechar jogo ou reiniciar tudo
        System.exit(0); 
    }

	private boolean isMoveValid(Point2D targetPos, Direction dir) {
		
		for (GameObject obj : currentRoom.getObjects()) {
			if (!obj.getPosition().equals(targetPos))
				continue;
			
			if (obj instanceof Dangerous) {
				GameCharacter activeFish = isSmallFishTurn ? SmallFish.getInstance() : BigFish.getInstance();
				
				if (((Dangerous) obj).isLethalTo(activeFish)) {
					activeFish.setFishDeath(true);
					activeFish.move(dir.asVector());
					triggerGameOver("O " + activeFish.getName() + " morreu devido a " + obj.getName() + "!");
					ImageGUI.getInstance().showMessage("Reinício", "Nível reiniciado");
					return false;
				}
			}

			if (obj.isSolid() && !(obj instanceof MovableObject)) {
				if (obj instanceof HoledWall) {
					GameCharacter activeFish = isSmallFishTurn ? SmallFish.getInstance() : BigFish.getInstance();
					if (!activeFish.isSmall()) {
						return false;
					}
				} else {
					return false;
				}
			}
		}

		for (GameObject obj : currentRoom.getObjects()) {
			if (!obj.getPosition().equals(targetPos))
				continue;

			if (obj instanceof GameCharacter)
				return false; 

			if (obj instanceof MovableObject) {
				if (obj instanceof Buoy && dir == Direction.DOWN && isSmallFishTurn) {
					return false;
				}
				if (obj.isSolid()) {
					return pushMovable((MovableObject) obj, dir);
				}
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
		
		for (GameObject tile : currentRoom.getObjects()) {
			if (!tile.getPosition().equals(nextPos))
				continue;

			if (tile instanceof GameCharacter)
				return false;

			if (tile instanceof Solid && ((Solid) tile).isSolid()) {
				if (tile instanceof MovableObject)
					continue;
				
				if (tile instanceof HoledWall) {
					MovableObject leadingObj = chain.get(chain.size() - 1);
					if (leadingObj.isSmall()) {
						continue;
					}
					return false;
				}

				return false; 
			}
		}

		GameCharacter activeFish = isSmallFishTurn ? SmallFish.getInstance() : BigFish.getInstance();

		if (chain.size() > activeFish.getPushLimit())
			return false;

		boolean hasHeavy = false;
		for (MovableObject o : chain) {
		    if (o instanceof Heavy && ((Heavy) o).isHeavy()) {
		        hasHeavy = true;
		        break;
		    }
		}

		if (hasHeavy && !activeFish.canPushHeavy())
			return false;
		
		for (MovableObject obj : chain) {
			if (obj instanceof Anchor) {
				Anchor anchor = (Anchor) obj;
				
				if (dir == Direction.UP) {
					return false;
				}
				
				if ((dir == Direction.LEFT || dir == Direction.RIGHT) && anchor.hasMovedHorizontally()) {
					return false;
				}
			}
		}
		for (int i = chain.size() - 1; i >= 0; i--) {
			MovableObject obj = chain.get(i);
			obj.move(dir.asVector());
			
			if (obj instanceof Stone) {
				Stone s = (Stone) obj;
				if (!s.hasMoved() && (dir == Direction.LEFT || dir == Direction.RIGHT)) {
					s.setMoved(true);
					spawnCrab(s.getPosition().plus(Direction.UP.asVector()));
				}
			}
			if (obj instanceof Anchor) {
				if (dir == Direction.LEFT || dir == Direction.RIGHT) {
					((Anchor) obj).setMovedHorizontally(true);
				}
			}
		}

		return true;
	}
	
	private void spawnCrab(Point2D position) {
		for (GameObject obj : currentRoom.getObjects()) {
			if (obj.getPosition().equals(position) && obj.isSolid()) {
				return;
			}
		}
		Crab crab = new Crab(currentRoom);
		crab.setPosition(position);
		currentRoom.addObject(crab);
	}

	private boolean isSupported(MovableElement obj) {
	    Point2D posBelow = obj.getPosition().plus(Direction.DOWN.asVector());

	    for (GameObject other : currentRoom.getObjects()) {
	        if (other.getPosition().equals(posBelow)) {
	            if (other instanceof Support && ((Support) other).isSupport()) {
	                if (other instanceof HoledWall && obj.isSmall()) {
	                    return false;
	                }
	                
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

			if (!(obj instanceof MovableElement))
				continue;

			MovableElement m = (MovableElement) obj;
			
			if (m instanceof SmallFish || m instanceof BigFish)
				continue;

			if (m instanceof Floatable && ((Floatable) m).triesToFloat()) {
				if (hasObjectAbove(m)) {
					applyGravity(m);
				} else {
					applyBuoyancy(m);
				}
			} else {
				applyGravity(m);
			}
		}

		validateStackDeath(SmallFish.getInstance());
		validateStackDeath(BigFish.getInstance());
	}
	private boolean hasObjectAbove(MovableElement obj) {
		Point2D posAbove = obj.getPosition().plus(Direction.UP.asVector());
		for (GameObject other : currentRoom.getObjects()) {
			if (other.getPosition().equals(posAbove) && other instanceof MovableObject) {
				return true;
			}
		}
		return false;
	}

	private void applyBuoyancy(MovableElement m) {
		Point2D posAbove = m.getPosition().plus(Direction.UP.asVector());
		boolean blocked = false;
		
		for (GameObject obj : currentRoom.getObjects()) {
			if (obj.getPosition().equals(posAbove) && obj instanceof Solid && ((Solid)obj).isSolid()) {
				blocked = true;
				break;
			}
		}
		if (!blocked && ImageGUI.getInstance().isWithinBounds(posAbove)) {
			m.move(Direction.UP.asVector());
		}
	}

	private void applyGravity(MovableElement m) {
		Point2D posBelow = m.getPosition().plus(Direction.DOWN.asVector());

		if (SmallFish.getInstance().getPosition().equals(posBelow)) {
			if (m instanceof Heavy && ((Heavy) m).isHeavy()) {
				SmallFish.getInstance().setFishDeath(true);
				m.move(Direction.DOWN.asVector());
				triggerGameOver("O peixe pequeno morreu esmagado! Clica OK para voltar ao início.");
				ImageGUI.getInstance().showMessage("Reinício", "Nível reiniciado");
				return;
			}
			return;
		}
		if (BigFish.getInstance().getPosition().equals(posBelow))
			return;

		Trunk trunkBelow = null;
		for (GameObject t : currentRoom.getObjects()) {
			if (t instanceof Trunk && t.getPosition().equals(posBelow)) {
				trunkBelow = (Trunk) t;
				break;
			}
		}

		if (m instanceof Heavy && ((Heavy) m).isHeavy() && trunkBelow != null) {
			currentRoom.getObjects().remove(trunkBelow);
			m.move(Direction.DOWN.asVector());
			updateGUI();
			ImageGUI.getInstance().update();
			return;
		}

		if (!isSupported(m)) {
			m.move(Direction.DOWN.asVector());

			if (m instanceof Bomb && isSupported(m)) {
				explosion(m.getPosition());
				return;
			}
			if (m instanceof Dangerous && m instanceof GameCharacter) {
				checkEnemyCollisions((GameCharacter) m);
			}
		}
	}

	private void moveEnemies() {
		List<GameObject> objectsCopy = new ArrayList<>(currentRoom.getObjects());
		
		for (GameObject obj : objectsCopy) {
			if (obj instanceof GameCharacter && ((GameCharacter)obj).isEnemy()) {
				GameCharacter enemy = (GameCharacter) obj;
				
				Direction randomDir = Math.random() < 0.5 ? Direction.LEFT : Direction.RIGHT;
				Point2D newPos = enemy.getPosition().plus(randomDir.asVector());

				if (canEnemyMove(enemy, newPos)) {
					enemy.setPosition(newPos);
					checkEnemyCollisions(enemy);
				}
			}
		}
	}

	private boolean canEnemyMove(GameCharacter enemy, Point2D target) {
		if (!ImageGUI.getInstance().isWithinBounds(target)) return false;
		
		for (GameObject obj : currentRoom.getObjects()) {
			if (obj.getPosition().equals(target)) {
				if (obj instanceof Trap || obj instanceof BigFish) {
					currentRoom.removeObject(enemy); 
					return false; 
				}
				if (obj instanceof SmallFish) {
					return true;
				}
				if (obj instanceof Solid && ((Solid)obj).isSolid()) {
					if (obj instanceof HoledWall && enemy.isSmall()) continue;
					return false;
				}
			}
		}
		return true;
	}

	private void checkEnemyCollisions(GameCharacter enemy) {
		Point2D pos = enemy.getPosition();
		
		if (pos.equals(SmallFish.getInstance().getPosition())) {
			if (enemy instanceof Dangerous && ((Dangerous)enemy).isLethalTo(SmallFish.getInstance())) {
				triggerGameOver("O Peixe Pequeno foi apanhado pelo inimigo!");
			}
		}
		
		if (pos.equals(BigFish.getInstance().getPosition())) {
			currentRoom.removeObject(enemy);
		}
		
		for(GameObject obj : currentRoom.getObjects()) {
			if(obj.getPosition().equals(pos) && obj instanceof Trap) {
				currentRoom.removeObject(enemy);
				break;
			}
		}
	}

	private void explosion(Point2D center) {

		List<Point2D> zone = center.getNeighbourhoodPoints();
		zone.add(center);

		List<GameObject> toRemove = new ArrayList<>();
		boolean fishDied = false;

		for (Point2D p : zone) {
			if (SmallFish.getInstance().getPosition().equals(p)) {
				SmallFish.getInstance().setFishDeath(true);
				fishDied = true;
			}
			if (BigFish.getInstance().getPosition().equals(p)) {
				BigFish.getInstance().setFishDeath(true);
				fishDied = true;
			}
		}

		for (GameObject obj : currentRoom.getObjects()) {
			if (!(obj instanceof GameCharacter) && zone.contains(obj.getPosition())) {
				toRemove.add(obj);
			}
		}

		currentRoom.getObjects().removeAll(toRemove);

		for (Point2D p : zone) {
			Water water = new Water(currentRoom);
			water.setPosition(p);
			currentRoom.getObjects().add(water);
		}

		updateGUI();
		ImageGUI.getInstance().update();

		if (fishDied) {
			triggerGameOver("O peixe explodiu. Clica OK para reiniciar.");
			ImageGUI.getInstance().showMessage("Reinício", "Nível reiniciado");
		}
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
		    if (o instanceof Heavy && ((Heavy) o).isHeavy()) {
		        heavyCount++;
		    }
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
			ImageGUI.getInstance().showMessage("Reinício", "Nível reiniciado");
		}
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
		this.ticksAtLevelStart = lastTickProcessed;
		this.numberFish = 2;

		updateGUI();
		ImageGUI.getInstance().update();
	}
}