package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class Player {

    private PlayerController playerController;
    private Body playerBody;

    public Player(World world, float WORLD_WIDTH, float WORLD_HEIGHT) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
        bodyDef.fixedRotation = true;
        this.playerBody = world.createBody(bodyDef);

        playerController = new PlayerController(this.playerBody);
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public Body getBody() {
        return playerBody;
    }
}
