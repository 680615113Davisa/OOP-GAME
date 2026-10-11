package com.game.oop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Soldier extends Player {
    private static final int CHARACTER_SIZE = 100;
    private static final float PICKUP_SIZE = 32f;
    private static final float PICKUP_DISTANCE = 48f;
    private static final float ATTACK_RANGE = 100f;
    private static final int BASE_ATTACK_DAMAGE = 10;
    private static final int POST_IT_DAMAGE_BONUS = 5;

    private final Texture[] directionSheets = new Texture[4];
    private final Texture[] coinFrames = new Texture[5];
    private final Animation<TextureRegion> backAnimation;
    private final Animation<TextureRegion> frontAnimation;
    private final Animation<TextureRegion> leftAnimation;
    private final Animation<TextureRegion> rightAnimation;
    private final Animation<TextureRegion> coinAnimation;
    private final Texture potionTexture;
    private final TextureRegion potionFrame;
    private final Texture postItTexture;
    private final TextureRegion postItFrame;
    private final List<Pickup> pickups = new ArrayList<>();
    private Animation<TextureRegion> currentAnimation;
    private float animationTime;
    private float pickupAnimationTime;
    private float pickupSpawnTime;
    private boolean initialPickupsSpawned;
    private int nextPickupType;
    private int bonusDamage;

    public Soldier() {
        this(200f, 200f);
    }

    public Soldier(float x, float y) {
        super(x, y, 100, 200f, "Dragon.png");
        this.name = "Soldier";
        backAnimation = loadAnimation("Back", 0);
        frontAnimation = loadAnimation("Front", 1);
        leftAnimation = loadAnimation("Left", 2);
        rightAnimation = loadAnimation("Right", 3);
        currentAnimation = frontAnimation;

        coinAnimation = loadCoinAnimation();
        potionTexture = loadTextureFromZip("potion_draft1.zip", "potion_draft1.png");
        potionFrame = new TextureRegion(potionTexture);
        postItTexture = new Texture("postit.png");
        postItFrame = new TextureRegion(postItTexture);
    }

    private Animation<TextureRegion> loadAnimation(String direction, int textureOffset) {
        Texture sheet = loadTextureFromZip("Soldier.zip", direction + ".png");
        directionSheets[textureOffset] = sheet;

        int frameWidth = sheet.getWidth() / 4;
        if (frameWidth == 0) {
            throw new GdxRuntimeException("Invalid Soldier sprite sheet: " + direction);
        }

        TextureRegion[] frames = new TextureRegion[4];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = new TextureRegion(sheet, i * frameWidth, 0,
                frameWidth, sheet.getHeight());
        }
        return new Animation<>(0.12f, frames);
    }

    private Animation<TextureRegion> loadCoinAnimation() {
        TextureRegion[] frames = new TextureRegion[coinFrames.length];
        for (int i = 0; i < frames.length; i++) {
            coinFrames[i] = loadTextureFromZip("Coin (2).zip", "Coin" + (i + 1) + ".png");
            frames[i] = new TextureRegion(coinFrames[i]);
        }
        return new Animation<>(0.1f, frames);
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
            throw new GdxRuntimeException("Could not load " + targetFileName
                + " from " + zipPath, e);
        }

        throw new GdxRuntimeException("Missing " + targetFileName + " in " + zipPath);
    }

    public void update(float dx, float dy, float delta, float screenWidth, float screenHeight) {
        pickupAnimationTime += delta;
        boolean moving = dx != 0f || dy != 0f;
        if (moving) {
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            move(dx / length, dy / length, delta);

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

        setX(clamp(getX(), 0f, Math.max(0f, screenWidth - CHARACTER_SIZE)));
        setY(clamp(getY(), 0f, Math.max(0f, screenHeight - CHARACTER_SIZE)));

        updatePickups(delta, screenWidth, screenHeight);
    }

    private void updatePickups(float delta, float screenWidth, float screenHeight) {
        if (!initialPickupsSpawned) {
            addPickup(getX() + CHARACTER_SIZE + 24f, getY() + 24f, PickupType.COIN, screenWidth, screenHeight);
            addPickup(getX() - PICKUP_SIZE - 24f, getY() + CHARACTER_SIZE - PICKUP_SIZE,
                PickupType.POTION, screenWidth, screenHeight);
            addPickup(getX() + 30f, getY() + CHARACTER_SIZE + 24f,
                PickupType.POST_IT, screenWidth, screenHeight);
            initialPickupsSpawned = true;
        }

        pickupSpawnTime += delta;
        if (pickupSpawnTime >= 5f) {
            pickupSpawnTime = 0f;
            float pickupX = MathUtils.random(0f, Math.max(0f, screenWidth - PICKUP_SIZE));
            float pickupY = MathUtils.random(0f, Math.max(0f, screenHeight - PICKUP_SIZE));
            PickupType type = PickupType.values()[nextPickupType];
            addPickup(pickupX, pickupY, type, screenWidth, screenHeight);
            nextPickupType = (nextPickupType + 1) % PickupType.values().length;
        }

        for (int i = pickups.size() - 1; i >= 0; i--) {
            Pickup pickup = pickups.get(i);
            if (isTouching(pickup)) {
                collect(pickup);
                pickups.remove(i);
            }
        }
    }

    private void addPickup(float pickupX, float pickupY, PickupType type,
                           float screenWidth, float screenHeight) {
        float maxX = Math.max(0f, screenWidth - PICKUP_SIZE);
        float maxY = Math.max(0f, screenHeight - PICKUP_SIZE);
        pickups.add(new Pickup(clamp(pickupX, 0f, maxX), clamp(pickupY, 0f, maxY), type));
    }

    private boolean isTouching(Pickup pickup) {
        float dx = getCenterX() - (pickup.x + PICKUP_SIZE / 2f);
        float dy = getCenterY() - (pickup.y + PICKUP_SIZE / 2f);
        return dx * dx + dy * dy <= PICKUP_DISTANCE * PICKUP_DISTANCE;
    }

    private void collect(Pickup pickup) {
        pickup.type.collect(this);
    }

    public int getAttackDamage() {
        return BASE_ATTACK_DAMAGE + bonusDamage;
    }

    public boolean attack(Monster monster) {
        if (monster == null || !monster.isAlive()) return false;

        float soldierCenterX = getCenterX();
        float soldierCenterY = getCenterY();
        float monsterCenterX = monster.getX() + 32f;
        float monsterCenterY = monster.getY() + 32f;
        float dx = monsterCenterX - soldierCenterX;
        float dy = monsterCenterY - soldierCenterY;
        if (dx * dx + dy * dy > ATTACK_RANGE * ATTACK_RANGE) return false;

        monster.takeDamage(getAttackDamage());
        System.out.println("Soldier dealt " + getAttackDamage() + " damage!");
        return true;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    @Override
    public void handleInput(float delta) {
        float dx = 0f;
        float dy = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += 1f;

        update(dx, dy, delta, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) attack();
    }

    @Override
    public float getCenterX() {
        return getX() + CHARACTER_SIZE / 2f;
    }

    @Override
    public float getCenterY() {
        return getY() + CHARACTER_SIZE / 2f;
    }

    @Override
    public void draw(SpriteBatch batch) {
        drawPickups(batch);
        drawCharacter(batch);
    }

    private void drawPickups(SpriteBatch batch) {
        for (Pickup pickup : pickups) {
            TextureRegion itemFrame;
            switch (pickup.type) {
                case POTION:
                    itemFrame = potionFrame;
                    break;
                case POST_IT:
                    itemFrame = postItFrame;
                    break;
                default:
                    itemFrame = coinAnimation.getKeyFrame(pickupAnimationTime, true);
                    break;
            }
            batch.draw(itemFrame, pickup.x, pickup.y, PICKUP_SIZE, PICKUP_SIZE);
        }
    }

    private void drawCharacter(SpriteBatch batch) {
        TextureRegion frame = currentAnimation.getKeyFrame(animationTime, true);
        batch.draw(frame, getX(), getY(), CHARACTER_SIZE, CHARACTER_SIZE);
    }

    @Override
    public void attack() {
    }

    @Override
    public void takeDamage(int damage) {
        if (damage <= 0) return;

        super.takeDamage(damage);
        health = Math.max(0, health - damage);
    }

    @Override
    public void heal(int amount) {
        if (amount <= 0) return;

        int previousHealth = health;
        super.heal(amount);
        int restoredHealth = health - previousHealth;
        if (restoredHealth > 0) {
            super.takeDamage(-restoredHealth);
        }
    }

    public void dispose() {
        super.dispose();
        for (Texture sheet : directionSheets) {
            if (sheet != null) sheet.dispose();
        }
        for (Texture coinFrame : coinFrames) {
            if (coinFrame != null) coinFrame.dispose();
        }
        potionTexture.dispose();
        postItTexture.dispose();
    }

    private enum PickupType {
        COIN {
            @Override
            void collect(Soldier soldier) {
                soldier.addCoin(1);
                System.out.println("Coin collected!");
            }
        },
        POTION {
            @Override
            void collect(Soldier soldier) {
                soldier.heal(20);
                System.out.println("Health potion collected!");
            }
        },
        POST_IT {
            @Override
            void collect(Soldier soldier) {
                soldier.bonusDamage += POST_IT_DAMAGE_BONUS;
                System.out.println("Post-it collected! Attack damage: " + soldier.getAttackDamage());
            }
        };

        abstract void collect(Soldier soldier);
    }

    private static class Pickup {
        private final float x;
        private final float y;
        private final PickupType type;

        private Pickup(float x, float y, PickupType type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }
    }
}
