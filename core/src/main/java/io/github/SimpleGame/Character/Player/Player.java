package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Resource.Game;

public class Player {
    //身体//
    protected PlayerController playerController;
    protected Body playerBody;
    protected Sprite playerSprite;
    //接口//
    private PlayerAnimationHandler actionHandler;
    private PlayerFilpHandler flipChecker;
    private PlayerAttributeHandler attributeHandler;
    protected PlayerTextureHandler playerTextureHandler;
    //状态//
    protected static boolean isequipped = false;
    private World world;
    public float attackCooldownTimer = 0f;
    public float attackCooldown = 0.05f;//影响帧伤，很强大
    public int weaponID = -1;


    public Player() {}
    public Player(World world, float WORLD_WIDTH, float WORLD_HEIGHT) {
        this.world = world;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
        bodyDef.fixedRotation = true;
        this.playerBody = world.createBody(bodyDef);
        this.playerBody.setUserData("player");//用于碰撞检测
        playerController = new PlayerController(this.playerBody);

        PolygonShape BoundingBox = new PolygonShape();
        BoundingBox.setAsBox(1.2f, 1.8f);
        FixtureDef fixtureDef = new FixtureDef();//定义碰撞属性
        fixtureDef.shape = BoundingBox;
        fixtureDef.density = 1.0f;//密度
        fixtureDef.friction = 0.5f;//摩擦力
        fixtureDef.restitution = 0.5f;//弹性

        playerBody.createFixture(fixtureDef);
        BoundingBox.dispose();
        this.actionHandler = new PlayerAnimation();
        this.flipChecker = new PlayerPlayerFilpCheck();
        this.attributeHandler = new PlayerAttribute(20,50,5);
        this.playerTextureHandler = new PlayerTexture(attributeHandler);
        this.playerTextureHandler.load();
    }
    //玩家动画更新//
    public PlayerController setAction(PlayerController playerController, World world) {
        return actionHandler.handleAction(playerController, this, world);
    }
    //玩家是否翻转//
    public Sprite filpCheck(Sprite sprite,PlayerController playerController,SpriteBatch batch) {
        return flipChecker.checkFlip(sprite,playerController,batch);
    }
    //常用的getter和setter//
    public PlayerController getPlayerController() {return playerController;}

    public Body getBody() {return playerBody;}

    public Sprite getPlayerSprite() {return playerSprite;}

    public PlayerAttributeHandler getAttributeHandler() {return attributeHandler;}

    public PlayerTextureHandler getPlayerTextureHandler() {return playerTextureHandler;}

    public float getX() {return playerBody.getPosition().x;}

    public float getY() {return playerBody.getPosition().y;}

    public World getWorld() {
        return world;
    }

    public boolean isIsequipped() {return isequipped;}

    public void setIsequipped(boolean isequipped) {this.isequipped = isequipped;}

    public void render(SpriteBatch batch,float deltaTime) {
        attributeHandler.update(playerTextureHandler);
        playerTextureHandler.render(batch, deltaTime);
    }
    public boolean checkDeath() {
        if (attributeHandler.getHP() <= 0) {
            return true;
        }
        return false;
    }
    public void update() {
        if (checkDeath()) {
            // 触发游戏重启
            Game.getInstance().restartGame();  // 需要实现Game单例
        }
    }
    public PlayerAnimation getActionHandler() {
        return (PlayerAnimation) actionHandler;
    }
    public void dispose() {
        if (playerBody != null) {
            playerBody.getWorld().destroyBody(playerBody);
            playerBody = null;
        }
        if(playerTextureHandler != null){
            playerTextureHandler.dispose();
            playerTextureHandler = null;
        }
        if(playerController != null){
            playerController = null;
        }
        if(playerSprite!=  null){
            playerSprite.getTexture().dispose();
        }
        if(attributeHandler != null){
            attributeHandler = null;
        }
    }

}
