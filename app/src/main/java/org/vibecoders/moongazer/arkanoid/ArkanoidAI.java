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
    private static final double PREDICTION_TOLERANCE = 15.0;
    private static final double POWERUP_PRIORITY_DISTANCE = 150.0;
    private static final double URGENT_PRIORITY_BASE = 1000.0;
    private static final double NORMAL_PRIORITY_BASE = 500.0;
    private static final double TIME_PENALTY_MULTIPLIER = 10.0;
    private static final double MAX_TIME_TO_REACH = 5.0;
    private static final double BALL_FAR_DISTANCE = 200.0;
    private static final double BALL_CLOSE_DISTANCE = 100.0;
    private static final double POWERUP_VERY_CLOSE_DISTANCE = 80.0;
    private static final double MIN_VELOCITY_THRESHOLD = 0.01;

    private boolean enabled = false;

    @Override
    public void update(Paddle paddle, List<Ball> balls, List<PowerUp> powerUps) {
        if (!enabled) {
            if (paddle != null) {
                paddle.moveLeft(false);
                paddle.moveRight(false);
            }
            return;
        }
        if (paddle == null) {
            return;
        }

        Ball targetBall = findMostUrgentBall(balls, paddle);
        PowerUp targetPowerUp = findBestPowerUp(powerUps, paddle);

        if (targetPowerUp != null && shouldPrioritizePowerUp(targetPowerUp, targetBall, paddle)) {
            moveToCollectPowerUp(paddle, targetPowerUp);
        } else if (targetBall != null) {
            moveToHitBall(paddle, targetBall);
        } else {
            paddle.moveLeft(false);
            paddle.moveRight(false);
        }
    }

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

            if (dy >= 0) {
                continue;
            }

            float ballY = ball.getBounds().y;
            double distanceToPaddle = Math.abs(ballY - paddleY);

            double priority = 0;
            if (ballY < paddleY) {
                priority = URGENT_PRIORITY_BASE - distanceToPaddle;
            } else {
                if (Math.abs(dy) < MIN_VELOCITY_THRESHOLD) {
                    continue;
                }
                double timeToReach = (ballY - paddleY) / Math.abs(dy);
                if (timeToReach > 0 && timeToReach < MAX_TIME_TO_REACH) {
                    priority = NORMAL_PRIORITY_BASE - timeToReach * TIME_PENALTY_MULTIPLIER;
                }
            }

            if (priority > highestPriority) {
                highestPriority = priority;
                mostUrgent = ball;
            }
        }

        return mostUrgent;
    }

    private PowerUp findBestPowerUp(List<PowerUp> powerUps, Paddle paddle) {
        if (powerUps == null || powerUps.isEmpty()) {
            return null;
        }

        PowerUp best = null;
        double closestDistance = Double.MAX_VALUE;
        float paddleX = paddle.getBounds().x + paddle.getBounds().width / 2;
        float paddleY = paddle.getBounds().y;

        for (PowerUp powerUp : powerUps) {
            if (powerUp.y < paddleY - paddle.getBounds().height - 50) {
                continue;
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

    private boolean shouldPrioritizePowerUp(PowerUp powerUp, Ball ball, Paddle paddle) {
        if (powerUp == null) {
            return false;
        }
        if (ball == null) {
            return true;
        }

        float paddleX = paddle.getBounds().x + paddle.getBounds().width / 2;
        float paddleY = paddle.getBounds().y;
        double powerUpDx = powerUp.x + powerUp.width / 2 - paddleX;
        double powerUpDy = powerUp.y - paddleY;
        double powerUpDistance = Math.sqrt(powerUpDx * powerUpDx + powerUpDy * powerUpDy);

        if (powerUpDistance < POWERUP_PRIORITY_DISTANCE) {
            Vector2 velocity = ball.getVelocity();
            float dy = velocity.y;
            float ballY = ball.getBounds().y;
            
            if (dy >= 0 || ballY < paddleY) {
                return true;
            }
            
            if (Math.abs(dy) >= MIN_VELOCITY_THRESHOLD) {
                double timeToReach = (ballY - paddleY) / Math.abs(dy);
                if (timeToReach > 0 && timeToReach < 2.0) {
                    return false;
                }
            }
            
            return powerUpDistance < POWERUP_VERY_CLOSE_DISTANCE;
        }
        return false;
    }

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
            if (Math.abs(dy) < MIN_VELOCITY_THRESHOLD) {
                targetX = ballX;
            } else {
                double timeToReach = (ballY - paddleY) / Math.abs(dy);
                if (timeToReach > 0) {
                    targetX = predictBallXWithBounce(ballX, dx, timeToReach);
                } else {
                    targetX = ballX;
                }
            }
        } else {
            targetX = ballX;
        }

        targetX = Math.max(SIDE_PANEL_WIDTH + ball.getBounds().width / 2,
                          Math.min(SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH - ball.getBounds().width / 2, targetX));

        double difference = targetX - paddleX;
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

    private double predictBallXWithBounce(float ballX, float dx, double timeToReach) {
        double predictedX = ballX + dx * timeToReach;
        float leftWall = SIDE_PANEL_WIDTH;
        float rightWall = SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH;
        
        while (predictedX < leftWall || predictedX > rightWall) {
            if (predictedX < leftWall) {
                predictedX = 2 * leftWall - predictedX;
                dx = -dx;
            } else if (predictedX > rightWall) {
                predictedX = 2 * rightWall - predictedX;
                dx = -dx;
            }
        }
        return predictedX;
    }

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