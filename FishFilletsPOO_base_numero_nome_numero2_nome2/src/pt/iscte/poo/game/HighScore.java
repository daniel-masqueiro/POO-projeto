package pt.iscte.poo.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class HighScore implements Comparable<HighScore> {
	private String name;
	private int moves;
	private int time;

	public HighScore(String name, int moves, int time) {
		this.name = name;
		this.moves = moves;
		this.time = time;
	}

	public int scoreValue() {
		return moves + time;
	}

	@Override
	public int compareTo(HighScore other) {
		return this.scoreValue() - other.scoreValue();
	}

	@Override
	public String toString() {
		return name + " | Mov: " + moves + " | Tempo: " + time + "s";
	}

	private static final String FILE_NAME = "highscores.txt";
	private static List<HighScore> scores = new ArrayList<>();

	public static void registerScore(String name, int moves, int time) {
		loadFromFile();
		scores.add(new HighScore(name, moves, time));
		Collections.sort(scores);
		if (scores.size() > 10) {
			scores.remove(scores.size() - 1);
		}

		saveToFile();
	}

	public static String getBoard() {
		String text = "=== TOP 10 ===\n\n";
		int i = 1;
		for (HighScore s : scores) {
			text = text + i + ". " + s.toString() + "\n";
			i++;
		}
		return text;
	}

	private static void loadFromFile() {
		scores.clear();
		File file = new File(FILE_NAME);
		if (!file.exists())
			return;

		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] parts = line.split(";");
				if (parts.length == 3) {
					String n = parts[0];
					int m = Integer.parseInt(parts[1]);
					int t = Integer.parseInt(parts[2]);
					scores.add(new HighScore(n, m, t));
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
		}
	}

	private static void saveToFile() {
		try {
			PrintWriter writer = new PrintWriter(new File(FILE_NAME));
			for (HighScore s : scores) {
				writer.println(s.name + ";" + s.moves + ";" + s.time);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			System.err.println("Erro: Não foi possível gravar o ficheiro de Highscores.");
		}
	}
}