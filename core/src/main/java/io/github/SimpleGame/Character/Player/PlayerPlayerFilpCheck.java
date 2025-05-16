package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Resource.ResourceManager;

public class PlayerPlayerFilpCheck extends Player implements PlayerFilpHandler {
    @Override
    public Sprite checkFlip(Sprite sprite, PlayerController playerController, SpriteBatch batch) {
        if(playerSprite==null){
            playerSprite= ResourceManager.getInstance().getPlayerSprite();
        }
        boolean isFlipped =playerController.isFlipped();
        playerSprite.setPosition(
            playerController.getPosition().x - playerSprite.getWidth() / 2,
            playerController.getPosition().y - playerSprite.getHeight() / 2
        );
        playerSprite.setFlip(isFlipped, false);
        return playerSprite;
    }
}
