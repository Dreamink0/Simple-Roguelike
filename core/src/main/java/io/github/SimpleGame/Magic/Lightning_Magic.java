package io.github.SimpleGame.Magic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Resource.ResourceManager;

import static io.github.SimpleGame.Config.PIXELS_PER_METER;

public class Lightning_Magic extends Magic{
    private TextureAtlas Lighting_Magic_Atlas;
    private Animation<TextureRegion> Lightning_Magic_Animation;
    private Texture ICON;
    private AssetManager assetManager;
    private String Lightning_Magic_PATH="Magic/Lightning/Lightning.atlas";
    private String Lightning_Magic_ICON_PATH="Magic/Lightning/ICON.png";
    private World world;
    private Rectangle boundingBox;
    private Rectangle ICON_BoundingBox;
    private Body body;
    private Body ICON_Body;
    private Body attachedBody;
    private boolean isAttached = false;
    private  float offsetX;
    private  float offsetY;
    private float speedX = 15f; // 水平速度
    private float speedY = 3f; // 初始垂直速度
    private float gravity = 0f; // 重力加速度
    private float startX, startY;
    private float stateTime=0f;
    private TextureRegion currentFrame; // 当前帧
    private Boolean flag=false;//标记是否被拾取
    float x;
    float y;
    private float magicDuration = 1.0f; // 魔法持续时间（秒）
    private float magicStartTime = 0f;  // 魔法开始释放的时间
    //默认构造方法设置动画，传入参数类型：xxx.atlas;
    public Lightning_Magic(){
        //读取
        assetManager = new AssetManager();
        assetManager.load(Lightning_Magic_ICON_PATH, Texture.class);
        assetManager.load(Lightning_Magic_PATH,TextureAtlas.class);
        assetManager.finishLoading();
        //赋值
        this.ICON = assetManager.get(Lightning_Magic_ICON_PATH,Texture.class);
        this.Lighting_Magic_Atlas=assetManager.get(Lightning_Magic_PATH,TextureAtlas.class);
        this.Lightning_Magic_Animation = new Animation<>(0.05f,Lighting_Magic_Atlas.findRegions("Lightning"));
        this.currentFrame = Lightning_Magic_Animation.getKeyFrame(0f);
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
            batch.draw(ICON, x, y, ICON.getWidth() / 16, ICON.getHeight() / 16);
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
        }
    }
    //该方法实现使用魔法的渲染
    @Override
    public void Magic_render(SpriteBatch batch,Player player) {
        if (flag) {
            float time=Gdx.graphics.getDeltaTime();
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
            // 绘制魔法效果（翻转贴图）
            float scaleX = isFlipped ? -1f : 1f;
            batch.draw(
                currentFrame,
                currentX,
                currentY,
                scaleX * currentFrame.getRegionWidth() * 0.2f, // 水平翻转
                currentFrame.getRegionHeight() * 0.2f
            );
            if (currentY < 0 || currentX > Gdx.graphics.getWidth()) {
                flag = false;
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
            bodyDef.position.set(player.getBody().getPosition().x + offsetX/PIXELS_PER_METER,
                player.getBody().getPosition().y + offsetY/PIXELS_PER_METER);

            this.attachedBody = world.createBody(bodyDef);
            this.attachedBody.setUserData("weapon");

            PolygonShape shape = new PolygonShape();
            float scaledWidth = ICON.getWidth()/ PIXELS_PER_METER/2;
            float scaledHeight = ICON.getHeight()/ PIXELS_PER_METER/2;

            shape.setAsBox(scaledWidth/2, scaledHeight/2);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = true;
            this.attachedBody.createFixture(fixtureDef).setUserData("weapon");
            shape.dispose();

            isAttached = true;
        }
    }
}
