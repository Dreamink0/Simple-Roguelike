package io.github.SimpleGame.Item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.physics.box2d.Body;
import io.github.SimpleGame.Character.Player.Player;
//import io.github.SimpleGame.Magic.Ice;
import io.github.SimpleGame.Resource.HitboxManager;
import io.github.SimpleGame.Tool.AnimationTool;
import io.github.SimpleGame.Tool.Listener;

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
        if(ID==0){
            if(WeaponID==0){
                batch.begin();
                animationTools[0].render(batch,player.getX(),player.getY(),0.1f,true);
                batch.end();
                Cooldown = 0.3f;
                duration = 1.4f;
                if(!isCreated){
                    effects=hitbox.create(player.getWorld(),animationTool[0],0,0,0.3f,0.3f);
                    effects.setUserData(this);
                    isCreated = true;
                }else{
                    effects.setActive(true);
                    hitbox.update(player.getX(),player.getY(),effects);
                    effects.setUserData(this);
                }
            }
            if(WeaponID==1) {}
            if(WeaponID==2){
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
                    float direction = player.getPlayerController().isFlipped ? -4: 4;
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
                animationTools[0].render(batch,player.getX(),player.getY(),0.5f,true,player.getPlayerController().isFlipped);
                batch.end();
                Cooldown = 0.3f;
                duration = 0.8f;
                if(!isCreated){
                    effects=hitbox.create(player.getWorld(),animationTool[0],0,0,0.7f,0.6f);
                    effects.setUserData(this);
                    isCreated = true;
                }else{
                    effects.setActive(true);
                    hitbox.update(player.getX(),player.getY(),effects);
                    effects.setUserData(this);
                }
            }
            if(WeaponID==4){

                duration=10f;
            }
            if(WeaponID==12){
                batch.begin();
                batch.setColor(1,1,1,0.75f);
                if (player.getPlayerController().isFlipped){
                    animationTools[0].render(batch,player.getX()+5f, player.getY(), 0.1f,0.1f,true,player.getPlayerController().isFlipped);
                }else{
                    animationTools[0].render(batch,player.getX()-5f, player.getY(), 0.1f,0.1f,true,player.getPlayerController().isFlipped);
                }
                float tpDistance = 0.4f;
                boolean isFlipped = player.getPlayerController().isFlipped;
                float direction = isFlipped ? -tpDistance : tpDistance;
                float newX = player.getX() + direction;
                float newY = player.getY();
                player.getBody().setTransform(newX, newY, player.getBody().getAngle());
                batch.setColor(1,1,1,1f);
                batch.end();
                duration=1;
            }
            if(WeaponID==23){
                batch.begin();
                batch.setColor(155,255,255,0.85f);
                animationTools[0].render(batch,player.getX(),player.getY(),0.15f,true);
                if(animationTools[0].isAnimationFinished()){
                    animationTools[1].render(batch,player.getX(),player.getY(),0.14f,true);
                    animationTools[2].render(batch,player.getX(),player.getY(),0.13f,true);
                    animationTools[3].render(batch,player.getX(),player.getY(),0.15f,true);
                    animationTools[4].render(batch,player.getX(),player.getY(),0.1f,true,player.getPlayerController().isFlipped);
                }else if(animationTools[2].isAnimationFinished()){
                    animationTools[0].resetStateTime();
                    animationTools[1].resetStateTime();
                    animationTools[2].resetStateTime();
                    animationTools[3].resetStateTime();
                    animationTools[4].resetStateTime();
                }
                batch.setColor(1,1,1,1);
                batch.end();
                Cooldown = 0.05f;
                duration = 0.75f;
                if(!isCreated){
                    effects=hitbox.create(player.getWorld(),animationTool[0],0,0,0.3f,0.3f);
                    effects.setUserData(this);
                    isCreated = true;
                }else{
                    effects.setActive(true);
                    hitbox.update(player.getX(),player.getY(),effects);
                    effects.setUserData(this);
                }
            }
        }
        if(timer <= 0){
            effects.setActive(false);
            timer = duration;
            Weapon.FLAG = false;
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
}
