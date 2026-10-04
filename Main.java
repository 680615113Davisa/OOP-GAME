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
    private List<Scorpion> scorpionList; // List to store all scorpion monsters in the game
    private Texture background;
    private boolean isPlaying = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        worldItems = new ArrayList<>();
        scorpionList = new ArrayList<>(); // Initialize the scorpion list
        background = new Texture("backgroundoop.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if (!isPlaying) {
            // Select character screen
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                player = new LiquidCat(200, 200);
                isPlaying = true;

                // Spawn scorpions on the map when the game starts
                scorpionList.add(new Scorpion(600, 300));

                System.out.println("Started as Liquid Cat");
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                player = new Dragon(200, 200);
                isPlaying = true;

                // Spawn scorpions on the map when the game starts
                scorpionList.add(new Scorpion(600, 300));

                System.out.println("Started as Dragon");
            }
        } else {
            float delta = Gdx.graphics.getDeltaTime();

            // Handle player input and movement
            player.handleInput(delta);

            batch.begin();

            // Draw background
            batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            // Draw player
            player.draw(batch);

            // If player is LiquidCat, update and draw puddles
            if (player instanceof LiquidCat) {
                ((LiquidCat) player).updateAndDrawPuddles(batch, delta, null);
            }

            // Update and draw all scorpion monsters
            for (int i = scorpionList.size() - 1; i >= 0; i--) {
                Scorpion scorpion = scorpionList.get(i);
                scorpion.update(delta, player, worldItems);
                scorpion.draw(batch);

                // Remove scorpion if it is dead
                if (!scorpion.isAlive()) {
                    scorpion.dispose();
                    scorpionList.remove(i);
                }
            }

            // Handle world items (coins, health potions)
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
        for (Scorpion scorpion : scorpionList) {
            scorpion.dispose();
        }
    }
}
