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
import io.github.SimpleGame.Main;
import io.github.SimpleGame.Tool.Listener;

import static com.badlogic.gdx.math.MathUtils.cos;
import static io.github.SimpleGame.Config.*;

public class Lightning_Magic extends Magic {
    // 资源管理
    private AssetManager assetManager;
    protected TextureAtlas lightningAtlas;
    protected TextureAtlas thunderAtlas;
    protected Animation<TextureRegion> lightningAnimation;
    protected Animation<TextureRegion> thunderAnimation;
    protected Texture iconTexture;

    // 物理系统
    protected World world;
    protected Rectangle iconBoundingBox;
    protected Body iconBody;
    protected Body attachedBody;
    protected boolean isAttached = false;
    protected Body lightningBody;
    protected PolygonShape shape;

    // 状态管理
    protected float stateTime = 0f;
    protected float magicDuration = 5.95f;
    protected long magicStartTimeNano = 0;
    protected boolean active = false;
    public boolean flag=false;

    // 运动参数
    protected float speedX = 2f;
    protected float speedY = 0f;
    protected float gravity = 0f;
    protected float startX, startY;
    protected float currentX, currentY;
    protected float x, y;
    private static final float COOLDOWN_DURATION = 10f;
    private long lastUsedTime = 0;
    private Player player;
    // 构造函数
    public Lightning_Magic() {
        assetManager = new AssetManager();
        loadAssets();
        initAnimations();
        resetMagicState();
    }

    // 资源加载
    private void loadAssets() {
        assetManager.load(Config.LIGHTNING_MAGIC_ICON_PATH, Texture.class);
        assetManager.load(LIGHTNING_MAGIC_ICON2_PATH,Texture.class);
        assetManager.load(Config.LIGHTNING_MAGIC_PATH, TextureAtlas.class);
        assetManager.load(Config.THUNDER_STRIKE_PATH, TextureAtlas.class);
        assetManager.finishLoading();

        iconTexture = assetManager.get(Config.LIGHTNING_MAGIC_ICON2_PATH, Texture.class);
        lightningAtlas = assetManager.get(Config.LIGHTNING_MAGIC_PATH, TextureAtlas.class);
        thunderAtlas = assetManager.get(Config.THUNDER_STRIKE_PATH, TextureAtlas.class);
    }

    // 初始化动画
    private void initAnimations() {
        setTextureFilter(lightningAtlas);
        setTextureFilter(thunderAtlas);

        lightningAnimation = new Animation<>(0.1f, lightningAtlas.findRegions("Lightning"));
        thunderAnimation = new Animation<>(0.1f, thunderAtlas.findRegions("Thunderstrike"));
    }

    // 设置纹理过滤
    private void setTextureFilter(TextureAtlas atlas) {
        for (Texture texture : atlas.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    // 创建魔法道具
    @Override
    public void magicCreate(World world, float x, float y) {
        this.world = world;this.x = x;this.y = y;
        float width = iconTexture.getWidth() / PIXELS_PER_METER;
        float height = iconTexture.getHeight() / PIXELS_PER_METER;
        this.iconBoundingBox = new Rectangle(x - width/2, y - height/2, width, height);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x + width, y + height);

        this.iconBody = world.createBody(bodyDef);
        this.iconBody.setUserData("Lightning");

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        this.iconBody.createFixture(fixtureDef).setUserData("Lightning");
        shape.dispose();
    }

    // 拾取魔法
    @Override
    public void magicObtain(SpriteBatch batch,SpriteBatch UIbatch,Player player) {
        this.player = player;
        updatePosition(player);
        if(Listener.LightningMagic_Flag
            &&(Gdx.input.isKeyJustPressed(Input.Keys.E))
            &&((Math.abs(player.getX()-x)<=2f)
            &&(Math.abs(player.getY()-y)<=2f))){
            if (iconBody != null) {
                world.destroyBody(iconBody);
                iconBody = null;
            }
            flag=true;
        }
        if(flag==true){
            UIbatch.begin();
            if (!active) {
                if (!isCooldownElapsed()) {
                    iconTexture = assetManager.get(Config.LIGHTNING_MAGIC_ICON_PATH, Texture.class);
                } else {
                    iconTexture = assetManager.get(Config.LIGHTNING_MAGIC_ICON2_PATH, Texture.class);
                }
                UIbatch.draw(iconTexture, WORLD_WIDTH/2f+8f, WORLD_WIDTH/2f-10f,
                    iconTexture.getWidth()*2/PIXELS_PER_METER, iconTexture.getHeight()*2/PIXELS_PER_METER);
            } else {
                if (!isCooldownElapsed()) {
                    iconTexture = assetManager.get(Config.LIGHTNING_MAGIC_ICON_PATH, Texture.class);
                }
                UIbatch.draw(iconTexture, WORLD_WIDTH/2+8f, WORLD_WIDTH/2f-10f,
                    iconTexture.getWidth()*2/PIXELS_PER_METER, iconTexture.getHeight()*2/PIXELS_PER_METER);
            }
            UIbatch.end();
            if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                if (active || !isCooldownElapsed()) {
                    return; // 已激活或冷却未结束时不执行
                }
                player.getAttributeHandler().setMP(player.getAttributeHandler().getMaxMP() - 25f);
                attachToPlayer(player);
                active = true;
                startX = player.getX();
                startY = player.getY() - 1.7f;
                stateTime = 0f;
                magicStartTimeNano = System.nanoTime();
                lastUsedTime = System.nanoTime(); //记录魔法使用时间
                boolean isFlipped = player.getPlayerController().isFlipped();
                speedX = isFlipped ? -2f : 2f;
            }
        }else{
            batch.begin();
            batch.draw(iconTexture, x, y,
                iconTexture.getWidth()*4/PIXELS_PER_METER, iconTexture.getHeight()*4/PIXELS_PER_METER);
            batch.end();
        }
    }

