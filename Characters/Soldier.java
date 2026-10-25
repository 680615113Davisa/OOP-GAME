package Characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Soldier demonstrates the four OOP principles:
 * inheritance from Player, abstraction through Player's contract,
 * polymorphism by overriding Player methods, and encapsulation through
 * private state and behavior.
 */
public class Soldier extends Player {
    private static final float CHARACTER_SIZE = 100f;
    private static final float ATTACK_RANGE = 110f;
    private static final int DAMAGE_INCREASE_PER_NOTE = 5;
    private static final int NOTE_DROP_CHANCE_PERCENT = 20;

    // Encapsulation: implementation details and mutable state stay private.
    private final Texture[] directionSheets = new Texture[4];
    private final Texture postItTexture;
    private final List<Postit> damageNotes = new ArrayList<>();
    private int attackDamage = 10;
    private final Animation<TextureRegion> backAnimation;
    private final Animation<TextureRegion> frontAnimation;
    private final Animation<TextureRegion> leftAnimation;
    private final Animation<TextureRegion> rightAnimation;

    private Animation<TextureRegion> currentAnimation;
    private float animationTime = 0f;
    private boolean attackRequested = false;

    public Soldier(float x, float y) {
        // Inheritance: reuse Player's health, movement, and shared character state.
        // Character needs a base texture, but draw() uses the Soldier sprite sheets below.
        super(x, y, 200, 200f, "Dragon.png");
        this.name = "Soldier";
        postItTexture = new Texture("postit.png");

        frontAnimation = loadAnimation("Front.png", 0);
        leftAnimation = loadAnimation("Left.png", 1);
        backAnimation = loadAnimation("Back.png", 2);
        rightAnimation = loadAnimation("Right.png", 3);
        currentAnimation = frontAnimation;
    }

    // Encapsulation: sprite loading is an internal implementation detail.
    private Animation<TextureRegion> loadAnimation(String fileName, int textureIndex) {
        Texture sheet = loadTextureFromZip("Soldier.zip", fileName);
        directionSheets[textureIndex] = sheet;

        int frameWidth = sheet.getWidth() / 4;
        TextureRegion[] frames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            frames[i] = new TextureRegion(sheet, i * frameWidth, 0, frameWidth, sheet.getHeight());
        }
        return new Animation<>(0.12f, frames);
    }

    private Texture loadTextureFromZip(String zipPath, String targetFileName) {
        try (ZipInputStream zip = new ZipInputStream(Gdx.files.internal(zipPath).read())) {
            ZipEntry entry;
            byte[] buffer = new byte[4096];

            while ((entry = zip.getNextEntry()) != null) {
                String fileName = entry.getName();
                int slash = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
                fileName = fileName.substring(slash + 1).replace(" ", "");

                if (!fileName.equalsIgnoreCase(targetFileName.replace(" ", ""))) continue;

                ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();
                int bytesRead;
                while ((bytesRead = zip.read(buffer)) != -1) {
                    imageBytes.write(buffer, 0, bytesRead);
                }

                byte[] data = imageBytes.toByteArray();
                Pixmap pixmap = new Pixmap(data, 0, data.length);
                try {
                    return new Texture(pixmap);
                } finally {
                    pixmap.dispose();
                }
            }
        } catch (IOException e) {
            throw new GdxRuntimeException("Could not load " + targetFileName + " from " + zipPath, e);
        }

        throw new GdxRuntimeException("Missing " + targetFileName + " in " + zipPath);
    }

    // Polymorphism: Soldier supplies its own behavior for the Player input contract.
    @Override
    public void handleInput(float delta) {
        float dx = 0f;
        float dy = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += 1f;

        if (dx != 0f || dy != 0f) {
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            dx /= length;
            dy /= length;
            move(dx, dy, delta);

            Animation<TextureRegion> nextAnimation;
            if (Math.abs(dx) > Math.abs(dy)) {
                nextAnimation = dx < 0f ? leftAnimation : rightAnimation;
            } else {
                nextAnimation = dy > 0f ? backAnimation : frontAnimation;
            }

            if (nextAnimation != currentAnimation) {
                currentAnimation = nextAnimation;
                animationTime = 0f;
            }
            animationTime += delta;
        } else {
            animationTime = 0f;
        }

        float maxX = Math.max(0f, mapWidth - CHARACTER_SIZE);
        float maxY = Math.max(0f, mapHeight - CHARACTER_SIZE);
        this.x = Math.max(0f, Math.min(this.x, maxX));
        this.y = Math.max(0f, Math.min(this.y, maxY));

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            attack();
        }

        collectDamageNotes();
    }

    // Abstraction and polymorphism: implement Player's abstract attack operation.
    @Override
    public void attack() {
        attackRequested = true;
    }

    /** Called by GameScreen once per click. */
    public boolean consumeAttackRequest() {
        if (!attackRequested) return false;
        attackRequested = false;
        return true;
    }

    /** Damages one nearby monster. */
    public boolean attack(Monster monster) {
        if (monster == null || !monster.isAlive()) return false;

        float dx = (monster.getX() + 32f) - getCenterX();
        float dy = (monster.getY() + 32f) - getCenterY();
        if (dx * dx + dy * dy > ATTACK_RANGE * ATTACK_RANGE) return false;

        monster.takeDamage(attackDamage);
        System.out.println("Soldier dealt " + attackDamage + " damage!");
        if (!monster.isAlive() && Math.random() * 100 < NOTE_DROP_CHANCE_PERCENT) {
            damageNotes.add(new Postit(monster.getX(), monster.getY()));
            System.out.println("A damage post-it dropped!");
        }
        return true;
    }

    private void collectDamageNotes() {
        Iterator<Postit> iterator = damageNotes.iterator();
        while (iterator.hasNext()) {
            Postit note = iterator.next();
            float dx = note.x + 16f - getCenterX();
            float dy = note.y + 16f - getCenterY();
            if (dx * dx + dy * dy < 40f * 40f) {
                attackDamage += DAMAGE_INCREASE_PER_NOTE;
                iterator.remove();
                System.out.println("Damage increased to " + attackDamage + "!");
            }
        }
    }

    // Polymorphism: GameScreen can call these through the Player type.
    @Override
    public float getCenterX() {
        return this.x + CHARACTER_SIZE / 2f;
    }

    @Override
    public float getCenterY() {
        return this.y + CHARACTER_SIZE / 2f;
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion frame = currentAnimation.getKeyFrame(animationTime, true);
        batch.draw(frame, x, y, CHARACTER_SIZE, CHARACTER_SIZE);
        for (Postit note : damageNotes) {
            batch.draw(postItTexture, note.x, note.y, 32f, 32f);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Texture sheet : directionSheets) {
            if (sheet != null) sheet.dispose();
        }
        postItTexture.dispose();
    }

    // Encapsulated pickup data; callers cannot change a dropped note's position.
    private static final class Postit {
        private final float x;
        private final float y;

        private Postit(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
