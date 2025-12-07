package pt.iscte.poo.game;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {
    private static final String FILE_NAME = "highscores.txt";
    private List<Score> scores;

    public HighScoreManager() {
        scores = new ArrayList<>();
        loadScores();
    }

    public void addScore(String name, int moves, int time) {
        scores.add(new Score(name, moves, time));
        Collections.sort(scores);
        
        if (scores.size() > 10) {
            scores.remove(scores.size() - 1);
        }
        saveScores();
    }

    public String getHighScoresBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TOP 10 MELHORES JOGADORES ===\n\n");
        int i = 1;
        for (Score s : scores) {
            sb.append(i++).append(". ").append(s.toString()).append("\n");
        }
        return sb.toString();
    }

    private void saveScores() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Score s : scores) {
                writer.println(s.getName() + ";" + s.getMoves() + ";" + s.getTime());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadScores() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    scores.add(new Score(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                }
            }
            Collections.sort(scores);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}