    // 更新位置
    public void updatePosition(Player player) {
        if (isAttached && attachedBody != null && player.getPlayerController() != null) {
            Body playerBody = player.getPlayerController().getBody();
            if (playerBody != null) {
                Vector2 playerPos = playerBody.getPosition();
                attachedBody.setTransform(
                    playerPos.x + 100 / PIXELS_PER_METER,
                    playerPos.y + 100 / PIXELS_PER_METER,
                    0
                );
            }
        }
    }

    // 附着到玩家
    public void attachToPlayer(Player player) {
        if (!isAttached) {
            if (iconBody != null) {
                world.destroyBody(iconBody);
                iconBody = null;
            }

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(
                player.getBody().getPosition().x / PIXELS_PER_METER,
                player.getBody().getPosition().y / PIXELS_PER_METER
            );

            this.attachedBody = world.createBody(bodyDef);
            this.attachedBody.setUserData("Lightning");
            isAttached = true;
        }
    }

    // 渲染魔法效果
    @Override
    public void magicRender(SpriteBatch batch, Player player) {
        if (!active) return;
        updateMagicState(Gdx.graphics.getDeltaTime());
        if (lightningBody != null) {
            // 根据朝向设置固定角度
            boolean isFlipped = player.getPlayerController().isFlipped();
            float angle = isFlipped ? MathUtils.degreesToRadians * 120 : MathUtils.degreesToRadians * 60;
            lightningBody.setTransform(currentX + 2, currentY + 2, angle);
        }
        // 创建或更新碰撞体位置
        if (lightningBody == null && getElapsedTime() < magicDuration) {
            // 创建新的碰撞体
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(currentX, currentY);

            lightningBody = world.createBody(bodyDef);
            lightningBody.setUserData("lightning_Magic");

            shape = new PolygonShape();
            shape.setAsBox(5f, 5f);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = true;
            lightningBody.createFixture(fixtureDef).setUserData("lightning_Magic");
        } else if (lightningBody != null) {
            // 更新现有碰撞体位置
            lightningBody.setTransform(currentX+2, currentY+2, 60f);
        }

        renderLightningEffect(batch);
        renderThunderEffects(batch);
        checkDeactivation();
    }

    // 更新魔法状态
    private void updateMagicState(float deltaTime) {
        stateTime += Math.min(deltaTime, 0.25f);

        if (magicStartTimeNano == 0) {
            magicStartTimeNano = System.nanoTime();
        }

        // 计算运动轨迹
        float elapsedTime = getElapsedTime();
        currentX = startX + speedX * elapsedTime;
        currentY = startY + speedY * elapsedTime + 0.5f * gravity * elapsedTime * elapsedTime;
        speedY += gravity * deltaTime;

        // 优化碰撞体清理逻辑
        if (elapsedTime >= 0.1f && lightningBody != null &&
            (elapsedTime >= magicDuration || currentY < 0 || currentX > Gdx.graphics.getWidth())) {
            world.destroyBody(lightningBody);
            lightningBody = null;
            shape.dispose();
        }
    }

