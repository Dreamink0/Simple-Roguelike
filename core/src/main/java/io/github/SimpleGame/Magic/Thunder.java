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
import io.github.SimpleGame.Resource.Game;
import io.github.SimpleGame.Tool.AnimationTool;

import static com.badlogic.gdx.math.MathUtils.cos;
import static io.github.SimpleGame.Resource.Game.world;

public class Thunder extends Magic{
    private static final int MAX_EFFECT_BODIES = 5; // 最大效果刚体数
    private static final BodyPool effectBodyPool; // 刚体对象池

    static {
        effectBodyPool = new BodyPool(MAX_EFFECT_BODIES); // 初始化对象池
    }
    public Thunder(World world, Player player, float x, float y) {
        super(world, player, x, y);
        this.x =x;
        this.y =y;
        Animations.loadAssets("LightningMagic");
        Hitboxes = new MagicHitbox(world,player,Animations);
        Hitboxes.createIconBody(x,y);
        Attributes = new MagicAttribute(3,2,10,5.5f,30);
        magicState = new MagicState(Attributes,Animations,Hitboxes);
        label = "Lightning";
    }

    @Override
    public void render(SpriteBatch batch, SpriteBatch UIbatch, Player player) {
        Body EffectsBody= Hitboxes.getEffectsBody();
        //获得魔法道具时的判定
        if(distance(player)){
            flag=true;
            Hitboxes.destroyIconBody();
        }
        if(!flag){
            Animations.render(batch,x,y,flag,index);
        }else{
            isObtain = true;
            equippedMagic.add(this);
            index = equippedMagic.indexOf(this);
            Animations.render(UIbatch,x,y,flag,index);
        }
        //获得后释放魔法的判定
        if (isObtain && Gdx.input.isKeyJustPressed(Input.Keys.F)&&(player.getAttributeHandler().getMaxMP()-Attributes.getMPcost()>=0)) {
            if (!Active && cooldownTimer <= 0) {
                Active = true;
                isActivating = true;
                StartX = player.getX();
                StartY = player.getY();
                isFlip = player.getPlayerController().isFlipped();
                cooldownTimer = Attributes.getCooldown();
                player.getAttributeHandler().setMP(player.getAttributeHandler().getMaxMP()-Attributes.getMPcost());
            }
        }
        if (Active) {
            Animations.isActive = true;
            //只在激活瞬间创建效果体
            if (isActivating) {
                time = System.currentTimeMillis();
                isActivating = false;
            }
            long timeSinceActivation = (System.currentTimeMillis() - time);
            if(timeSinceActivation >= Attributes.getDuration()*1000){
                Active = false;
                time = 0;
                StartX = 0f;
                StartY = 0f;
                if (EffectsBody != null) {
                    effectBodyPool.release(EffectsBody); // 将刚体放回对象池
                }
            }
            ThunderAnimation.render(batch,StartX, StartY,isFlip,Animations,Hitboxes,Active);
        }
        //如果技能处于非激活状态且冷却结束重置状态
        if (!Active && cooldownTimer <= 0&&(player.getAttributeHandler().getMaxMP()-Attributes.getMPcost()>=0)){
            Animations.isActive = false;
        }
        //更新冷却计时器
        if(cooldownTimer > 0 && !Active) {
            cooldownTimer -= Gdx.graphics.getDeltaTime();
            if (cooldownTimer < 0) cooldownTimer = 0;
        }
        if(dst(player)&&!flag){
            Tips.E(batch,player.getX(),player.getY());
        }
        //伤害造成逻辑
        if(Active){
            Body effectsBody = Hitboxes.getEffectsBody();
            if(effectsBody!=null){
                effectsBody.setUserData(this);
            }
        }
    }
    public void dispose() {
        Animations.dispose();
    }

    // 对象池类，用于管理Body对象
    private static class BodyPool {
        private final Body[] bodies;
        private int count;

        public BodyPool(int capacity) {
            bodies = new Body[capacity];
            count = 0;
        }

        public Body acquire(float x, float y, float width, float height) {
            if (count > 0) {
                return bodies[--count]; // 从池中取出一个刚体
            }
            // 如果池中没有可用刚体，则创建新的刚体
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(x, y);

            Body body = Game.world.createBody(bodyDef);
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width, height);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = true;

            body.createFixture(fixtureDef).setUserData("Magic");
            shape.dispose();

            return body;
        }

        public void release(Body body) {
            if (count < bodies.length) {
                body.setActive(false); // 停用刚体
                bodies[count++] = body; // 将刚体放回池中
            } else {
                Game.world.destroyBody(body); // 池已满，销毁刚体
            }
        }
    }
}
class ThunderAnimation{
    private static float stateTime=0f;
    public static void render(SpriteBatch batch,float x, float y, Boolean flip,MagicAnimation animation,MagicHitbox Hitboxes,Boolean Active) {
        AnimationTool[] animations = animation.getAnimations();
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(),0.25f);
        stateTime+=deltaTime;
        int flag=1;
        if(!flip){
            flag=-1;
        }
        float CurrentX = x+0.5f*stateTime*flag;
        float CurrentY = y;
        batch.begin();
        for (int i = 0; i <= 360; i += 35) {
            float offsetX = cos((float) i + stateTime * 2)*(1+stateTime/2);
            float offsetY = MathUtils.sin((float) i + stateTime * 2)*(1+stateTime/2);
            if (i == 0 && (Hitboxes.getEffectsBody() == null || !Hitboxes.getEffectsBody().getWorld().isLocked())) {
                Hitboxes.createEffectBody(CurrentX+offsetX,CurrentY,2.8f,3);
            }
            animations[1].render(batch,CurrentX+offsetX,CurrentY+offsetY,0.1f,false,flip);
        }
        batch.end();
        if (!Active) {
            stateTime = 0f;
        }
    }
}

