package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Config;

public class PlayerTexture implements PlayerTextureHandler{
    private Texture HPtexture;
    private Texture MPtexture;
    private Texture DEFtexture;
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
                assetManager.load("UI/DEF/DEF" + i + ".png", Texture.class);
            }
            assetManager.finishLoading();
            get();
        }
    }

    @Override
    public void get() {
        if(attributeHandler.getHP()!=1||attributeHandler.getMP()!=1||attributeHandler.getDEF()!=1){
            HPtexture=MPtexture=DEFtexture=null;
        }
        int H=read(attributeHandler.getHP()); if(H <= 0) {H=1;}
        int M=read(attributeHandler.getMP()); if(M <= 0) {M=1;}
        int D=read(attributeHandler.getDEF()); if(D <= 0) {D=1;}
        HPtexture = assetManager.get("UI/HP/HP" + H + ".png", Texture.class);
        MPtexture = assetManager.get("UI/MP/MP" + M + ".png", Texture.class);
        DEFtexture = assetManager.get("UI/DEF/DEF" + D + ".png", Texture.class);
    }

    @Override
    public void render(SpriteBatch batch, float deltaTime) {
        float uiScale=0.1f;
//        batch.draw(DEFtexture, Config.WORLD_WIDTH/2-11f, Config.WORLD_HEIGHT/2-7.5f, DEFtexture.getWidth()*uiScale*1.2f,DEFtexture.getHeight() * uiScale);
        batch.draw(HPtexture, Config.WORLD_WIDTH/2-11f, Config.WORLD_HEIGHT/2-7f, HPtexture.getWidth() * uiScale*1.5f, HPtexture.getHeight() * uiScale);
        batch.draw(MPtexture, Config.WORLD_WIDTH/2-11f, Config.WORLD_HEIGHT/2-7.3f, MPtexture.getWidth() * uiScale*1.2f, MPtexture.getHeight() * uiScale*0.7f);
    }

    @Override
    public void dispose() {
        if(assetManager != null){assetManager.dispose();}
        if(HPtexture != null){HPtexture.dispose();}
        if(MPtexture != null){MPtexture.dispose();}
        if(DEFtexture != null){DEFtexture.dispose();}
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

    public Texture getDEFtexture() {return DEFtexture;}
}
