package io.github680615035Davisa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Dragon extends Player {
    private Texture spriteSheet;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> fireAnimation;
    private float stateTime;
    private float fireTimer = 0f;
    private final float FIRE_DURATION = 2.0f;

    private boolean isBreathing = false;
    private boolean facingRight = true;

    public Dragon(float x, float y) {
        super(x, y, 150, 180f, "Dragon.png");

        spriteSheet = new Texture("Dragon.png");

        int frameWidth = 64;
        int frameHeight = 64;
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);

        TextureRegion[] allFrames = new TextureRegion[16];
        int index = 0;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 8; col++) {
                if (row < tmp.length && col < tmp[row].length) {
                    allFrames[index++] = tmp[row][col];
                }
            }
        }

        TextureRegion[] walkFrames = new TextureRegion[4];
        System.arraycopy(allFrames, 0, walkFrames, 0, 4);
        walkAnimation = new Animation<>(0.12f, walkFrames);

        TextureRegion[] fireFrames = new TextureRegion[3];
        System.arraycopy(allFrames, 8, fireFrames, 0, 3);
        fireAnimation = new Animation<>(0.15f, fireFrames);

        stateTime = 0f;
    }

    @Override
    public void handleInput(float delta) {
        super.handleInput(delta);

        if (fireTimer > 0) {
            fireTimer -= delta;
            isBreathing = true;
        } else {
            isBreathing = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            facingRight = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            facingRight = true;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (fireTimer <= 0) {
                fireTimer = FIRE_DURATION;
                attack();
            }
        }
    }

    // Added getter method to check breathing state for monster collision
    public boolean isBreathing() {
        return isBreathing;
    }

    @Override
    public void attack() {
        System.out.println("Dragon breathes fire! (Cooldown 2s)");
    }

    @Override
    public void draw(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();

        TextureRegion currentFrame;
        if (isBreathing) {
            currentFrame = fireAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        }

        if (!facingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (facingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        float drawWidth = 128;
        float drawHeight = 128;
        batch.draw(currentFrame, x, y, drawWidth, drawHeight);
    }
}
