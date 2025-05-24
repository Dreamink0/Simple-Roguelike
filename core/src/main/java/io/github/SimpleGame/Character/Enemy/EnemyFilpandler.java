package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface EnemyFilpandler {
    Sprite checkFlip(Sprite sprite, Enemy enemy, SpriteBatch batch);
}
