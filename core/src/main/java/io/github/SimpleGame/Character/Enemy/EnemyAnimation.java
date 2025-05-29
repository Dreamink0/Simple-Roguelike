package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Tool.AnimationTool;

public class EnemyAnimation implements EnemyAnimationHandler{
    protected AssetManager assetManager=new AssetManager();
    private AnimationTool[] animationTools;
    private EnemyState enemyState;
    private Texture IdleTexture;
    private Texture RunTexture;
    private Texture HitTexture;
    private Texture HurtTexture;
    private Texture DeathTexture;
    private float x;
    private float y;
    private boolean flip;
    public boolean hasPlayedDeathAnimation = false;
    public float deathAnimationTimer = 0f;
    public final float DEATH_ANIMATION_DURATION =1f; // 根据死亡动画总时长调整

    @Override
    public void load(String className) {
        if(className.equals("Goblin")){
            animationTools = new AnimationTool[5];
            assetManager.load("Enemy/goblin/goblin scout - silhouette all animations-idle.png", Texture.class);
            assetManager.load("Enemy/goblin/goblin scout - silhouette all animations-run.png",Texture.class);
            assetManager.load("Enemy/goblin/goblin scout - silhouette all animations-hit.png",Texture.class);
            assetManager.load("Enemy/goblin/goblin scout - silhouette all animations-hurt.png",Texture.class);
            assetManager.load("Enemy/goblin/goblin scout - silhouette all animations-death 1.png",Texture.class);
            assetManager.finishLoading();
            for (int i = 0; i < animationTools.length; i++) {animationTools[i]=new AnimationTool();}
            IdleTexture = assetManager.get("Enemy/goblin/goblin scout - silhouette all animations-idle.png", Texture.class);
            RunTexture = assetManager.get("Enemy/goblin/goblin scout - silhouette all animations-run.png", Texture.class);
            HitTexture = assetManager.get("Enemy/goblin/goblin scout - silhouette all animations-hit.png", Texture.class);
            HurtTexture = assetManager.get("Enemy/goblin/goblin scout - silhouette all animations-hurt.png", Texture.class);
            DeathTexture = assetManager.get("Enemy/goblin/goblin scout - silhouette all animations-death 1.png", Texture.class);
            animationTools[0].create("Idle",IdleTexture,1,8,0.15f);
            animationTools[1].create("Run",RunTexture,1,8,0.15f);
            animationTools[2].create("Hit",HitTexture,1,3,0.05f);
            animationTools[3].create("Hurt",HurtTexture,1,3,0.1f);
            animationTools[4].create("Death",DeathTexture,1,12,0.15f);
        }

    }

    @Override
    public void dispose() {
        if(animationTools!=null){for (int i = 0; i < animationTools.length; i++) animationTools[i].dispose();}
        if (assetManager != null) {
            assetManager.dispose(); // 非空判断避免 NPE
        }
        animationTools = null;
        IdleTexture = null;
        RunTexture = null;
        HitTexture = null;
        HurtTexture = null;
        DeathTexture = null;
        assetManager = null;
    }

    public AnimationTool[] getAnimationTools() {return animationTools;}

    public void render(SpriteBatch batch, EnemyState enemyState, Player player){
        this.enemyState = enemyState;
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(),5);
        Enemy.State currentState = enemyState.currentState;
        AnimationTool currentAnimation;
        if(enemyState.getEnemyBody()!=null){
            x = enemyState.getEnemyBody().getPosition().x;
            y = enemyState.getEnemyBody().getPosition().y;
            flip = player.getX() < x;
        }
       if(currentState == Enemy.State.DIE){
           // 如果是第一次进入死亡状态，重置计时器
           if (!hasPlayedDeathAnimation) {
               deathAnimationTimer = 0f;
               if (animationTools != null && animationTools[4] != null) {
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
                    // 只有在死亡动画未完成时才渲染
                    if (deathAnimationTimer < DEATH_ANIMATION_DURATION) {
                        animationTools[4].render(batch, x, y, 0.1f, false, flip);
                    }
                }else if(currentState == Enemy.State.ATTACK){
                    animationTools[2].render(batch, x, y, 0.1f, false, flip);
                }else if(currentState == Enemy.State.CHASE){
                    animationTools[1].render(batch, x, y, 0.1f, true, flip);
                }else if(currentState == Enemy.State.HURT){
                    animationTools[3].render(batch, x, y, 0.1f, false, flip);
                }else{
                    currentAnimation.render(batch, x, y, 0.1f, true, flip);
                }
            }
            if (currentState == Enemy.State.DIE && hasPlayedDeathAnimation) {
                deathAnimationTimer += deltaTime;
                if(enemyState.getEnemyBody()!=null){
                    enemyState.freeBody();
                }
            }
        }
    }
}
