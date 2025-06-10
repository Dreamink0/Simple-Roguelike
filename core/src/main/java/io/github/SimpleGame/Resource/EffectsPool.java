package io.github.SimpleGame.Resource;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;

public class EffectsPool extends Pool<Body> {
    private float  x, y;
    private float originalW, originalH;
    private float W,H;
    private float scaleX, scaleY;
    private World world;
    public EffectsPool(int cap){
        super(cap);
    }
    public void init(World world, float originalW, float originalH,float scaleX, float scaleY) {
        this.world = world;
        this.originalW = originalW;
        this.originalH = originalH;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    @Override
    protected Body newObject() {
        return createBody(originalW, originalH, scaleX, scaleY); // 使用默认参数创建
    }
    public Body newObj(float x, float y, float Oriwidth, float Oriheight, float scaleX, float scaleY) {
        Body body = createBody(Oriwidth, Oriheight, scaleX, scaleY);
        body.setTransform(x, y, 0); // 立即设置位置
        return body;
    }
    private Body createBody(float Oriwidth, float Oriheight, float scaleX, float scaleY) {
        float width = Oriwidth * scaleX;
        float height = Oriheight * scaleY;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(0, 0); // 初始位置由调用方设置
        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor=true;
        fixtureDef.density = 0;
        fixtureDef.friction = 1;
        fixtureDef.restitution = 1;
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }
}
