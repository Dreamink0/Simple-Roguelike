package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.physics.box2d.*;

import static io.github.SimpleGame.Resource.Game.world;

public class EnemyPhysic implements EnemyPhysicHandler{
    private float x, y,  originalWidth, originalHeight;
    public Body enemyBody;
    public EnemyPhysic(float x, float y, float originalWidth, float originalHeight){
        this.x = x; this.y = y;
        this.originalWidth = originalWidth; this.originalHeight = originalHeight;
    }
    @Override
    public Body createBody(Body enemyBody) {
        this.enemyBody = enemyBody;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;

        enemyBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(originalWidth, originalHeight);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1;
        fixtureDef.friction = 0;
        fixtureDef.restitution = 0f;
        enemyBody.createFixture(fixtureDef);
        shape.dispose();

        enemyBody.setUserData("enemy");
        this.enemyBody = enemyBody;
        return enemyBody;
    }

    @Override
    public void setCollisionBoxSize(float width, float height) {
        if (enemyBody == null) return;

        for (Fixture fixture : enemyBody.getFixtureList()) {
            enemyBody.destroyFixture(fixture);
        }

        PolygonShape newShape = new PolygonShape();
        newShape.setAsBox(width, height);

        FixtureDef newFixtureDef = new FixtureDef();
        newFixtureDef.shape = newShape;
        newFixtureDef.density = 1;
        newFixtureDef.friction = 0;
        newFixtureDef.restitution = 0f;

        enemyBody.createFixture(newFixtureDef);
        newShape.dispose();
    }

    public float getOriginalWidth() {
        return originalWidth;
    }

    public float getOriginalHeight() {
        return originalHeight;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Body getEnemyBody() {
        return enemyBody;
    }

    public void dispose() {
        if (enemyBody != null) {
            enemyBody.destroyFixture(enemyBody.getFixtureList().get(0));
            world.destroyBody(enemyBody);
        }
    }
}
