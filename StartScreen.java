package com.game.oop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** Start page with a background and one button to enter the game. */
public class StartScreen {
    private static final float BUTTON_WIDTH = 288f;
    private static final float BUTTON_HEIGHT = 96f;
    private static final float TUTORIAL_SIZE = 68f;

    private final Texture background;
    private final Texture startButton;
    private final Texture tutorialButton;
    private final Texture tutorialPage;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Runnable onStart;
    private boolean tutorialOpen;

    public StartScreen(Runnable onStart) {
        this.onStart = onStart;
        background = new Texture("startbackground.png");
        startButton = new Texture("start.png");
        tutorialButton = new Texture("tutorialicon.png");
        tutorialPage = new Texture("tuition.png");
        batch = new SpriteBatch();
        font = new BitmapFont();
    }

    public void render() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float buttonX = (screenWidth - BUTTON_WIDTH) / 2f;
        float buttonY = (screenHeight - BUTTON_HEIGHT) / 2f;
        float tutorialX = screenWidth - TUTORIAL_SIZE - 24f;
        float tutorialY = screenHeight - TUTORIAL_SIZE - 24f;

        if (tutorialOpen && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            tutorialOpen = false;
        }

        if (Gdx.input.justTouched()) {
            float pointerX = Gdx.input.getX();
            float pointerY = screenHeight - Gdx.input.getY();
            if (tutorialOpen) {
                tutorialOpen = false;
                return;
            }
            if (pointerX >= tutorialX && pointerX <= tutorialX + TUTORIAL_SIZE
                && pointerY >= tutorialY && pointerY <= tutorialY + TUTORIAL_SIZE) {
                tutorialOpen = true;
                return;
            }
            if (pointerX >= buttonX && pointerX <= buttonX + BUTTON_WIDTH
                && pointerY >= buttonY && pointerY <= buttonY + BUTTON_HEIGHT) {
                onStart.run();
                return;
            }
        }

        batch.begin();
        batch.draw(background, 0f, 0f, screenWidth, screenHeight);
        if (tutorialOpen) {
            float scale = Math.min(screenWidth * 0.86f / tutorialPage.getWidth(),
                screenHeight * 0.88f / tutorialPage.getHeight());
            float pageWidth = tutorialPage.getWidth() * scale;
            float pageHeight = tutorialPage.getHeight() * scale;
            batch.draw(tutorialPage, (screenWidth - pageWidth) / 2f,
                (screenHeight - pageHeight) / 2f, pageWidth, pageHeight);
        } else {
            batch.draw(startButton, buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
            batch.draw(tutorialButton, tutorialX, tutorialY, TUTORIAL_SIZE, TUTORIAL_SIZE);
        }
        batch.end();
    }

    public void dispose() {
        background.dispose();
        startButton.dispose();
        tutorialButton.dispose();
        tutorialPage.dispose();
        batch.dispose();
        font.dispose();
    }
}
