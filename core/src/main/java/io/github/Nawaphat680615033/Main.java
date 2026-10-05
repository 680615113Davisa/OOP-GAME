package io.github.Nawaphat680615033;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// เปลี่ยนจาก ApplicationAdapter เป็น Game แทน
public class Main extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // เปิดเกมมา ให้ไปที่หน้าจอเริ่มเกมก่อน
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render(); // คำสั่งนี้จำเป็นมาก เพื่อให้ Screen ปัจจุบันทำงาน
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
