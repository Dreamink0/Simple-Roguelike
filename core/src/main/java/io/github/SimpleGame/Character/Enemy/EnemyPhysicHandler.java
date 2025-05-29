package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.physics.box2d.Body;

public interface EnemyPhysicHandler {
    Body createBody(Body enemyBody);
    void setCollisionBoxSize(float width, float height);
}
