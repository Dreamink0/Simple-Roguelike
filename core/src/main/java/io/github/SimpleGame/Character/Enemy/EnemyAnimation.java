package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Item.Tips;
import io.github.SimpleGame.Resource.SoundManager;
import io.github.SimpleGame.Tool.AnimationTool;

public class EnemyAnimation implements EnemyAnimationHandler{
    protected AssetManager assetManager=new AssetManager();
    protected AnimationTool[] animationTools;
    protected AnimationTool[] effectsAnimations;
    protected Texture[] effectTexture;
    protected float x;
    protected float y;
    protected boolean flip;
    public boolean hasPlayedDeathAnimation = false;
    public float deathAnimationTimer = 0f;
    public float DEATH_ANIMATION_DURATION =1f; // 根据死亡动画总时长调整
    protected String className;

    @Override
    public void load(String className) {
        this.className = className;
        String[] effectPaths = {
            "Effects/hitEffects.png",
            "Effects/Goblinblood.png",
        };
        for (String path : effectPaths) {
            assetManager.load(path, Texture.class);
        }
        assetManager.finishLoading();
        effectTexture = new Texture[effectPaths.length + 1];
        effectsAnimations = new AnimationTool[effectPaths.length + 1];
        for (int i = 0; i < effectPaths.length; i++) {
            effectTexture[i] = assetManager.get(effectPaths[i], Texture.class);
            effectsAnimations[i] = new AnimationTool();
            if (i == 0) {
                effectsAnimations[i].create("Hit", effectTexture[i], 1, 4, 0.2f);
            } else if (i == 1) {
                effectsAnimations[i].create("GobBlood", effectTexture[i], 1, 6, 0.1f);
            }
        }
         if(className.equals("Goblin")){
            animationTools = new AnimationTool[5];
            GoblinResource resource = new GoblinResource();
            animationTools = resource.getAnimationTool();
            DEATH_ANIMATION_DURATION = 1f;
        }
         if (className.equals("Flyingeye")){
             animationTools = new AnimationTool[5];
             FlyingeyeResource resource = new FlyingeyeResource();
            animationTools = resource.getAnimationTool();
            DEATH_ANIMATION_DURATION = 0.38f;
         }
         if(className.equals("Skeleton")){
             animationTools = new AnimationTool[5];
             SkeletonResource resource = new SkeletonResource();
             animationTools = resource.getAnimationTool();
             DEATH_ANIMATION_DURATION = 1f;
         }

    }
    public void render(SpriteBatch batch, EnemyState enemyState, Player player){
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(),5);
        Enemy.State currentState = enemyState.currentState;
        AnimationTool currentAnimation;
        if(enemyState.getEnemyBody()!=null){
            x = enemyState.getEnemyBody().getPosition().x;
            y = enemyState.getEnemyBody().getPosition().y;
            flip = player.getX() < x;
        }
        if(currentState == Enemy.State.DIE){
            //如果是第一次进入死亡状态，重置计时器
            if (!hasPlayedDeathAnimation) {
                deathAnimationTimer = 0f;
                if (animationTools != null && animationTools[4] != null) {
                    animationTools[4].resetStateTime(); //重置死亡动画时间
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
                    // 只有在死亡动画未完成时才渲染
                    if(className.equals("Goblin")){
                        if(flip){
                            effectsAnimations[1].render(batch, x+3f, y-0.4f, 0.1f, true, false);
                        }else{
                            effectsAnimations[1].render(batch, x-3f, y-0.4f, 0.1f, true, true);
                        }
                    }else{
                        effectsAnimations[0].render(batch, x, y, 0.1f, true, flip);
                    }
                    if (deathAnimationTimer < DEATH_ANIMATION_DURATION) {
                        animationTools[4].render(batch, x, y, 0.1f, false, flip);
                    }
                }else if(currentState == Enemy.State.ATTACK){
                    animationTools[2].render(batch, x, y, 0.1f, false, flip);
                }else if(currentState == Enemy.State.CHASE){
                    animationTools[1].render(batch, x, y, 0.1f, true, flip);
                }else if(currentState == Enemy.State.HURT){
                    effectsAnimations[0].render(batch, x, y, 0.15f, false, flip);
                    animationTools[3].render(batch, x, y, 0.1f, true, flip);
                }else{
                    currentAnimation.render(batch, x, y, 0.1f, true, flip);
                }
            }
        }
        if (currentState == Enemy.State.DIE && hasPlayedDeathAnimation) {
            deathAnimationTimer += deltaTime;
            if(enemyState.getEnemyBody()!=null){
                enemyState.freeBody();
            }
        }
    }
    @Override
    public void dispose() {
        if(animationTools!=null){for (int i = 0; i < animationTools.length; i++) animationTools[i].dispose();}
        if (assetManager != null) {
            assetManager.dispose();
        }
    }

    public AnimationTool[] getAnimationTools() {return animationTools;}
}
