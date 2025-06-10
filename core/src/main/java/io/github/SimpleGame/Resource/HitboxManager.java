package io.github.SimpleGame.Resource;

import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Tool.AnimationTool;

public class HitboxManager {
    private World world;
    private EffectsPool effectsPool;
    public Body create(World world, AnimationTool animationTool, float x, float y,float scaleX, float scaleY){
        this.world = world;
        float W = animationTool.getTextureRegion().getRegionWidth()*scaleX;
        float H = animationTool.getTextureRegion().getRegionHeight()*scaleY;
        effectsPool = new EffectsPool(150);
        effectsPool.init(world,W,H,scaleX,scaleY);
        return effectsPool.obtain();
    }
    public void free(Body body){
        effectsPool.free(body);
    }
    public void destroy(Body body){
        if(body!=null){
            world.destroyBody(body);
            effectsPool.free(body);
        }
    }
    public void update(float x, float y,Body body) {
        if(body!=null){
            body.setTransform(x,y,0);
        }
    }
}
