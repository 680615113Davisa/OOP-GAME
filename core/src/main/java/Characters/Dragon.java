package Characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

public class Dragon extends Player {
    private Texture spriteSheet;
    private Animation<TextureRegion> walkAnimation; // แอนิเมชันเดิน (เฟรม 1-4)
    private Animation<TextureRegion> fireAnimation; // แอนิเมชันพ่นไฟ (เฟรม 5-16)
    private float stateTime;

    private boolean isBreathing = false;
    private boolean facingRight = true;

    public Dragon(float x, float y) {
        super(x, y, 150, 180f, "Dragon.png");

        spriteSheet = new Texture("Dragon.png");

        // * สำคัญ: ปรับขนาด 64, 64 ให้ตรงกับขนาดพิกเซลจริงของแต่ละช่องในรูป Dragon.png ของคุณ *
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

        // 1. ตัดแบ่งช่วงที่ 1: เฟรมที่ 1 ถึง 4 (index 0 ถึง 3) สำหรับเดินปกติ
        TextureRegion[] walkFrames = new TextureRegion[4];
        System.arraycopy(allFrames, 0, walkFrames, 0, 4);
        walkAnimation = new Animation<>(0.12f, walkFrames);

        // 2. ตัดแบ่งช่วงที่ 2: เฟรมที่ 5 ถึง 16 (index 4 ถึง 15) สำหรับพ่นไฟ
        TextureRegion[] fireFrames = new TextureRegion[7];
        System.arraycopy(allFrames, 4, fireFrames, 0, 7);
        fireAnimation = new Animation<>(0.08f, fireFrames); // เล่นไวขึ้นนิดนึงตอนพ่นไฟ

        stateTime = 0f;
    }

    @Override
    public void handleInput(float delta, java.util.ArrayList<Characters.Monster> activeMonsters) {
        // ต้องส่ง activeMonsters ต่อไปให้คลาสแม่ด้วย
        super.handleInput(delta, activeMonsters); // จัดการการเคลื่อนไหวใหม่ WASD

        // เช็คการหันซ้าย - ขวา
        if (com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
            facingRight = false;
        }
        if (com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            facingRight = true;
        }

        // กดปุ่ม E ค้างไว้เพื่อเปลี่ยนเป็นท่าพ่นไฟ (เฟรม 5-16)
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            isBreathing = true;
            attack();
        } else {
            isBreathing = false;
        }
    }

    @Override
    public void attack() {
        System.out.println("🔥 Dragon breathes fire!");
    }

    @Override
    public void draw(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();

        TextureRegion currentFrame;
        if (isBreathing) {
            // ถ้ากด E ให้เล่นแอนิเมชันพ่นไฟ (เฟรม 5-16)
            currentFrame = fireAnimation.getKeyFrame(stateTime, true);
        } else {
            // ถ้าไม่ได้กด ให้เล่นแอนิเมชันเดินปกติ (เฟรม 1-4)
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        }

        // พลิกภาพซ้าย-ขวาตามทิศทางที่ตัวละครหัน
        if (!facingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (facingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        batch.draw(currentFrame, x, y);
    }

    @Override
    public void update(float delta, ArrayList<Monster> activeMonsters) {
        handleInput(delta, activeMonsters);
    }
}


