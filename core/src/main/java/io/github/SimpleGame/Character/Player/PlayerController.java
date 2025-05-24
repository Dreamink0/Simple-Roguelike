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
    public boolean isFlipped = false;

    private static final float MOVE_FORCE = 750f;
    private static float MAX_SPEED = 7f;
    private static final float ATTACK_DURATION = 1.5f; //攻击动画持续时间
    private float attackTimer = 0f;
    private boolean isAttacking = false;
    private boolean speedReducedDuringAttack = false;
    public PlayerController(Body body) {
        this.body = body;
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
            float currentMaxSpeed = isAttacking ? MAX_SPEED / 2 : MAX_SPEED; //攻击时速度减半
            if (velocity.len2() > currentMaxSpeed * currentMaxSpeed) {
                velocity.nor().scl(currentMaxSpeed);
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
        boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.D);

        if (rightPressed) {
            isFlipped = false;
        } else if (leftPressed) {
            isFlipped = true;
        }
        return isFlipped;
    }
    public boolean isMoving() {
        Vector2 velocity = body.getLinearVelocity();
        return velocity.len2() > 0.5f;
    }
    public boolean isAttacking() {
        if (isAttacking) {
            attackTimer -= Gdx.graphics.getDeltaTime();
            if (attackTimer <= 0) {
                isAttacking = false;
                MAX_SPEED = 7f; // 恢复原始速度
            }
        }
        return isAttacking;
    }
    public void startAttack() {
        if (!isAttacking) {
            isAttacking = true;
            attackTimer = ATTACK_DURATION;
            MAX_SPEED = MAX_SPEED / 2; // 将最大速度减半
        }
    }
    public Body getBody() {
        return body;
    }
}
