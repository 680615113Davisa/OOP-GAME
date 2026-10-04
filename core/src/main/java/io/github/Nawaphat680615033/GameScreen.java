package io.github.Nawaphat680615033;

import Characters.Monster;
import Characters.Player;
import Characters.Scorpion;
import Item.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera; // อย่าลืม Import กล้อง
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private final Main game;
    private Player player;
    private List<Item> worldItems;
    private List<Monster> monsters;
    private float spawnTimer = 0f;
    private float spawnInterval = 1.5f;

    private Texture background;

    // 1. ประกาศตัวแปรรองรับกล้อง
    private OrthographicCamera camera;

    public GameScreen(Main game, Player selectedPlayer) {
        this.game = game;
        this.player = selectedPlayer;
        this.worldItems = new ArrayList<>();
        this.monsters = new ArrayList<>();
        this.background = new Texture("backgroundoop.png");

        // เพิ่มบรรทัดนี้ลงไป เพื่อดึงขนาดกว้าง/ยาวของไฟล์รูปภาพไปตั้งค่าเป็นขอบเขตแมพ
        this.player.setMapBounds(this.background.getWidth(), this.background.getHeight());

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void spawnMonster() {
        // ... (โค้ดสุ่มจุดเกิดมอนสเตอร์เหมือนเดิม ไม่ต้องแก้) ...
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float spawnX = 0, spawnY = 0;

        int edge = (int)(Math.random() * 4);
        switch (edge) {
            case 0: spawnX = (float)(Math.random() * screenWidth); spawnY = screenHeight + 50; break;
            case 1: spawnX = (float)(Math.random() * screenWidth); spawnY = -50; break;
            case 2: spawnX = -50; spawnY = (float)(Math.random() * screenHeight); break;
            case 3: spawnX = screenWidth + 50; spawnY = (float)(Math.random() * screenHeight); break;
        }

        // เนื่องจากกล้องขยับ เราอาจจะอยากให้มันสปอว์นอิงจากตำแหน่งผู้เล่นแทนขอบจอ (ถ้าอยากให้สปอว์นรอบตัวผู้เล่นจริงๆ สามารถเปลี่ยน spawnX, Y ให้อิงจาก player.getX(), getY() ได้ในอนาคตครับ)
        monsters.add(new Scorpion(spawnX, spawnY));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // --- อัปเดตลอจิกต่างๆ ---
        player.handleInput(delta, (java.util.ArrayList<Characters.Monster>) monsters);

        spawnTimer += delta;
        if (spawnTimer >= spawnInterval) {
            spawnMonster();
            spawnTimer = 0f;
        }

        // อัปเดตตำแหน่งกล้องให้ตามตัวละครก่อน (บวก 32 คือให้อยู่กึ่งกลางตัว)
        camera.position.x = player.getCenterX();
        camera.position.y = player.getCenterY();

        // --- เพิ่มโค้ดล็อคกล้อง (Camera Clamping) ตรงนี้ ---
        // คำนวณระยะห่างจากกึ่งกลางจอไปยังขอบกล้อง
        float halfCameraWidth = camera.viewportWidth / 2f;
        float halfCameraHeight = camera.viewportHeight / 2f;

        // ดึงขนาดของแผนที่จากรูปภาพ
        float mapWidth = background.getWidth();
        float mapHeight = background.getHeight();

        // ล็อคกล้องแกน X (ซ้าย-ขวา)
        if (camera.position.x < halfCameraWidth) {
            camera.position.x = halfCameraWidth; // ชนขอบซ้าย
        } else if (camera.position.x > mapWidth - halfCameraWidth) {
            camera.position.x = mapWidth - halfCameraWidth; // ชนขอบขวา
        }

        // ล็อคกล้องแกน Y (ล่าง-บน)
        if (camera.position.y < halfCameraHeight) {
            camera.position.y = halfCameraHeight; // ชนขอบล่าง
        } else if (camera.position.y > mapHeight - halfCameraHeight) {
            camera.position.y = mapHeight - halfCameraHeight; // ชนขอบบน
        }
        // ---------------------------------------------

        camera.update(); // สั่งอัปเดตกล้องหลังจากคำนวณตำแหน่งเสร็จแล้ว

        // เซ็ตให้วาดกราฟิกอิงจากมุมมองกล้อง
        game.batch.setProjectionMatrix(camera.combined);

        // --- วาดกราฟิกลงจอภาพ ---
        game.batch.begin();

        // วาดภาพพื้นหลัง (ถ้ากล้องเดินไปไกลกว่าขอบภาพ ภาพจะแหว่ง ต้องใช้รูปฉากที่ใหญ่มากๆ หรือเขียนโค้ดต่อภาพฉากครับ)
        game.batch.draw(background, 0, 0, background.getWidth(), background.getHeight());

        player.draw(game.batch);

        player.update(game.batch, delta, monsters);

        for (int i = monsters.size() - 1; i >= 0; i--) {
            Monster m = monsters.get(i);
            m.moveTowards(player.getX(), player.getY(), delta);
            m.updatePoison(delta, worldItems);
            m.draw(game.batch);

            float distToPlayer = (float) Math.sqrt(Math.pow(player.getX() - m.getX(), 2) + Math.pow(player.getY() - m.getY(), 2));
            if (distToPlayer < 40f) {
                player.takeDamage(1);
            }

            if (!m.isAlive()) {
                monsters.remove(i);
            }
        }

        for (int i = worldItems.size() - 1; i >= 0; i--) {
            Item item = worldItems.get(i);
            item.draw(game.batch);

            float dist = (float) Math.sqrt(Math.pow(player.getX() - item.getX(), 2) + Math.pow(player.getY() - item.getY(), 2));
            if (dist < 30f && !item.isCollected()) {
                item.applyEffect(player);
            }

            if (item.isCollected()) {
                worldItems.remove(i);
            }
        }
        game.batch.end();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        background.dispose();
    }
}
