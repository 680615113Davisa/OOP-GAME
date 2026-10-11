package io.github680615035Davisa;

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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CharacterSelectScreen implements Screen {
    private static final float UI_WIDTH = 636f;
    private static final float UI_HEIGHT = 504f;

    private static final float CARD_WIDTH = 176f;
    private static final float CARD_HEIGHT = 270f;
    private static final float BUTTON_WIDTH = 142f;
    private static final float BUTTON_HEIGHT = 38f;

    private static final Color LIGHT_SAND = new Color(1.00f, 0.94f, 0.69f, 1f);
    private static final Color PANEL = new Color(0.21f, 0.12f, 0.065f, 0.97f);
    private static final Color PANEL_TOP = new Color(0.34f, 0.19f, 0.085f, 1f);
    private static final Color MUTED = new Color(0.84f, 0.70f, 0.52f, 1f);
    private static final Color CAT_ACCENT = new Color(0.58f, 0.62f, 0.28f, 1f);
    private static final Color DRAGON_ACCENT = new Color(0.82f, 0.32f, 0.12f, 1f);
    private static final Color GRIM_ACCENT = new Color(0.55f, 0.38f, 0.27f, 1f);

    private final Main game;
    private final OrthographicCamera camera;
    private final OrthographicCamera backgroundCamera;
    private final Viewport viewport;
    private final Vector3 touchPoint = new Vector3();
    private final Texture background;
    private final Texture catSheet;
    private final Texture dragonSheet;
    private final Texture grimSheet;
    private final Texture uiPixel;
    private final Texture coinIcon;
    private final TextureRegion catPortrait;
    private final TextureRegion dragonPortrait;
    private final TextureRegion grimPortrait;
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
        coinIcon = new Texture("coin.png");

        catPortrait = firstFrame(catSheet, 8, 1);
        dragonPortrait = firstFrame(dragonSheet, 8, 2);
        grimPortrait = firstFrame(grimSheet, 7, 1);

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

        float cardY = 70f;
        float gap = 27f;
        float total = CARD_WIDTH * 3f + gap * 2f;
        float firstX = (w - total) / 2f;
        float catX = firstX;
        float dragonX = firstX + CARD_WIDTH + gap;
        float grimX = firstX + (CARD_WIDTH + gap) * 2f;

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
                if (GameSave.buyDragon()) {
                    showMessage("Dragon unlocked! 10,000 coins spent.");
                } else {
                    showMessage("Not enough coins. Dragon costs 10,000.");
                }
            }
            if (insideButton(mx, my, grimX, cardY)) {
                if (GameSave.isGrimUnlocked()) {
                    startGrim();
                    return;
                }
                if (GameSave.buyGrim()) {
                    showMessage("Grim unlocked! 20,000 coins spent.");
                } else {
                    showMessage("Not enough coins. Grim costs 20,000.");
                }
            }
        }

        game.batch.begin();

        drawRect(0f, h - 118f, w, 118f, new Color(0.20f, 0.105f, 0.040f, 0.95f));

        drawCenteredShadowed("CHOOSE YOUR FIGHTER", w / 2f, h - 34f, 1.72f, LIGHT_SAND, 3f);
        drawCenteredShadowed("COLLECT COINS  -  UNLOCK DESERT LEGENDS", w / 2f,
            h - 66f, 0.70f, MUTED, 2f);

        drawWallet(w - 22f, h - 91f);

        drawCharacterCard(catX, cardY, catPortrait,
            "LIQUID CAT", "Poison Puddle", "Immune in Liquid Form", 0, true, 1, CAT_ACCENT);
        drawCharacterCard(dragonX, cardY, dragonPortrait,
            "DRAGON", "Fire Breath", "150 HP", GameSave.DRAGON_COST,
            GameSave.isDragonUnlocked(), 2, DRAGON_ACCENT);
        drawCharacterCard(grimX, cardY, grimPortrait,
            "GRIM", "Soul Aura", "Power Grows on Kills", GameSave.GRIM_COST,
            GameSave.isGrimUnlocked(), 3, GRIM_ACCENT);

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

        drawRect(x - 4f, y - 4f, CARD_WIDTH + 8f, CARD_HEIGHT + 8f, new Color(0.12f, 0.065f, 0.025f, 0.92f));
        drawRect(x - 2f, y - 2f, CARD_WIDTH + 4f, CARD_HEIGHT + 4f, border);
        drawRect(x, y, CARD_WIDTH, CARD_HEIGHT, PANEL);
        drawRect(x, y + CARD_HEIGHT - 48f, CARD_WIDTH, 48f, PANEL_TOP);
        drawRect(x + 10f, y + CARD_HEIGHT - 50f, CARD_WIDTH - 20f, 2f, accent);

        drawCenteredShadowed(name, x + CARD_WIDTH / 2f, y + CARD_HEIGHT - 18f, 1.02f,
            unlocked ? Color.WHITE : MUTED, 2f);
        drawCenteredShadowed("KEY " + hotkey, x + CARD_WIDTH / 2f, y + CARD_HEIGHT - 38f,
            0.58f, accent, 1f);

        float portraitSize = 96f;
        float portraitX = x + (CARD_WIDTH - portraitSize) / 2f;
        float portraitY = y + 116f;

        drawRect(portraitX - 4f, portraitY - 4f, portraitSize + 8f, portraitSize + 8f,
            new Color(accent.r, accent.g, accent.b, unlocked ? 0.44f : 0.18f));
        drawRect(portraitX - 1f, portraitY - 1f, portraitSize + 2f, portraitSize + 2f,
            new Color(0.17f, 0.09f, 0.035f, 1f));

        if (!unlocked) game.batch.setColor(0.42f, 0.34f, 0.26f, 1f);
        else game.batch.setColor(Color.WHITE);
        game.batch.draw(portrait, portraitX, portraitY, portraitSize, portraitSize);
        game.batch.setColor(Color.WHITE);

        if (!unlocked) {
            drawRect(portraitX, portraitY + 31f, portraitSize, 34f,
                new Color(0.15f, 0.075f, 0.025f, 0.90f));
            drawCenteredShadowed("SEALED", x + CARD_WIDTH / 2f, portraitY + 55f,
                0.84f, LIGHT_SAND, 2f);
        }

        drawCenteredShadowed(line1, x + CARD_WIDTH / 2f, y + 102f, 0.70f,
            Color.WHITE, 1.5f);
        drawCenteredShadowed(line2, x + CARD_WIDTH / 2f, y + 83f, 0.64f,
            MUTED, 1f);

        float buttonX = x + (CARD_WIDTH - BUTTON_WIDTH) / 2f;
        float buttonY = y + 24f;
        boolean canAfford = GameSave.getCoins() >= cost;
        Color buttonColor;
        String buttonText;

        if (cost == 0 || unlocked) {
            buttonColor = new Color(accent.r * 0.55f, accent.g * 0.55f, accent.b * 0.55f, 1f);
            buttonText = "ENTER";
        } else if (canAfford) {
            buttonColor = new Color(0.68f, 0.36f, 0.10f, 1f);
            buttonText = "UNLOCK " + formatCoins(cost);
        } else {
            buttonColor = new Color(0.27f, 0.18f, 0.105f, 1f);
            buttonText = formatCoins(cost) + " COINS";
        }

        drawRect(buttonX - 2f, buttonY - 2f, BUTTON_WIDTH + 4f, BUTTON_HEIGHT + 4f,
            unlocked || canAfford ? accent : new Color(0.45f, 0.32f, 0.19f, 1f));
        drawRect(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, buttonColor);
        drawCenteredShadowed(buttonText, x + CARD_WIDTH / 2f, buttonY + 25f,
            0.68f, Color.WHITE, 1.5f);
    }

    private void drawWallet(float rightX, float y) {
        int coins = GameSave.getCoins();
        String text = formatCoins(coins);
        font.getData().setScale(1.05f);
        layout.setText(font, text);
        float iconSize = 26f;
        float contentWidth = iconSize + 10f + layout.width;
        float x = rightX - contentWidth;
        float boxX = x - 74f;
        float boxW = contentWidth + 88f;
        drawRect(boxX, y - 25f, boxW, 32f, new Color(0.24f, 0.13f, 0.055f, 0.96f));
        drawCenteredShadowed("DESERT COINS", boxX + 40f, y - 4f, 0.52f, MUTED, 1f);

        game.batch.setColor(Color.WHITE);
        game.batch.draw(coinIcon, x, y - 20f, iconSize, iconSize);
        font.getData().setScale(1.05f);
        font.setColor(LIGHT_SAND);
        font.draw(game.batch, text, x + iconSize + 10f, y);
        font.setColor(Color.WHITE);
        font.getData().setScale(1f);
    }

    private boolean insideButton(float px, float py, float cardX, float cardY) {
        float x = cardX + (CARD_WIDTH - BUTTON_WIDTH) / 2f;
        float y = cardY + 24f;
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

        font.setColor(new Color(0f, 0f, 0f, 0.88f));
        font.draw(game.batch, text, centerX - layout.width / 2f + shadowOffset,
            baselineY - shadowOffset);

        font.setColor(color);
        font.draw(game.batch, text, centerX - layout.width / 2f, baselineY);

        font.setColor(Color.WHITE);
        font.getData().setScale(1f);
    }

    private String formatCoins(int value) {
        return String.format("%,d", value);
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
        coinIcon.dispose();
        uiPixel.dispose();
        font.dispose();
    }
}
