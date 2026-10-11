package io.github.Nawaphat680615033;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen implements Screen {

    private static final float VIRTUAL_WIDTH = 636f;
    private static final float VIRTUAL_HEIGHT = 504f;

    private static final float START_BUTTON_WIDTH = 288f;
    private static final float START_BUTTON_HEIGHT = 96f;
    private static final float TUTORIAL_BUTTON_SIZE = 68f;
    private static final float EDGE_PADDING = 24f;

    private final Main game;
    private final Texture background;
    private final Texture startButton;
    private final Texture tutorialButton;
    private final Texture tutorialPage;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Vector3 touchPoint = new Vector3();

    private boolean tutorialOpen = false;

    public MainMenuScreen(Main game) {
        this.game = game;

        background = new Texture("startbackground.png");
        startButton = new Texture("start.png");
        tutorialButton = new Texture("tutorialicon.png");
        tutorialPage = new Texture("tuition.png");

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0f);
        camera.update();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.04f, 0.02f, 1f);

        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        float startX = (VIRTUAL_WIDTH - START_BUTTON_WIDTH) / 2f;
        float startY = (VIRTUAL_HEIGHT - START_BUTTON_HEIGHT) / 2f;

        float tutorialX = VIRTUAL_WIDTH - TUTORIAL_BUTTON_SIZE - EDGE_PADDING;
        float tutorialY = VIRTUAL_HEIGHT - TUTORIAL_BUTTON_SIZE - EDGE_PADDING;

        if (tutorialOpen && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            tutorialOpen = false;
        }

        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
            viewport.unproject(touchPoint);

            float pointerX = touchPoint.x;
            float pointerY = touchPoint.y;

            if (tutorialOpen) {
                tutorialOpen = false;
            } else if (inside(pointerX, pointerY,
                tutorialX, tutorialY,
                TUTORIAL_BUTTON_SIZE, TUTORIAL_BUTTON_SIZE)) {
                tutorialOpen = true;
            } else if (inside(pointerX, pointerY,
                startX, startY,
                START_BUTTON_WIDTH, START_BUTTON_HEIGHT)) {
                game.setScreen(new CharacterSelectScreen(game));
                dispose();
                return;
            }
        }

        game.batch.begin();
        game.batch.draw(background, 0f, 0f, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        if (tutorialOpen) {
            drawTutorial();
        } else {
            game.batch.draw(startButton, startX, startY,
                START_BUTTON_WIDTH, START_BUTTON_HEIGHT);
            game.batch.draw(tutorialButton, tutorialX, tutorialY,
                TUTORIAL_BUTTON_SIZE, TUTORIAL_BUTTON_SIZE);
        }

        game.batch.end();
    }

    private boolean inside(float px, float py,
                           float x, float y,
                           float width, float height) {
        return px >= x && px <= x + width
            && py >= y && py <= y + height;
    }

    private void drawTutorial() {
        float scale = Math.min(
            VIRTUAL_WIDTH * 0.86f / tutorialPage.getWidth(),
            VIRTUAL_HEIGHT * 0.88f / tutorialPage.getHeight()
        );

        float pageWidth = tutorialPage.getWidth() * scale;
        float pageHeight = tutorialPage.getHeight() * scale;

        game.batch.draw(
            tutorialPage,
            (VIRTUAL_WIDTH - pageWidth) / 2f,
            (VIRTUAL_HEIGHT - pageHeight) / 2f,
            pageWidth,
            pageHeight
        );
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        background.dispose();
        startButton.dispose();
        tutorialButton.dispose();
        tutorialPage.dispose();
    }
}