    // 获取经过时间
    private float getElapsedTime() {
        return (System.nanoTime() - magicStartTimeNano) / 1_000_000_000f;
    }

    // 渲染闪电效果
    private void renderLightningEffect(SpriteBatch batch) {
        TextureRegion frame = lightningAnimation.getKeyFrame(stateTime, false);
      //使用固定偏移模式替代随机抖动，创建更稳定的视觉效果
        for (int i = 0; i <= 360; i += 60) {
            float angle = i;
            // 产生平滑动画效果
            float offsetX = cos(angle + stateTime * 5)*(1+stateTime/2);
            float offsetY = MathUtils.sin(angle + stateTime * 5)*(1+stateTime/2);
            float colorOffset = 7+MathUtils.sin(stateTime * 7 + i) * 0.1f;
            // 保存当前颜色
            float[] originalColor = new float[4];
            originalColor[0] = batch.getColor().r;
            originalColor[1] = batch.getColor().g;
            originalColor[2] = batch.getColor().b;
            originalColor[3] = batch.getColor().a;
            batch.setColor(1 + colorOffset, 0.8f + colorOffset, 0.2f + colorOffset, 1);
            batch.draw(frame, currentX + offsetX, currentY + offsetY,
                frame.getRegionWidth() * 0.1f,
                frame.getRegionHeight() * 0.1f);
            batch.setColor(originalColor[0], originalColor[1], originalColor[2], originalColor[3]);
        }
    }

    // 渲染雷电特效
    private void renderThunderEffects(SpriteBatch batch) {
        TextureRegion frame = thunderAnimation.getKeyFrame(stateTime, true);
        boolean isFlipped = player.getPlayerController().isFlipped();
        // 自动镜像纹理区域
        if (isFlipped != frame.isFlipX()) {
            frame.flip(true, false);
        }
        // 创建同心圆扩散效果，提供更连贯的运动感
        for (int i = 0; i <= 360; i += 45) {
            float angle = i;
            float radius = 5 + 2 * MathUtils.sin(stateTime * 5);
            float offsetX = cos(angle + stateTime * 2)*(1+stateTime/2);
            float offsetY = MathUtils.sin(angle + stateTime * 2)*(1+stateTime/2);
            // 添加轻微的颜色变化效果
            float colorOffset = stateTime;
            float[] originalColor = new float[4];
            originalColor[0] = batch.getColor().r;
            originalColor[1] = batch.getColor().g;
            originalColor[2] = batch.getColor().b;
            originalColor[3] = batch.getColor().a;
            // 应用临时颜色进行绘制
            batch.setColor(1 + colorOffset, 0.8f + colorOffset, 0.2f + colorOffset, 1);

            batch.draw(frame, currentX + offsetX - 0.1f, currentY + offsetY,
                frame.getRegionWidth() * 0.1f,
                frame.getRegionHeight() * 0.1f);

            // 恢复原始颜色
            batch.setColor(originalColor[0], originalColor[1], originalColor[2], originalColor[3]);
        }
    }
    // 检查去激活条件
    private void checkDeactivation() {
        if (getElapsedTime() >= magicDuration || currentY < 0 || currentX > Gdx.graphics.getWidth()) {
            resetMagicState();
        }
    }

    // 重置魔法状态
    private void resetMagicState() {
        active = false;
        stateTime = 0f;
        magicStartTimeNano = 0;
        speedY = 0f;
    }
    private boolean isCooldownElapsed() {
        if (lastUsedTime == 0) {
            return true; //初始状态无冷却限制
        }
        long currentTime = System.nanoTime();
        long cooldownNanos = (long) (COOLDOWN_DURATION * 1_000_000_000L);
        return (currentTime - lastUsedTime) >= cooldownNanos;
    }
    public void dispose() {
        lightningAtlas.dispose();
        thunderAtlas.dispose();
        iconTexture.dispose();
        assetManager.dispose();
    }
}
