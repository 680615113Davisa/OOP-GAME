package com.game.oop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** Character selection page shown after the start page. */
public class CharacterSelectScreen {
    private static final float SELECT_WIDTH = 96f;
    private static final float SELECT_HEIGHT = 34f;

    private final Texture background;
    private final Texture characterHeader;
    private final Texture selectButton;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final CharacterSelectedListener listener;
    private final Texture[] portraitTextures = new Texture[4];
    private final TextureRegion[] portraits = new TextureRegion[4];

    public CharacterSelectScreen(CharacterSelectedListener listener) {
        this.listener = listener;
        background = new Texture("startbackground.png");
        characterHeader = new Texture("Character.png");
        selectButton = new Texture("selecticon.png");
        batch = new SpriteBatch();
        font = new BitmapFont();
        portraitTextures[0] = loadTextureFromZip("liquidcat.zip", "Catli2.png");
        portraits[0] = firstFrame(portraitTextures[0], 8, 3);
        portraitTextures[1] = new Texture("Dragon.png");
        portraits[1] = firstFrame(portraitTextures[1], 8, 2);
        portraitTextures[2] = loadTextureFromZip("Soldier.zip", "Front.png");
        portraits[2] = firstFrame(portraitTextures[2], 4, 1);
        portraitTextures[3] = loadTextureFromZip("Dracula_Bat5 (1).zip", "Dracula_Bat5.png");
        portraits[3] = new TextureRegion(portraitTextures[3]);
    }

    public void render() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float portraitSize = 116f;
        float portraitY = screenHeight * 0.43f;
        float selectY = portraitY - SELECT_HEIGHT - 24f;
        float[] centersX = {
            screenWidth * 0.125f,
            screenWidth * 0.375f,
            screenWidth * 0.625f,
            screenWidth * 0.875f
        };

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            listener.selected("Liquid Cat");
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            listener.selected("Dragon");
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            listener.selected("Soldier");
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            listener.selected("Grim");
            return;
        }
        if (Gdx.input.justTouched()) {
            float mouseX = Gdx.input.getX();
            float mouseY = screenHeight - Gdx.input.getY();
            if (insideSelect(mouseX, mouseY, centersX[0], selectY)) {
                listener.selected("Liquid Cat");
                return;
            }
            if (insideSelect(mouseX, mouseY, centersX[1], selectY)) {
                listener.selected("Dragon");
                return;
            }
            if (insideSelect(mouseX, mouseY, centersX[2], selectY)) {
                listener.selected("Soldier");
                return;
            }
            if (insideSelect(mouseX, mouseY, centersX[3], selectY)) {
                listener.selected("Grim");
                return;
            }
        }

        batch.begin();
        batch.draw(background, 0f, 0f, screenWidth, screenHeight);
        batch.draw(characterHeader, screenWidth * 0.32f, screenHeight * 0.77f,
            screenWidth * 0.36f, screenHeight * 0.13f);
        font.setColor(Color.WHITE);
        drawChoice(batch, 0, centersX[0], portraitY, selectY, portraitSize);
        drawChoice(batch, 1, centersX[1], portraitY, selectY, portraitSize);
        drawChoice(batch, 2, centersX[2], portraitY, selectY, portraitSize);
        drawChoice(batch, 3, centersX[3], portraitY, selectY, portraitSize);
        font.getData().setScale(1f);
        font.draw(batch, "Press 1-4 or click a character", screenWidth * 0.34f, screenHeight * 0.14f);
        batch.end();

    }

    private boolean insideSelect(float x, float y, float centerX, float selectY) {
        float selectX = centerX - SELECT_WIDTH / 2f;
        return x >= selectX && x <= selectX + SELECT_WIDTH
            && y >= selectY && y <= selectY + SELECT_HEIGHT;
    }

    private void drawChoice(SpriteBatch spriteBatch, int index, float centerX,
                            float portraitY, float selectY, float portraitSize) {
        float characterSize = index == 1 ? portraitSize * 1.5f : portraitSize;
        float dragonOffset = index == 1 ? portraitSize * 0.25f : 0f;
        float soldierOffset = index == 2 ? portraitSize * 0.15f : 0f;
        spriteBatch.draw(portraits[index], centerX + dragonOffset - soldierOffset - characterSize / 2f,
            portraitY - dragonOffset, characterSize, characterSize);
        spriteBatch.draw(selectButton, centerX - SELECT_WIDTH / 2f,
            selectY, SELECT_WIDTH, SELECT_HEIGHT);
    }

    private TextureRegion firstFrame(Texture texture, int columns, int rows) {
        return new TextureRegion(texture, 0, 0,
            texture.getWidth() / columns, texture.getHeight() / rows);
    }

    private Texture loadTextureFromZip(String zipPath, String targetFileName) {
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

    public void dispose() {
        background.dispose();
        characterHeader.dispose();
        selectButton.dispose();
        batch.dispose();
        font.dispose();
        for (Texture texture : portraitTextures) {
            if (texture != null) texture.dispose();
        }
    }

    public interface CharacterSelectedListener {
        void selected(String characterName);
    }
}
