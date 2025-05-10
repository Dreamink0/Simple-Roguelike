package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public class Player {

    private PlayerController playerController;
    private Body playerBody;

    public Player(World world, float WORLD_WIDTH, float WORLD_HEIGHT) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
        bodyDef.fixedRotation = true;
        this.playerBody = world.createBody(bodyDef);
        this.playerBody.setUserData("player");//用于碰撞检测
        playerController = new PlayerController(this.playerBody);

        /*新加代码*/
        PolygonShape BoundingBox = new PolygonShape();
        BoundingBox.setAsBox(0.375f, 0.375f);
        FixtureDef fixtureDef = new FixtureDef();//定义碰撞属性
        fixtureDef.shape = BoundingBox;
        fixtureDef.density = 1.0f;//密度
        fixtureDef.friction = 0.5f;//摩擦力
        fixtureDef.restitution = 0.5f;//弹性
        playerBody.createFixture(fixtureDef);
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public Body getBody() {
        return playerBody;
    }
}
