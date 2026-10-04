package Characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.nio.channels.FileLock;

public class Scorpion extends Monster {

    // ประกาศตัวแปรสำหรับทำ Animation
    private Animation<TextureRegion> walkAnimation;
    private float stateTime = 0f;

    public Scorpion(float x, float y) {
        super("Scorpion", x, y, 80, 200f, "Scorpion.png");

        // --- ลอจิกการทำหลายเฟรม (Animation) ---
        Texture spriteSheet = new Texture("Scorpion.png");

        // ⚠️ สำคัญ: เปลี่ยนเลข 4 ให้เป็น "จำนวนคอลัมน์" (จำนวนช่อง) ตามภาพแมงป่องของคุณจริงๆ
        int columns = 4;

        int frameWidth = spriteSheet.getWidth() / columns;
        int frameHeight = spriteSheet.getHeight(); // สมมติว่ามีแค่ 1 แถว

        // หั่นภาพตามความกว้างและสูง
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);

        // เอาภาพที่หั่นแล้วมาใส่ใน Array 1 มิติ
        TextureRegion[] walkFrames = new TextureRegion[columns];
        for (int i = 0; i < columns; i++) {
            walkFrames[i] = tmp[0][i];
        }

        // สร้าง Animation (0.1f คือความเร็วในการสลับภาพ ยิ่งน้อยยิ่งสลับไว)
        walkAnimation = new Animation<>(0.1f, walkFrames);
    }

    @Override
    public void update(float delta, Player player) {
        chase(player, delta);

        if (isNearPlayer(player)) {
            attack(player);
        }
    }

    private boolean isNearPlayer(Player player) {
        float dx = player.getX() - this.getX();
        float dy = player.getY() - this.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return this.getHitbox().overlaps(player.getHitbox());
    }

    private void chase(Player player, float delta) {
        moveTowards(player.getX(), player.getY(), delta);
    }

    public void attack(Player player) {
        System.out.println("Scorpion ต่อย! ผู้เล่นติดพิษ!");
        player.takeDamage(5);
    }

    // --- เขียนทับเมธอด draw เพื่อแสดงผลแบบแอนิเมชัน ---
    @Override
    public void draw(SpriteBatch batch) {
        // นับเวลาที่ผ่านไปเพื่อบอกว่าตอนนี้ควรแสดงเฟรมไหน
        stateTime += Gdx.graphics.getDeltaTime();

        // ดึงภาพเฟรมปัจจุบัน (true = ให้เล่นวนซ้ำไปเรื่อยๆ)
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);

        // วาดแมงป่องลงจอ (คุณสามารถแก้เลข 64, 64 เป็นขนาดพิกเซลที่ต้องการให้แสดงผลได้)
        batch.draw(currentFrame, x, y, 64, 64);
    }
    public Rectangle getHitbox() {
        return new Rectangle(this.x, this.y, 64, 64);
    }

}
