package io.github.SimpleGame.Magic;
//定义魔法类逻辑
//动画
//贴图
//碰撞箱
//.......
//各种魔法子类
//魔法的话，我将用ID来标记是什么魔法的函数
//需要实现方法：
//创建魔法
//魔法的拾取
//魔法的释放
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;

import java.util.ArrayList;

public abstract class Magic {
    private TextureRegion MagicTextureAltlas;
    private Texture MagicTexture;
    private float Magicduration;
    private BoundingBox HitBox;
    private Body Body;
    private AssetManager assetManager;
    private World world;
    //必须实现的方法
    public abstract void Magic_create(World world,float x,float y);
    public abstract void Magic_obtain(SpriteBatch batch, Player player);
    public abstract void Magic_render(SpriteBatch batch,Player player);

    //所有属性的Getter和Setter
    public TextureRegion getMagicTextureAltlas() {return MagicTextureAltlas;}

    public void setMagicTextureAltlas(TextureRegion magicTextureAltlas) {MagicTextureAltlas = magicTextureAltlas;}

    public Texture getMagicTexture() {return MagicTexture;}

    public void setMagicTexture(Texture magicTexture) {MagicTexture = magicTexture;}

    public float getMagicduration() {return Magicduration;}

    public void setMagicduration(float magicduration) {Magicduration = magicduration;}

    public BoundingBox getHitBox() {return HitBox;}

    public void setHitBox(BoundingBox hitBox) {HitBox = hitBox;}

    public Body getBody() {return Body;}

    public void setBody(Body body) {Body = body;}

    public AssetManager getAssetManager() {return assetManager;}

    public void setAssetManager(AssetManager assetManager) {this.assetManager = assetManager;}

    public World getWorld() {return world;}

    public void setWorld(World world) {this.world = world;}
}
//魔法物品栏管理,后续实现
class MagicManager{
    ArrayList<Magic> magics = new ArrayList<Magic>();
}
