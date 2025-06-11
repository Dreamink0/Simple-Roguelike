package io.github.SimpleGame.Item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.physics.box2d.Body;
import io.github.SimpleGame.Character.Player.Player;
//import io.github.SimpleGame.Magic.Ice;
import io.github.SimpleGame.Resource.HitboxManager;
import io.github.SimpleGame.Tool.AnimationTool;
import io.github.SimpleGame.Tool.Listener;

import java.util.Random;

import static io.github.SimpleGame.Resource.Game.batch;

public class WeaponEffects {
    public int ID;
    public int WeaponID;
    private static Player player;
    private final AnimationTool[] animationTool;
    private HitboxManager hitbox;
    private float duration=0f;
    private float Cooldown;
    private float timer = 0f;
    private boolean isCreated = false;
    private Body effects;
    private WeaponAttribute attribute;
    private float BulletTime=0f;
    private boolean isFlipped;
    public WeaponEffects(int ID,int WeaponID,Player player){
        this.ID=ID;
        this.WeaponID=WeaponID;
        WeaponEffects.player =player;
        WeaponEffectsTextureManager manager = new WeaponEffectsTextureManager();
        manager.create(ID,WeaponID);
        animationTool= manager.getAnimation();//获得基础特效，而渲染方式可以重写，然后组合更多特效,记得用get获得动画
    }
    public void render(SpriteBatch batch){
        if(!isCreated) {
            attribute = new WeaponAttribute();
            hitbox = new HitboxManager();
        }
        AnimationTool[] animationTools = animationTool;
        if(ID==0) {
            if (WeaponID == 0) {
                batch.begin();
                animationTools[0].render(batch, player.getX(), player.getY(), 0.1f, true);
                batch.end();
                Cooldown = 0.3f;
                duration = 1.4f;
                if (!isCreated) {
                    effects = hitbox.create(player.getWorld(), animationTool[0], 0, 0, 0.3f, 0.3f);
                    effects.setUserData(this);
                    isCreated = true;
                } else {
                    effects.setActive(true);
                    hitbox.update(player.getX(), player.getY(), effects);
                    effects.setUserData(this);
                }
            }//火焰光环!
            if (WeaponID == 1) {
                float Startx = player.getX();
                float Starty = player.getY();
                if (Gdx.input.isKeyPressed(Input.Keys.J)) {
                    Startx = player.getX();
                    Starty = player.getY();
                    isFlipped = player.getPlayerController().isFlipped;
                }
                batch.begin();
                BulletTime += Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
                float speed = 35;
                float currentx;
                final float direction = isFlipped ? -1 : 1; // 记录初始方向
                currentx = Startx + BulletTime * speed * direction;
                animationTools[0].render(batch, currentx, Starty, 0.01f, true, !isFlipped);
                Cooldown = 0.1f;
                duration = 0.4f;
                if (!isCreated) {
                    effects = hitbox.create(player.getWorld(), animationTool[0], 0, 0, 0.1f, 0.1f);
                    effects.setUserData(this);
                    isCreated = true;
                } else {
                    effects.setActive(true);
                    hitbox.update(currentx, Starty, effects);
                    effects.setUserData(this);
                }
                batch.end();
            }//寒冰射手!
            if (WeaponID == 2) {
                batch.begin();
                float forceMultiplier = 10f;
                boolean isCollidingWithWall = isPlayerCollidingWithWall();
                boolean isMovingLeft = Gdx.input.isKeyPressed(Input.Keys.A);
                boolean isMovingRight = Gdx.input.isKeyPressed(Input.Keys.D);
                boolean isMovingUp = Gdx.input.isKeyPressed(Input.Keys.W);
                boolean isMovingDown = Gdx.input.isKeyPressed(Input.Keys.S);
                float forceX = 0f;
                float forceY = 0f;
                if (isMovingLeft && !isMovingRight) {
                    forceX = -forceMultiplier;
                } else if (isMovingRight && !isMovingLeft) {
                    forceX = forceMultiplier;
                }
                if (isMovingUp && !isMovingDown) {
                    forceY = forceMultiplier;
                } else if (isMovingDown && !isMovingUp) {
                    forceY = -forceMultiplier;
                }
                if (forceX == 0f && forceY == 0f) {
                    float direction = player.getPlayerController().isFlipped ? -4 : 4;
                    forceX = -forceMultiplier * direction;
                }
                if (isCollidingWithWall) {
                    forceX = -forceX;
                    forceY = -forceY;
                }
                player.getBody().applyLinearImpulse(
                    forceX,
                    forceY,
                    player.getX(),
                    player.getY(),
                    true
                );
                animationTools[0].render(batch, player.getX(), player.getY(), 0.5f, true, player.getPlayerController().isFlipped);
                batch.end();
                Cooldown = 0.3f;
                duration = 0.8f;
                if (!isCreated) {
                    effects = hitbox.create(player.getWorld(), animationTool[0], 0, 0, 0.7f, 0.6f);
                    effects.setUserData(this);
                    isCreated = true;
                } else {
                    effects.setActive(true);
                    hitbox.update(player.getX(), player.getY(), effects);
                    effects.setUserData(this);
                }
            }//只有暴风才能击倒大树!
            if (WeaponID == 3) {
                batch.begin();
                if (Gdx.input.isKeyPressed(Input.Keys.J)) {
                    // 使用更平滑的颜色渐变效果，基于sin函数的RGB三色通道变化
                    float t = (System.currentTimeMillis() % 2000) / 2000f; // 循环时间因子

                    float r = (float) Math.sin(2 * Math.PI * t * 3 + 0) * 0.5f + 0.5f;
                    float g = (float) Math.sin(2 * Math.PI * t * 3 + 2 * Math.PI / 3) * 0.5f + 0.5f;
                    float b = (float) Math.sin(2 * Math.PI * t * 3 + 4 * Math.PI / 3) * 0.5f + 0.5f;

                    Color color = new Color(r, g, b, 1.0f);
                    batch.setColor(color);
                } else {
                    // 默认颜色为白色
                    batch.setColor(1, 1, 1, 1);
                }
                int flip = player.getPlayerController().isFlipped ? -12 : 12;
                animationTools[0].render(batch, player.getX() + flip, player.getY(), 0.3f,0.2f, true, player.getPlayerController().isFlipped);
                Cooldown = 0.1f;
                duration = 0.4f;
                if (!isCreated) {
                    effects = hitbox.create(player.getWorld(), animationTool[0], 0, 0, 0.4f, 0.2f);
                    effects.setUserData(this);
                    isCreated = true;
                } else {
                    effects.setActive(true);
                    hitbox.update(player.getX() + flip*0.9f, player.getY(), effects);
                    effects.setUserData(this);
                }
                batch.end();
                batch.setColor(1, 1, 1, 1);
            }//七彩彗星鸭子乐!
            if (WeaponID == 4) {
                batch.begin();
                batch.setColor(1, 1, 1, 0.75f);
                int flag = 0;
                if (player.getPlayerController().isFlipped) {
                    flag = -1;
                } else {
                    flag = 1;
                }
                if (player.getPlayerController().isAttacking()) {
                    animationTools[0].render(batch, player.getX(), player.getY() + 3, 0.1f, true, player.getPlayerController().isFlipped);
                } else {
                    animationTools[1].render(batch, player.getX(), player.getY() + 3, 0.1f, true, player.getPlayerController().isFlipped);
                }
                Cooldown = 0.25f;
                duration = 0.25f;
                if (!isCreated) {
                    effects = hitbox.create(player.getWorld(), animationTool[0], 0, 0, 0.2f, 0.2f);
                    effects.setUserData(this);
                    isCreated = true;
                } else {
                    effects.setActive(true);
                    hitbox.update(player.getX() + 4 * flag, player.getY(), effects);
                    effects.setUserData(this);
                }
                batch.end();
                batch.setColor(1, 1, 1, 1f);
            }//赞美星辰！
            if (WeaponID == 5) {
                isFlipped = player.getPlayerController().isFlipped;
                batch.begin();
                animationTools[0].render(batch, player.getX(), player.getY(), 0.02f, true,isFlipped);
                batch.end();
                Cooldown=0.15f;
                duration = 0.275f;
                if (!isCreated) {
                    effects = hitbox.create(player.getWorld(), animationTool[0], 0, 0, 0.13f, 0.1f);
                    effects.setUserData(this);
                    isCreated = true;
                } else {
                    effects.setActive(true);
                    hitbox.update(player.getX(), player.getY(), effects);
                    effects.setUserData(this);
                }
            }//小刀！
            if (WeaponID == 6) {
                boolean flip = player.getPlayerController().isFlipped;
                int flag;
                if(flip){
                    flag=1;
                }else{
                    flag=-1;
                }
                batch.begin();
                for(int i=0; i<9; i++) {
                    float angle = (float) (i*Math.PI*2/9);
                    float radius = 4.5f+(float)Math.sin(timer*5+i)*0.5f;
                    float scale = 0.02f+(0.06f*((i+1)/9f));
                    float x = (float)(player.getX() + radius * Math.cos(angle) * flag);
                    float y = (float)(player.getY() + 2.8f * Math.sin(angle));
                    animationTools[0].render(batch, x, y, scale*0.3f, true, !flip);
                    float r, b,g;
                    r = 1.5f + 0.5f * (float)Math.sin(timer + i);
                    b = 0.5f + 1.5f * (float)Math.cos(timer + i * 0.7f);
                    g = 0.5f + 0.5f * (float)Math.sin(timer + i * 0.7f);
                    batch.setColor(r, g, b, 0.7f);
                    if(animationTools[i].isAnimationFinished()) {
                        if(i + 1 < animationTool.length) {
                            animationTools[i].render(batch, x, y, scale * 1.5f, true, flip);
                            animationTools[i].render(batch,
                                x - 0.2f * flag,
                                y + 0.1f,
                                scale * 1f,
                                true,
                                !flip);
                        }
                    }
                }
                batch.setColor(1, 1, 1, 1);
                batch.end();
                Cooldown=0.08f;
                duration=600f;
                if (!isCreated) {
                    effects=hitbox.create(player.getWorld(),animationTools[0],player.getX(),player.getY(),0.4f,0.4f);
                    effects.setUserData(this);
                    isCreated = true;
                } else {
                    effects.setActive(true);
                    hitbox.update(player.getX(), player.getY(), effects);
                    effects.setUserData(this);
                }
                Cooldown=0.15f;
                duration=1f;
            }//光的力量!
            if (WeaponID == 12) {
                batch.begin();
                batch.setColor(1, 1, 1, 0.75f);
                if (player.getPlayerController().isFlipped) {
                    animationTools[0].render(batch, player.getX() + 5f, player.getY(), 0.1f, 0.1f, true, player.getPlayerController().isFlipped);
                } else {
                    animationTools[0].render(batch, player.getX() - 5f, player.getY(), 0.1f, 0.1f, true, player.getPlayerController().isFlipped);
                }
                float tpDistance = 0.4f;
                boolean isFlipped = player.getPlayerController().isFlipped;
                float direction = isFlipped ? -tpDistance : tpDistance;
                float newX = player.getX() + direction;
                float newY = player.getY();
                player.getBody().setTransform(newX, newY, player.getBody().getAngle());
                batch.setColor(1, 1, 1, 1f);
                batch.end();
                duration = 1;
                Cooldown = 0.15f;
                if (!isCreated) {
                    effects = hitbox.create(player.getWorld(), animationTool[0], 0, 0, 0.3f, 0.2f);
                    effects.setUserData(this);
                    isCreated = true;
                } else {
                    effects.setActive(true);
                    hitbox.update(player.getX(), player.getY(), effects);
                    effects.setUserData(this);
                }
            }//博尔特!
            if (WeaponID == 23) {
                batch.begin();
                batch.setColor(155, 255, 255, 0.85f);
                animationTools[0].render(batch, player.getX(), player.getY(), 0.15f, true);
                if (animationTools[0].isAnimationFinished()) {
                    animationTools[1].render(batch, player.getX(), player.getY(), 0.14f, true);
                    animationTools[2].render(batch, player.getX(), player.getY(), 0.13f, true);
                    animationTools[3].render(batch, player.getX(), player.getY(), 0.15f, true);
                    animationTools[4].render(batch, player.getX(), player.getY(), 0.1f, true, player.getPlayerController().isFlipped);
                } else if (animationTools[2].isAnimationFinished()) {
                    animationTools[0].resetStateTime();
                    animationTools[1].resetStateTime();
                    animationTools[2].resetStateTime();
                    animationTools[3].resetStateTime();
                    animationTools[4].resetStateTime();
                }
                batch.setColor(1, 1, 1, 1);
                batch.end();
                Cooldown = 0.05f;
                duration = 0.75f;
                if (!isCreated) {
                    effects = hitbox.create(player.getWorld(), animationTool[0], 0, 0, 0.3f, 0.3f);
                    effects.setUserData(this);
                    isCreated = true;
                } else {
                    effects.setActive(true);
                    hitbox.update(player.getX(), player.getY(), effects);
                    effects.setUserData(this);
                }
            }//我去，好帅的特效!
        }
        if(timer <= 0){
            if(effects!=null){
                effects.setActive(false);
            }
            timer = duration;
            BulletTime = 0;
            Weapon.FLAG = false;
            if(animationTools!=null){
                for (AnimationTool tool : animationTools) {
                    tool.resetStateTime();
                }
            }
        }
        timer -= Math.min(Gdx.graphics.getDeltaTime(),0.25f);
    }

    private boolean isPlayerCollidingWithWall() {
        return Listener.wall_Flag;
    }
    public AnimationTool[] getAnimationTool() {
        return animationTool;
    }
    public float getDuration() {
        return duration;
    }
    public float getDamage(){
        attribute=attribute.readData(ID,WeaponID);
        return attribute.getDamage();
    }
    public float getCooldown(){
        return Cooldown;
    }
    public float getID(){
        return WeaponID;
    }
    public void dispose(){
        if(animationTool!=null){
            for(AnimationTool animationTool:animationTool){
                animationTool.dispose();
            }
        }
    }
    public int getWeaponID(int ID){
        return WeaponID;
    }
    public SpriteBatch getBatch(){
        return batch;
    }
}
