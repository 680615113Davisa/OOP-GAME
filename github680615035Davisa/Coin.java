package io.github680615035Davisa;

public class Coin extends Item {
    public Coin(float x, float y) {
        super(x, y, "coin.png");
    }

    @Override
    public void applyEffect(Player player) {
        player.addCoin(1);
        GameSave.addCoins(1);
        this.isCollected = true;
        System.out.println("Coin collected! Wallet: " + GameSave.getCoins());
    }
}
