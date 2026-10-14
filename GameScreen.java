package io.github680615035Davisa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private enum Stage {
        EASY,
        MEDIUM,
        HARD,
        VICTORY
    }

    private static final int EASY_SCORPIONS = 10;

    private static final int MEDIUM_SCORPIONS = 2;
    private static final int MEDIUM_CACTUS = 2;

    private static final int HARD_SCORPIONS = 5;
    private static final int HARD_CACTUS = 2;
    private static final int HARD_MUMMY = 1;

    private static final int EASY_MAX_ACTIVE = 20;
    private static final int MEDIUM_MAX_ACTIVE = 25;
    private static final int HARD_MAX_ACTIVE = 18;

    private final Main game;
    private final Player player;
    private final List<Item> worldItems;
    private final List<Monster> monsters;
    private final Texture background;
    private final OrthographicCamera camera;
    private final BitmapFont hudFont;

    private Stage currentStage = Stage.EASY;

    private int scorpionsSpawned = 0;
    private int cactusSpawned = 0;
    private int mummySpawned = 0;
    private int stageKills = 0;

    private float spawnTimer = 0f;
    private float spawnInterval = 0.35f;
    private float stageBannerTimer = 2.5f;

    public GameScreen(Main game, Player selectedPlayer) {
        this.game = game;
        this.player = selectedPlayer;
        this.worldItems = new ArrayList<>();
        this.monsters = new ArrayList<>();
        this.background = new Texture("backgroundoop.png");
        this.hudFont = new BitmapFont();

        float mapWidth = Math.max((float) background.getWidth(), (float) Gdx.graphics.getWidth());
        float mapHeight = Math.max((float) background.getHeight(), (float) Gdx.graphics.getHeight());
        this.player.setMapBounds(mapWidth, mapHeight);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0,
            Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());

        if (!player.isAlive()) {
            System.out.println("Game Over!");
            game.setScreen(new GameOverScreen(game));
            dispose();
            return;
        }

        if (currentStage == Stage.VICTORY) {
            renderVictory();
            return;
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        player.handleInput(delta);

        if (player instanceof Soldier) {
            Soldier soldier = (Soldier) player;
            if (soldier.consumeAttackRequest()) {
                for (Monster monster : monsters) {
                    if (soldier.attack(monster)) {
                        break;
                    }
                }
            }
        }

        if (player instanceof Grim) {
            ((Grim) player).updateAura(monsters, delta);
        }

        updateStageSpawner(delta);

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

        if (player instanceof LiquidCat) {
            ((LiquidCat) player).updateAndDrawPuddles(game.batch, delta, monsters);
        }

        player.draw(game.batch);

        for (int i = monsters.size() - 1; i >= 0; i--) {
            Monster monster = monsters.get(i);

            monster.avoidOtherMonsters(monsters);
            monster.update(delta, player, worldItems);
            monster.draw(game.batch);

            if (monster instanceof Cactus) {
                ((Cactus) monster).updateBullets(delta, player, game.batch);
            }

            float pCenterX = player.getCenterX();
            float pCenterY = player.getCenterY();
            float mCenterX = monster.getX() + 32f;
            float mCenterY = monster.getY() + 32f;

            if (monster instanceof Mummy) {
                mCenterX = monster.getX() + 55f;
                mCenterY = monster.getY() + 55f;
            }

            float distToPlayer = (float) Math.sqrt(
                Math.pow(pCenterX - mCenterX, 2)
                    + Math.pow(pCenterY - mCenterY, 2)
            );

            if (player instanceof Dragon) {
                if (((Dragon) player).isBreathing() && distToPlayer < 120f) {
                    monster.takeDamage(2);
                }
            }

            if (!monster.isAlive()) {
                monster.die(worldItems);

                if (player instanceof Grim) {
                    ((Grim) player).increaseAuraDamage();
                }

                monster.dispose();
                monsters.remove(i);
                stageKills++;
            }
        }

        for (int i = worldItems.size() - 1; i >= 0; i--) {
            Item item = worldItems.get(i);
            item.draw(game.batch);

            float pCenterX = player.getCenterX();
            float pCenterY = player.getCenterY();
            float iCenterX = item.getX() + 16f;
            float iCenterY = item.getY() + 16f;
            float dist = (float) Math.sqrt(
                Math.pow(pCenterX - iCenterX, 2)
                    + Math.pow(pCenterY - iCenterY, 2)
            );

            if (dist < 40f && !item.isCollected()) {
                item.applyEffect(player);
            }

            if (item.isCollected()) {
                item.dispose();
                worldItems.remove(i);
            }
        }

        checkStageClear();
        drawHud();

        game.batch.end();
    }

    private void updateStageSpawner(float delta) {
        if (stageBannerTimer > 0f) {
            stageBannerTimer -= delta;
            return;
        }

        if (monsters.size() >= getMaxActiveMonsters()) {
            return;
        }

        spawnTimer += delta;

        if (spawnTimer >= spawnInterval) {
            spawnNextEnemy();
            spawnTimer = 0f;
        }
    }

    private void spawnNextEnemy() {
        // Spawn Mummy boss close to the player (emerging from the ground near the player)
        if (currentStage == Stage.HARD && mummySpawned < HARD_MUMMY) {
            float angle = (float) (Math.random() * Math.PI * 2);
            float distance = 100f + (float) (Math.random() * 50f);
            float spawnX = player.getCenterX() + (float) Math.cos(angle) * distance;
            float spawnY = player.getCenterY() + (float) Math.sin(angle) * distance;

            monsters.add(new Mummy(spawnX, spawnY));
            mummySpawned++;
            return;
        }

        int scorpionTarget = getScorpionTarget();
        int cactusTarget = getCactusTarget();

        boolean canSpawnScorpion = scorpionsSpawned < scorpionTarget;
        boolean canSpawnCactus = cactusSpawned < cactusTarget;

        if (!canSpawnScorpion && !canSpawnCactus) {
            return;
        }

        // Spawn regular monsters (Scorpions and Cactus) around the middle area, avoiding extreme map edges
        float[] spawn = middleAreaSpawnPosition();

        if (canSpawnScorpion && canSpawnCactus) {
            int mixedSpawnIndex = scorpionsSpawned + cactusSpawned;
            if (mixedSpawnIndex % 3 == 2) {
                monsters.add(new Cactus(spawn[0], spawn[1]));
                cactusSpawned++;
            } else {
                monsters.add(new Scorpion(spawn[0], spawn[1]));
                scorpionsSpawned++;
            }
        } else if (canSpawnScorpion) {
            monsters.add(new Scorpion(spawn[0], spawn[1]));
            scorpionsSpawned++;
        } else {
            monsters.add(new Cactus(spawn[0], spawn[1]));
            cactusSpawned++;
        }
    }

    // Helper method to spawn regular monsters in the middle background area rather than edges
    private float[] middleAreaSpawnPosition() {
        float mapWidth = background.getWidth();
        float mapHeight = background.getHeight();

        // Random positions focused towards the center region of the map
        float spawnX = mapWidth * 0.25f + (float) (Math.random() * (mapWidth * 0.5f));
        float spawnY = mapHeight * 0.25f + (float) (Math.random() * (mapHeight * 0.5f));

        return new float[] {spawnX, spawnY};
    }

    private void checkStageClear() {
        boolean allEnemiesSpawned =
            scorpionsSpawned >= getScorpionTarget()
                && cactusSpawned >= getCactusTarget()
                && mummySpawned >= getMummyTarget();

        if (allEnemiesSpawned && monsters.isEmpty()) {
            goToNextStage();
        }
    }

    private void goToNextStage() {
        switch (currentStage) {
            case EASY:
                currentStage = Stage.MEDIUM;
                System.out.println("EASY CLEAR! MEDIUM START!");
                break;

            case MEDIUM:
                currentStage = Stage.HARD;
                System.out.println("MEDIUM CLEAR! HARD START!");
                break;

            case HARD:
                currentStage = Stage.VICTORY;
                System.out.println("YOU WIN!");
                return;

            default:
                return;
        }

        scorpionsSpawned = 0;
        cactusSpawned = 0;
        mummySpawned = 0;
        stageKills = 0;
        spawnTimer = 0f;
        stageBannerTimer = 2.5f;
    }

    private int getScorpionTarget() {
        switch (currentStage) {
            case EASY:
                return EASY_SCORPIONS;
            case MEDIUM:
                return MEDIUM_SCORPIONS;
            case HARD:
                return HARD_SCORPIONS;
            default:
                return 0;
        }
    }

    private int getCactusTarget() {
        switch (currentStage) {
            case MEDIUM:
                return MEDIUM_CACTUS;
            case HARD:
                return HARD_CACTUS;
            default:
                return 0;
        }
    }

    private int getMummyTarget() {
        return currentStage == Stage.HARD ? HARD_MUMMY : 0;
    }

    private int getStageTotalEnemies() {
        return getScorpionTarget() + getCactusTarget() + getMummyTarget();
    }

    private int getMaxActiveMonsters() {
        switch (currentStage) {
            case EASY:
                return EASY_MAX_ACTIVE;
            case MEDIUM:
                return MEDIUM_MAX_ACTIVE;
            case HARD:
                return HARD_MAX_ACTIVE;
            default:
                return 0;
        }
    }

    private void drawHud() {
        float hudX = camera.position.x - camera.viewportWidth / 2f + 20f;
        float hudY = camera.position.y + camera.viewportHeight / 2f - 20f;

        hudFont.getData().setScale(1f);
        hudFont.draw(game.batch,
            "HP: " + player.getHealth() + "/" + player.getMaxHealth(),
            hudX, hudY);

        hudFont.draw(game.batch,
            "Run Coins: " + player.getCoinScore(),
            hudX, hudY - 22f);

        hudFont.draw(game.batch,
            "Wallet: " + String.format("%,d", GameSave.getCoins()),
            hudX, hudY - 44f);

        hudFont.draw(game.batch,
            "Stage: " + currentStage.name(),
            hudX, hudY - 66f);

        int enemiesLeft = Math.max(0, getStageTotalEnemies() - stageKills);
        hudFont.draw(game.batch,
            "Enemies Left: " + enemiesLeft,
            hudX, hudY - 88f);

        if (stageBannerTimer > 0f) {
            hudFont.getData().setScale(2.2f);
            String stageText = currentStage.name();
            float textX = camera.position.x - 70f;
            float textY = camera.position.y + 20f;
            hudFont.draw(game.batch, stageText, textX, textY);

            hudFont.getData().setScale(1f);
            String info = getStageInfoText();
            hudFont.draw(game.batch, info,
                camera.position.x - 135f,
                camera.position.y - 15f);
        }

        hudFont.getData().setScale(1f);
    }

    private String getStageInfoText() {
        switch (currentStage) {
            case EASY:
                return "200 Scorpions";
            case MEDIUM:
                return "200 Scorpions + 100 Cactus";
            case HARD:
                return "50 Scorpions + 25 Cactus + Mummy Boss";
            default:
                return "";
        }
    }

    private void renderVictory() {
        ScreenUtils.clear(0.08f, 0.04f, 0.02f, 1f);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        float mapWidth = Math.max((float) background.getWidth(), (float) Gdx.graphics.getWidth());
        float mapHeight = Math.max((float) background.getHeight(), (float) Gdx.graphics.getHeight());
        game.batch.draw(background, 0, 0, mapWidth, mapHeight);

        hudFont.getData().setScale(2.5f);
        hudFont.draw(game.batch, "YOU WIN!",
            camera.position.x - 90f,
            camera.position.y + 20f);

        hudFont.getData().setScale(1f);
        hudFont.draw(game.batch, "Press ENTER to return to menu",
            camera.position.x - 125f,
            camera.position.y - 20f);

        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    @Override
    public void show() {}

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
        hudFont.dispose();
        player.dispose();

        for (Monster monster : monsters) {
            monster.dispose();
        }

        for (Item item : worldItems) {
            item.dispose();
        }
    }
}
