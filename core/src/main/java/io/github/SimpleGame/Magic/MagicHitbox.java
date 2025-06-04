package io.github.SimpleGame.Magic;

import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;

public class MagicHitbox {
    private MagicAnimation magicAnimation;
    private World world;
    private Player player;
    public Body magicIconBody;
    public Body EffectsBody;

    //先得创建可以拾取的魔法图标
    //一个是可拾取的魔法图标，一个是施法魔法后的效果
    public MagicHitbox(World world, Player player,MagicAnimation magicAnimation){
        this.world = world;
        this.player = player;
        this.magicAnimation = magicAnimation;
    }

    public void createIconBody(float x, float y){
         float width = 1.2f*magicAnimation.getIconTexture()[0].getWidth() / Config.PIXELS_PER_METER;
         float height = 1.2f*magicAnimation.getIconTexture()[0].getHeight() / Config.PIXELS_PER_METER;
         BodyDef bodyDef = new BodyDef();
         bodyDef.type = BodyDef.BodyType.KinematicBody;
         bodyDef.position.set(x+width/2, y+height/2);
         this.magicIconBody = world.createBody(bodyDef);
         this.magicIconBody.setUserData("MagicIcon");
         PolygonShape shape = new PolygonShape();
         shape.setAsBox(width, height);
         FixtureDef fixtureDef = new FixtureDef();
         fixtureDef.shape = shape;
         fixtureDef.isSensor = true;
         this.magicIconBody.createFixture(fixtureDef).setUserData("MagicIcon");
         shape.dispose();
    }

    public void destroyIconBody(){
        if(this.magicIconBody != null){
            world.destroyBody(this.magicIconBody);
        }
    }

    public void destroyEffectBody() {
        if (this.EffectsBody != null && !this.EffectsBody.getWorld().isLocked()) {
            world.destroyBody(this.EffectsBody);
            this.EffectsBody = null;
        }
    }

    public void createEffectWithBody(Body effectBody) {
        if (effectBody != null) {
            this.EffectsBody = effectBody;
        }
    }

    public Body getEffectsBody() {
        return EffectsBody;
    }

    public void setEffectsBody(Body effectsBody) {
        EffectsBody = effectsBody;
    }

    public void clearEffectBody() {
        this.EffectsBody = null;
    }

    public void createEffectBody(float currentX, float y, float v, int i) {
        if (EffectsBody != null) {
            return; // 已经存在效果体，直接返回
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(currentX, y);

        EffectsBody = world.createBody(bodyDef);
        EffectsBody.setUserData("MagicEffect");

        CircleShape shape = new CircleShape();
        shape.setRadius(v / Config.PIXELS_PER_METER);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        // 初始化默认的碰撞过滤器
        Filter filter = new Filter();

        // 根据不同魔法类型设置不同的碰撞属性
        if (i == 0) { // 雷电魔法
            filter.categoryBits = (short) 0x0002; // BIT_MAGIC_LIGHTNING
            filter.maskBits = (short) 0x0004; // BIT_ENEMY
        } else if (i == 1) { // 暗影魔法
            filter.categoryBits = (short) 0x0008; // BIT_MAGIC_SHADOW
            filter.maskBits = (short) 0x0004; // BIT_ENEMY
        } else {
            // 默认魔法
            filter.categoryBits = (short) 0x0001; // BIT_MAGIC
            filter.maskBits = (short) 0x0004; // BIT_ENEMY
        }

        Fixture fixture = EffectsBody.createFixture(fixtureDef);
        fixture.setFilterData(filter);  // 使用 setFilterData 方法代替直接赋值
        fixture.setUserData(this);  // 修复：先保存Fixture引用再设置userData
        shape.dispose();
    }
}
