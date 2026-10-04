package Characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

public class Grim extends Player {

    // --- ตั้งค่าออร่าดาเมจวงแหวน ---
    private float damageRadius = 160f; // รัศมีวงแหวน (ปรับขยายให้พอดีกับรูปกะโหลกได้)
    private int auraDamage = 10;       // ดาเมจที่ทำได้ต่อรอบ (ไม่ต้องแรงมากเพราะโดนเรื่อยๆ)
    private float damageTickRate = 1.0f; // ทำดาเมจทุกๆ 1 วินาที (ศัตรูจะได้มีเวลาเดินถึงตัวเรา)
    private float damageTimer = 0f;

    // --- ตัวแปรสำหรับ Animation ---
    private Animation<TextureRegion> idleAnimation;
    private float stateTime = 0f;

    public Grim(float x, float y) {
        super(x, y, 150, 120f, "Grim_soul-sheet.png");

        // 1. แก้ไขเป็น 7 เฟรม ให้ตรงกับจำนวนภาพใน Grim_soul-sheet.png
        int columns = 7;

        int frameWidth = this.texture.getWidth() / columns;
        int frameHeight = this.texture.getHeight();

        // 2. หั่นภาพ (Split)
        TextureRegion[][] tmp = TextureRegion.split(this.texture, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[columns];
        for (int i = 0; i < columns; i++) {
            frames[i] = tmp[0][i];
        }

        // 3. สร้าง Animation (0.15f คือความเร็วในการสลับภาพ)
        idleAnimation = new Animation<>(0.15f, frames);
    }

    @Override
    public float getCenterX() {
        float frameWidth = this.texture.getWidth() / 7f;
        return this.x + (frameWidth / 2f);
    }

    @Override
    public float getCenterY() {
        float frameHeight = this.texture.getHeight();
        return this.y + (frameHeight / 2f);
    }

    @Override
    public void update(float delta, ArrayList<Monster> activeMonsters) {
        handleInput(delta, activeMonsters);                // 1. ให้รับคำสั่งเดินจากคีย์บอร์ด
        updateAura(activeMonsters, delta); // 2. ให้วงแหวนเช็กการทำดาเมจศัตรู
    }

    // ลอจิกทำดาเมจแบบ "ออร่า" (เข้ามาในวงโดนดาเมจ แต่เดินทะลุได้)
    private void updateAura(ArrayList<Monster> activeMonsters, float delta) {
        damageTimer += delta;

        // เมื่อเวลาผ่านไปครบกำหนด (เช่น ทุกๆ 1 วินาที)
        if (damageTimer >= damageTickRate) {
            // วนลูปเช็กมอนสเตอร์ทุกตัวในฉาก
            for (Monster m : activeMonsters) {
                // ถ้ามอนสเตอร์ตัวนั้นเข้ามาอยู่ในวงแหวน
                if (isEnemyInRadius(m)) {
                    m.takeDamage(auraDamage); // โดนดาเมจออร่า
                    System.out.println("Grim's Aura hits " + m.name + " for " + auraDamage + " damage!");
                }
            }
            damageTimer = 0f; // รีเซ็ตเวลาเพื่อนับรอบดาเมจใหม่
        }
    }

    // ฟังก์ชันคำนวณระยะห่างว่าศัตรูอยู่ในวงหรือไม่
    private boolean isEnemyInRadius(Monster m) {
        // หาจุดกึ่งกลางของตัว Grim (อิงจากขนาดเฟรม 1 ช่อง)
        float frameWidth = this.texture.getWidth() / 7f;
        float frameHeight = this.texture.getHeight();
        float grimCenterX = this.getX() + (frameWidth / 2f);
        float grimCenterY = this.getY() + (frameHeight / 2f);

        // หาจุดกึ่งกลางของมอนสเตอร์ (สมมติมอนสเตอร์กว้าง 64x64)
        float monsterCenterX = m.getX() + 32;
        float monsterCenterY = m.getY() + 32;

        float dx = monsterCenterX - grimCenterX;
        float dy = monsterCenterY - grimCenterY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // คืนค่า True ถ้ามอนสเตอร์เดินเข้ามาใกล้กว่ารัศมีวงแหวนกะโหลก
        return distance <= damageRadius;
    }

    @Override
    public void attack() {
        // ว่างไว้ หรือใส่การโจมตีแบบคลิกเมาส์เพิ่มเติมได้
    }

    @Override
    public void draw(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();

        // ดึงภาพแอนิเมชันมาเล่นวนซ้ำ
        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime, true);

        // วาดภาพ Grim ขนาดเต็ม 1 เฟรม (พร้อมวงแหวนที่ติดมากับรูป)
        batch.draw(currentFrame, x, y, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
    }
}
