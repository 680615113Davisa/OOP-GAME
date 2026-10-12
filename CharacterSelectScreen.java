package io.github.Nawaphat680615033;

import Characters.Dragon;
import Characters.Grim;
import Characters.LiquidCat;
import Characters.Soldier;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** Character shop + selection screen with persistent unlocks. */
public class CharacterSelectScreen implements Screen {
    private static final float UI_WIDTH = 636f;
    private static final float UI_HEIGHT = 504f;

    // Four compact cards so the layout stays exactly the same when resized.
    private static final float CARD_WIDTH = 136f;
    private static final float CARD_HEIGHT = 258f;
    private static final float BUTTON_WIDTH = 116f;
    private static final float BUTTON_HEIGHT = 36f;

    private static final Color LIGHT_SAND = new Color(1.00f, 0.94f, 0.69f, 1f);
    private static final Color PANEL = new Color(0.21f, 0.12f, 0.065f, 0.97f);
    private static final Color PANEL_TOP = new Color(0.34f, 0.19f, 0.085f, 1f);
    private static final Color MUTED = new Color(0.84f, 0.70f, 0.52f, 1f);
    private static final Color CAT_ACCENT = new Color(0.58f, 0.62f, 0.28f, 1f);
    private static final Color DRAGON_ACCENT = new Color(0.82f, 0.32f, 0.12f, 1f);
    private static final Color GRIM_ACCENT = new Color(0.55f, 0.38f, 0.27f, 1f);
    private static final Color SOLDIER_ACCENT = new Color(0.66f, 0.53f, 0.28f, 1f);

    private final Main game;
    private final OrthographicCamera camera;
    private final OrthographicCamera backgroundCamera;
    private final Viewport viewport;
    private final Vector3 touchPoint = new Vector3();

    private final Texture background;
    private final Texture catSheet;
    private final Texture dragonSheet;
    private final Texture grimSheet;
    private final Texture soldierSheet;
    private final Texture uiPixel;
    private final Texture coinIcon;

    private final TextureRegion catPortrait;
    private final TextureRegion dragonPortrait;
    private final TextureRegion grimPortrait;
    private final TextureRegion soldierPortrait;

    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    private String message = "";
    private float messageTimer = 0f;

