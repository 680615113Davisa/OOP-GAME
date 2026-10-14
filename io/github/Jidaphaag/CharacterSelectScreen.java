package io.github.Jidaphaag;

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
    private static final float UI_WIDTH = 800f;
    private static final float UI_HEIGHT = 500f;

    private static final float BUTTON_WIDTH = 190f;
    private static final float BUTTON_HEIGHT = 46f;

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
    private final Texture characterTitle;
    private final Texture selectButton;

    private final TextureRegion catPortrait;
    private final TextureRegion dragonPortrait;
    private final TextureRegion grimPortrait;
    private final TextureRegion soldierPortrait;

    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    private String message = "";
    private float messageTimer = 0f;
    private int selectedCharacter = 0;

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
        characterTitle = new Texture("Character.png");
        selectButton = new Texture("selecticon.png");

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

        drawBackground();
        prepareUiViewport();

        if (handleNavigationInput()) return;

        if (handleCardClick()) return;
        drawSelectionScreen();
    }

    private void drawBackground() {
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
    }

    private void prepareUiViewport() {
        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
    }

    /** Handles screen-level controls and returns true when the screen changes. */
    private boolean handleNavigationInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            startCat();
            return true;
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
            else showMessage("Soldier is locked. Buy it for " + String.format("%,d", GameSave.SOLDIER_COST) + " coins.");
        }
        return false;
    }

    /** Handles selecting a character and activating its purchase/enter button. */
    private boolean handleCardClick() {
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
            viewport.unproject(touchPoint);
            float mx = touchPoint.x;
            float my = touchPoint.y;
            for (int i = 0; i < 4; i++) {
                float y = 355.5f - i * 70f;
                if (mx >= 29f && mx <= 190f && my >= y - 21f && my <= y + 19f) {
                    selectedCharacter = i;
                    return false;
                }
            }
            if (mx >= 560f && mx <= 750f && my >= 20f && my <= 83f) {
                return activateSelectedCharacter();
            }
        }
        return false;
    }

    private boolean activateSelectedCharacter() {
        switch (selectedCharacter) {
            case 0: startCat(); return true;
            case 1:
                if (GameSave.isDragonUnlocked()) { startDragon(); return true; }
                if (GameSave.buyDragon()) showMessage("Dragon unlocked! 10,000 coins spent.");
                else showMessage("Not enough coins. Dragon costs 10,000.");
                break;
            case 2:
                if (GameSave.isGrimUnlocked()) { startGrim(); return true; }
                if (GameSave.buyGrim()) showMessage("Grim unlocked! 20,000 coins spent.");
                else showMessage("Not enough coins. Grim costs 20,000.");
                break;
            case 3:
                if (GameSave.isSoldierUnlocked()) { startSoldier(); return true; }
                if (GameSave.buySoldier()) showMessage("Soldier unlocked! 25000 coins spent.");
                else showMessage("Not enough coins. Soldier costs 25,000. ");
                break;
            default: break;
        }
        return false;
    }

    private void drawSelectionScreen() {
        float w = UI_WIDTH;
        float h = UI_HEIGHT;
        game.batch.begin();
        Color shade = new Color(0.025f, 0.035f, 0.055f, 0.76f);
        drawRect(0f, 0f, w, h, shade);
        drawRect(0f, h - 72f, w, 72f, new Color(0.025f, 0.045f, 0.075f, 0.96f));
        game.batch.setColor(Color.WHITE);
        game.batch.draw(characterTitle, 24f, h - 70f, 230f, 70f);
        drawWallet(w - 24f, h - 39f);

        String[] names = {"LIQUID CAT", "DRAGON", "GRIM", "SOLDIER"};
        String[] abilities = {"Poison Puddle", "Fire Breath", "Soul Aura", "Close Combat"};
        String[] stats = {"Liquid Immunity", "150 HP", "Power on Kills", "10 Damage"};
        TextureRegion[] portraits = {catPortrait, dragonPortrait, grimPortrait, soldierPortrait};
        Color[] accents = {CAT_ACCENT, DRAGON_ACCENT, GRIM_ACCENT, SOLDIER_ACCENT};
        int[] costs = {0, GameSave.DRAGON_COST, GameSave.GRIM_COST, GameSave.SOLDIER_COST};
        boolean[] unlocked = {true, GameSave.isDragonUnlocked(), GameSave.isGrimUnlocked(), GameSave.isSoldierUnlocked()};

        // Left rail mirrors the stacked character navigation in the reference.
        drawRect(18f, 94f, 183f, 314f, new Color(0.055f, 0.07f, 0.09f, 0.88f));
        for (int i = 0; i < 4; i++) {
            float y = 355.5f - i * 70f;
            if (i == selectedCharacter) {
                drawRect(27f, y - 23f, 165f, 44f,
                    new Color(0.70f, 0.58f, 0.30f, 1f));
            }
            drawRect(29f, y - 21f, 161f, 40f,
                i == selectedCharacter ? new Color(0.16f, 0.14f, 0.10f, 0.98f) : new Color(0.10f, 0.11f, 0.12f, 0.88f));
            game.batch.setColor(unlocked[i] ? Color.WHITE : new Color(0.38f, 0.38f, 0.38f, 1f));
            game.batch.draw(portraits[i], 35f, y - 15f, 36f, 36f);
            game.batch.setColor(Color.WHITE);
            drawShadowText(names[i], 110f, y + 17f, 0.88f, LIGHT_SAND, 1f);
            drawShadowText(unlocked[i] ? "AVAILABLE" : "LOCKED", 110f, y - 2f,
                0.64f, MUTED, 1f);
        }

        // Large character preview and a separate information panel.
        TextureRegion selectedPortrait = portraits[selectedCharacter];
        Color accent = accents[selectedCharacter];
        drawRect(215f, 94f, 326f, 314f, new Color(0.10f, 0.09f, 0.075f, 0.52f));
        drawRect(535f, 94f, 250f, 314f, new Color(0.035f, 0.045f, 0.06f, 0.93f));
        drawRect(535f, 350f, 250f, 58f, new Color(0.90f, 0.89f, 0.84f, 1f));
        drawCenteredShadowed(names[selectedCharacter], 660f, 389f, 1.00f,
            new Color(0.13f, 0.14f, 0.16f, 1f), 0.5f);
        drawCenteredShadowed(unlocked[selectedCharacter] ? "UNLOCKED" : "LOCKED", 660f, 369f,
            0.62f, accent, 0.5f);
        float previewSize = 250f;
        game.batch.setColor(unlocked[selectedCharacter] ? Color.WHITE : new Color(0.42f, 0.42f, 0.42f, 1f));
        game.batch.draw(selectedPortrait, 253f, 111f, previewSize, previewSize);
        game.batch.setColor(Color.WHITE);
        drawRect(552f, 309f, 214f, 2f, accent);
        drawShadowText("ABILITY", 552f, 288f, 0.68f, MUTED, 1f);
        drawShadowText(abilities[selectedCharacter], 552f, 262f, 0.92f, LIGHT_SAND, 1f);
        drawShadowText("SPECIAL", 552f, 220f, 0.68f, MUTED, 1f);
        drawShadowText(stats[selectedCharacter], 552f, 194f, 0.82f, LIGHT_SAND, 1f);
        drawShadowText("COST : " + String.format("%,d", costs[selectedCharacter]) + " COINS",
            552f, 151f, 0.68f, MUTED, 1f);

        game.batch.setColor(Color.WHITE);
        game.batch.draw(selectButton, 560f, 20f, BUTTON_WIDTH, 63f);
        drawCenteredShadowed("ESC  -  BACK", 402f, 22f, 0.70f, MUTED, 1f);

        if (messageTimer > 0f && !message.isEmpty()) {
            float boxW = Math.min(520f, w - 40f);
            float boxX = (w - boxW) / 2f;
            drawRect(boxX, 34f, boxW, 36f, new Color(0.24f, 0.13f, 0.055f, 0.97f));
            drawCenteredShadowed(message, w / 2f, 57f, 0.73f, Color.WHITE, 1.5f);
        }

        game.batch.setColor(Color.WHITE);
        game.batch.end();
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
        float iconSize = 40f;
        float contentWidth = iconSize + 7f + layout.width;
        float x = rightX - contentWidth;

        drawShadowText("COINS: ", x - 70f, y, 1.5f, MUTED, 1f);
        game.batch.setColor(Color.WHITE);
        game.batch.draw(coinIcon, x, y - 30f, iconSize, iconSize);
        drawShadowText(text, x + iconSize + 7f, y, 1.5f, LIGHT_SAND, 1f);
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
        characterTitle.dispose();
        selectButton.dispose();
        uiPixel.dispose();
        font.dispose();
    }
}
