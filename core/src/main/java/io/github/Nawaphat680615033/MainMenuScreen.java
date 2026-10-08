package io.github.Nawaphat680615033;
import Characters.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {
    private final Main game;
    private Texture background;
    private Texture startButton;
    private boolean waitingForCharacter;
    private OrthographicCamera camera;
    private static final float BUTTON_WIDTH = 288f;
    private static final float BUTTON_HEIGHT = 96f;

    public MainMenuScreen(Main game) {
        this(game, false);
    }

    public MainMenuScreen(Main game, boolean skipStartButton) {
        this.game = game;
        background = new Texture("startbackground.png");
        startButton = new Texture("start.png");
        this.waitingForCharacter = skipStartButton;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        float buttonX = (screenWidth - BUTTON_WIDTH) / 2f;
        float buttonY = (screenHeight - BUTTON_HEIGHT) / 2f;

        if (!waitingForCharacter) {
            if (Gdx.input.justTouched()) {
                float pointerX = Gdx.input.getX();
                float pointerY = screenHeight - Gdx.input.getY();
                if (pointerX >= buttonX && pointerX <= buttonX + BUTTON_WIDTH
                    && pointerY >= buttonY && pointerY <= buttonY + BUTTON_HEIGHT) {
                    waitingForCharacter = true;
                    System.out.println("Start Game Pressed! Press 1 for LiquidCat, 2 for Dragon, 3 for Grim.");
                }
            }
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                game.setScreen(new GameScreen(game, new LiquidCat(200, 200)));
                dispose();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                game.setScreen(new GameScreen(game, new Dragon(200, 200)));
                dispose();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                game.setScreen(new GameScreen(game, new Grim(200, 200)));
                dispose();
            }
        }

        game.batch.begin();
        game.batch.draw(background, 0f, 0f, screenWidth, screenHeight);
        if (!waitingForCharacter) {
            game.batch.draw(startButton, buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        }
        game.batch.end();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        background.dispose();
        startButton.dispose();
    }
}
