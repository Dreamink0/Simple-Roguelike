package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class PlayerController {
    private final Body body;
    public boolean isFlipped = false;//ly我加的

    private static final float MOVE_FORCE = 20f;
    private static final float MAX_SPEED = 8f;
    private static final float DAMPING = 0.08f; // 阻尼
    private static final float BOX_SIZE = 0.5f; // 碰撞大小

    public PlayerController(Body body) {
        this.body = body;
        setupBody();
    }

    private void setupBody() {
        body.setLinearDamping(DAMPING);
        body.setFixedRotation(true);
        body.setType(BodyDef.BodyType.DynamicBody);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(BOX_SIZE, BOX_SIZE);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.0f;

        body.createFixture(fixtureDef);

        shape.dispose();
    }

    public void update() {
        boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean downPressed = Gdx.input.isKeyPressed(Input.Keys.S);

        Vector2 moveDirection = new Vector2(0, 0);

        if (leftPressed) moveDirection.x = -1;
        if (rightPressed) moveDirection.x = 1;
        if (upPressed) moveDirection.y = 1;
        if (downPressed) moveDirection.y = -1;

        if (moveDirection.len2() > 0) {
            moveDirection.nor(); // 归一化

            Vector2 force = moveDirection.scl(MOVE_FORCE);
            body.applyForceToCenter(force, true);

            Vector2 velocity = body.getLinearVelocity();
            if (velocity.len2() > MAX_SPEED * MAX_SPEED) {
                velocity.nor().scl(MAX_SPEED);
                body.setLinearVelocity(velocity);
            }
        } else {
            Vector2 velocity = body.getLinearVelocity();
            velocity.scl(0.95f); // 缩放向量
            body.setLinearVelocity(velocity);
        }
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public boolean isFlipped() {
        Vector2 velocity = body.getLinearVelocity();
        if (velocity.x > 0) {
            isFlipped = true;
        } else if (velocity.x < 0) {
            isFlipped = false;
        }
        return isFlipped;
    }
}
