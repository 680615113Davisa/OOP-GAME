package io.github.Jidaphaag;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;

/** Screen shown after the player loses. */
public class GameOverScreen implements Screen {
    private final Main game;
    private final OrthographicCamera camera;
    private final Texture background;
    private final Texture gameOverImage;
    private final BitmapFont font;
    private final GlyphLayout layout;

    public GameOverScreen(Main game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.background = new Texture("startbackground.png");
        this.gameOverImage = new Texture("gameover.png");
        this.font = new BitmapFont();
        this.layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        Gdx.gl.glViewport(0, 0, screenWidth, screenHeight);
        camera.setToOrtho(false, screenWidth, screenHeight);
        ScreenUtils.clear(0.12f, 0.02f, 0.02f, 1f);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        game.batch.draw(background,
                camera.position.x - camera.viewportWidth / 2f,
                camera.position.y - camera.viewportHeight / 2f,
                camera.viewportWidth,
                camera.viewportHeight);

        float maxImageWidth = camera.viewportWidth * 0.8f;
        float maxImageHeight = camera.viewportHeight * 0.65f;
        float imageScale = Math.min(
                maxImageWidth / gameOverImage.getWidth(),
                maxImageHeight / gameOverImage.getHeight());
        float imageWidth = gameOverImage.getWidth() * imageScale;
        float imageHeight = gameOverImage.getHeight() * imageScale;
        game.batch.draw(gameOverImage,
                camera.position.x - imageWidth / 2f,
                camera.position.y + 20f - imageHeight / 2f,
                imageWidth,
                imageHeight);

        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new CharacterSelectScreen(game));
            dispose();
        }
    }

    private void drawCentered(String text, float y, float scale) {
        font.getData().setScale(scale);
        layout.setText(font, text);
        font.draw(game.batch, text,
                camera.position.x - layout.width / 2f,
                y + layout.height / 2f);
        font.getData().setScale(1f);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        background.dispose();
        gameOverImage.dispose();
        font.dispose();
    }
}
