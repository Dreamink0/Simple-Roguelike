package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

        if (isAttacking) {newAnimation = playerAttackAnimation;
        } else {newAnimation = isMoving ? playerRunAnimation : playerIdleAnimation;}

        if (newAnimation != currentAnimation) {stateTime = 0;currentAnimation = newAnimation;}

        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        playerSprite.setRegion(currentFrame);
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
}
