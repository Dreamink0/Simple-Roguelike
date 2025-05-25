package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Character.Player.Player;

public interface EnemyStateHandler {
    void update(float deltaTime, SpriteBatch batch);
    void patrol(float deltaTime);
    void chase(float deltaTime);
    void attack(float deltaTime);
    void die();
    float calculateDistance(Player player);
}
