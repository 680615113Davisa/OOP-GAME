package com.game.oop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;


    private Player player;
    private List<Item> worldItems;


    private boolean isPlaying = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        worldItems = new ArrayList<>();

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


            player.handleInput(delta);

            batch.begin();
            player.draw(batch);


            if (player instanceof LiquidCat) {
                ((LiquidCat) player).updateAndDrawPuddles(batch, delta, null);
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
    }
}
