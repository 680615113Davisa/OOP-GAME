package io.github680615035Davisa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Dragon extends Player {
    private Texture spriteSheet;
    private Animation<TextureRegion> walkAnimation; // แอนิเมชันเดิน (วงสีเขียว: 4 เฟรมแรก)
    private Animation<TextureRegion> fireAnimation; // แอนิเมชันพ่นไฟ (วงสีแดง: 3 เฟรมแรกของแถวล่าง)
    private float stateTime;
    private float fireTimer = 0f;          // นับเวลาการพ่นไฟ
    private final float FIRE_DURATION = 2.0f; // พ่นค้างไว้ 2 วินาที (คูลดาวน์/ระยะเวลาท่าทาง)

    private boolean isBreathing = false;
    private boolean facingRight = true;

    public Dragon(float x, float y) {
        super(x, y, 150, 180f, "Dragon.png");

        spriteSheet = new Texture("Dragon.png");

        int frameWidth = 64;
        int frameHeight = 64;
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);

        // นำภาพทั้งหมด 16 เฟรม (2 แถว x 8 คอลัมน์) มาเรียงต่อกันเป็นอาเรย์เดียว
        TextureRegion[] allFrames = new TextureRegion[16];
        int index = 0;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 8; col++) {
                if (row < tmp.length && col < tmp[row].length) {
                    allFrames[index++] = tmp[row][col];
                }
            }
        }

        // 1. วงสีเขียว: เฟรมที่ 1 ถึง 4 (index 0-3) สำหรับเดินปกติ
        TextureRegion[] walkFrames = new TextureRegion[4];
        System.arraycopy(allFrames, 0, walkFrames, 0, 4);
        walkAnimation = new Animation<>(0.12f, walkFrames);

        // 2. วงสีแดง: แถวล่าง 3 ตัวแรก (index 8 ถึง 10 ในอาเรย์รวม เนื่องจากขึ้นแถวใหม่ที่ index 8) สำหรับพ่นไฟ
        TextureRegion[] fireFrames = new TextureRegion[3];
        System.arraycopy(allFrames, 8, fireFrames, 0, 3);
        fireAnimation = new Animation<>(0.15f, fireFrames);

        stateTime = 0f;
    }

    @Override
    public void handleInput(float delta) {
        super.handleInput(delta); // จัดการการเคลื่อนไหว WASD

        // นับถอยหลังเวลาพ่นไฟ
        if (fireTimer > 0) {
            fireTimer -= delta;
            isBreathing = true;
        } else {
            isBreathing = false;
        }

        // เช็คการหันซ้าย-ขวา
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            facingRight = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            facingRight = true;
        }

        // คลิกซ้ายเพื่อพ่นไฟ (ถ้าหมดเวลาคูลดาวน์ 2 วินาทีแล้วถึงจะกดพ่นใหม่ได้)
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (fireTimer <= 0) {
                fireTimer = FIRE_DURATION; // ตั้งเวลาพ่นค้างไว้ 2 วินาที
                attack();
            }
        }
    }

    @Override
    public void attack() {
        System.out.println("🔥 Dragon breathes fire! (Cooldown 2s)");
    }

    @Override
    public void draw(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();

        TextureRegion currentFrame;
        if (isBreathing) {
            currentFrame = fireAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        }

        // พลิกภาพซ้าย-ขวาตามทิศทางที่ตัวละครหัน
        if (!facingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (facingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }


        float drawWidth = 128 ;
        float drawHeight = 128 ;
        batch.draw(currentFrame, x, y, drawWidth, drawHeight);
    }
}
