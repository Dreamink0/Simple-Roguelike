package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

public interface PlayerAnimationHandler {
    PlayerController handleAction(PlayerController playerController, Player player, World world);
}
