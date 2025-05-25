package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;

public interface EnemyAnimationHandler {
    void load();
    void dispose();
}
