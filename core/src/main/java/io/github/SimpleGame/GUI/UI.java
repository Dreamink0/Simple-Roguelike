package io.github.SimpleGame.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Resource.SoundManager;
import io.github.SimpleGame.Tool.AnimationTool;

import static io.github.SimpleGame.Item.WeaponRender.UI_SPACING;

public class UI {
    private AssetManager assetManager =new AssetManager();
    private Texture Toptexture = null;
    private Texture Bottomtexture = null;
    private Texture Bottomtexture2 = null;
    private Texture Bottomtexture3 =null;
    private Texture Bottomtexture4 =null;
    private Texture Bottomtexture5 =null;
    private Texture Bottomtexture6 =null;
    private Texture Bottomtexture7 =null;
    private AnimationTool animationTool2;
    private AnimationTool animationTool;
    public void create(){
        String[] Path={
            "UI/GUI2/P1.png",
            "UI/GUI2/P2.png",
            "UI/GUI2/P3.png",
            "UI/GUI/UI/BlackMediumCircleBoxWithBorder_27x27.png",
            "UI/Rsheet.png",
            "Sprites/BasePlayer/Player-dead.png",
            "Sprites/BasePlayer/DIED-sheet.png",
            "UI/BlackBar.png"
        };
        for (String s : Path) {
            assetManager.load(s, Texture.class);
        }
        assetManager.finishLoading();
        Toptexture = assetManager.get(Path[0], Texture.class);
        Bottomtexture = assetManager.get(Path[1], Texture.class);
        Bottomtexture2 =assetManager.get(Path[2], Texture.class);
        Bottomtexture3=assetManager.get(Path[3],Texture.class);
        Bottomtexture4=assetManager.get(Path[4],Texture.class);
        Bottomtexture5=assetManager.get(Path[5],Texture.class);
        Bottomtexture6=assetManager.get(Path[6],Texture.class);
        Bottomtexture7=assetManager.get(Path[7],Texture.class);
        animationTool = new AnimationTool();
        animationTool2 = new AnimationTool();
        animationTool.create("B4",Bottomtexture4,1,20,0.1f);
        animationTool2.create("DIED",Bottomtexture6,1,5,0.2f);
    }
    public void render(SpriteBatch batch,Player player){
        batch.begin();
        batch.setColor(1,0, 0, 0.5f);
        if (player.getAttributeHandler().getHP() > 0) {
            animationTool.render(batch, Config.WORLD_WIDTH/2-10, Config.WORLD_HEIGHT/2-7, 0.05f, 0.06f, true);
        }else{
            batch.setColor(1,0,0,1f);
            AnimationTool NEWanimationTool = new AnimationTool();
            NEWanimationTool.create("B5",Bottomtexture5,1,20,0.1f);
            NEWanimationTool.render(batch, Config.WORLD_WIDTH/2-10, Config.WORLD_HEIGHT/2-7, 0.05f, 0.06f, true);
        }
        batch.setColor(1,1,1,1);
        if(player.getAttributeHandler().getHP() < 0){
            batch.setColor(0, 0, 0, 0.5f);
            batch.draw(Bottomtexture7, Config.WORLD_WIDTH/2-13, Config.WORLD_HEIGHT/2-3f, 40, 6);
            batch.setColor(1,0, 0, 0.5f);
            animationTool2.render(batch, Config.WORLD_WIDTH/2, Config.WORLD_HEIGHT/2, 0.07f, false);
        }
        batch.end();
    }
}
