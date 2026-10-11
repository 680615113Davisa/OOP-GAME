package io.github680615035Davisa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public final class GameSave {
    private static final String PREFS_NAME = "oop-game-save";
    private static final String KEY_COINS = "walletCoins";
    private static final String KEY_DRAGON_UNLOCKED = "dragonUnlocked";
    private static final String KEY_GRIM_UNLOCKED = "grimUnlocked";

    public static final int DRAGON_COST = 10_000;
    public static final int GRIM_COST = 20_000;

    private GameSave() {
    }

    private static Preferences prefs() {
        return Gdx.app.getPreferences(PREFS_NAME);
    }

    public static int getCoins() {
        return Math.max(0, prefs().getInteger(KEY_COINS, 0));
    }

    public static void addCoins(int amount) {
        if (amount <= 0) return;
        Preferences prefs = prefs();
        long next = (long) prefs.getInteger(KEY_COINS, 0) + amount;
        int safeValue = (int) Math.min(Integer.MAX_VALUE, next);
        prefs.putInteger(KEY_COINS, safeValue);
        prefs.flush();
    }

    public static boolean spendCoins(int amount) {
        if (amount < 0) return false;
        Preferences prefs = prefs();
        int current = Math.max(0, prefs.getInteger(KEY_COINS, 0));
        if (current < amount) return false;
        prefs.putInteger(KEY_COINS, current - amount);
        prefs.flush();
        return true;
    }

    public static boolean isDragonUnlocked() {
        return prefs().getBoolean(KEY_DRAGON_UNLOCKED, false);
    }

    public static boolean isGrimUnlocked() {
        return prefs().getBoolean(KEY_GRIM_UNLOCKED, false);
    }

    public static boolean buyDragon() {
        if (isDragonUnlocked()) return true;
        if (!spendCoins(DRAGON_COST)) return false;
        Preferences prefs = prefs();
        prefs.putBoolean(KEY_DRAGON_UNLOCKED, true);
        prefs.flush();
        return true;
    }

    public static boolean buyGrim() {
        if (isGrimUnlocked()) return true;
        if (!spendCoins(GRIM_COST)) return false;
        Preferences prefs = prefs();
        prefs.putBoolean(KEY_GRIM_UNLOCKED, true);
        prefs.flush();
        return true;
    }
}
