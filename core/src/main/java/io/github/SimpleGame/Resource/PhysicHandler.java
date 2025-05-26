package io.github.SimpleGame.Resource;

import com.badlogic.gdx.physics.box2d.World;

public interface PhysicHandler {
    void update(World world,float deltaTime);
}
