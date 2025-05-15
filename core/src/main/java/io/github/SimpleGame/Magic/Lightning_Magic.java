package io.github.SimpleGame.Magic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;

import java.util.Random;

import static io.github.SimpleGame.Config.PIXELS_PER_METER;

public class Lightning_Magic extends Magic{
    //材质
    private TextureAtlas Lighting_Magic_Atlas;
    private TextureAtlas ThunderStrike_Atlas;
    private Animation<TextureRegion> Lightning_Magic_Animation;
    private Animation<TextureRegion> ThunderStrike_Animation;
    private Texture ICON;
    private AssetManager assetManager;
    private World world;
    //碰撞
    private Rectangle boundingBox;
    private Rectangle ICON_BoundingBox;
    private Body body;
    private Body ICON_Body;
    private Body attachedBody;
    private boolean isAttached = false;
    //物理
    private float speedX = 2f; // 水平速度
    private float speedY = 0f; // 初始垂直速度
    private float gravity = 0f; // 重力加速度
    private float startX, startY;
    private float stateTime=0f;
    private TextureRegion currentFrame; // 当前帧
    private Boolean flag=false;//标记是否被拾取
    float x;
    float y;
    private float magicDuration  =3.33f; // 魔法持续时间（秒）
    private float magicStartTime = 0f;
    private long magicStartTimeNano = 0;

