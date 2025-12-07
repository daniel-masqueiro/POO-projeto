package pt.iscte.poo.game;

import java.io.Serializable;

public class Score implements Comparable<Score>, Serializable {
	private String name;
	private int moves;
	private int timeInSeconds;

	public Score(String name, int moves, int timeInSeconds) {
		this.name = name;
		this.moves = moves;
		this.timeInSeconds = timeInSeconds;
	}

	public int getValue() {
		return moves + timeInSeconds;
	}

	@Override
	public int compareTo(Score other) {
		return Integer.compare(this.getValue(), other.getValue());
	}

	@Override
	public String toString() {
		return String.format("%-10s | Mov: %3d | Tempo: %3ds | Total: %3d", name, moves, timeInSeconds, getValue());
	}
	public String getName() {
		return name;
	}

	public int getMoves() {
		return moves;
	}

	public int getTime() {
		return timeInSeconds;
	}
}