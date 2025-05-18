package io.github.SimpleGame.Magic;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;

import java.util.ArrayList;
public abstract class Magic {
    //必须实现的方法
    public abstract void magicCreate(World world,float x,float y);
    public abstract void magicObtain(SpriteBatch batch, Player player);
    public abstract void magicRender(SpriteBatch batch,Player player);
}
//魔法物品栏管理,后续实现
class MagicManager{
    private ArrayList<Magic> magicArrayList = new ArrayList<Magic>();
    public void addMagic(Magic magic)
    {
        magicArrayList.add(magic);
    }
    public void removeMagic(Magic magic)
    {
        magicArrayList.remove(magic);
    }
    public void render(SpriteBatch batch,Player player)
    {
        for(Magic magic:magicArrayList)
        {
            magic.magicRender(batch,player);
        }
    }
}
