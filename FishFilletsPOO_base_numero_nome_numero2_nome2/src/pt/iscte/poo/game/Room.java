package pt.iscte.poo.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import objects.Water;
import objects.Anchor;
import objects.BigFish;
import objects.Bomb;
import objects.Cup;
import objects.GameObject;
import objects.HoledWall;
import objects.SmallFish;
import objects.SteelHorizontal;
import objects.SteelVertical;
import objects.Stone;
import objects.Trap;
import objects.Trunk;
import objects.Wall;
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

	public static void fillWithWater(Room r) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				GameObject water = new Water(r);
				water.setPosition(i, j);
				r.addObject(water);
			}
		}
	}

	public static GameObject createGameObject(Room r, String tileType) {
		switch (tileType) {
		case "W":
			return new Wall(r);
		case "X":
			return new HoledWall(r);
		case "Y":
			return new Trunk(r);
		case "T":
			return new Trap(r);
		case "b":
			return new Bomb(r);
		case "H":
			return new SteelHorizontal(r);
		case "V":
			return new SteelVertical(r);
		case "C":
			return new Cup(r);
		case "R":
			return new Stone(r);
		case "A":
			return new Anchor(r);
		case "S": {
			GameObject sf = SmallFish.getInstance();
			sf.setRoom(r);
			return sf;
		}
		case "B": {
			GameObject bf = BigFish.getInstance();
			bf.setRoom(r);
			return bf;
		}
		default:
			System.err.println("Caracter invalido: " + tileType);
			return null;
		}
	}

	public static Room readRoom(File f, GameEngine engine) {
		Room r = new Room();
		r.setEngine(engine);
		r.setName(f.getName());

		fillWithWater(r);

		try (Scanner s = new Scanner(f)) {
			int y = 0;
	        while (s.hasNextLine()) {
	            String line = s.nextLine(); 
	            for (int x = 0; x < line.length(); x++) {
	                char c = line.charAt(x); 
	                if (c == ' ') {
	                    continue; 
	                }
	                String tileString = String.valueOf(c);
	                GameObject o = createGameObject(r, tileString);

	                if (o != null) {
	                    o.setPosition(x, y);
	                    r.addObject(o);
	                }
	            }
	            y++; 
	        }
		} catch (FileNotFoundException e) {
			System.err.println("Erro: Ficheiro do mapa não encontrado - " + f.getName());
		}

		return r;

	}

}