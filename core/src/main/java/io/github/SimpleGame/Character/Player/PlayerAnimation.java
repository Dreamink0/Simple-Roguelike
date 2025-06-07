package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Resource.EffectManager;
import io.github.SimpleGame.Resource.ResourceManager;
import io.github.SimpleGame.Resource.SoundManager;
import io.github.SimpleGame.Tool.AnimationTool;

import static io.github.SimpleGame.Resource.Game.UIbatch;
import static io.github.SimpleGame.Resource.Game.batch;

public class PlayerAnimation extends Player implements PlayerAnimationHandler {
    float stateTime=0f;
    private float accumulator = 0f;
    private Animation<TextureRegion> playerIdleAnimation;
    private Animation<TextureRegion> playerRunAnimation;
    private Animation<TextureRegion> playerAttackAnimation;
    private Animation<TextureRegion> playerDeadAnimation;
    private Animation<TextureRegion> currentAnimation;
    public static EffectManager effectManager;
    Boolean flag=false;//检查是否读取了动画
    private float soundTimer=0f;
    private int deadcount = 1;

    @Override
    public PlayerController handleAction(PlayerController playerController, Player player, World world) {
        if(flag==false)load();
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        stateTime += deltaTime;
        boolean isAttacking = playerController.isAttacking();
        boolean isMoving = playerController.isMoving();
        Animation<TextureRegion> newAnimation;
        //检查玩家是否死亡
        boolean isDead = player.getAttributeHandler().getHP() <= 0;
        effectManager = EffectManager.getInstance();
        if(deadcount==1){
            effectManager.clearAllEffects();
        }
        if(deadcount == 0){
            effectManager.instantDeathEffect();
        }
        if(isDead){
            deadcount--;
            playerController.getBody().setLinearVelocity(0, 0);
            playerController.getBody().setAngularVelocity(0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.J) && player.attackCooldownTimer <= 0) {
            if (!isAttacking && !isDead) {  // 只有在非死亡状态才能攻击
                if (soundTimer <= 0) {
                    SoundManager.playSound("playerHit");
                    soundTimer = 0.5f;
                }
                playerController.startAttack();
                isAttacking = true;
                player.attackCooldownTimer = player.attackCooldown;
            }
        }

        player.attackCooldownTimer -= deltaTime;
        soundTimer -= deltaTime;

        if (!isAttacking && !isDead) {  //死亡时不处理攻击和移动的碰撞
            setCollisionBoxSize(1.2f, 1.8f, player);
        } else if (!isDead) {
            setCollisionBoxSize(3.5f, 1.7f, player);
        }

        if (isDead) {
            // 设置死亡动画
            newAnimation = playerDeadAnimation;
        } else if (isAttacking) {
            newAnimation = playerAttackAnimation;
        } else {
            newAnimation = isMoving ? playerRunAnimation : playerIdleAnimation;
        }

        if (newAnimation != currentAnimation) {
            stateTime = 0;
            currentAnimation = newAnimation;
        }

        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, !isDead); // 死亡动画只播放一次
        playerSprite.setRegion(currentFrame);

        // 死亡时保持死亡帧大小，否则根据动画类型设置大小
        if (!isDead) {
            if (isAttacking) {
                playerSprite.setSize(
                    (2.1f * currentFrame.getRegionWidth() / Config.PIXELS_PER_METER) * Config.PLAYER_SCALE,
                    (2 * currentFrame.getRegionHeight() / Config.PIXELS_PER_METER) * Config.PLAYER_SCALE
                );
            } else {
                playerSprite.setSize(
                    (2 * currentFrame.getRegionWidth() / Config.PIXELS_PER_METER) * Config.PLAYER_SCALE,
                    (2 * currentFrame.getRegionHeight() / Config.PIXELS_PER_METER) * Config.PLAYER_SCALE
                );
            }
        } else {
            // 固定为死亡动画最后一帧的尺寸
            playerSprite.setSize(
                (2 * currentFrame.getRegionWidth() / Config.PIXELS_PER_METER) * Config.PLAYER_SCALE,
                (2 * currentFrame.getRegionHeight() / Config.PIXELS_PER_METER) * Config.PLAYER_SCALE
            );
        }

        accumulator += deltaTime;
        while (accumulator >= Config.TIME_STEP) {
            world.step(Config.TIME_STEP, 6, 2);
            accumulator -= Config.TIME_STEP;
        }

        return playerController;
    }
    public void load(){
        this.playerSprite = ResourceManager.getInstance().getPlayerSprite();
        this.playerIdleAnimation = ResourceManager.getInstance().getPlayerIdleAnimation();
        this.playerRunAnimation = ResourceManager.getInstance().getPlayerRunAnimation();
        this.playerAttackAnimation = ResourceManager.getInstance().getPlayerAttackAnimation();
        this.playerDeadAnimation = ResourceManager.getInstance().getPlayerDeadAnimation();
    }

    private void setCollisionBoxSize(float width, float height,Player player) {
        if (player.playerBody == null) return;

        for (Fixture fixture : player.playerBody.getFixtureList()) {
            player.playerBody.destroyFixture(fixture);
        }

        PolygonShape newShape = new PolygonShape();
        newShape.setAsBox(width, height);

        FixtureDef newFixtureDef = new FixtureDef();
        newFixtureDef.shape = newShape;
        newFixtureDef.density = 1;
        newFixtureDef.friction = 0.5f;
        newFixtureDef.restitution = 1.5f;

        player.playerBody.createFixture(newFixtureDef);
        newShape.dispose();
    }
}
