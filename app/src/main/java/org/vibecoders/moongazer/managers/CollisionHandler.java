package org.vibecoders.moongazer.managers;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.arkanoid.Ball;
import org.vibecoders.moongazer.arkanoid.Brick;
import org.vibecoders.moongazer.arkanoid.Paddle;
import java.util.List;

public class CollisionHandler {
    private static final Logger log = LoggerFactory.getLogger(CollisionHandler.class);
    private static final float MIN_HORIZONTAL_VELOCITY_THRESHOLD = 50f;
    private static final float MIN_HORIZONTAL_RATIO = 0.3f;

    /** Handles collisions of the ball with the game window walls.
     *
     * @param ball The ball to check for wall collisions.
     * @param ballIndex The index of the ball in the balls list.
     * @param balls The list of all balls in play.
     * @param sidePanel The width of the side panel (left boundary).
     * @param gameplayWidth The width of the gameplay area.
     * @param windowHeight The height of the game window.
     * @param onBallLostCallback Callback to execute when all balls are lost.
     * @return True if the ball was lost (went below the bottom), false otherwise.
     */
    public static boolean handleWallCollisions(Ball ball, int ballIndex, List<Ball> balls,
                                               float sidePanel, float gameplayWidth,
                                               float windowHeight, Runnable onBallLostCallback) {
        float ballX = ball.getBounds().x;
        float ballY = ball.getBounds().y;
        float ballRadius = ball.getRadius();

        if (ballX - ballRadius <= sidePanel) {
            ball.getBounds().x = sidePanel + ballRadius + 1f;
            ball.reverseX();
        }
        if (ballX + ballRadius >= sidePanel + gameplayWidth) {
            ball.getBounds().x = sidePanel + gameplayWidth - ballRadius - 1f;
            ball.reverseX();
        }
        if (ballY + ballRadius >= windowHeight) {
            ball.getBounds().y = windowHeight - ballRadius - 1f;
            ball.reverseY();
        }
        if (ballY - ballRadius <= 0) {
            balls.remove(ballIndex);
            log.info("Ball lost! Remaining balls: {}", balls.size());
            if (balls.isEmpty()) {
                onBallLostCallback.run();
            }
            return true;
        }
        return false;
    }

    /** Updates the score and combo based on the brick hit.
     *
     * @param brick The brick that was hit.
     * @param context The score context to update.
     */
    public static void updateScoreAndCombo(Brick brick, ScoreContext context) {
        if (brick.getType() != Brick.BrickType.BREAKABLE) return;

        context.combo++;
        if (context.combo > context.maxCombo) {
            context.maxCombo = context.combo;
        }

        float multiplier = 1.0f + (context.combo * 0.03f);
        int scoreGain = (int) (10 * multiplier);
        context.score += scoreGain;
        if (context.score > context.bestScore) {
            context.bestScore = context.score;
        }

        log.debug("Breakable brick hit! Combo: {}x, Multiplier: {:.2f}x, Score gained: {}, Total: {}",
                  context.combo, multiplier, scoreGain, context.score);
    }

    /** Handles collision of a super ball with a breakable brick.
     *
     * @param ball The ball involved in the collision.
     * @param brick The brick involved in the collision.
     * @param context The brick collision context.
     * @return True if the collision was handled as a super ball hit, false otherwise.
     */
    public static boolean handleSuperBallBrickCollision(Ball ball, Brick brick,
                                                       BrickCollisionContext context) {
        if (!ball.isSuperBall() || brick.getType() != Brick.BrickType.BREAKABLE) return false;

        brick.hit();
        context.setLastHitBrick(brick);
        context.resetCollisionCooldown();
        updateScoreAndCombo(brick, context.getScoreContext());

        if (brick.isDestroyed()) {
            context.setLastHitBrick(null);
            context.onBrickDestroyed(brick);
            if (brick.getPowerUpType() != Brick.PowerUpType.NONE) {
                context.spawnPowerUp(brick);
            }
        }
        return true;
    }

