package io.github.Nawaphat680615033;
import Characters.Monster;
import Characters.Player;
import Characters.Scorpion;
import Characters.LiquidCat;
import Characters.Grim;
import Characters.Dragon;
import Item.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private final Main game;
    private Player player;
    private List<Item> worldItems;
    private List<Monster> monsters;
    private float spawnTimer = 0f;
    private float spawnInterval = 1.5f;
    private Texture background;
    private OrthographicCamera camera;

    public GameScreen(Main game, Player selectedPlayer) {
        this.game = game;
        this.player = selectedPlayer;
        this.worldItems = new ArrayList<>();
        this.monsters = new ArrayList<>();
        this.background = new Texture("backgroundoop.png");

        float mapWidth = Math.max((float) background.getWidth(), (float) Gdx.graphics.getWidth());
        float mapHeight = Math.max((float) background.getHeight(), (float) Gdx.graphics.getHeight());
        this.player.setMapBounds(mapWidth, mapHeight);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void spawnMonster() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float spawnX = 0, spawnY = 0;
        int edge = (int)(Math.random() * 4);
        switch (edge) {
            case 0: spawnX = (float)(Math.random() * screenWidth); spawnY = screenHeight + 50; break;
            case 1: spawnX = (float)(Math.random() * screenWidth); spawnY = -50; break;
            case 2: spawnX = -50; spawnY = (float)(Math.random() * screenHeight); break;
            case 3: spawnX = screenWidth + 50; spawnY = (float)(Math.random() * screenHeight); break;
        }
        monsters.add(new Scorpion(spawnX, spawnY));
    }

    @Override
    public void render(float delta) {
        if (!player.isAlive()) {
            System.out.println("Game Over!");
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return;
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        player.handleInput(delta);

        if (player instanceof Grim) {
            ((Grim) player).updateAura(monsters, delta);
        }

        spawnTimer += delta;
        if (spawnTimer >= spawnInterval) {
            spawnMonster();
            spawnTimer = 0f;
        }

        float mapWidth = Math.max((float) background.getWidth(), (float) Gdx.graphics.getWidth());
        float mapHeight = Math.max((float) background.getHeight(), (float) Gdx.graphics.getHeight());

        camera.position.x = player.getCenterX();
        camera.position.y = player.getCenterY();

        float halfCameraWidth = camera.viewportWidth / 2f;
        float halfCameraHeight = camera.viewportHeight / 2f;

        if (camera.position.x < halfCameraWidth) {
            camera.position.x = halfCameraWidth;
        } else if (camera.position.x > mapWidth - halfCameraWidth) {
            camera.position.x = mapWidth - halfCameraWidth;
        }
        if (camera.position.y < halfCameraHeight) {
            camera.position.y = halfCameraHeight;
        } else if (camera.position.y > mapHeight - halfCameraHeight) {
            camera.position.y = mapHeight - halfCameraHeight;
        }

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        game.batch.draw(background, 0, 0, mapWidth, mapHeight);

        player.draw(game.batch);

        if (player instanceof LiquidCat) {
            ((LiquidCat) player).updateAndDrawPuddles(game.batch, delta, monsters);
        }

        for (int i = monsters.size() - 1; i >= 0; i--) {
            Monster m = monsters.get(i);
            m.avoidOtherMonsters(monsters);
            m.update(delta, player, worldItems);
            m.draw(game.batch);

            if (m.attackTimer > 0) {
                m.attackTimer -= delta;
            }

            float pCenterX = player.getCenterX();
            float pCenterY = player.getCenterY();
            float mCenterX = m.getX() + 32f;
            float mCenterY = m.getY() + 32f;
            float distToPlayer = (float) Math.sqrt(Math.pow(pCenterX - mCenterX, 2) + Math.pow(pCenterY - mCenterY, 2));

            if (player instanceof Dragon) {
                if (((Dragon) player).isBreathing() && distToPlayer < 120f) {
                    m.takeDamage(2);
                }
            }

            if (distToPlayer < 40f) {
                if (m.attackTimer <= 0f) {
                    player.takeDamage(1);
                    m.attackTimer = 1.0f;
                }
            }

            if (!m.isAlive()) {
                m.die(worldItems);
                if (player instanceof Grim) {
                    ((Grim) player).increaseAuraDamage();
                }
                m.dispose();
                monsters.remove(i);
            }
        }

        for (int i = worldItems.size() - 1; i >= 0; i--) {
            Item item = worldItems.get(i);
            item.draw(game.batch);

            float pCenterX = player.getCenterX();
            float pCenterY = player.getCenterY();
            float iCenterX = item.getX() + 16f;
            float iCenterY = item.getY() + 16f;
            float dist = (float) Math.sqrt(Math.pow(pCenterX - iCenterX, 2) + Math.pow(pCenterY - iCenterY, 2));

            if (dist < 40f && !item.isCollected()) {
                item.applyEffect(player);
            }

            if (item.isCollected()) {
                item.dispose();
                worldItems.remove(i);
            }
        }
        game.batch.end();
    }

    @Override public void show() {}

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        float mapWidth = Math.max((float) background.getWidth(), (float) width);
        float mapHeight = Math.max((float) background.getHeight(), (float) height);
        player.setMapBounds(mapWidth, mapHeight);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        background.dispose();
        player.dispose();
        for (Monster m : monsters) m.dispose();
        for (Item i : worldItems) i.dispose();
    }
}
