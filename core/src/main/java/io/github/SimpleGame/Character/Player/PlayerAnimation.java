package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Resource.EffectManager;
import io.github.SimpleGame.Resource.ResourceManager;
import io.github.SimpleGame.Resource.SoundManager;

import static io.github.SimpleGame.Resource.Game.*;

public class PlayerAnimation extends Player implements PlayerAnimationHandler {
    float stateTime=0f;
    private float accumulator = 0f;
    private Animation<TextureRegion> playerIdleAnimation;
    private Animation<TextureRegion> playerRunAnimation;
    private Animation<TextureRegion> playerHurtAnimation;
    private Animation<TextureRegion> playerAttackAnimation;
    private Animation<TextureRegion> playerDeadAnimation;
    private Animation<TextureRegion> playerRushAnimation;
    private Animation<TextureRegion> currentAnimation;
    public static EffectManager effectManager;
    Boolean flag=false;//检查是否读取了动画
    private float soundTimer=0f;
    private int deadcount = 1;
    public boolean wasDead = false;
    private float hurtTimer=0f;
    private float hurtCooldown = 0.01f; // 增加到0.5秒冷却时间
    private float lastHP = 20;

    @Override
    public PlayerController handleAction(PlayerController playerController, Player player, World world) {
        Vector2 vector2 = player.getPlayerController().getBody().getLinearVelocity();
        if(flag==false)load();
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        stateTime += deltaTime;
        boolean isAttacking = playerController.isAttacking();
        boolean isMoving = playerController.isMoving();
        Animation<TextureRegion> newAnimation = null;
        if (vector2.len2() > 45 * 45) {
            newAnimation = playerRushAnimation;
            if (newAnimation != currentAnimation) {
                stateTime = 0;
                currentAnimation = newAnimation;
            }
        }
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
            if (!isAttacking && !isDead) {  //只有在非死亡状态才能攻击
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
        hurtTimer -= deltaTime;
        float currentHP = player.getAttributeHandler().getHP();
        //受伤逻辑 - 仅当HP下降且不在冷却时触发
        if (currentHP < lastHP && hurtTimer <= 0 && !isDead && currentHP > 0) {
//            SoundManager.playSound("playerHit");
            effectManager.activateEffect(EffectManager.EffectType.HURT);
            hurtTimer = hurtCooldown;
            newAnimation = playerHurtAnimation;
        }
        lastHP = currentHP;

        // 碰撞框大小设置
        if (!isAttacking && !isDead) {
            setCollisionBoxSize(1.2f, 1.8f, player);
        } else if (!isDead) {
            setCollisionBoxSize(3.5f, 1.7f, player);
        }
        if (isDead && !wasDead) {
            SoundManager.playSound("dead");
            wasDead = true;
        } else if (!isDead) {
            wasDead = false;
        }

        //动画选择逻辑 - 优先级顺序：死亡 > 攻击 > 受伤 > 移动/冲刺 > 默认idle
        if (isDead) {
            newAnimation = playerDeadAnimation;
        } else if (isAttacking) {
            newAnimation = playerAttackAnimation;
        } else if (hurtTimer > 0 && hurtTimer < hurtCooldown) {
            hurtTimer = hurtCooldown;
            newAnimation = playerHurtAnimation;
        } else if (newAnimation == null) {
            newAnimation = isMoving ? playerRunAnimation : playerIdleAnimation;
        }

        // 动画切换处理
        if (newAnimation != currentAnimation) {
            stateTime = 0;
            currentAnimation = newAnimation;
        }

        // 获取当前帧并设置到精灵
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, !isDead); // 死亡动画只播放一次
        playerSprite.setRegion(currentFrame);

        // 根据不同动画类型设置精灵大小
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

        // 物理世界更新
        accumulator += deltaTime;
        while (accumulator >= Config.TIME_STEP) {
            world.step(Config.TIME_STEP, 6, 2);
            accumulator -= Config.TIME_STEP;
        }

        return playerController;
    }

    public void load() {
        this.playerSprite = ResourceManager.getInstance().getPlayerSprite();
        this.playerIdleAnimation = ResourceManager.getInstance().getPlayerIdleAnimation();
        this.playerRunAnimation = ResourceManager.getInstance().getPlayerRunAnimation();
        this.playerAttackAnimation = ResourceManager.getInstance().getPlayerAttackAnimation();
        this.playerDeadAnimation = ResourceManager.getInstance().getPlayerDeadAnimation();
        this.playerRushAnimation = ResourceManager.getInstance().getPlayerRushAnimation();
        this.playerHurtAnimation = ResourceManager.getInstance().getPlayerHurtAnimation();
        currentAnimation = playerIdleAnimation;
        stateTime = 0;
        flag = true; // 标记已加载
    }

    private void setCollisionBoxSize(float width, float height, Player player) {
        if (player.playerBody == null) return;

        // 清除现有碰撞体
        for (Fixture fixture : player.playerBody.getFixtureList()) {
            player.playerBody.destroyFixture(fixture);
        }

        // 创建新碰撞体
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
