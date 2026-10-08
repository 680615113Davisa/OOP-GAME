package io.github680615035Davisa;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Player player;
    private List<Item> worldItems;
    private List<Monster> monsterList;
    private Texture background;

    private float spawnTimer = 0f;
    private final float SPAWN_INTERVAL = 5.0f;
    private boolean isPlaying = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        worldItems = new ArrayList<>();
        monsterList = new ArrayList<>();
        background = new Texture("backgroundoop.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if (!isPlaying) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                player = new LiquidCat(200, 200);
                isPlaying = true;
                System.out.println("Started as Liquid Cat");
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                player = new Dragon(200, 200);
                isPlaying = true;
                System.out.println("Started as Dragon");
            }
        } else {
            float delta = Gdx.graphics.getDeltaTime();

            // Check if player is dead (!player.isAlive())
            if (!player.isAlive()) {
                System.out.println("Game Over! Player died.");
                isPlaying = false; // Return to character selection screen
                monsterList.clear(); // Clear remaining monsters
                worldItems.clear();  // Clear items on ground
                return;
            }

            spawnTimer += delta;
            if (spawnTimer >= SPAWN_INTERVAL) {
                float screenW = Gdx.graphics.getWidth();
                float screenH = Gdx.graphics.getHeight();

                int side = (int) (Math.random() * 4);
                switch (side) {
                    case 0:
                        monsterList.add(new Scorpion((float) (Math.random() * screenW), screenH + 50));
                        break;
                    case 1:
                        monsterList.add(new Scorpion((float) (Math.random() * screenW), -50));
                        break;
                    case 2:
                        monsterList.add(new Scorpion(-50, (float) (Math.random() * screenH)));
                        break;
                    case 3:
                        monsterList.add(new Scorpion(screenW + 50, (float) (Math.random() * screenH)));
                        break;
                }

                spawnTimer = 0f;
            }
            player.handleInput(delta);

            batch.begin();

            batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            player.draw(batch);

            if (player instanceof LiquidCat) {
                ((LiquidCat) player).updateAndDrawPuddles(batch, delta, monsterList);
            }

            // Dragon fire breath damage check
            if (player instanceof Dragon && ((Dragon) player).isBreathing()) {
                for (Monster monster : monsterList) {
                    float dist = (float) Math.sqrt(Math.pow(player.x - monster.x, 2) + Math.pow(player.y - monster.y, 2));
                    if (dist < 120f) {
                        monster.takeDamage(1);
                    }
                }
            }

            for (int i = monsterList.size() - 1; i >= 0; i--) {
                Monster monster = monsterList.get(i);

                if (monster instanceof Scorpion) {
                    ((Scorpion) monster).update(delta, player, worldItems);
                }

                monster.draw(batch);

                if (!monster.isAlive()) {
                    monster.die(worldItems); // Call die method to drop items when defeated
                    monster.dispose();
                    monsterList.remove(i);
                }
            }

            for (int i = worldItems.size() - 1; i >= 0; i--) {
                Item item = worldItems.get(i);
                item.draw(batch);

                float dist = (float) Math.sqrt(Math.pow(player.x - item.x, 2) + Math.pow(player.y - item.y, 2));
                if (dist < 30f && !item.isCollected()) {
                    item.applyEffect(player);
                }

                if (item.isCollected()) {
                    worldItems.remove(i);
                }
            }

            batch.end();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        for (Monster monster : monsterList) {
            monster.dispose();
        }
    }
}
