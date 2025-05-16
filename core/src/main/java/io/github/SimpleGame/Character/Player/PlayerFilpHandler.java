package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface PlayerFilpHandler {
    Sprite checkFlip(Sprite sprite, PlayerController playerController, SpriteBatch batch);
}