    //默认构造方法设置动画，传入参数类型：xxx.atlas;
    public Lightning_Magic(){
        //读取
        assetManager = new AssetManager();
        assetManager.load(Config.LIGHTNING_MAGIC_ICON_PATH, Texture.class);
        assetManager.load(Config.LIGHTNING_MAGIC_PATH,TextureAtlas.class);
        assetManager.load(Config.THUNDER_STRIKE_PATH,TextureAtlas.class);
        assetManager.finishLoading();
        //赋值
        this.ICON = assetManager.get(Config.LIGHTNING_MAGIC_ICON_PATH,Texture.class);
        this.Lighting_Magic_Atlas=assetManager.get(Config.LIGHTNING_MAGIC_PATH,TextureAtlas.class);
        this.ThunderStrike_Atlas=assetManager.get(Config.THUNDER_STRIKE_PATH,TextureAtlas.class);
        this.Lightning_Magic_Animation = new Animation<>(0.1f,Lighting_Magic_Atlas.findRegions("Lightning"));
        this.ThunderStrike_Animation = new Animation<>(0.1f,ThunderStrike_Atlas.findRegions("Thunderstrike"));
        this.currentFrame = Lightning_Magic_Animation.getKeyFrame(0f);
        this.magicStartTime = Gdx.graphics.getDeltaTime();
    }
    //该方法实现魔法在世界中创建可以拾取的道具
    @Override
    public void Magic_create(World world,float x,float y) {
        this.world = world;
        this.x = x;
        this.y = y;
        //获得贴图长，宽准备创建碰撞
        float Width = ICON.getWidth()/4/PIXELS_PER_METER;
        float Height = ICON.getHeight()/4/PIXELS_PER_METER;
        //设置碰撞
        this.ICON_BoundingBox = new Rectangle(x-Width,y-Height,Width,Height);

        BodyDef BodyDef = new BodyDef();
        BodyDef.type = com.badlogic.gdx.physics.box2d.BodyDef.BodyType.KinematicBody;
        BodyDef.position.set(x+2*Width,y+2*Height);

        this.ICON_Body = world.createBody(BodyDef);
        this.ICON_Body.setUserData("Lightning");

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(2*Width,2*Height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        this.ICON_Body.createFixture(fixtureDef).setUserData("Lightning");

        shape.dispose();
    }
    //该方法实现魔法的拾取逻辑
    //调用的时候要传入来自主程序的世界和玩家
    @Override
    public void Magic_obtain(SpriteBatch batch, Player player){
        updatePos(player,player.getX(), player.getY());
        if (!flag){//如果未施法，显示在施法位置
            updatePos(player, player.getX(), player.getY());
            batch.draw(ICON, player.getX()+18,player.getY()-8, ICON.getWidth() / 16, ICON.getHeight() / 16);
        }else{//如果已施法，显示在物品栏位置
            batch.draw(ICON, player.getX()+18,player.getY()-8, ICON.getWidth() / 16, ICON.getHeight() / 16);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F)){
            attachToPlayer(player,player.getX(),player.getY());
            updatePos(player,player.getX(), player.getY());
            this.flag = true;
            this.startX = player.getX();
            this.startY = player.getY() - 1.7f;
            this.stateTime = 0f;
            this.magicStartTimeNano = System.nanoTime();
        }
    }
    //该方法实现使用魔法的渲染
    @Override
    public void Magic_render(SpriteBatch batch,Player player) {
        if (flag) {
            float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
            stateTime += deltaTime;
            this.currentFrame = Lightning_Magic_Animation.getKeyFrame(stateTime, true);
            // 获取玩家朝向（假设 isFlipped() 返回 true 表示朝左，false 表示朝右）
            boolean isFlipped = player.getPlayerController().isFlipped();
            // 根据朝向调整水平速度（speedX）
            float effectiveSpeedX = isFlipped ? -Math.abs(speedX) : Math.abs(speedX);
            // 计算当前位置（水平+垂直）
            float currentTime = stateTime;
            float currentX = startX + effectiveSpeedX * currentTime; // 水平移动（受朝向影响）
            float currentY = startY + speedY * currentTime + 0.5f * gravity * currentTime * currentTime;
            speedY += gravity * deltaTime;
            // 绘制魔法效果（翻转贴图）;
            float scaleX = isFlipped ? -1f : 1f;
                batch.draw(currentFrame, currentX, currentY,
                    scaleX * currentFrame.getRegionWidth() * 0.15f, // 水平翻转
                    currentFrame.getRegionHeight() * 0.15f
                );
            if (magicStartTimeNano > 1) { // 确保已记录开始时间
                long currentTimeNano = System.nanoTime();
                float elapsedTimeSeconds = (currentTimeNano - magicStartTimeNano) / 1_000_000_000f; // 转换为秒
                if (elapsedTimeSeconds >= magicDuration || currentY < 0 || currentX > Gdx.graphics.getWidth()) {
                    flag = false;
                    magicStartTimeNano = 0; // 重置开始时间
                }
            }
            currentX = startX + effectiveSpeedX * currentTime; // 水平移动（受朝向影响）
            currentY = startY + speedY * currentTime + 0.5f * gravity * currentTime * currentTime;
            this.currentFrame = ThunderStrike_Animation.getKeyFrame(stateTime, true);
            for(int i=0;i<=360;i+=120){
                batch.draw(currentFrame, currentX-0.1f, currentY- i%360-MathUtils.sin(i)*3,
                    scaleX * currentFrame.getRegionWidth() * 0.15f, // 水平翻转
                    currentFrame.getRegionHeight() * 0.15f
                );
            }
            if (magicStartTimeNano>0) { // 确保已记录开始时间
                long currentTimeNano = System.nanoTime();
                float elapsedTimeSeconds = (currentTimeNano - magicStartTimeNano) / 1_000_000_000f; // 转换为秒
                if (elapsedTimeSeconds >= magicDuration || currentY < 0 || currentX > Gdx.graphics.getWidth()) {
                    magicStartTimeNano = 0; // 重置开始时间
                }
            }
        }
    }
    //更新碰撞以及贴图的位置
    public void updatePos(Player player,float x, float y){
        if (isAttached && attachedBody != null && player.getPlayerController() != null) {
            //获取玩家位置并更新位置
            Body playerBody = player.getPlayerController().getBody();
            if (playerBody != null) {
                Vector2 playerPos = playerBody.getPosition();
                attachedBody.setTransform(
                    playerPos.x + 100/PIXELS_PER_METER,
                    playerPos.y + 100/PIXELS_PER_METER,
                    0
                );
            }
        }
    }
    //销毁原有的碰撞
    public void attachToPlayer(Player player, float offsetX, float offsetY) {
        if (!isAttached) {
            if (ICON_Body != null) {
                world.destroyBody(ICON_Body );
                ICON_Body  = null;
            }
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(
                player.getBody().getPosition().x /PIXELS_PER_METER,
                player.getBody().getPosition().y /PIXELS_PER_METER
            );

            this.attachedBody = world.createBody(bodyDef);
            this.attachedBody.setUserData("Lightning");

            PolygonShape shape = new PolygonShape();
            float scaledWidth = 2*ICON.getWidth()/ PIXELS_PER_METER;
            float scaledHeight = 2*ICON.getHeight()/ PIXELS_PER_METER;

            shape.setAsBox(scaledWidth, scaledHeight);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = true;
            this.attachedBody.createFixture(fixtureDef).setUserData("Lightning");
            shape.dispose();

            isAttached = true;
        }
    }
}
