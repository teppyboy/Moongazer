package org.vibecoders.moongazer;

import com.badlogic.gdx.Gdx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SaveGameManager {
    private static final Logger log = LoggerFactory.getLogger(SaveGameManager.class);
    private static final String DB_FILE = "userdata.db";
    private static Connection connection;

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

    public static class StoryGameSave {
        public int stageId;
        public int currentScore;
        public int highScore;
        public int lives;
        public int bricksDestroyed;
        public String gameStateJson;
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

    public static class SaveSlot {
        public int slotId;
        public String slotName;
        public int currentStageId;
        public int currentScore;
        public int lives;
        public int bricksDestroyed;
        public String gameStateJson;
        public String progressJson;
        public long timestamp;

        public SaveSlot(int slotId, String slotName, int currentStageId, int currentScore,
                       int lives, int bricksDestroyed, String gameStateJson,
                       String progressJson, long timestamp) {
            this.slotId = slotId;
            this.slotName = slotName;
            this.currentStageId = currentStageId;
            this.currentScore = currentScore;
            this.lives = lives;
            this.bricksDestroyed = bricksDestroyed;
            this.gameStateJson = gameStateJson;
            this.progressJson = progressJson;
            this.timestamp = timestamp;
        }
    }

    public static void initialize() {
        try {
            String dbPath = Gdx.files.local(DB_FILE).file().getAbsolutePath();
            String url = "jdbc:sqlite:" + dbPath;
            connection = DriverManager.getConnection(url);
            createTables();
            log.info("SaveGameManager initialized. Database: {}", dbPath);
        } catch (SQLException e) {
            log.error("Failed to initialize SaveGameManager", e);
        }
    }

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

        String createSaveSlotsTable = """
            CREATE TABLE IF NOT EXISTS save_slots (
                slot_id INTEGER PRIMARY KEY AUTOINCREMENT,
                slot_name TEXT NOT NULL,
                current_stage_id INTEGER NOT NULL,
                current_score INTEGER NOT NULL,
                lives INTEGER NOT NULL,
                bricks_destroyed INTEGER NOT NULL,
                game_state_json TEXT NOT NULL,
                progress_json TEXT,
                timestamp INTEGER NOT NULL
            )
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createHighScoreTable);
            stmt.execute(createScoresTable);
            stmt.execute(createStoryGameSavesTable);
            stmt.execute(createStoryHighScoresTable);
            stmt.execute(createSaveSlotsTable);
            String initHighScore = """
                INSERT OR IGNORE INTO high_score (id, score, wave, timestamp)
                VALUES (1, 0, 0, 0)
                """;
            stmt.execute(initHighScore);
            log.debug("Database tables created/verified");
        }
    }

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

    public static List<StoryGameSave> getAllStoryGameSaves() {
        List<StoryGameSave> saves = new ArrayList<>();
        if (connection == null) {
            log.warn("Database not initialized, returning empty save list");
            return saves;
        }
        String query = """
            SELECT stage_id, current_score, high_score, lives, bricks_destroyed,
                   game_state_json, timestamp
            FROM story_game_saves
            ORDER BY timestamp DESC
            """;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                StoryGameSave save = new StoryGameSave(
                    rs.getInt("stage_id"),
                    rs.getInt("current_score"),
                    rs.getInt("high_score"),
                    rs.getInt("lives"),
                    rs.getInt("bricks_destroyed"),
                    rs.getString("game_state_json"),
                    rs.getLong("timestamp")
                );
                saves.add(save);
            }
            log.debug("Retrieved {} story game saves", saves.size());
        } catch (SQLException e) {
            log.error("Failed to get all story game saves", e);
        }
        return saves;
    }

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

    // Save Slot Management Methods
    public static List<SaveSlot> getAllSaveSlots() {
        List<SaveSlot> slots = new ArrayList<>();
        if (connection == null) {
            log.warn("Database not initialized, returning empty slot list");
            return slots;
        }
        String query = """
            SELECT slot_id, slot_name, current_stage_id, current_score, lives,
                   bricks_destroyed, game_state_json, progress_json, timestamp
            FROM save_slots
            ORDER BY timestamp DESC
            """;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                SaveSlot slot = new SaveSlot(
                    rs.getInt("slot_id"),
                    rs.getString("slot_name"),
                    rs.getInt("current_stage_id"),
                    rs.getInt("current_score"),
                    rs.getInt("lives"),
                    rs.getInt("bricks_destroyed"),
                    rs.getString("game_state_json"),
                    rs.getString("progress_json"),
                    rs.getLong("timestamp")
                );
                slots.add(slot);
            }
            log.debug("Retrieved {} save slots", slots.size());
        } catch (SQLException e) {
            log.error("Failed to get all save slots", e);
        }
        return slots;
    }

    public static SaveSlot getSaveSlot(int slotId) {
        if (connection == null) {
            log.warn("Database not initialized");
            return null;
        }
        String query = """
            SELECT slot_id, slot_name, current_stage_id, current_score, lives,
                   bricks_destroyed, game_state_json, progress_json, timestamp
            FROM save_slots
            WHERE slot_id = ?
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, slotId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new SaveSlot(
                    rs.getInt("slot_id"),
                    rs.getString("slot_name"),
                    rs.getInt("current_stage_id"),
                    rs.getInt("current_score"),
                    rs.getInt("lives"),
                    rs.getInt("bricks_destroyed"),
                    rs.getString("game_state_json"),
                    rs.getString("progress_json"),
                    rs.getLong("timestamp")
                );
            }
        } catch (SQLException e) {
            log.error("Failed to get save slot", e);
        }
        return null;
    }

    public static int createSaveSlot(String slotName, int stageId, int currentScore,
                                      int lives, int bricksDestroyed, String gameStateJson,
                                      String progressJson) {
        if (connection == null) {
            log.warn("Database not initialized, cannot create save slot");
            return -1;
        }
        String insert = """
            INSERT INTO save_slots (slot_name, current_stage_id, current_score, lives,
                                   bricks_destroyed, game_state_json, progress_json, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, slotName);
            pstmt.setInt(2, stageId);
            pstmt.setInt(3, currentScore);
            pstmt.setInt(4, lives);
            pstmt.setInt(5, bricksDestroyed);
            pstmt.setString(6, gameStateJson);
            pstmt.setString(7, progressJson);
            pstmt.setLong(8, System.currentTimeMillis());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int slotId = rs.getInt(1);
                log.info("Created save slot {} with ID {}", slotName, slotId);
                return slotId;
            }
        } catch (SQLException e) {
            log.error("Failed to create save slot", e);
        }
        return -1;
    }

    public static boolean updateSaveSlot(int slotId, String slotName, int stageId,
                                        int currentScore, int lives, int bricksDestroyed,
                                        String gameStateJson, String progressJson) {
        if (connection == null) {
            log.warn("Database not initialized, cannot update save slot");
            return false;
        }
        String update = """
            UPDATE save_slots
            SET slot_name = ?, current_stage_id = ?, current_score = ?, lives = ?,
                bricks_destroyed = ?, game_state_json = ?, progress_json = ?, timestamp = ?
            WHERE slot_id = ?
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(update)) {
            pstmt.setString(1, slotName);
            pstmt.setInt(2, stageId);
            pstmt.setInt(3, currentScore);
            pstmt.setInt(4, lives);
            pstmt.setInt(5, bricksDestroyed);
            pstmt.setString(6, gameStateJson);
            pstmt.setString(7, progressJson);
            pstmt.setLong(8, System.currentTimeMillis());
            pstmt.setInt(9, slotId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Updated save slot {}", slotId);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to update save slot", e);
        }
        return false;
    }

    public static boolean deleteSaveSlot(int slotId) {
        if (connection == null) {
            log.warn("Database not initialized, cannot delete save slot");
            return false;
        }
        String delete = "DELETE FROM save_slots WHERE slot_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(delete)) {
            pstmt.setInt(1, slotId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Deleted save slot {}", slotId);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to delete save slot", e);
        }
        return false;
    }

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
