package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Tool.AnimationTool;

import static io.github.SimpleGame.Resource.Game.player;

public class EnemyEffects extends EnemyAnimation{
    private static final String[] effectPaths = {
        "Effects/hitEffects.png",
        "Effects/Goblinblood.png",
    };
    private Texture texture;
    public void load(int ID){
        for (String effectPath : effectPaths) {
            assetManager.load(effectPath, Texture.class);
        }
        assetManager.finishLoading();
        effectsAnimations = new AnimationTool();
        get(ID);
    }
    private void get(int ID){
        if(ID>0&&ID<effectPaths.length){
            texture = assetManager.get(effectPaths[ID], Texture.class);
            if(ID==1){effectsAnimations.create("GobBlood", texture, 1, 6, 0.1f);}
        }else{
            texture = assetManager.get(effectPaths[0], Texture.class);
            effectsAnimations.create("Hit", texture, 1, 4, 0.2f);
        }
    }
    public void render(SpriteBatch batch, EnemyState enemyState, Player player){
        if(enemyState.getEnemyBody()!=null){
            x = enemyState.getEnemyBody().getPosition().x;
            y = enemyState.getEnemyBody().getPosition().y;
        }
        if(ID==0){
            if(flip){
                effectsAnimations.render(batch, x+3f, y-0.4f, 0.1f, true, false);
            }else{
                effectsAnimations.render(batch, x-3f, y-0.4f, 0.1f, true, true);
            }
        }else{
            effectsAnimations.render(batch, x, y, 0.1f, true, !flip);
        }
    }

    public Texture getTexture() {
        return texture;
    }
}
