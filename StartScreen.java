package com.game.oop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StartScreen {
    private static final float START_BUTTON_WIDTH = 288f;
    private static final float START_BUTTON_HEIGHT = 96f;
    private static final float TUTORIAL_BUTTON_SIZE = 68f;
    private static final float EDGE_PADDING = 24f;

    private final Texture background;
    private final Texture tutorialPage;
    private final SpriteBatch batch;
    private final Button startButton;
    private final Button tutorialButton;
    private final Runnable onStart;
    private boolean tutorialOpen;

    public StartScreen(Runnable onStart) {
        this.onStart = onStart;
        background = new Texture("startbackground.png");
        tutorialPage = new Texture("tuition.png");
        batch = new SpriteBatch();
        startButton = new Button("start.png", START_BUTTON_WIDTH, START_BUTTON_HEIGHT);
        tutorialButton = new Button("tutorialicon.png", TUTORIAL_BUTTON_SIZE,
            TUTORIAL_BUTTON_SIZE);
    }

    public void render() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        positionButtons(screenWidth, screenHeight);
        if (handleInput(screenHeight)) return;
        draw(screenWidth, screenHeight);
    }

    private void positionButtons(float screenWidth, float screenHeight) {
        startButton.setPosition((screenWidth - START_BUTTON_WIDTH) / 2f,
            (screenHeight - START_BUTTON_HEIGHT) / 2f);
        tutorialButton.setPosition(screenWidth - TUTORIAL_BUTTON_SIZE - EDGE_PADDING,
            screenHeight - TUTORIAL_BUTTON_SIZE - EDGE_PADDING);
    }

    private boolean handleInput(float screenHeight) {
        if (tutorialOpen && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            tutorialOpen = false;
        }
        if (!Gdx.input.justTouched()) return false;

        float pointerX = Gdx.input.getX();
        float pointerY = screenHeight - Gdx.input.getY();
        if (tutorialOpen) {
            tutorialOpen = false;
            return true;
        } else if (tutorialButton.contains(pointerX, pointerY)) {
            tutorialOpen = true;
            return true;
        } else if (startButton.contains(pointerX, pointerY)) {
            onStart.run();
            return true;
        }
        return false;
    }

    private void draw(float screenWidth, float screenHeight) {
        batch.begin();
        batch.draw(background, 0f, 0f, screenWidth, screenHeight);
        if (tutorialOpen) {
            drawTutorial(screenWidth, screenHeight);
        } else {
            startButton.draw(batch);
            tutorialButton.draw(batch);
        }
        batch.end();
    }

    private void drawTutorial(float screenWidth, float screenHeight) {
        float scale = Math.min(screenWidth * 0.86f / tutorialPage.getWidth(),
            screenHeight * 0.88f / tutorialPage.getHeight());
        float pageWidth = tutorialPage.getWidth() * scale;
        float pageHeight = tutorialPage.getHeight() * scale;
        batch.draw(tutorialPage, (screenWidth - pageWidth) / 2f,
            (screenHeight - pageHeight) / 2f, pageWidth, pageHeight);
    }

    public void dispose() {
        background.dispose();
        tutorialPage.dispose();
        startButton.dispose();
        tutorialButton.dispose();
        batch.dispose();
    }

    private static final class Button {
        private final Texture texture;
        private final float width;
        private final float height;
        private float x;
        private float y;

        private Button(String texturePath, float width, float height) {
            texture = new Texture(texturePath);
            this.width = width;
            this.height = height;
        }

        private void setPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }

        private boolean contains(float pointX, float pointY) {
            return pointX >= x && pointX <= x + width
                && pointY >= y && pointY <= y + height;
        }

        private void draw(SpriteBatch batch) {
            batch.draw(texture, x, y, width, height);
        }

        private void dispose() {
            texture.dispose();
        }
    }
}
