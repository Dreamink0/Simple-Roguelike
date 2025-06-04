package io.github.SimpleGame.Magic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
    protected AssetManager assetManager=new AssetManager();
    private Texture[] iconTexture;
    private Texture[] effcetTeture;
    private AnimationTool[] animations;
    public Boolean isActive = false;
    private Texture currentICON;
    private float stateTime = 0f;
    public void loadAssets(String className){
        if(className.equals("LightningMagic")){
            animations = ThunderTexture.getAnimation();
            iconTexture = ThunderTexture.getICONTexture();
            effcetTeture = ThunderTexture.getEffectTexture();
        }
        if(className.equals("DarkMagic")){
            animations = DarkTexture.getAnimation();
            iconTexture = DarkTexture.getICONTexture();
            effcetTeture = DarkTexture.getEffectTexture();
        }
    }

    public Texture[] getIconTexture() {
        return iconTexture;
    }

    public Texture[] getEffcetTeture() {
        return effcetTeture;
    }

    public AnimationTool[] getAnimations() {
        return animations;
    }
    public void render(SpriteBatch batch,float x,float y,Boolean flag, int renderIndex){
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        stateTime+=deltaTime;
        if(!isActive){currentICON = iconTexture[0];}
        else{currentICON = iconTexture[1];}
        float width = iconTexture[0].getWidth() / PIXELS_PER_METER;
        float height = iconTexture[0].getHeight() / PIXELS_PER_METER;
        if(!flag){
            batch.begin();
            batch.draw(iconTexture[0], x - width / 2, y - height / 2, width*4, height*4);
            batch.end();
        }else{
            if(renderIndex >= 0){
                float radius = 0.5f;
                float startAngle = 180f;
                float angleInDegrees = startAngle + renderIndex * 60f;
                float angleInRadians = (float) Math.toRadians(angleInDegrees);
                float uiX = Config.WORLD_WIDTH / 2+8f + radius * (float) cos(angleInRadians);
                float uiY = Config.WORLD_HEIGHT / 2-6f + radius * (float)Math.sin(angleInRadians);
                batch.begin();
                batch.draw(currentICON, uiX - width, uiY - height, width*2, height*2);
                batch.end();
            }
        }
    }
    public void Effectrender(SpriteBatch batch, float x, float y, Boolean flip){
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += Math.min(deltaTime, 0.25f);
        batch.begin();
        animations[0].render(batch,x,y, 0.1f, false,flip);
        if(animations.length>1){
            if(animations[0].isAnimationFinished()){
                int flag=1;
                if(flip){
                    flag=-1;
                }
                animations[1].render(batch,x+2*flag,y, 0.1f, true,flip);
            }
        }
        batch.end();
    }
    public void dispose(){
         for(AnimationTool animation:animations){
            animation.dispose();
        }
         for(Texture texture:iconTexture){
            texture.dispose();
        }
          for(Texture texture:effcetTeture){
            texture.dispose();
        }
          assetManager.dispose();
    }
}
