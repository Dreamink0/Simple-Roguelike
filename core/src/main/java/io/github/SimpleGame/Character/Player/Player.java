package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Attribute.CharacterAttributes;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Resource.ResourceManager;
import io.github.SimpleGame.Resource.WorldManager;

public class Player {
    private PlayerController playerController;
    private Body playerBody;
    private CharacterAttributes characterAttributes;
    private Animation<TextureRegion> playerIdleAnimation;
    private Animation<TextureRegion> playerRunAnimation;
    private Animation<TextureRegion> playerAttackAnimation;
    private Sprite playerSprite;
    float stateTime=0f;
    private float accumulator = 0f;
    private Animation<TextureRegion> currentAnimation;

    public Player(World world, float WORLD_WIDTH, float WORLD_HEIGHT) {
        this.characterAttributes=new CharacterAttributes(20,50,10);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
        bodyDef.fixedRotation = true;
        this.playerBody = world.createBody(bodyDef);
        this.playerBody.setUserData("player");//用于碰撞检测
        playerController = new PlayerController(this.playerBody);

        PolygonShape BoundingBox = new PolygonShape();
        BoundingBox.setAsBox(0.8f, 1.8f);
        FixtureDef fixtureDef = new FixtureDef();//定义碰撞属性
        fixtureDef.shape = BoundingBox;
        fixtureDef.density = 1.0f;//密度
        fixtureDef.friction = 0.5f;//摩擦力
        fixtureDef.restitution = 0.5f;//弹性

        playerBody.createFixture(fixtureDef);
        BoundingBox.dispose();
    }
    public PlayerController getPlayerController() {
        return playerController;
    }
    public Body getBody() {
        return playerBody;
    }
    public void dispose() {
        if (playerBody != null) {
            playerBody.getWorld().destroyBody(playerBody);
            playerBody = null;
        }
    }
    public CharacterAttributes getCharacterAttributes() {
        return characterAttributes;
    }
    public void getAnimation(ResourceManager resourceManager) {
        this.playerIdleAnimation = resourceManager.getPlayerIdleAnimation();
        this.playerRunAnimation = resourceManager.getPlayerRunAnimation();
        this.playerAttackAnimation = resourceManager.getPlayerAttackAnimation();
    }
    public void getSprite(ResourceManager resourceManager) {
        this.playerSprite = resourceManager.getPlayerSprite();
    }

    public Sprite getPlayerSprite() {
        return playerSprite;
    }

    public Animation<TextureRegion> getPlayerIdleAnimation() {return playerIdleAnimation;}

    public Animation<TextureRegion> getPlayerRunAnimation() {return playerRunAnimation;}

    public Animation<TextureRegion> getPlayerAttackAnimation() {return playerAttackAnimation;}

    public PlayerController ActionCheck(PlayerController playerController, Player player, World world) {
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        stateTime += deltaTime;
        // 根据玩家移动状态选择动画
        //ActionCheek(playerController,play)
        boolean isAttacking = playerController.isAttacking();
        boolean isMoving = playerController.isMoving();
        Animation<TextureRegion> newAnimation;
        if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            playerController.startAttack();
        }
        if (isAttacking) {
            newAnimation = playerAttackAnimation;
        }else{
            newAnimation = isMoving ? playerRunAnimation : playerIdleAnimation;
        }
        if (newAnimation != currentAnimation) {
            stateTime = 0;
            currentAnimation = newAnimation;
        }
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        playerSprite.setRegion(currentFrame);
        if (isAttacking) {
            TextureRegion attackFrame = player.getPlayerAttackAnimation().getKeyFrame(stateTime, true);
            if (attackFrame != null) {
                playerSprite.setRegion(attackFrame); // 使用攻击动画帧
            }
        }
        accumulator += deltaTime;
        while (accumulator >= Config.TIME_STEP) {
            world.step(Config.TIME_STEP, 6, 2);
            accumulator -= Config.TIME_STEP;
        }
        return playerController;
    }

    public Sprite FilpCheck(Sprite sprite,PlayerController playerController,SpriteBatch batch) {
        boolean isFlipped =playerController.isFlipped();
        playerSprite.setPosition(
            playerController.getPosition().x - playerSprite.getWidth() / 2,
            playerController.getPosition().y - playerSprite.getHeight() / 2
        );
        playerSprite.setFlip(isFlipped, false);
        return playerSprite;
    }
}
