package org.vibecoders.moongazer;

import com.badlogic.gdx.Gdx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages game save data using SQLite database.
 * Stores high scores and all scores from Endless mode.
 */
public class SaveGameManager {
    private static final Logger log = LoggerFactory.getLogger(SaveGameManager.class);
    private static final String DB_FILE = "userdata.db";
    private static Connection connection;

    /**
     * Represents a score entry in the database
     */
    public static class ScoreEntry {
        public int id;
        public int score;
        public int wave;
        public long timestamp;

        public ScoreEntry(int id, int score, int wave, long timestamp) {
            this.id = id;
            this.score = score;
            this.wave = wave;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return String.format("Score{id=%d, score=%d, wave=%d, time=%s}",
                id, score, wave, Instant.ofEpochMilli(timestamp));
        }
    }

    /**
     * Represents a story mode save game
     */
    public static class StoryGameSave {
        public int stageId;
        public int currentScore;
        public int highScore;
        public int lives;
        public int bricksDestroyed;
        public String gameStateJson; // JSON containing balls, bricks, paddle, powerups state
        public long timestamp;

        public StoryGameSave(int stageId, int currentScore, int highScore, int lives,
                            int bricksDestroyed, String gameStateJson, long timestamp) {
            this.stageId = stageId;
            this.currentScore = currentScore;
            this.highScore = highScore;
            this.lives = lives;
            this.bricksDestroyed = bricksDestroyed;
            this.gameStateJson = gameStateJson;
            this.timestamp = timestamp;
        }
    }

    /**
     * Initialize the database connection and create tables if they don't exist
     */
    public static void initialize() {
        try {
            // Get the local storage path from LibGDX
            String dbPath = Gdx.files.local(DB_FILE).file().getAbsolutePath();
            String url = "jdbc:sqlite:" + dbPath;

            connection = DriverManager.getConnection(url);
            createTables();

            log.info("SaveGameManager initialized. Database: {}", dbPath);
        } catch (SQLException e) {
            log.error("Failed to initialize SaveGameManager", e);
        }
    }

