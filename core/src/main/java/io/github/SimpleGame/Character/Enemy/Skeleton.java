package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Tool.AnimationTool;

import static io.github.SimpleGame.Resource.Game.player;
import static io.github.SimpleGame.Resource.Game.world;

public class Skeleton extends Enemy{
    private SkeletonAnimation SKanimation;
    public Skeleton(World world, Player player, float x, float y) {
        super(world, player, x, y);
        SKanimation=new SkeletonAnimation();
        SKanimation.load("Skeleton");
        enemyPhysic = new EnemyPhysic(x, y, 0.5f, 2f);
        Body enemyBody = enemyPhysic.createBody(enemyPhysic.getEnemyBody());
        enemyBody.setUserData(this);
        attribute = new EnemyAttribute(25, 5, 4, 15, 0.5f);
        enemyState = new EnemyState(enemyBody, currentState, player, enemyPhysic, attribute);
    }
    @Override
    public void render(SpriteBatch batch, Player player) {
        SKanimation.render(batch,enemyState,player);
        enemyState.update(batch);
    }
    @Override
    public void dispose() {
        if( animation != null){
            animation.dispose();
        }
        if(SKanimation!= null){
            SKanimation.dispose();
        }
    }
}
class SkeletonAnimation extends EnemyAnimation{
    public SkeletonAnimation() {
        super();
    }
    @Override
    public void load(String className) {
        super.load(className);
    }
    @Override
    public void render(SpriteBatch batch, EnemyState enemyState, Player player) {
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(),5);
        Enemy.State currentState = enemyState.currentState;
        AnimationTool currentAnimation;
        if(enemyState.getEnemyBody()!=null){
            x = enemyState.getEnemyBody().getPosition().x;
            y = enemyState.getEnemyBody().getPosition().y;
            flip = player.getX() < x;
        }
        if(currentState == Enemy.State.DIE){
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
                    effectsAnimations[0].render(batch, x, y, 0.15f, true, flip);
                    if (deathAnimationTimer < DEATH_ANIMATION_DURATION) {
                        float offsetX = flip ? -0.5f : 0.5f;
                        animationTools[4].render(batch, x + offsetX, y, 0.15f, false, flip);
                    }
                } else if (currentState == Enemy.State.ATTACK) {
                    float offsetX = flip ? -1f : 1f;
                    animationTools[2].render(batch, x + offsetX, y, 0.15f, true, flip);
                } else if (currentState == Enemy.State.CHASE) {
                    float offsetX = flip ? -1f : 1f;
                    animationTools[1].render(batch, x + offsetX, y, 0.15f, true, flip);
                } else if (currentState == Enemy.State.HURT) {
                    float offsetX = flip ? -1f : 1f;
                    effectsAnimations[0].render(batch, x, y, 0.15f, false, flip);
                    animationTools[3].render(batch, x + offsetX, y, 0.15f, true, flip);
                } else {
                    float offsetX = flip ? -1f : 1f;
                    currentAnimation.render(batch, x + offsetX, y, 0.15f, true, flip);
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
}
