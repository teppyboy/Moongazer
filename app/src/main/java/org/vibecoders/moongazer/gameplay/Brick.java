package org.vibecoders.moongazer.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import org.vibecoders.moongazer.managers.Assets;

public class Brick {
    public enum BrickType {
        BREAKABLE,
        UNBREAKABLE
    }

    private Rectangle bounds;
    private BrickType type;
    private Texture texture;
    private boolean destroyed;
    private int hits; // For multi-hit bricks in the future

    public Brick(float x, float y, float width, float height, BrickType type) {
        this.bounds = new Rectangle(x, y, width, height);
        this.type = type;
        this.destroyed = false;
        this.hits = (type == BrickType.UNBREAKABLE) ? -1 : 1;
        loadTexture();
    }

    private void loadTexture() {
        try {
            // Try direct loading without AssetManager
            String path;
            if (type == BrickType.UNBREAKABLE) {
                path = "arkanoid_assets/bricks/unbreakable_brick.png";
            } else {
                path = "arkanoid_assets/bricks/breakable_brick_lv1.png";
            }

            com.badlogic.gdx.files.FileHandle fileHandle = Gdx.files.internal(path);
            if (fileHandle.exists()) {
                texture = new Texture(fileHandle);
            } else {
                createFallbackTexture();
            }
        } catch (Exception e) {
            // Fallback to generated texture if asset not found
            createFallbackTexture();
        }
    }

    private void createFallbackTexture() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap((int)bounds.width, (int)bounds.height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        if (type == BrickType.UNBREAKABLE) {
            pixmap.setColor(0.3f, 0.3f, 0.3f, 1f); // Dark gray
        } else {
            pixmap.setColor(0.2f, 0.6f, 0.9f, 1f); // Blue
        }
        pixmap.fill();

        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.drawRectangle(0, 0, (int)bounds.width, (int)bounds.height);

        texture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void hit() {
        if (type == BrickType.UNBREAKABLE) {
            return;
        }
        hits--;
        if (hits <= 0) {
            destroyed = true;
        }
    }

    public void render(SpriteBatch batch) {
        if (!destroyed && texture != null) {
            // Draw texture scaled to brick bounds
            batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public BrickType getType() {
        return type;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