    public CharacterSelectScreen(Main game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(UI_WIDTH, UI_HEIGHT, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        backgroundCamera = new OrthographicCamera();
        backgroundCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        background = new Texture("startbackground.png");
        catSheet = new Texture("Cat right.png");
        dragonSheet = new Texture("Dragon.png");
        grimSheet = new Texture("Grim_soul-sheet.png");
        soldierSheet = loadTextureFromZip("Soldier.zip", "Front.png");
        coinIcon = new Texture("coin.png");

        catPortrait = firstFrame(catSheet, 8, 1);
        dragonPortrait = firstFrame(dragonSheet, 8, 2);
        grimPortrait = firstFrame(grimSheet, 7, 1);
        soldierPortrait = firstFrame(soldierSheet, 4, 1);

        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixel.setColor(Color.WHITE);
        pixel.fill();
        uiPixel = new Texture(pixel);
        pixel.dispose();

        font = new BitmapFont();
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
            throw new GdxRuntimeException("Could not load " + targetFileName + " from " + zipPath, e);
        }

        throw new GdxRuntimeException("Missing " + targetFileName + " in " + zipPath);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.12f, 0.07f, 0.03f, 1f);
        if (messageTimer > 0f) messageTimer -= delta;

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        backgroundCamera.setToOrtho(false, screenW, screenH);
        backgroundCamera.update();
        Gdx.gl.glViewport(0, 0, (int) screenW, (int) screenH);
        game.batch.setProjectionMatrix(backgroundCamera.combined);
        game.batch.begin();
        game.batch.setColor(Color.WHITE);
        game.batch.draw(background, 0f, 0f, screenW, screenH);
        game.batch.setColor(new Color(0.22f, 0.11f, 0.035f, 0.50f));
        game.batch.draw(uiPixel, 0f, 0f, screenW, screenH);
        game.batch.setColor(Color.WHITE);
        game.batch.end();

        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        float w = UI_WIDTH;
        float h = UI_HEIGHT;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            startCat();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            if (GameSave.isDragonUnlocked()) startDragon();
            else showMessage("Dragon is locked. Buy it for 10,000 coins.");
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            if (GameSave.isGrimUnlocked()) startGrim();
            else showMessage("Grim is locked. Buy it for 20,000 coins.");
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            if (GameSave.isSoldierUnlocked()) startSoldier();
            else showMessage("Soldier is locked. Buy it for 30,000 coins.");
        }

        float cardY = 76f;
        float gap = 12f;
        float total = CARD_WIDTH * 4f + gap * 3f;
        float firstX = (w - total) / 2f;

        float catX = firstX;
        float dragonX = firstX + (CARD_WIDTH + gap);
        float grimX = firstX + (CARD_WIDTH + gap) * 2f;
        float soldierX = firstX + (CARD_WIDTH + gap) * 3f;

        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
            viewport.unproject(touchPoint);
            float mx = touchPoint.x;
            float my = touchPoint.y;

            if (insideButton(mx, my, catX, cardY)) {
                startCat();
                return;
            }

            if (insideButton(mx, my, dragonX, cardY)) {
                if (GameSave.isDragonUnlocked()) {
                    startDragon();
                    return;
                }
                if (GameSave.buyDragon()) showMessage("Dragon unlocked! 10,000 coins spent.");
                else showMessage("Not enough coins. Dragon costs 10,000.");
            }

            if (insideButton(mx, my, grimX, cardY)) {
                if (GameSave.isGrimUnlocked()) {
                    startGrim();
                    return;
                }
                if (GameSave.buyGrim()) showMessage("Grim unlocked! 20,000 coins spent.");
                else showMessage("Not enough coins. Grim costs 20,000.");
            }

            if (insideButton(mx, my, soldierX, cardY)) {
                if (GameSave.isSoldierUnlocked()) {
                    startSoldier();
                    return;
                }
                if (GameSave.buySoldier()) showMessage("Soldier unlocked! 30,000 coins spent.");
                else showMessage("Not enough coins. Soldier costs 30,000.");
            }
        }

        game.batch.begin();

        drawRect(0f, h - 118f, w, 118f, new Color(0.20f, 0.105f, 0.040f, 0.95f));
        drawCenteredShadowed("CHOOSE YOUR FIGHTER", w / 2f, h - 34f, 1.72f, LIGHT_SAND, 3f);
        drawCenteredShadowed("COLLECT COINS  -  UNLOCK DESERT LEGENDS", w / 2f,
            h - 66f, 0.70f, MUTED, 2f);
        drawWallet(w - 22f, h - 91f);

        drawCharacterCard(catX, cardY, catPortrait,
            "LIQUID CAT", "Poison Puddle", "Liquid Immunity", 0, true, 1, CAT_ACCENT);
        drawCharacterCard(dragonX, cardY, dragonPortrait,
            "DRAGON", "Fire Breath", "150 HP", GameSave.DRAGON_COST,
            GameSave.isDragonUnlocked(), 2, DRAGON_ACCENT);
        drawCharacterCard(grimX, cardY, grimPortrait,
            "GRIM", "Soul Aura", "Power on Kills", GameSave.GRIM_COST,
            GameSave.isGrimUnlocked(), 3, GRIM_ACCENT);
        drawCharacterCard(soldierX, cardY, soldierPortrait,
            "SOLDIER", "Close Combat", "10 Damage", GameSave.SOLDIER_COST,
            GameSave.isSoldierUnlocked(), 4, SOLDIER_ACCENT);

        drawCenteredShadowed("ESC  -  BACK", w / 2f, 22f, 0.68f, MUTED, 1.5f);

        if (messageTimer > 0f && !message.isEmpty()) {
            float boxW = Math.min(520f, w - 40f);
            float boxX = (w - boxW) / 2f;
            drawRect(boxX, 34f, boxW, 36f, new Color(0.24f, 0.13f, 0.055f, 0.97f));
            drawCenteredShadowed(message, w / 2f, 57f, 0.73f, Color.WHITE, 1.5f);
        }

        game.batch.setColor(Color.WHITE);
        game.batch.end();
    }

    private void drawCharacterCard(float x, float y, TextureRegion portrait,
                                   String name, String line1, String line2,
                                   int cost, boolean unlocked, int hotkey, Color accent) {
        Color border = unlocked ? accent : new Color(0.42f, 0.31f, 0.20f, 1f);

        drawRect(x - 3f, y - 3f, CARD_WIDTH + 6f, CARD_HEIGHT + 6f,
            new Color(0.12f, 0.065f, 0.025f, 0.92f));
        drawRect(x - 1f, y - 1f, CARD_WIDTH + 2f, CARD_HEIGHT + 2f, border);
        drawRect(x, y, CARD_WIDTH, CARD_HEIGHT, PANEL);
        drawRect(x, y + CARD_HEIGHT - 46f, CARD_WIDTH, 46f, PANEL_TOP);
        drawRect(x + 8f, y + CARD_HEIGHT - 48f, CARD_WIDTH - 16f, 2f, accent);

        drawCenteredShadowed(name, x + CARD_WIDTH / 2f, y + CARD_HEIGHT - 17f,
            0.83f, unlocked ? Color.WHITE : MUTED, 1.5f);
        drawCenteredShadowed("KEY " + hotkey, x + CARD_WIDTH / 2f, y + CARD_HEIGHT - 36f,
            0.48f, accent, 1f);

        float portraitSize = 76f;
        float portraitX = x + (CARD_WIDTH - portraitSize) / 2f;
        float portraitY = y + 116f;

        game.batch.setColor(unlocked ? Color.WHITE : new Color(0.30f, 0.30f, 0.30f, 1f));
        game.batch.draw(portrait, portraitX, portraitY, portraitSize, portraitSize);
        game.batch.setColor(Color.WHITE);

        if (!unlocked) {
            drawRect(portraitX, portraitY + 25f, portraitSize, 26f,
                new Color(0.08f, 0.04f, 0.02f, 0.84f));
            drawCenteredShadowed("SEALED", x + CARD_WIDTH / 2f, portraitY + 43f,
                0.57f, LIGHT_SAND, 1f);
        }

        drawCenteredShadowed(line1, x + CARD_WIDTH / 2f, y + 100f, 0.53f,
            new Color(0.95f, 0.88f, 0.75f, 1f), 1f);
        drawCenteredShadowed(line2, x + CARD_WIDTH / 2f, y + 83f, 0.48f,
            MUTED, 1f);

        float buttonX = x + (CARD_WIDTH - BUTTON_WIDTH) / 2f;
        float buttonY = y + 20f;
        boolean canAfford = GameSave.getCoins() >= cost;
        Color buttonColor;
        String buttonText;

        if (cost == 0 || unlocked) {
            buttonColor = new Color(0.31f, 0.39f, 0.16f, 1f);
            buttonText = "ENTER";
        } else if (canAfford) {
            buttonColor = new Color(0.58f, 0.31f, 0.10f, 1f);
            buttonText = "BUY " + shortCoins(cost);
        } else {
            buttonColor = new Color(0.25f, 0.15f, 0.09f, 1f);
            buttonText = shortCoins(cost) + " COINS";
        }

        drawRect(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, buttonColor);
        drawCenteredShadowed(buttonText, x + CARD_WIDTH / 2f, buttonY + 23f,
            0.52f, Color.WHITE, 1f);
    }

    private String shortCoins(int value) {
        if (value >= 1000 && value % 1000 == 0) return (value / 1000) + "K";
        return String.format("%,d", value);
    }

    private void drawWallet(float rightX, float y) {
        int coins = GameSave.getCoins();
        String text = String.format("%,d", coins);

        font.getData().setScale(0.82f);
        layout.setText(font, text);
        float iconSize = 20f;
        float contentWidth = iconSize + 7f + layout.width;
        float x = rightX - contentWidth;

        drawShadowText("DESERT COINS", x - 70f, y, 0.48f, MUTED, 1f);
        game.batch.setColor(Color.WHITE);
        game.batch.draw(coinIcon, x, y - 16f, iconSize, iconSize);
        drawShadowText(text, x + iconSize + 7f, y, 0.82f, LIGHT_SAND, 1f);
    }

    private boolean insideButton(float px, float py, float cardX, float cardY) {
        float x = cardX + (CARD_WIDTH - BUTTON_WIDTH) / 2f;
        float y = cardY + 20f;
        return px >= x && px <= x + BUTTON_WIDTH && py >= y && py <= y + BUTTON_HEIGHT;
    }

    private void drawRect(float x, float y, float width, float height, Color color) {
        game.batch.setColor(color);
        game.batch.draw(uiPixel, x, y, width, height);
        game.batch.setColor(Color.WHITE);
    }

    private void drawCenteredShadowed(String text, float centerX, float baselineY,
                                      float scale, Color color, float shadowOffset) {
        font.getData().setScale(scale);
        layout.setText(font, text);
        float x = centerX - layout.width / 2f;
        drawShadowText(text, x, baselineY, scale, color, shadowOffset);
    }

    private void drawShadowText(String text, float x, float y,
                                float scale, Color color, float shadowOffset) {
        font.getData().setScale(scale);
        font.setColor(0.08f, 0.035f, 0.015f, 0.95f);
        font.draw(game.batch, text, x + shadowOffset, y - shadowOffset);
        font.setColor(color);
        font.draw(game.batch, text, x, y);
        font.setColor(Color.WHITE);
        font.getData().setScale(1f);
    }

    private void showMessage(String text) {
        message = text;
        messageTimer = 2.6f;
        System.out.println(text);
    }

    private void startCat() {
        game.setScreen(new GameScreen(game, new LiquidCat(200, 200)));
        dispose();
    }

    private void startDragon() {
        if (!GameSave.isDragonUnlocked()) return;
        game.setScreen(new GameScreen(game, new Dragon(200, 200)));
        dispose();
    }

    private void startGrim() {
        if (!GameSave.isGrimUnlocked()) return;
        game.setScreen(new GameScreen(game, new Grim(200, 200)));
        dispose();
    }

    private void startSoldier() {
        if (!GameSave.isSoldierUnlocked()) return;
        game.setScreen(new GameScreen(game, new Soldier(200, 200)));
        dispose();
    }

    @Override public void show() {}

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        backgroundCamera.setToOrtho(false, width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        background.dispose();
        catSheet.dispose();
        dragonSheet.dispose();
        grimSheet.dispose();
        soldierSheet.dispose();
        coinIcon.dispose();
        uiPixel.dispose();
        font.dispose();
    }
}
