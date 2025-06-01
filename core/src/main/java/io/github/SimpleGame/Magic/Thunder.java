package io.github.SimpleGame.Magic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Item.Tips;

import static com.badlogic.gdx.math.MathUtils.cos;

public class Thunder extends Magic implements EffectsRenderHandler{
    private Boolean isObtain=false;
    private Boolean isFlip=false;
    private float stateTime=0f;
    private Body EffectsBody;
    private float CooldownTimer=0f;
    private float Cooldown = 10f;
    private float StartX;
    private float StartY;
    private long time = 0;
    private boolean isActivating = false; // 新增状态标志

    public Thunder(World world, Player player, float x, float y) {
        super(world, player, x, y);
        this.x =x;
        this.y =y;
        Animations.loadAssets("LightningMagic");
        Hitboxes = new MagicHitbox(world,player,Animations);
        Hitboxes.createIconBody(x,y);
        Attributes = new MagicAttribute(5,2,10,6f);
    }

    @Override
    public void render(SpriteBatch batch, SpriteBatch UIbatch, Player player) {
        //未获得魔法时，渲染道具位置;
        if(distance(player)){
            flag=true;
            Hitboxes.destoryIconBody();
        }
        if(!flag){
            Animations.render(batch,x,y,flag,index);
        }else{
            isObtain = true;
            equippedMagic.add(this);
            index = equippedMagic.indexOf(this);
            Animations.render(UIbatch,x,y,flag,index);
        }

        //处理魔法释放逻辑
        if (isObtain && Gdx.input.isKeyJustPressed(Input.Keys.F)&&(player.getAttributeHandler().getMaxMP()-30f>=0)) {
            if (!Active && CooldownTimer <= 0) {
                Active = true;
                isActivating = true;
                StartX = player.getX();
                StartY = player.getY();
                isFlip = player.getPlayerController().isFlipped();
                CooldownTimer = Attributes.getCooldown();
                player.getAttributeHandler().setMP(player.getAttributeHandler().getMaxMP()-30f);
            }
        }

        if (Active) {
            Animations.isActive = true;

            //只在激活瞬间创建效果体
            if (isActivating) {
                time = System.currentTimeMillis();
                isActivating = false;
            }

            Effectsrender(batch, player, StartX, StartY, isFlip);

            long timeSinceActivation = (System.currentTimeMillis() - time);
            if(timeSinceActivation >= Attributes.getDuration()*1000){
                Active = false;
                time = 0;
                stateTime = 0f;
                StartX = 0f;
                StartY = 0f;
                if (this.EffectsBody != null) {
                    world.destroyBody(this.EffectsBody);
                    this.EffectsBody = null;
                }
            }
        }

        //如果技能处于非激活状态且冷却结束重置状态
        if (!Active && CooldownTimer <= 0&&(player.getAttributeHandler().getMaxMP()-30f>=0)){
            Animations.isActive = false;
        }

        //更新冷却计时器
        if(CooldownTimer > 0 && !Active) {
            CooldownTimer -= Gdx.graphics.getDeltaTime();
            if (CooldownTimer < 0) CooldownTimer = 0;
        }

        if(dst(player)&&!flag){
            Tips.E(batch,player.getX(),player.getY());
        }
    }

    @Override
    public void Effectsrender(SpriteBatch batch,Player player,float x, float y, Boolean flip) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += Math.min(deltaTime, 0.25f);
        int directionFlag = 1;
        if(flip){ directionFlag = -1; }

        float CurrentX = x + Attributes.getSpeed() * stateTime * directionFlag;
        float CurrentY = y;

        batch.begin();

        for (int i = 0; i <= 360; i += 35) {
            float offsetX = cos((float) i + stateTime * 2)*(1+stateTime/2);
            float offsetY = MathUtils.sin((float) i + stateTime * 2)*(1+stateTime/2);

            // 只在第一次循环时创建碰撞体
            if (i == 0 && (EffectsBody == null || !EffectsBody.getWorld().isLocked())) {
                createEffectBody(CurrentX, y);
            }

            Animations.getAnimations()[0].render(batch,CurrentX+offsetX-1,CurrentY+offsetY,0.1f,false,flip);

            if(Animations.getAnimations()[0].isAnimationFinished()){
                Animations.getAnimations()[1].render(batch,CurrentX+offsetX,CurrentY+offsetY,0.1f,false,flip);
            }
        }

        batch.end();
    }

    @Override
    public void createEffectBody(float x, float y) {
        // 清除上一帧的碰撞体
        if (this.EffectsBody != null && !this.EffectsBody.getWorld().isLocked()) {
            world.destroyBody(this.EffectsBody);
            this.EffectsBody = null;
        }

        if (this.EffectsBody == null) {
            float width = 5 * Animations.getIconTexture()[0].getWidth() / Config.PIXELS_PER_METER;
            float height = 5 * Animations.getIconTexture()[0].getHeight() / Config.PIXELS_PER_METER;

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(x, y);

            EffectsBody = world.createBody(bodyDef);
            EffectsBody.setUserData("Magic");

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width, height);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = true;

            EffectsBody.createFixture(fixtureDef).setUserData("Magic");
            shape.dispose();
        }
    }
}
