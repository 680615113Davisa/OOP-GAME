package com.game.oop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.badlogic.gdx.utils.ScreenUtils;

/** Owns the game loop, start page, and playable scene. */
public class GameApp extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background;
    private StartScreen startScreen;
    private CharacterSelectScreen characterSelectScreen;
    private Player player;
    private boolean choosingCharacter;
    private boolean gameStarted;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("backgroundplay.png");
        startScreen = new StartScreen(() -> choosingCharacter = true);
        characterSelectScreen = new CharacterSelectScreen(this::startGame);
    }

    @Override
    public void render() {
        if (!gameStarted) {
            if (choosingCharacter) {
                characterSelectScreen.render();
            } else {
                startScreen.render();
            }
            return;
        }

        float delta = Gdx.graphics.getDeltaTime();
        player.handleInput(delta);

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        batch.begin();
        batch.draw(background, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        player.draw(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        startScreen.dispose();
        characterSelectScreen.dispose();
        if (player != null) player.dispose();
    }

    private void startGame(String characterName) {
        if (player != null) player.dispose();
        switch (characterName) {
            case "Liquid Cat":
                player = new LiquidCat(200f, 200f);
                break;
            case "Dragon":
                player = new Dragon(200f, 200f);
                break;
            case "Soldier":
                player = new Soldier(200f, 200f);
                break;
            case "Grim":
                player = new SelectableGrim(200f, 200f);
                break;
            default:
                player = new LiquidCat(200f, 200f);
                break;
        }
        gameStarted = true;
    }

    /** Playable Grim fallback kept local to GameApp so Grim.java stays untouched. */
    private static final class SelectableGrim extends Player {
        private static final float DRAW_SIZE = 100f;

        private SelectableGrim(float x, float y) {
            super(x, y, 150, 120f, "Dragon.png");
            Texture placeholder = texture;
            texture = loadTextureFromZip("Dracula_Bat5 (1).zip", "Dracula_Bat5.png");
            placeholder.dispose();
        }

        @Override
        public float getCenterX() {
            return 0;
        }

        @Override
        public float getCenterY() {
            return 0;
        }

        @Override
        public void attack() {
            System.out.println("Grim attacks!");
        }

        @Override
        public void draw(SpriteBatch batch) {
            batch.draw(texture, x, y, DRAW_SIZE, DRAW_SIZE);
        }
    }

    private static Texture loadTextureFromZip(String zipPath, String targetFileName) {
        try (ZipInputStream zip = new ZipInputStream(Gdx.files.internal(zipPath).read())) {
            ZipEntry entry;
            byte[] buffer = new byte[4096];
            while ((entry = zip.getNextEntry()) != null) {
                String fileName = entry.getName();
                int slash = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
                fileName = fileName.substring(slash + 1).replace(" ", "");
                if (!fileName.equalsIgnoreCase(targetFileName.replace(" ", ""))) continue;

                ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();
                int bytesRead;
                while ((bytesRead = zip.read(buffer)) != -1) {
                    imageBytes.write(buffer, 0, bytesRead);
                }
                byte[] data = imageBytes.toByteArray();
                Pixmap pixmap = new Pixmap(data, 0, data.length);
                try {
                    return new Texture(pixmap);
                } finally {
                    pixmap.dispose();
                }
            }
        } catch (IOException e) {
            throw new GdxRuntimeException("Could not load " + targetFileName
                + " from " + zipPath, e);
        }
        throw new GdxRuntimeException("Missing " + targetFileName + " in " + zipPath);
    }
}
