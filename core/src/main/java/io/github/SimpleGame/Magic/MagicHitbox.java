package io.github.SimpleGame.Magic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;

public class MagicHitbox {
    private MagicAnimation magicAnimation;
    private World world;
    private Player player;
    private Body magicIconBody;
    private Body EffectsBody;

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

    public void destoryIconBody(){
        if(this.magicIconBody != null){
            world.destroyBody(this.magicIconBody);
        }
    }
    public void createEffectBody(float x, float y) {
        //清除上一帧的碰撞体
        if (this.EffectsBody != null && !this.EffectsBody.getWorld().isLocked()) {
            world.destroyBody(this.EffectsBody);
            this.EffectsBody = null;
        }

        if (this.EffectsBody == null) {
            float width = 5 * magicAnimation.getIconTexture()[0].getWidth() / Config.PIXELS_PER_METER;
            float height = 5 * magicAnimation.getIconTexture()[0].getHeight() / Config.PIXELS_PER_METER;

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