    /** Handles collision of a regular ball with a brick.
     *
     * @param ball The ball involved in the collision.
     * @param brick The brick involved in the collision.
     * @param ballX The x-coordinate of the ball's center.
     * @param ballY The y-coordinate of the ball's center.
     * @param ballRadius The radius of the ball.
     * @param context The brick collision context.
     */
    public static void handleRegularBrickCollision(Ball ball, Brick brick, float ballX,
                                                   float ballY, float ballRadius,
                                                   BrickCollisionContext context) {
        Rectangle brickBounds = brick.getBounds();
        float overlapLeft = (ballX + ballRadius) - brickBounds.x;
        float overlapRight = (brickBounds.x + brickBounds.width) - (ballX - ballRadius);
        float overlapTop = (brickBounds.y + brickBounds.height) - (ballY - ballRadius);
        float overlapBottom = (ballY + ballRadius) - brickBounds.y;
        float minOverlapX = Math.min(overlapLeft, overlapRight);
        float minOverlapY = Math.min(overlapTop, overlapBottom);
        float separationDistance = ballRadius + 1.0f;

        boolean isUnbreakable = brick.getType() == Brick.BrickType.UNBREAKABLE;
        if (isUnbreakable && context.getScoreContext().combo > 0) {
            log.info("Combo broken by unbreakable brick! Lost combo: {}x", context.getScoreContext().combo);
            context.getScoreContext().combo = 0;
        }

        if (minOverlapX < minOverlapY) {
            ball.reverseX();
            ball.getBounds().x = overlapLeft < overlapRight
                ? brickBounds.x - ballRadius - separationDistance
                : brickBounds.x + brickBounds.width + ballRadius + separationDistance;
            if (isUnbreakable) adjustBallVelocityIfTooVertical(ball, true);
        } else {
            ball.reverseY();
            ball.getBounds().y = overlapTop < overlapBottom
                ? brickBounds.y + brickBounds.height + ballRadius + separationDistance
                : brickBounds.y - ballRadius - separationDistance;
            if (isUnbreakable) adjustBallVelocityIfTooVertical(ball, true);
        }

        brick.hit();
        context.setLastHitBrick(brick);
        context.resetCollisionCooldown();
        updateScoreAndCombo(brick, context.getScoreContext());

        if (brick.getType() == Brick.BrickType.BREAKABLE && brick.isDestroyed()) {
            context.setLastHitBrick(null);
            context.onBrickDestroyed(brick);
            if (brick.getPowerUpType() != Brick.PowerUpType.NONE) {
                context.spawnPowerUp(brick);
            }
        }
    }

    /** Handles collision of the ball with the paddle.
     *
     * @param ball The ball to check for paddle collision.
     * @param ballX The x-coordinate of the ball's center.
     * @param ballY The y-coordinate of the ball's center.
     * @param ballRadius The radius of the ball.
     * @param paddle The paddle to check for collision.
     * @param bricksDestroyed The number of bricks destroyed (for speed scaling).
     */
    public static void handlePaddleCollision(Ball ball, float ballX, float ballY,
                                            float ballRadius, Paddle paddle,
                                            int bricksDestroyed) {
        Rectangle paddleBounds = paddle.getBounds();
        if (!Intersector.overlaps(ball.getBounds(), paddleBounds) ||
            ball.getVelocity().y >= 0 ||
            ballY - ballRadius <= paddleBounds.y) return;

        paddle.onBallHit(ball.getVelocity().y);
        Audio.playSfxPaddleHit();
        ball.getBounds().y = paddleBounds.y + paddleBounds.height + ballRadius + 2f;

        float hitPos = Math.max(0.1f, Math.min(0.9f, (ballX - paddleBounds.x) / paddleBounds.width));
        float bounceAngle = -(hitPos - 0.5f) * 100f;
        float speedMultiplier = Math.min(1.0f + (bricksDestroyed * 0.002f), 1.3f);
        float finalSpeed = 350f * speedMultiplier;
        float angleInRadians = (float) Math.toRadians(90 + bounceAngle);

        ball.setVelocity(
            finalSpeed * (float) Math.cos(angleInRadians),
            finalSpeed * (float) Math.sin(angleInRadians));
    }

    /** Adjusts the ball's velocity if it is too vertical to prevent getting stuck.
     *
     * @param ball The ball whose velocity needs adjustment.
     * @param preserveHorizontalDirection Whether to preserve the current horizontal direction.
     */
    private static void adjustBallVelocityIfTooVertical(Ball ball, boolean preserveHorizontalDirection) {
        if (Math.abs(ball.getVelocity().x) < MIN_HORIZONTAL_VELOCITY_THRESHOLD) {
            float currentVelX = ball.getVelocity().x;
            float currentVelY = ball.getVelocity().y;
            float speed = (float) Math.sqrt(currentVelX * currentVelX + currentVelY * currentVelY);
            float directionX = preserveHorizontalDirection ? (currentVelX >= 0 ? 1 : -1) : (Math.random() > 0.5 ? 1 : -1);
            float horizontalVel = speed * MIN_HORIZONTAL_RATIO * directionX;
            float verticalVel = (float) Math.sqrt(speed * speed - horizontalVel * horizontalVel) * (currentVelY >= 0 ? 1 : -1);
            ball.setVelocity(horizontalVel, verticalVel);
            log.debug("Adjusted velocity to prevent vertical stuck: ({}, {})", horizontalVel, verticalVel);
        }
    }

    /**
     * Holds the score and combo context for the game.
     */
    public static class ScoreContext {
        public int score;
        public int bestScore;
        public int combo;
        public int maxCombo;

        public ScoreContext(int score, int bestScore, int combo, int maxCombo) {
            this.score = score;
            this.bestScore = bestScore;
            this.combo = combo;
            this.maxCombo = maxCombo;
        }
    }

    /**
     * Interface for handling brick collision events.
     */
    public interface BrickCollisionContext {
        void setLastHitBrick(Brick brick);
        void resetCollisionCooldown();
        void onBrickDestroyed(Brick brick);
        void spawnPowerUp(Brick brick);
        ScoreContext getScoreContext();
    }
}
