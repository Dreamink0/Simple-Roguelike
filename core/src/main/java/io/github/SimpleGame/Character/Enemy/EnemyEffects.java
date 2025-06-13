package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Tool.AnimationTool;

import static io.github.SimpleGame.Resource.Game.player;

public class EnemyEffects extends EnemyAnimation{
    private int ID;
    private static final String[] effectPaths = {
        "Effects/hitEffects.png",
        "Effects/Goblinblood.png",
    };
    private Texture texture;
    public void load(int ID){
        this.ID = ID;
        for (String effectPath : effectPaths) {
            assetManager.load(effectPath, Texture.class);
        }
        assetManager.finishLoading();
        effectsAnimations = new AnimationTool();
        get(ID);
    }
    private void get(int ID){
        if(ID>=0&&ID<effectPaths.length){
            texture = assetManager.get(effectPaths[ID], Texture.class);
            if(ID==0){effectsAnimations.create("Hit", texture, 1, 4, 0.1f);}
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
        if(ID==1){
            float offsetX = flip ? 3f : -3f;
            effectsAnimations.render(batch, x + offsetX, y - 0.4f, 0.1f, true, !flip);
        }
        if(ID>1){
            effectsAnimations.render(batch, x, y, 0.2f, true, !flip);
        }
    }

    public Texture getTexture() {
        return texture;
    }
}
