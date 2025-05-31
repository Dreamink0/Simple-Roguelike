package io.github.SimpleGame.Item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Tool.Listener;

import java.util.Random;

import static io.github.SimpleGame.Config.PIXELS_PER_METER;

public class Weapon {
    private Texture texture;
    private AssetManager assetManager;
    private  World world;
    private WeaponHitBox weaponHitBox;
    private WeaponRender weaponRender;
    public Weapon(World world, float x, float y, float scale) {
        this.world = world;
        assetManager = new AssetManager();
        Random random = new Random();
        int temp = random.nextInt(40);
        assetManager.load("Items/Weapon/Iicon_32_" + temp + ".png", Texture.class);
        assetManager.finishLoading();
        this.texture = assetManager.get("Items/Weapon/Iicon_32_" + temp + ".png", Texture.class);
        weaponHitBox = new WeaponHitBox(texture,world);
        weaponHitBox.create(x,y,scale);
        weaponRender = new WeaponRender(weaponHitBox);
    }
    public void render(SpriteBatch batch,SpriteBatch UIbatch,Player player) {
         weaponRender.render(batch,UIbatch,player);
    }
    public void dispose(){
        weaponRender.dispose();
    }
}
