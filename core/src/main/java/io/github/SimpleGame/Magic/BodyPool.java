package io.github.SimpleGame.Magic;

import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Resource.Game;

public class BodyPool {
    private final Body[] bodies;
    private int count;
    /**
     * 构造BodyPool实例
     * @param capacity 对象池容量
     */
    public BodyPool(int capacity) {
        bodies = new Body[capacity];
        count = 0;
    }

    /**
     * 获取一个Body对象
     * @param x Body的x坐标
     * @param y Body的y坐标
     * @param width Body的宽度
     * @param height Body的高度
     * @return Body对象
     */
    public Body acquire(float x, float y, float width, float height) {
        if (count > 0) {
            Body body = bodies[--count]; // 从池中取出一个刚体

            // 重置刚体属性
            body.setTransform(x, y, 0);
            body.setLinearVelocity(0, 0);
            body.setAngularVelocity(0);
            body.setActive(true);

            // 重新设置形状和夹具
            Fixture fixture = body.getFixtureList().first();
            if (fixture != null) {
                PolygonShape shape = (PolygonShape) fixture.getShape();
                shape.setAsBox(width, height);
            }

            return body;
        }

        // 如果池中没有可用刚体，则创建新的刚体
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);

        Body body = Game.world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData("Magic");
        shape.dispose();

        return body;
    }

    /**
     * 释放Body对象回对象池
     * @param body 要释放的Body对象
     */
    public void release(Body body) {
        if (body == null) return;

        if (count < bodies.length) {
            // 重置刚体状态
            body.setTransform(0, 0, 0);
            body.setLinearVelocity(0, 0);
            body.setAngularVelocity(0);
            body.setActive(false); // 停用刚体

            // 移除旧的夹具并添加新的夹具（可选）
            Fixture fixture = body.getFixtureList().first();
            if (fixture != null) {
                body.destroyFixture(fixture);
            }

            // 添加一个新的默认形状（可选）
            PolygonShape defaultShape = new PolygonShape();
            defaultShape.setAsBox(1, 1);
            FixtureDef defaultFixtureDef = new FixtureDef();
            defaultFixtureDef.shape = defaultShape;
            defaultFixtureDef.isSensor = true;
            body.createFixture(defaultFixtureDef);
            defaultShape.dispose();
            bodies[count++] = body; // 将刚体放回池中
        } else {
            // 池已满，销毁刚体
            if (body.getWorld() != null && !body.getWorld().isLocked()) {
                // 移除所有夹具并销毁刚体
                Fixture fixture = body.getFixtureList().first();
                while (fixture != null) {
                    body.destroyFixture(fixture);
                    fixture = body.getFixtureList().first();
                }

                Game.world.destroyBody(body);
            }
        }
    }

    /**
     * 释放所有Body对象
     */
    public void releaseAll() {
        for (int i = 0; i < count; i++) {
            Body body = bodies[i];
            if (body.getWorld() != null && !body.getWorld().isLocked()) {
                // 移除所有夹具
                Fixture fixture = body.getFixtureList().first();
                while (fixture != null) {
                    body.destroyFixture(fixture);
                    fixture = body.getFixtureList().first();
                }

                Game.world.destroyBody(body);
            }
        }
        count = 0;
    }
}
