package com.game.oop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** The playable soldier character. */
public class Soldier extends Player {
    private static final int CHARACTER_SIZE = 100;

    private final Texture[] characterTextures = new Texture[16];
    private final Animation<TextureRegion> backAnimation;
    private final Animation<TextureRegion> frontAnimation;
    private final Animation<TextureRegion> leftAnimation;
    private final Animation<TextureRegion> rightAnimation;
    private Animation<TextureRegion> currentAnimation;
    private float animationTime;

    public Soldier() {
        this(200f, 200f);
    }

    public Soldier(float x, float y) {
        super(x, y, 100, 200f,
            "Resprite_exports/Front/Front_0000.png");
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
            Texture frameTexture = new Texture(path);
            characterTextures[textureOffset + i] = frameTexture;
            frames[i] = new TextureRegion(frameTexture);
        }
        return new Animation<>(0.12f, frames);
    }

    public void update(float dx, float dy, float delta, float screenWidth, float screenHeight) {
        boolean moving = dx != 0f || dy != 0f;
        if (moving) {
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            move(dx / length, dy / length, delta);

            Animation<TextureRegion> nextAnimation;
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
