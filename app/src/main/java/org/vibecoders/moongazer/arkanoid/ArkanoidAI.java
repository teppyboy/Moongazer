package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.math.Vector2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

import static org.vibecoders.moongazer.Constants.*;

/**
 * AI implementation that automatically controls the paddle to hit balls and collect powerups
 */
public class ArkanoidAI implements PaddleAI {
    private static final Logger log = LoggerFactory.getLogger(ArkanoidAI.class);
    private boolean enabled = false;
    private static final double PREDICTION_TOLERANCE = 5.0; // Pixels tolerance for ball prediction
    private static final double POWERUP_PRIORITY_DISTANCE = 150.0; // Distance to prioritize powerup collection

    @Override
    public void update(Paddle paddle, List<Ball> balls, List<PowerUp> powerUps) {
        if (!enabled || paddle == null) {
            // Stop paddle movement when AI is disabled
            paddle.moveLeft(false);
            paddle.moveRight(false);
            return;
        }

        // Find the most urgent ball (one that's moving down and closest to paddle)
        Ball targetBall = findMostUrgentBall(balls, paddle);

        // Find the closest powerup that's worth collecting
        PowerUp targetPowerUp = findBestPowerUp(powerUps, paddle);

        // Decide what to do: prioritize powerup if it's close, otherwise track ball
        if (targetPowerUp != null && shouldPrioritizePowerUp(targetPowerUp, targetBall, paddle)) {
            moveToCollectPowerUp(paddle, targetPowerUp);
        } else if (targetBall != null) {
            moveToHitBall(paddle, targetBall);
        } else {
            // No ball or powerup to track, stop moving
            paddle.moveLeft(false);
            paddle.moveRight(false);
        }
    }

    /**
     * Find the ball that needs the most urgent attention
     * Prioritizes balls that are moving down and close to the paddle
     */
    private Ball findMostUrgentBall(List<Ball> balls, Paddle paddle) {
        if (balls == null || balls.isEmpty()) {
            return null;
        }

        Ball mostUrgent = null;
        double highestPriority = Double.NEGATIVE_INFINITY;
        float paddleY = paddle.getBounds().y;

        for (Ball ball : balls) {
            if (!ball.isActive() || ball.isStuckToPaddle()) {
                continue;
            }

            Vector2 velocity = ball.getVelocity();
            float dy = velocity.y;

            // Only care about balls moving down (negative Y means moving down in screen coordinates)
            if (dy >= 0) {
                continue;
            }

            // Calculate priority: higher priority for balls closer to paddle and moving down
            float ballY = ball.getBounds().y;
            double distanceToPaddle = Math.abs(ballY - paddleY);

            // Higher priority if ball is below paddle level (urgent!)
            double priority = 0;
            if (ballY < paddleY) {
                // Ball is below paddle - very urgent!
                priority = 1000.0 - distanceToPaddle;
            } else {
                // Ball is above paddle - predict when it will reach paddle
                double timeToReach = (ballY - paddleY) / Math.abs(dy);
                if (timeToReach > 0 && timeToReach < 5.0) { // Only consider if reaching soon
                    priority = 500.0 - timeToReach * 10;
                }
            }

            if (priority > highestPriority) {
                highestPriority = priority;
                mostUrgent = ball;
            }
        }

        return mostUrgent;
    }

    /**
     * Find the best powerup to collect
     */
    private PowerUp findBestPowerUp(List<PowerUp> powerUps, Paddle paddle) {
        if (powerUps == null || powerUps.isEmpty()) {
            return null;
        }

        PowerUp best = null;
        double closestDistance = Double.MAX_VALUE;
        float paddleX = paddle.getBounds().x + paddle.getBounds().width / 2;
        float paddleY = paddle.getBounds().y;

        for (PowerUp powerUp : powerUps) {
            // Only consider powerups that are above the paddle and falling
            if (powerUp.y < paddleY - paddle.getBounds().height - 50) {
                continue; // Too far below, not worth it
            }

            double dx = powerUp.x + powerUp.width / 2 - paddleX;
            double dy = powerUp.y - paddleY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < closestDistance) {
                closestDistance = distance;
                best = powerUp;
            }
        }

