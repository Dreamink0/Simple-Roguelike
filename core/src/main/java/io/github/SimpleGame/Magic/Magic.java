package io.github.SimpleGame.Magic;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;

import java.util.ArrayList;
public abstract class Magic {
    //魔法属性
    protected String label;
    protected Player player;
    protected World world;
    protected MagicAnimation Animations;
    protected MagicAttribute Attributes;
    protected MagicHitbox Hitboxes;
    protected MagicState magicState;
    protected float x;
    protected float y;
    protected int count=0;
    protected static final ArrayList<Magic> equippedMagic = new ArrayList<>();
    protected static final float UI_SPACING = 1.5f; //武器之间的间距
    protected static int index = -1;
    protected Boolean isClosest = false;
    protected Boolean Active = false;
    protected Boolean isObtain=false;
    protected Boolean isFlip=false;
    protected boolean isActivating = false; // 新增状态标志
    protected Boolean flag = false;
    protected float StartX;
    protected float StartY;
    protected long time;
    protected float cooldownTimer=0;

    //必须实现的方法
    public abstract void render(SpriteBatch batch,SpriteBatch UIbatch,Player player);
    public Magic(World world,Player player,float x,float y){
        this.world=world;
        this.player=player;
        this.x=x;
        this.y=y;
        Animations = new MagicAnimation();
    }
    public boolean distance(Player player){
        float distance = (float) Math.sqrt(Math.pow(player.getX() - x, 2) + Math.pow(player.getY() - y, 2));
        isClosest = distance <= 2 && Gdx.input.isKeyJustPressed(Input.Keys.E);
        return isClosest;
    }
    public boolean dst(Player player){
        return (Math.sqrt(Math.pow(player.getX() - x, 2) + Math.pow(player.getY() - y, 2)) <= 2);
    }
    public float getDamage(){
         return Attributes.getDamage();
    }
}
