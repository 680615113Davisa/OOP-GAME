package com.game.oop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

/** Playable scene using four-direction animations and backgroundplay.png. */
public class Soldier extends ApplicationAdapter {
    private static final int FRAME_SIZE = 64;
    private static final int CHARACTER_SIZE = FRAME_SIZE * 2;
    private static final float MOVE_SPEED = 200f;

    private SpriteBatch batch;
    private Texture background;
    private final Texture[] characterTextures = new Texture[16];
    private Animation<TextureRegion> backAnimation;
    private Animation<TextureRegion> frontAnimation;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private Animation<TextureRegion> currentAnimation;
    private float x = 200f;
    private float y = 200f;
    private float animationTime;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("backgroundplay.png");

        backAnimation = loadAnimation("Back", 0);
        frontAnimation = loadAnimation("Front", 4);
        leftAnimation = loadAnimation("Left", 8);
        rightAnimation = loadAnimation("Right", 12);
        currentAnimation = frontAnimation;
    }

    private Animation<TextureRegion> loadAnimation(String direction, int textureOffset) {
        TextureRegion[] frames = new TextureRegion[4];
        for (int i = 0; i < frames.length; i++) {
            String path = "Resprite_exports/" + direction + "/" + direction + "_000" + i + ".png";
            Texture texture = new Texture(path);
            characterTextures[textureOffset + i] = texture;
            frames[i] = new TextureRegion(texture);
        }
        return new Animation<>(0.12f, frames);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        float dx = 0f;
        float dy = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) dx -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) dy -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) dy += 1f;

        boolean moving = dx != 0f || dy != 0f;
        if (moving) {
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            x += dx / length * MOVE_SPEED * delta;
            y += dy / length * MOVE_SPEED * delta;

            Animation<TextureRegion> nextAnimation = currentAnimation;
            if (Math.abs(dx) > Math.abs(dy)) {
                nextAnimation = dx < 0f ? leftAnimation : rightAnimation;
            } else {
                nextAnimation = dy > 0f ? backAnimation : frontAnimation;
            }
            if (nextAnimation != currentAnimation) {
                currentAnimation = nextAnimation;
                animationTime = 0f;
            }
            animationTime += delta;
        }

        x = Math.max(0f, Math.min(x, Gdx.graphics.getWidth() - CHARACTER_SIZE));
        y = Math.max(0f, Math.min(y, Gdx.graphics.getHeight() - CHARACTER_SIZE));

        TextureRegion frame = currentAnimation.getKeyFrame(moving ? animationTime : 0f, true);

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        batch.begin();
        batch.draw(background, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(frame, x, y, CHARACTER_SIZE, CHARACTER_SIZE);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        for (Texture texture : characterTextures) {
            if (texture != null) texture.dispose();
        }
    }
}
