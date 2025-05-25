package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Resource.ResourceManager;

public class PlayerAniamtion extends Player implements PlayerAnimationHandler {
    float stateTime=0f;
    private float accumulator = 0f;
    private Animation<TextureRegion> playerIdleAnimation;
    private Animation<TextureRegion> playerRunAnimation;
    private Animation<TextureRegion> playerAttackAnimation;
    private Animation<TextureRegion> currentAnimation;
    Boolean flag=false;//检查是否读取了动画
    @Override
    public PlayerController handleAction(PlayerController playerController, Player player, World world) {
        if(flag==false)load();

        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        stateTime += deltaTime;

        boolean isAttacking = playerController.isAttacking();
        boolean isMoving = playerController.isMoving();
        Animation<TextureRegion> newAnimation;

        if(Gdx.input.isKeyJustPressed(Input.Keys.J)) {playerController.startAttack(); isAttacking = true;}
        if(!isAttacking){
            setCollisionBoxSize(1.2f,1.8f,player);
        }else{
            setCollisionBoxSize(4f,3f,player);
        }
        if (isAttacking) {newAnimation = playerAttackAnimation;
        } else {newAnimation = isMoving ? playerRunAnimation : playerIdleAnimation;}

        if (newAnimation != currentAnimation) {stateTime = 0;currentAnimation = newAnimation;}

        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        playerSprite.setRegion(currentFrame);

        // 根据不同动画调整尺寸
        if (isAttacking) {
            // 攻击动画时使用较小的尺寸
            playerSprite.setSize(
                (2 * currentFrame.getRegionWidth() / Config.PIXELS_PER_METER) * Config.PLAYER_SCALE,
                (2 * currentFrame.getRegionHeight() / Config.PIXELS_PER_METER) * Config.PLAYER_SCALE
            );
        } else {
            // 其他动画使用标准尺寸
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
        newFixtureDef.restitution = 2f;

        player.playerBody.createFixture(newFixtureDef);
        newShape.dispose();
    }
}
