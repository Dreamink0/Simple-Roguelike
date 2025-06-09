package io.github.SimpleGame.Magic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Tool.AnimationTool;

import static com.badlogic.gdx.graphics.Color.WHITE;
import static io.github.SimpleGame.Config.LIGHTNING_MAGIC_ICON2_PATH;
import static io.github.SimpleGame.Config.PIXELS_PER_METER;
import static io.github.SimpleGame.Magic.Magic.UI_SPACING;
import static io.github.SimpleGame.Magic.Magic.index;
import static java.lang.Math.cos;

public class MagicAnimation {
    protected AssetManager assetManager = new AssetManager();
    private Texture[] iconTexture;
    private Texture[] effectTexture;
    private AnimationTool[] animations;
    public Boolean isActive = false;
    private Texture currentICON;
    private float stateTime = 0f;

    public void loadAssets(String className){
        if(className.equals("LightningMagic")){
            animations = ThunderTexture.getAnimation();
            iconTexture = ThunderTexture.getICONTexture();
            effectTexture = ThunderTexture.getEffectTexture();
        }
        if(className.equals("DarkMagic")){
            animations = DarkTexture.getAnimation();
            iconTexture = DarkTexture.getICONTexture();
            effectTexture = DarkTexture.getEffectTexture();
        }
        if(className.equals("IceMagic")){
//            animations = IceTexture.getAnimation();
//            effectTexture  = IceTexture.getEffectTexture();
        }
    }

    public Texture[] getIconTexture() {
        return iconTexture;
    }

    public AnimationTool[] getAnimations() {
        return animations;
    }

    public void render(SpriteBatch batch, float x, float y, Boolean flag, int renderIndex){
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        stateTime += deltaTime;

        if(!isActive) {
            currentICON = iconTexture[0];
        } else {
            currentICON = iconTexture[1];
        }

        float width = iconTexture[0].getWidth() / PIXELS_PER_METER;
        float height = iconTexture[0].getHeight() / PIXELS_PER_METER;

        if(!flag) {
            batch.begin();
            batch.draw(iconTexture[0], x - width / 2, y - height / 2, width * 4, height * 4);
            batch.end();
        } else {
            if(renderIndex >= 0) {
                float spacing = 0.0015f;
                float startX = Config.WORLD_WIDTH / 2 + 8f;
                float startY = Config.WORLD_HEIGHT / 2 - 9.1f;

                float uiX = startX + renderIndex * spacing;

                batch.begin();
                batch.setColor(1,1,1,0.75f);
                batch.draw(currentICON, uiX, startY, width * 2.7f, height * 2.7f);
                batch.end();
            }
        }
    }

    public void dispose(){
        if (animations != null) {
            for(AnimationTool animation : animations){
                animation.dispose();
            }
        }

        if (iconTexture != null) {
            for(Texture texture : iconTexture){
                texture.dispose();
            }
        }

        if (effectTexture != null) {
            for(Texture texture : effectTexture){
                texture.dispose();
            }
        }

        assetManager.dispose();
    }
}
