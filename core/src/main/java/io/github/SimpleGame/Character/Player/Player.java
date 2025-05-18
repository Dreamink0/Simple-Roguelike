package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Resource.ResourceManager;

public class Player {
    //身体//
    protected PlayerController playerController;
    protected Body playerBody;
    protected Sprite playerSprite;
    //接口//
    private PlayerAnimationHandler actionHandler;
    private PlayerFilpHandler flipChecker;
    //状态//
    protected boolean isequipped = false;
    //属性
    public Texture HPtexture;
    public Texture  MPtexture;
    public Texture   DEFtexture;
    public float HP;
    public float MP;
    public float DEF;
    private float Damage;
    public final float MP_RECOVERY_RATE = 5f; //每秒恢复量
    public static final float RECOVERY_DELAY = 1.0f;//恢复延迟
    public long recoveryStartTime = 0;//恢复开始时间
    public Player() {}
    public Player(World world, float WORLD_WIDTH, float WORLD_HEIGHT) {
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
        this.actionHandler = new PlayerAniamtion();
        this.flipChecker = new PlayerPlayerFilpCheck();
        //初始化玩家属性
        this.HP=100f;this.MP=50f;this.DEF=20f;
    }
    //玩家动画更新//
    public PlayerController setAction(PlayerController playerController, Player player, World world) {
        return actionHandler.handleAction(playerController, this, world);
    }
    //玩家是否翻转//
    public Sprite filpCheck(Sprite sprite,PlayerController playerController,SpriteBatch batch) {
        return flipChecker.checkFlip(sprite,playerController,batch);
    }
    /////常用的getter和setter////
    public PlayerController getPlayerController() {return playerController;}

    public Body getBody() {return playerBody;}

    public Sprite getPlayerSprite() {return playerSprite;}

    public float getX() {return playerBody.getPosition().x;}

    public float getY() {return playerBody.getPosition().y;}

    public boolean isIsequipped() {return isequipped;}

    public void setIsequipped(boolean isequipped) {this.isequipped = isequipped;}

    public void render(SpriteBatch batch) {
        // 静态资源加载优化：避免在 render 中重复创建 AssetManager
        if (HPtexture == null || MPtexture == null || DEFtexture == null) {
            AssetManager assetManager = new AssetManager();
            for (int i = 1; i <= 5; i++) {
                assetManager.load("UI/HP/HP" + i + ".png", Texture.class);
                assetManager.load("UI/MP/MP" + i + ".png", Texture.class);
                assetManager.load("UI/DEF/DEF" + i + ".png", Texture.class);
            }
            assetManager.finishLoading();
            this.HPtexture = assetManager.get("UI/HP/HP5.png", Texture.class);
            this.MPtexture = assetManager.get("UI/MP/MP5.png", Texture.class);
            this.DEFtexture = assetManager.get("UI/DEF/DEF5.png", Texture.class);
                if(HP<=75&&HP>=50){
                    this.HPtexture = assetManager.get("UI/HP/HP4.png", Texture.class);
                }
                if(HP<=50&&HP>=25){
                    this.HPtexture = assetManager.get("UI/HP/HP3.png", Texture.class);
                }
                if(HP<=25){
                    this.HPtexture = assetManager.get("UI/HP/HP2.png", Texture.class);
                }
                if(HP<=0){
                    this.HPtexture = assetManager.get("UI/HP/HP1.png", Texture.class);
                }
                if(MP<50&&MP>=40){
                    MPtexture = assetManager.get("UI/MP/MP4.png", Texture.class);
                }
                if(MP<30&&MP>=20){
                    MPtexture = assetManager.get("UI/MP/MP3.png", Texture.class);
                }
                if(MP<=20&&MP>=10){
                    MPtexture = assetManager.get("UI/MP/MP2.png", Texture.class);
                }
                if(MP<=10){
                    MPtexture = assetManager.get("UI/MP/MP1.png", Texture.class);
                }
                if(DEF<20&&DEF>=10){
                    DEFtexture = assetManager.get("UI/DEF/DEF4.png", Texture.class);
                }
                if(DEF<=10&&DEF>=0){
                    DEFtexture = assetManager.get("UI/DEF/DEF3.png", Texture.class);
                }
                if(DEF==0){
                    DEFtexture = assetManager.get("UI/DEF/DEF1.png", Texture.class);
                }
        }
        float uiScale = 0.1f; // 缩放因子，可以根据需要调整
        // 使用固定屏幕坐标绘制UI，避免摄像机缩放影响
        batch.draw(HPtexture, Config.WORLD_WIDTH/2-10f, Config.WORLD_HEIGHT/2+6.5f, HPtexture.getWidth() * uiScale , HPtexture.getHeight() * uiScale);
        batch.draw(MPtexture, Config.WORLD_WIDTH/2-10f, Config.WORLD_HEIGHT/2+6.1f, MPtexture.getWidth() * uiScale , MPtexture.getHeight() * uiScale);
        batch.draw(DEFtexture, Config.WORLD_WIDTH/2-10f, Config.WORLD_HEIGHT/2+5.7f, DEFtexture.getWidth()*uiScale/2,DEFtexture.getHeight() * uiScale/2);
    }
        public void dispose() {
        if (playerBody != null) {
            playerBody.getWorld().destroyBody(playerBody);
            playerBody = null;
        }
        if(HPtexture!=null){
            HPtexture.dispose();
        }
        if (MPtexture!=null){
            MPtexture.dispose();
        }
        if (DEFtexture!=null){
            DEFtexture.dispose();
        }
    }

    public float getHealth(){
        return HP;
    }

    public float getMana(){
        return MP;
    }

    public float getDefense(){
        return DEF;
    }

    public void setMP(float MP) {
        this.MP = MP;
    }

    public void setDEF(float DEF) {
        this.DEF = DEF;
    }

    public void setHP(float HP) {
        this.HP = HP;
    }

    public void setDamage(float damage) {
        Damage = damage;
    }
}