    /**
     * Create the necessary tables if they don't exist
     */
    private static void createTables() throws SQLException {
        String createHighScoreTable = """
            CREATE TABLE IF NOT EXISTS high_score (
                id INTEGER PRIMARY KEY CHECK (id = 1),
                score INTEGER NOT NULL DEFAULT 0,
                wave INTEGER NOT NULL DEFAULT 0,
                timestamp INTEGER NOT NULL
            )
            """;

        String createScoresTable = """
            CREATE TABLE IF NOT EXISTS endless_scores (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                score INTEGER NOT NULL,
                wave INTEGER NOT NULL,
                timestamp INTEGER NOT NULL
            )
            """;

        String createStoryGameSavesTable = """
            CREATE TABLE IF NOT EXISTS story_game_saves (
                stage_id INTEGER PRIMARY KEY,
                current_score INTEGER NOT NULL,
                high_score INTEGER NOT NULL,
                lives INTEGER NOT NULL,
                bricks_destroyed INTEGER NOT NULL,
                game_state_json TEXT NOT NULL,
                timestamp INTEGER NOT NULL
            )
            """;

        String createStoryHighScoresTable = """
            CREATE TABLE IF NOT EXISTS story_high_scores (
                stage_id INTEGER PRIMARY KEY,
                high_score INTEGER NOT NULL,
                timestamp INTEGER NOT NULL
            )
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createHighScoreTable);
            stmt.execute(createScoresTable);
            stmt.execute(createStoryGameSavesTable);
            stmt.execute(createStoryHighScoresTable);

            // Initialize high score row if it doesn't exist
            String initHighScore = """
                INSERT OR IGNORE INTO high_score (id, score, wave, timestamp)
                VALUES (1, 0, 0, 0)
                """;
            stmt.execute(initHighScore);

            log.debug("Database tables created/verified");
        }
    }

    /**
     * Get the current high score
     * @return The high score, or 0 if none exists
     */
    public static int getHighScore() {
        if (connection == null) {
            log.warn("Database not initialized, returning 0 for high score");
            return 0;
        }

        String query = "SELECT score FROM high_score WHERE id = 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int highScore = rs.getInt("score");
                log.debug("Retrieved high score: {}", highScore);
                return highScore;
            }
        } catch (SQLException e) {
            log.error("Failed to get high score", e);
        }

        return 0;
    }

    /**
     * Get the wave number for the current high score
     * @return The wave number, or 0 if none exists
     */
    public static int getHighScoreWave() {
        if (connection == null) {
            log.warn("Database not initialized, returning 0 for high score wave");
            return 0;
        }

        String query = "SELECT wave FROM high_score WHERE id = 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("wave");
            }
        } catch (SQLException e) {
            log.error("Failed to get high score wave", e);
        }

        return 0;
    }

    /**
     * Update the high score if the new score is higher
     * @param score The new score
     * @param wave The wave number achieved
     * @return true if this was a new high score, false otherwise
     */
    public static boolean updateHighScore(int score, int wave) {
        if (connection == null) {
            log.warn("Database not initialized, cannot update high score");
            return false;
        }

        int currentHighScore = getHighScore();
        if (score > currentHighScore) {
            String update = """
                UPDATE high_score
                SET score = ?, wave = ?, timestamp = ?
                WHERE id = 1
                """;

            try (PreparedStatement pstmt = connection.prepareStatement(update)) {
                pstmt.setInt(1, score);
                pstmt.setInt(2, wave);
                pstmt.setLong(3, System.currentTimeMillis());
                pstmt.executeUpdate();

                log.info("New high score saved: {} (Wave: {})", score, wave);
                return true;
            } catch (SQLException e) {
                log.error("Failed to update high score", e);
            }
        } else {
            log.debug("Score {} is not higher than current high score {}", score, currentHighScore);
        }

        return false;
    }

    /**
     * Save a score from Endless mode
     * @param score The score achieved
     * @param wave The wave number reached
     */
    public static void saveEndlessScore(int score, int wave) {
        if (connection == null) {
            log.warn("Database not initialized, cannot save endless score");
            return;
        }

        String insert = """
            INSERT INTO endless_scores (score, wave, timestamp)
            VALUES (?, ?, ?)
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(insert)) {
            pstmt.setInt(1, score);
            pstmt.setInt(2, wave);
            pstmt.setLong(3, System.currentTimeMillis());
            pstmt.executeUpdate();

            log.info("Endless score saved: {} (Wave: {})", score, wave);
        } catch (SQLException e) {
            log.error("Failed to save endless score", e);
        }
    }

    /**
     * Get all scores from Endless mode, ordered by score (highest first)
     * @return List of all score entries
     */
    public static List<ScoreEntry> getAllEndlessScores() {
        List<ScoreEntry> scores = new ArrayList<>();

        if (connection == null) {
            log.warn("Database not initialized, returning empty score list");
            return scores;
        }

        String query = """
            SELECT id, score, wave, timestamp
            FROM endless_scores
            ORDER BY score DESC
            """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ScoreEntry entry = new ScoreEntry(
                    rs.getInt("id"),
                    rs.getInt("score"),
                    rs.getInt("wave"),
                    rs.getLong("timestamp")
                );
                scores.add(entry);
            }

            log.debug("Retrieved {} endless scores", scores.size());
        } catch (SQLException e) {
            log.error("Failed to get endless scores", e);
        }

        return scores;
    }

    /**
     * Get the top N scores from Endless mode
     * @param limit Maximum number of scores to return
     * @return List of top score entries
     */
    public static List<ScoreEntry> getTopEndlessScores(int limit) {
        List<ScoreEntry> scores = new ArrayList<>();

        if (connection == null) {
            log.warn("Database not initialized, returning empty score list");
            return scores;
        }

        String query = """
            SELECT id, score, wave, timestamp
            FROM endless_scores
            ORDER BY score DESC
            LIMIT ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ScoreEntry entry = new ScoreEntry(
                    rs.getInt("id"),
                    rs.getInt("score"),
                    rs.getInt("wave"),
                    rs.getLong("timestamp")
                );
                scores.add(entry);
            }

            log.debug("Retrieved top {} endless scores", scores.size());
        } catch (SQLException e) {
            log.error("Failed to get top endless scores", e);
        }

        return scores;
    }

    /**
     * Get the total number of games played in Endless mode
     * @return Total number of games
     */
    public static int getTotalGamesPlayed() {
        if (connection == null) {
            log.warn("Database not initialized, returning 0 for total games");
            return 0;
        }

        String query = "SELECT COUNT(*) as total FROM endless_scores";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            log.error("Failed to get total games played", e);
        }

        return 0;
    }

    /**
     * Save a story mode game state
     * @param stageId The stage ID (1-5)
     * @param currentScore Current score in the game
     * @param highScore High score for this stage
     * @param lives Current number of lives
     * @param bricksDestroyed Number of bricks destroyed
     * @param gameStateJson JSON string containing the full game state
     */
    public static void saveStoryGame(int stageId, int currentScore, int highScore,
                                     int lives, int bricksDestroyed, String gameStateJson) {
        if (connection == null) {
            log.warn("Database not initialized, cannot save story game");
            return;
        }

        String upsert = """
            INSERT OR REPLACE INTO story_game_saves
            (stage_id, current_score, high_score, lives, bricks_destroyed, game_state_json, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(upsert)) {
            pstmt.setInt(1, stageId);
            pstmt.setInt(2, currentScore);
            pstmt.setInt(3, highScore);
            pstmt.setInt(4, lives);
            pstmt.setInt(5, bricksDestroyed);
            pstmt.setString(6, gameStateJson);
            pstmt.setLong(7, System.currentTimeMillis());
            pstmt.executeUpdate();

            log.info("Story game saved for stage {} (Score: {}, Lives: {})", stageId, currentScore, lives);
        } catch (SQLException e) {
            log.error("Failed to save story game", e);
        }
    }

    /**
     * Load a story mode game state
     * @param stageId The stage ID to load
     * @return The saved game state, or null if no save exists
     */
    public static StoryGameSave loadStoryGame(int stageId) {
        if (connection == null) {
            log.warn("Database not initialized, cannot load story game");
            return null;
        }

        String query = """
            SELECT stage_id, current_score, high_score, lives, bricks_destroyed,
                   game_state_json, timestamp
            FROM story_game_saves
            WHERE stage_id = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, stageId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                StoryGameSave save = new StoryGameSave(
                    rs.getInt("stage_id"),
                    rs.getInt("current_score"),
                    rs.getInt("high_score"),
                    rs.getInt("lives"),
                    rs.getInt("bricks_destroyed"),
                    rs.getString("game_state_json"),
                    rs.getLong("timestamp")
                );
                log.info("Loaded story game save for stage {}", stageId);
                return save;
            }
        } catch (SQLException e) {
            log.error("Failed to load story game", e);
        }

        return null;
    }

    /**
     * Check if a save game exists for a stage
     * @param stageId The stage ID to check
     * @return true if a save exists, false otherwise
     */
    public static boolean hasSaveGame(int stageId) {
        if (connection == null) {
            log.warn("Database not initialized");
            return false;
        }

        String query = "SELECT COUNT(*) as count FROM story_game_saves WHERE stage_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, stageId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            log.error("Failed to check save game existence", e);
        }
        return false;
    }

    /**
     * Delete a story mode save game
     * @param stageId The stage ID to delete
     */
    public static void deleteStoryGameSave(int stageId) {
        if (connection == null) {
            log.warn("Database not initialized, cannot delete story game save");
            return;
        }

        String delete = "DELETE FROM story_game_saves WHERE stage_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(delete)) {
            pstmt.setInt(1, stageId);
            pstmt.executeUpdate();
            log.info("Deleted story game save for stage {}", stageId);
        } catch (SQLException e) {
            log.error("Failed to delete story game save", e);
        }
    }

    /**
     * Get the high score for a specific story stage
     * @param stageId The stage ID
     * @return The high score, or 0 if none exists
     */
    public static int getStoryHighScore(int stageId) {
        if (connection == null) {
            log.warn("Database not initialized, returning 0 for story high score");
            return 0;
        }

        String query = "SELECT high_score FROM story_high_scores WHERE stage_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, stageId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("high_score");
            }
        } catch (SQLException e) {
            log.error("Failed to get story high score", e);
        }
        return 0;
    }

    /**
     * Update the high score for a specific story stage
     * @param stageId The stage ID
     * @param score The new score
     * @return true if this was a new high score, false otherwise
     */
    public static boolean updateStoryHighScore(int stageId, int score) {
        if (connection == null) {
            log.warn("Database not initialized, cannot update story high score");
            return false;
        }

        int currentHighScore = getStoryHighScore(stageId);
        if (score > currentHighScore) {
            String upsert = """
                INSERT OR REPLACE INTO story_high_scores (stage_id, high_score, timestamp)
                VALUES (?, ?, ?)
                """;

            try (PreparedStatement pstmt = connection.prepareStatement(upsert)) {
                pstmt.setInt(1, stageId);
                pstmt.setInt(2, score);
                pstmt.setLong(3, System.currentTimeMillis());
                pstmt.executeUpdate();
                log.info("New high score for stage {}: {}", stageId, score);
                return true;
            } catch (SQLException e) {
                log.error("Failed to update story high score", e);
            }
        }
        return false;
    }

    /**
     * Close the database connection
     */
    public static void dispose() {
        if (connection != null) {
            try {
                connection.close();
                log.info("SaveGameManager disposed");
            } catch (SQLException e) {
                log.error("Failed to close database connection", e);
            }
        }
    }
}
