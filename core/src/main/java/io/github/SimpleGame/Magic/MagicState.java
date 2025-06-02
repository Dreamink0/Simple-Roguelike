package io.github.SimpleGame.Magic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Item.Tips;

import static io.github.SimpleGame.Magic.Magic.equippedMagic;
import static io.github.SimpleGame.Magic.Magic.index;
import static io.github.SimpleGame.Resource.Game.world;

public class MagicState {
    private MagicAttribute attribute;
    private MagicAnimation animation;
    private MagicHitbox hitbox;
    private float x,y;
    private float cooldownTimer=0;
    private float cooldown;
    private float duration;
     public  MagicState(MagicAttribute attribute,MagicAnimation animation,MagicHitbox hitbox){
        this.attribute = attribute;
        this.animation = animation;
        this.hitbox = hitbox;
        this.cooldown = attribute.getCooldown();
        this.duration = attribute.getDuration();
    }
}