        return best;
    }

    /**
     * Decide whether to prioritize collecting powerup over tracking ball
     */
    private boolean shouldPrioritizePowerUp(PowerUp powerUp, Ball ball, Paddle paddle) {
        if (powerUp == null) {
            return false;
        }

        if (ball == null) {
            return true; // No ball to track, collect powerup
        }

        float paddleX = paddle.getBounds().x + paddle.getBounds().width / 2;
        float paddleY = paddle.getBounds().y;

        // Calculate distance to powerup
        double powerUpDx = powerUp.x + powerUp.width / 2 - paddleX;
        double powerUpDy = powerUp.y - paddleY;
        double powerUpDistance = Math.sqrt(powerUpDx * powerUpDx + powerUpDy * powerUpDy);

        // Calculate distance to ball's predicted position
        double ballDistance = calculateBallDistance(ball, paddle);

        // Prioritize powerup if it's close and ball is not urgent
        if (powerUpDistance < POWERUP_PRIORITY_DISTANCE) {
            Vector2 velocity = ball.getVelocity();
            float dy = velocity.y;

            // If ball is moving up or far away, prioritize powerup
            if (dy > 0 || ballDistance > 200) {
                return true;
            }
            // If ball is close and moving down, prioritize ball
            if (dy < 0 && ballDistance < 100) {
                return false;
            }
            // Otherwise, prioritize powerup if it's very close
            return powerUpDistance < 80;
        }

        return false;
    }

    /**
     * Calculate distance from paddle center to ball's predicted position at paddle level
     */
    private double calculateBallDistance(Ball ball, Paddle paddle) {
        Vector2 velocity = ball.getVelocity();
        float dy = velocity.y;

        if (dy >= 0) {
            // Ball moving up, not urgent
            return Double.MAX_VALUE;
        }

        float paddleY = paddle.getBounds().y;
        float ballY = ball.getBounds().y;
        float ballX = ball.getBounds().x + ball.getBounds().width / 2;

        if (ballY < paddleY) {
            // Ball already below paddle - use current X position
            float paddleX = paddle.getBounds().x + paddle.getBounds().width / 2;
            return Math.abs(ballX - paddleX);
        }

        // Predict where ball will be when it reaches paddle level
        double timeToReach = (ballY - paddleY) / Math.abs(dy);
        if (timeToReach <= 0) {
            return Double.MAX_VALUE;
        }

        float dx = velocity.x;
        double predictedX = ballX + dx * timeToReach;
        float paddleX = paddle.getBounds().x + paddle.getBounds().width / 2;

        return Math.abs(predictedX - paddleX);
    }

    /**
     * Move paddle to intercept the ball
     */
    private void moveToHitBall(Paddle paddle, Ball ball) {
        float paddleX = paddle.getBounds().x + paddle.getBounds().width / 2;
        float paddleY = paddle.getBounds().y;
        float ballX = ball.getBounds().x + ball.getBounds().width / 2;
        float ballY = ball.getBounds().y;

        Vector2 velocity = ball.getVelocity();
        float dx = velocity.x;
        float dy = velocity.y;

        double targetX;

        if (dy < 0 && ballY > paddleY) {
            // Ball is moving down and above paddle - predict where it will hit
            double timeToReach = (ballY - paddleY) / Math.abs(dy);
            if (timeToReach > 0) {
                targetX = ballX + dx * timeToReach;
            } else {
                targetX = ballX;
            }
        } else if (ballY <= paddleY) {
            // Ball is at or below paddle level - track current position
            targetX = ballX;
        } else {
            // Ball moving up - just track current position
            targetX = ballX;
        }

        // Account for ball bouncing off walls
        targetX = Math.max(ball.getBounds().width / 2,
                          Math.min(WINDOW_WIDTH - ball.getBounds().width / 2, targetX));

        // Move paddle towards target
        double difference = targetX - paddleX;

        // Stop all movement first
        paddle.moveLeft(false);
        paddle.moveRight(false);

        if (Math.abs(difference) > PREDICTION_TOLERANCE) {
            if (difference > 0) {
                paddle.moveRight(true);
            } else {
                paddle.moveLeft(true);
            }
        }
    }

    /**
     * Move paddle to collect powerup
     */
    private void moveToCollectPowerUp(Paddle paddle, PowerUp powerUp) {
        float paddleX = paddle.getBounds().x + paddle.getBounds().width / 2;
        float powerUpX = powerUp.x + powerUp.width / 2;

        double difference = powerUpX - paddleX;

        paddle.moveLeft(false);
        paddle.moveRight(false);

        if (Math.abs(difference) > PREDICTION_TOLERANCE) {
            if (difference > 0) {
                paddle.moveRight(true);
            } else {
                paddle.moveLeft(true);
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        log.info("ArkanoidAI {}", enabled ? "ENABLED" : "DISABLED");
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

