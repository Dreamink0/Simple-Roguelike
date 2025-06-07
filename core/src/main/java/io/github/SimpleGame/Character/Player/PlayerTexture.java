package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Tool.AnimationTool;

public class PlayerTexture implements PlayerTextureHandler{
    private Texture HPtexture;
    private Texture MPtexture;
    private AssetManager assetManager;
    private PlayerAttributeHandler attributeHandler;

    public PlayerTexture(PlayerAttributeHandler attributeHandler) {
        this.attributeHandler = attributeHandler;
    }
    @Override
    public void load() {
        if(assetManager == null){
            assetManager = new AssetManager();
            for (int i = 1; i <= 5; i++) {
                assetManager.load("UI/HP/HP" + i + ".png", Texture.class);
                assetManager.load("UI/MP/MP" + i + ".png", Texture.class);
            }
            assetManager.finishLoading();
            get();
        }
    }

    @Override
    public void get() {
        if(attributeHandler.getHP()!=1||attributeHandler.getMP()!=1){
            HPtexture=MPtexture=null;
        }
        int H=read(attributeHandler.getHP()); if(H <= 0) {H=1;}
        int M=read(attributeHandler.getMP()); if(M <= 0) {M=1;}
        HPtexture = assetManager.get("UI/HP/HP" + H + ".png", Texture.class);
        MPtexture = assetManager.get("UI/MP/MP" + M + ".png", Texture.class);
    }

    @Override
    public void render(SpriteBatch batch, float deltaTime) {
        float uiScale=0.1f;
        batch.setColor(1, 1, 1, 0.95f);
        batch.draw(HPtexture, Config.WORLD_WIDTH/2-12f, Config.WORLD_HEIGHT/2+7.5f, HPtexture.getWidth() * uiScale*1.5f, HPtexture.getHeight() * uiScale*1.5f);
        batch.draw(MPtexture, Config.WORLD_WIDTH/2-12f, Config.WORLD_HEIGHT/2+7f, MPtexture.getWidth() * uiScale*1f, MPtexture.getHeight() * uiScale*1f);
    }

    @Override
    public void dispose() {
        if(assetManager != null){assetManager.dispose();}
        if(HPtexture != null){HPtexture.dispose();}
        if(MPtexture != null){MPtexture.dispose();}
        assetManager = null;
    }
    private int read(float percent){
        int flag=0;
        if(percent == 0){flag=1;}
        if(percent>=0.25  && percent <= 0.5){flag=2;}
        if(percent >= 0.5 && percent <=0.75){flag=3;}
        if (percent >= 0.75 && percent <= 0.9){flag=4;}
        if (percent >= 0.9){flag=5;}
        return flag;
    }
    public Texture getHPtexture() {return HPtexture;}

    public Texture getMPtexture() {return MPtexture;}
}
