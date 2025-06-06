package io.github.SimpleGame.GUI;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;

import static io.github.SimpleGame.Item.WeaponRender.UI_SPACING;
import static io.github.SimpleGame.Item.WeaponRender.getWeaponIndex;

public class UI {
    private static final AssetManager assetManager =new AssetManager();
    private static Texture Toptexture = null;
    private static Texture Bottomtexture = null;
    private static Texture Bottomtexture2 = null;
    private static Texture Bottomtexture3 =null;
    public static void create(){
        String[] Path={
            "UI/GUI2/P1.png",
            "UI/GUI2/P2.png",
            "UI/GUI2/P3.png",
            "UI/GUI/UI/BlackMediumCircleBoxWithBorder_27x27.png"
        };
        for (String s : Path) {
            assetManager.load(s, Texture.class);
        }
        assetManager.finishLoading();
        Toptexture = assetManager.get(Path[0], Texture.class);
        Bottomtexture = assetManager.get(Path[1], Texture.class);
        Bottomtexture2 =assetManager.get(Path[0], Texture.class);
        Bottomtexture3=assetManager.get(Path[3],Texture.class);
    }
    public static void render(SpriteBatch batch){
        batch.begin();
        TextureRegion region = new TextureRegion(Bottomtexture2);
        batch.draw(region,
            Config.WORLD_WIDTH/2+3f, Config.WORLD_HEIGHT/2-15f,
            0, 0,
            Config.WORLD_WIDTH/2, Config.WORLD_HEIGHT/2,
            1f, 1f, 5.0f);
        batch.end();
    }
}
