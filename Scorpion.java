package com.game.oop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.List;

public class Scorpion extends Monster {
    private Texture spriteSheet;
    private Animation<TextureRegion> animation;
    private float stateTime;
    private float attackTimer = 0f;
    private Player targetPlayer;
    private final float ATTACK_COOLDOWN = 1.0f;
    private final float ATTACK_RANGE = 45f;
    private final int ATTACK_DAMAGE = 10;

    public Scorpion(float x, float y) {
        super("Scorpion", x, y, 50, 100f, "scorpion.png");


        spriteSheet = new Texture("scorpion.png");


        int frameWidth = spriteSheet.getWidth() / 4;
        int frameHeight = spriteSheet.getHeight();

        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);


        TextureRegion[] frames = new TextureRegion[tmp[0].length];
        for (int i = 0; i < tmp[0].length; i++) {
            frames[i] = tmp[0][i];
        }


        animation = new Animation<>(0.15f, frames);
        stateTime = 0f;
    }

    @Override
    public void attack() {

        if (targetPlayer != null) {
            targetPlayer.takeDamage(10);
            System.out.println("Scorpion attacks player!");
        }
    }

    public void update(float delta, Player player, List<Item> worldItems) {

        stateTime += delta;
        updatePoison(delta, worldItems);

        // เก็บ Player ไว้ให้ attack() ใช้
        targetPlayer = player;

        if (attackTimer > 0) {
            attackTimer -= delta;
        }

        float dx = player.getX() - this.x;
        float dy = player.getY() - this.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // อยู่ใกล้พอ = โจมตี
        if (distance <= ATTACK_RANGE) {

            if (attackTimer <= 0) {
                attack();
                attackTimer = ATTACK_COOLDOWN;
            }

        } else {

            // ยังไม่ถึงตัว = เดินเข้าหา
            if (this.x < player.getX()) this.x += speed * delta;
            if (this.x > player.getX()) this.x -= speed * delta;
            if (this.y < player.getY()) this.y += speed * delta;
            if (this.y > player.getY()) this.y -= speed * delta;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {

        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);


        batch.draw(currentFrame, x, y, 64, 64);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }
}
