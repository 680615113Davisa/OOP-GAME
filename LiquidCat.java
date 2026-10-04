package com.game.oop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;

public class LiquidCat extends Player {
    private Texture walkSheet;
    private Texture liquidSheet;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> liquidAnimation;
    private float stateTime;
    private float liquidStateTime = 0f;

    private boolean isLiquid = false;
    private List<PoisonPuddle> puddles;
    private float cooldownTimer = 0f;
    private final float COOLDOWN_TIME = 5.0f;

    public LiquidCat(float x, float y) {
        super(x, y, 100, 200f, "Cat right.png");


        walkSheet = new Texture("Cat right.png");
        TextureRegion[][] walkTmp = TextureRegion.split(walkSheet, 64, 64);
        int walkCols = walkTmp[0].length;
        TextureRegion[] walkFrames = new TextureRegion[walkCols];
        for (int i = 0; i < walkCols; i++) {
            walkFrames[i] = walkTmp[0][i];
        }
        walkAnimation = new Animation<>(0.12f, walkFrames);


        liquidSheet = new Texture("Catli2.png");
        TextureRegion[][] liquidTmp = TextureRegion.split(liquidSheet, 64, 64);
        int liquidCols = liquidTmp[1].length;
        TextureRegion[] liquidFrames = new TextureRegion[liquidCols];
        for (int i = 0; i < liquidCols; i++) {
            liquidFrames[i] = liquidTmp[1][i];
        }
        liquidAnimation = new Animation<>(0.12f, liquidFrames);

        stateTime = 0f;
        puddles = new ArrayList<>();
    }

    @Override
    public void handleInput(float delta) {
        if (cooldownTimer > 0) {
            cooldownTimer -= delta;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            if (cooldownTimer <= 0) {
                isLiquid = !isLiquid;
                cooldownTimer = COOLDOWN_TIME;
                liquidStateTime = 0f;

                if (isLiquid) {
                    System.out.println("Liquid !");
                } else {
                    System.out.println("N Cat");
                }
            } else {
                System.out.println("Cooldown is active! Please wait.");
            }
        }

        if (isLiquid) {
            this.speed = 60f;
        } else {
            this.speed = 200f;
        }

        super.handleInput(delta);

        if (!isLiquid) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                puddles.add(new PoisonPuddle(this.x, this.y));
                System.out.println("Left Click Dropped a poison puddle!");
            }
        }
    }

    public void updateAndDrawPuddles(SpriteBatch batch, float delta, List<Monster> monsters) {
        for (int i = puddles.size() - 1; i >= 0; i--) {
            PoisonPuddle puddle = puddles.get(i);
            puddle.update(delta);
            puddle.draw(batch);

            if (monsters != null) {
                for (Monster monster : monsters) {
                    if (puddle.collidesWith(monster)) {
                        monster.applyPoison(4.0f);
                    }
                }
            }

            if (puddle.isExpired()) {
                puddle.dispose();
                puddles.remove(i);
            }
        }
    }

    @Override
    public void takeDamage(int damage) {
        if (isLiquid) {
            System.out.println("Damage blocked! Cat is in liquid ");
            return;
        }
        super.takeDamage(damage);
    }

    @Override
    public void attack() {
        if (!isLiquid) {
            System.out.println("Cat attack!");
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;

        if (isLiquid) {
            liquidStateTime += Gdx.graphics.getDeltaTime();

            currentFrame = liquidAnimation.getKeyFrame(liquidStateTime, false);
        } else {

            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        }

        batch.draw(currentFrame, x, y);
    }

    public boolean isLiquid() {
        return isLiquid;
    }
}
