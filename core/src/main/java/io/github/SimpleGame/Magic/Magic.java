package io.github.SimpleGame.Magic;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;

import java.util.ArrayList;
public abstract class Magic {
    //必须实现的方法
    public abstract void Magic_create(World world,float x,float y);
    public abstract void Magic_obtain(SpriteBatch batch, Player player);
    public abstract void Magic_render(SpriteBatch batch,Player player);
}
//魔法物品栏管理,后续实现
class MagicManager{
    private ArrayList<Magic> magics = new ArrayList<Magic>();

}
