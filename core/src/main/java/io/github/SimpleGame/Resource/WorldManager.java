package io.github.SimpleGame.Resource;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Item.WeaponEffects;

public class WorldManager{
    private World world;
    private Box2DDebugRenderer debugRenderer;
    public WorldManager() {
        Box2D.init();
        this.world = new World(new Vector2(0, 0), true);
        this.debugRenderer = new Box2DDebugRenderer();
    }
    public World getWorld() {return world;}
    public Box2DDebugRenderer getDebugRenderer() {return debugRenderer;}
}
