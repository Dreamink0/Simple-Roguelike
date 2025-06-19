package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Resource.EffectManager;
import io.github.SimpleGame.Tool.AnimationTool;

import java.util.Iterator;

public class EnemyAnimation implements EnemyAnimationHandler{
    protected static AssetManager assetManager=new AssetManager();
    protected AnimationTool[] animationTools;
    protected AnimationTool effectsAnimations;
    protected Texture texture;
    protected String className;
    protected int ID;
    protected float x;
    protected float y;
    protected boolean flip;
    protected EnemyEffects effects;
    protected boolean hasPlayedDeathAnimation = false;
    protected float deathAnimationTimer = 0f;
    protected float DEATH_ANIMATION_DURATION =1f; // 根据死亡动画总时长调整
    protected float scale = 0.1f;

    @Override
    public void load(String className) {
        this.className = className;
        effects = new EnemyEffects();
         if(className.equals("Goblin")){
            ID=1;
            animationTools = new AnimationTool[5];
            GoblinResource resource = new GoblinResource();
            animationTools = resource.getAnimationTool();
            DEATH_ANIMATION_DURATION = 1f;
        }
         if (className.equals("Flyingeye")){
             ID=2;
             animationTools = new AnimationTool[5];
             FlyingeyeResource resource = new FlyingeyeResource();
             animationTools = resource.getAnimationTool();
             DEATH_ANIMATION_DURATION = 0.38f;
         }
         if(className.equals("Skeleton")){
             ID=3;
             animationTools = new AnimationTool[5];
             SkeletonResource resource = new SkeletonResource();
             animationTools = resource.getAnimationTool();
             DEATH_ANIMATION_DURATION = 1f;
             scale=0.15f;
         }
         if(className.equals("Frog")){
             ID=4;
             animationTools = new AnimationTool[5];
             FrogResource resource = new FrogResource();
             animationTools = resource.getAnimationTool();
             DEATH_ANIMATION_DURATION = 0.25f;
         }
         if(className.equals("NightBorne")){
             ID=5;
             animationTools = new AnimationTool[5];
             NightBorneResource resource = new NightBorneResource();
             animationTools = resource.getAnimationTool();
             DEATH_ANIMATION_DURATION = 1f;
             scale=0.12f;
         }
         if(className.equals("BadCat")){
             ID=6;
             animationTools = new AnimationTool[5];
             BadCatResource resource = new BadCatResource();
             animationTools = resource.getAnimationTool();
             DEATH_ANIMATION_DURATION = 1f;
             scale=0.05f;
         }
        effects.load(ID);
    }
    public void render(SpriteBatch batch, EnemyState enemyState, Player player){
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 5);
        Enemy.State currentState = enemyState.currentState;
        AnimationTool currentAnimation;
        if (enemyState.getEnemyBody() != null) {
            x = enemyState.getEnemyBody().getPosition().x;
            y = enemyState.getEnemyBody().getPosition().y;
            flip = player.getX() < x;
        }
        if (currentState == Enemy.State.DIE) {
            // 如果是第一次进入死亡状态，重置计时器
            if (!hasPlayedDeathAnimation) {
                deathAnimationTimer = 0f;
                if (animationTools[4].isAnimationFinished()) {
                    animationTools[4].resetStateTime(); // 重置死亡动画时间
                }
            }
            hasPlayedDeathAnimation = true;
        }

        if (!(currentState == Enemy.State.DIE && deathAnimationTimer >= DEATH_ANIMATION_DURATION)) {
            currentAnimation = switch (currentState) {
                case CHASE -> animationTools[1];
                case ATTACK -> animationTools[2];
                case HURT -> animationTools[3];
                case DIE -> animationTools[4];
                default -> animationTools[0];
            };
            if (currentAnimation != null) {
                // 死亡动画单独处理
                if (currentState == Enemy.State.DIE) {
                    effects.render(batch, enemyState, player);
                    // 只有在死亡动画未完成时才渲染
                    if (deathAnimationTimer < DEATH_ANIMATION_DURATION) {
                        animationTools[4].render(batch, x, y, scale, false, flip);
                    }
                } else if (currentState == Enemy.State.ATTACK) {
                    animationTools[2].resetStateTime();
                    animationTools[2].render(batch, x, y, scale, true, flip);
                } else if (currentState == Enemy.State.CHASE) {
                    animationTools[1].resetStateTime();
                    animationTools[1].render(batch, x, y, scale, true, flip);
                } else if (currentState == Enemy.State.HURT) {
                    effects.render(batch, enemyState, player);
                    EffectManager.getInstance().applyEffectToObject(
                        batch,EffectManager.EffectType.DEATH,
                        1f,
                        () ->animationTools[3].render(batch, x, y, scale, true, flip)
                    );
                } else {
                    currentAnimation.render(batch, x, y, scale, true, flip);
                }
            }
        }

        if (currentState == Enemy.State.DIE && hasPlayedDeathAnimation) {
            deathAnimationTimer += deltaTime;
            if (enemyState.getEnemyBody() != null) {
                enemyState.freeBody();
            }
        }
    }
    public boolean isAnimationFinished(){
        return hasPlayedDeathAnimation && deathAnimationTimer >= DEATH_ANIMATION_DURATION;
    }
    @Override
    public void dispose() {
        if(animationTools!=null){
            for (AnimationTool animationTool : animationTools) animationTool.dispose();
        }
        if (assetManager != null) {
            assetManager.dispose();
        }
    }
    public String getClassName() {return className;}
    public AnimationTool[] getAnimationTools() {return animationTools;}
}
