package com.game.oop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private LiquidCat player;
    private List<Item> worldItems;

    @Override
    public void create() {
        batch = new SpriteBatch();
        player = new LiquidCat(200, 200);

        worldItems = new ArrayList<>();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();


        player.handleInput(delta);


        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);


        batch.begin();


        player.draw(batch);


        player.updateAndDrawPuddles(batch, delta, null);


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

    @Override
    public void dispose() {
        batch.dispose();
    }
}
