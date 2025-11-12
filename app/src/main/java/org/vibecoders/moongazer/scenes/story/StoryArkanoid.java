package org.vibecoders.moongazer.scenes.story;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.SaveGameManager;
import org.vibecoders.moongazer.arkanoid.Ball;
import org.vibecoders.moongazer.arkanoid.Brick;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;
import org.vibecoders.moongazer.ui.GameWinMenu;

public class StoryArkanoid extends Arkanoid {
    private Runnable onLevelCompleteCallback;
    private Runnable onGameOverCallback;
    private Runnable onReturnToMainMenuCallback;
    protected GameWinMenu gameWinMenu;
    private int requiredBricks;
    protected int startingLives;
    protected int stageId = 0;

    public StoryArkanoid(Game game, int rows, int startingLives) {
        super(game);
        this.requiredBricks = rows;
        this.startingLives = startingLives;
        this.lives = startingLives;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public static class GameState {
        public PaddleState paddle;
        public java.util.List<BallState> balls;
        public java.util.List<BrickState> bricks;

        public static class PaddleState {
            public float x, y, width, height;
        }

        public static class BallState {
            public float x, y, radius;
            public float velocityX, velocityY;
            public boolean active;
            public boolean stuckToPaddle;
        }

        public static class BrickState {
            public float x, y, width, height;
            public String type;
            public String powerUpType;
            public int durability;
            public boolean destroyed;
        }
    }

    @Override
    protected void init() {
        super.init();
        pauseMenu.setStoryMode(true);
        pauseMenu.setOnSaveGame(() -> openSaveMenu());
        createBrickGrid(requiredBricks, 30);

        // Initialize game win menu
        gameWinMenu = new GameWinMenu();
        setupGameWinMenuCallbacks();

        log.info("Story Arkanoid initialized with {} rows and {} lives", requiredBricks, startingLives);
    }

    /**
     * Setup callbacks for the game win menu
     */
    private void setupGameWinMenuCallbacks() {
        gameWinMenu.setOnContinue(() -> {
            log.info("Continue clicked from win menu");
            gameInputEnabled = true;
            if (onLevelCompleteCallback != null) {
                onLevelCompleteCallback.run();
            }
        });

        gameWinMenu.setOnMainMenu(() -> {
            log.info("Main Menu clicked from win menu");
            returnToMainMenu();
        });

        gameWinMenu.setOnQuit(() -> {
            log.info("Quit clicked from win menu");
            
            // Save high score before quitting
            if (score > 0) {
                SaveGameManager.updateStoryHighScore(stageId, score);
                log.info("High score saved before quit: {} for stage {}", score, stageId);
            }
            
            Gdx.app.exit();
        });
    }

    private void openSaveMenu() {
        try {
            String gameStateJson = serializeGameState();
            String progressJson = "{}"; // Can be expanded to include story progress

            // Create save data
            org.vibecoders.moongazer.scenes.LoadScene.SaveGameData saveData =
                new org.vibecoders.moongazer.scenes.LoadScene.SaveGameData(
                    stageId, score, lives, bricksDestroyed, gameStateJson, progressJson
                );

            // Set the LoadScene to save mode with the save data
            if (game.loadScene instanceof org.vibecoders.moongazer.scenes.LoadScene) {
                org.vibecoders.moongazer.scenes.LoadScene loadScene =
                    (org.vibecoders.moongazer.scenes.LoadScene) game.loadScene;
                loadScene.setSaveData(saveData);

                // Close pause menu immediately without animation before transitioning
                pauseMenu.forceClose();
                if (game.transition == null) {
                    game.transition = new org.vibecoders.moongazer.scenes.Transition(
                        game, game.storyStageScene, game.loadScene,
                        org.vibecoders.moongazer.enums.State.LOAD_GAME, 350
                    );
                }
            }
        } catch (Exception e) {
            log.error("Failed to open save menu", e);
        }
    }

    public boolean loadGame() {
        try {
            // Check if we're loading from a save slot
            if (game.loadingSaveSlotId != -1) {
                SaveGameManager.SaveSlot slot = SaveGameManager.getSaveSlot(game.loadingSaveSlotId);
                if (slot != null && slot.currentStageId == stageId) {
                    score = slot.currentScore;
                    lives = slot.lives;
                    bricksDestroyed = slot.bricksDestroyed;
                    deserializeGameState(slot.gameStateJson);
                    log.info("Game loaded from save slot {} for stage {} - Score: {}, Lives: {}",
                            game.loadingSaveSlotId, stageId, score, lives);
                    game.loadingSaveSlotId = -1;
                    return true;
                }
            }

            // Fall back to old save system for backward compatibility
            SaveGameManager.StoryGameSave save = SaveGameManager.loadStoryGame(stageId);
            if (save == null) {
                log.info("No save game found for stage {}", stageId);
                return false;
            }
            score = save.currentScore;
            lives = save.lives;
            bricksDestroyed = save.bricksDestroyed;
            deserializeGameState(save.gameStateJson);
            log.info("Game loaded for stage {} - Score: {}, Lives: {}", stageId, score, lives);
            return true;
        } catch (Exception e) {
            log.error("Failed to load game", e);
            return false;
        }
    }

    private String serializeGameState() {
        GameState state = new GameState();
        state.paddle = new GameState.PaddleState();
        state.paddle.x = paddle.getBounds().x;
        state.paddle.y = paddle.getBounds().y;
        state.paddle.width = paddle.getBounds().width;
        state.paddle.height = paddle.getBounds().height;
        state.balls = new java.util.ArrayList<>();
        for (Ball ball : balls) {
            GameState.BallState ballState = new GameState.BallState();
            ballState.x = ball.getBounds().x;
            ballState.y = ball.getBounds().y;
            ballState.radius = ball.getRadius();
            ballState.velocityX = ball.getVelocity().x;
            ballState.velocityY = ball.getVelocity().y;
            ballState.active = ball.isActive();
            ballState.stuckToPaddle = ball.isStuckToPaddle();
            state.balls.add(ballState);
        }
        state.bricks = new java.util.ArrayList<>();
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                GameState.BrickState brickState = new GameState.BrickState();
                brickState.x = brick.getBounds().x;
                brickState.y = brick.getBounds().y;
                brickState.width = brick.getBounds().width;
                brickState.height = brick.getBounds().height;
                brickState.type = brick.getType().name();
                brickState.powerUpType = brick.getPowerUpType().name();
                brickState.durability = brick.getDurability();
                brickState.destroyed = brick.isDestroyed();
                state.bricks.add(brickState);
            }
        }
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        return json.toJson(state);
    }

    private void deserializeGameState(String gameStateJson) {
        Json json = new Json();
        GameState state = json.fromJson(GameState.class, gameStateJson);
        paddle.getBounds().x = state.paddle.x;
        paddle.getBounds().y = state.paddle.y;
        paddle.getBounds().width = state.paddle.width;
        paddle.getBounds().height = state.paddle.height;
        balls.clear();
        for (GameState.BallState ballState : state.balls) {
            Ball ball = new Ball(ballState.x, ballState.y, ballState.radius);
            ball.getVelocity().set(ballState.velocityX, ballState.velocityY);
            if (ballState.active) {
                ball.launch();
            }
            if (ballState.stuckToPaddle) {
                ball.setStuckToPaddle(true);
                ball.setStuckOffsetX(ballState.x - state.paddle.x);
            }
            balls.add(ball);
        }
        bricks.clear();
        for (GameState.BrickState brickState : state.bricks) {
            Brick.BrickType type = Brick.BrickType.valueOf(brickState.type);
            Brick.PowerUpType powerUpType = Brick.PowerUpType.valueOf(brickState.powerUpType);
            Brick brick = new Brick(brickState.x, brickState.y, brickState.width, brickState.height,
                                   type, brickState.durability, powerUpType);
            bricks.add(brick);
        }
    }

    public void setOnLevelComplete(Runnable callback) {
        this.onLevelCompleteCallback = callback;
    }
    public void setOnGameOver(Runnable callback) {
        this.onGameOverCallback = callback;
    }
    public void setOnReturnToMainMenu(Runnable callback) {
        this.onReturnToMainMenuCallback = callback;
    }
    @Override
    protected void onLevelComplete() {
        log.info("Story level complete!");
        SaveGameManager.updateStoryHighScore(stageId, score);
        SaveGameManager.deleteStoryGameSave(stageId);

        // Disable game input while showing win menu
        gameInputEnabled = false;

        // Show win menu with stats (snapshot will be captured in render method)
        gameWinMenu.show(score, maxCombo, lives);
    }
    @Override
    protected void onGameOver() {
        log.info("Story game over!");
        SaveGameManager.updateStoryHighScore(stageId, score);

        gameInputEnabled = false;
        if (gameOverMenu != null) {
            gameOverMenu.show(score);
        }

        if (onGameOverCallback != null) {
            onGameOverCallback.run();
        }
    }
    @Override
    protected void onPausePressed() {
        pauseMenu.pause();
    }
    @Override
    protected void restartGame() {
        score = 0;
        lives = startingLives;
        bricksDestroyed = 0;
        initGameplay();
    }
    @Override
    protected void returnToMainMenu() {
        log.info("Returning to main menu from story mode");
        
        // Save high score before returning to main menu
        if (score > 0) {
            SaveGameManager.updateStoryHighScore(stageId, score);
            log.info("High score saved: {} for stage {}", score, stageId);
        }
        
        if (onReturnToMainMenuCallback != null) {
            log.info("Calling returnToMainMenu callback");
            onReturnToMainMenuCallback.run();
        } else {
            log.warn("onReturnToMainMenuCallback is null!");
            Gdx.app.exit();
        }
    }
    @Override
    public void render(SpriteBatch batch) {
        // Handle input and game updates
        if (!pauseMenu.isPaused() && !gameOverMenu.isVisible() && !gameWinMenu.isVisible() &&
            Gdx.input.getInputProcessor() != inputMultiplexer) {
            restoreInputProcessor();
        }

        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (pauseCooldown > 0) {
            pauseCooldown -= delta;
        }

        // Update game logic if not paused, game over, or won
        if (!pauseMenu.isPaused() && !gameOverMenu.isVisible() && !gameWinMenu.isVisible()) {
            handleInput(delta);
            updateGameplay(delta);
            handleCollisions();
        }

        // Render gameplay and UI
        renderGameplay(batch);
        renderUI(batch);

        // Handle pause menu rendering
        if (pauseMenu.isPaused()) {
            if (gameSnapshot == null) {
                batch.end();
                gameFrameBuffer.begin();
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                batch.begin();
                renderGameplay(batch);
                renderUI(batch);
                batch.end();
                gameFrameBuffer.end();
                gameSnapshot = gameFrameBuffer.getColorBufferTexture();
                batch.begin();
            }
            pauseMenu.render(batch, gameSnapshot);
        }
        // Handle game over menu rendering
        else if (gameOverMenu.isVisible()) {
            if (gameSnapshot == null) {
                batch.end();
                gameFrameBuffer.begin();
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                batch.begin();
                renderGameplay(batch);
                renderUI(batch);
                batch.end();
                gameFrameBuffer.end();
                gameSnapshot = gameFrameBuffer.getColorBufferTexture();
                batch.begin();
            }
            gameOverMenu.render(batch, gameSnapshot);
        }
        // Handle game win menu rendering
        else if (gameWinMenu.isVisible()) {
            if (gameSnapshot == null) {
                batch.end();
                gameFrameBuffer.begin();
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                batch.begin();
                renderGameplay(batch);
                renderUI(batch);
                batch.end();
                gameFrameBuffer.end();
                gameSnapshot = gameFrameBuffer.getColorBufferTexture();
                batch.begin();
            }
            gameWinMenu.render(batch, gameSnapshot);
        }
        // Clear snapshot when no menu is visible
        else {
            if (gameSnapshot != null) {
                gameSnapshot = null;
            }
        }
    }

    @Override
    public void dispose() {
        // Save high score before disposing
        if (score > 0 && stageId > 0) {
            SaveGameManager.updateStoryHighScore(stageId, score);
            log.info("High score saved on dispose: {} for stage {}", score, stageId);
        }
        
        super.dispose();
        if (gameWinMenu != null) {
            gameWinMenu.dispose();
        }
    }
}
