package com.game.oop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** Start page with a background and one button to enter the game. */
public class StartScreen {
    private static final float BUTTON_WIDTH = 288f;
    private static final float BUTTON_HEIGHT = 96f;

    private final Texture background;
    private final Texture startButton;
    private final SpriteBatch batch;
    private final Runnable onStart;

    public StartScreen(Runnable onStart) {
        this.onStart = onStart;
        background = new Texture("startbackground.png");
        startButton = new Texture("start.png");
        batch = new SpriteBatch();
    }

    public void render() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float buttonX = (screenWidth - BUTTON_WIDTH) / 2f;
        float buttonY = (screenHeight - BUTTON_HEIGHT) / 2f;

        if (Gdx.input.justTouched()) {
            float pointerX = Gdx.input.getX();
            float pointerY = screenHeight - Gdx.input.getY();
            if (pointerX >= buttonX && pointerX <= buttonX + BUTTON_WIDTH
                && pointerY >= buttonY && pointerY <= buttonY + BUTTON_HEIGHT) {
                onStart.run();
                return;
            }
        }

        batch.begin();
        batch.draw(background, 0f, 0f, screenWidth, screenHeight);
        batch.draw(startButton, buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        batch.end();
    }

    public void dispose() {
        background.dispose();
        startButton.dispose();
        batch.dispose();
    }
}
