package io.github.SimpleGame.Resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Character.Enemy.EnemyGenerate;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.GUI.UI;
import io.github.SimpleGame.Item.Weapon;
import io.github.SimpleGame.Magic.Dark;
//import io.github.SimpleGame.Magic.Ice;
import io.github.SimpleGame.Magic.Thunder;
import io.github.SimpleGame.Tool.AnimationTool;

import static io.github.SimpleGame.Config.WORLD_HEIGHT;
import static io.github.SimpleGame.Config.WORLD_WIDTH;
import static io.github.SimpleGame.Resource.Game.batch;
import static io.github.SimpleGame.Resource.Game.player;


public class GameRender implements GameRenderHandler{
    private final UI ui;
    private EnemyGenerate enemyGenerate;
    public GameRender() {
        ui=new UI();
        ui.create();
        enemyGenerate = new EnemyGenerate();
    }
    @Override
    public void render(SpriteBatch batch,SpriteBatch UIbatch) {
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(),5);
        ui.render(UIbatch,player);
        EffectManager effectManager = EffectManager.getInstance();

        //在这里渲染各种东东
        effectManager.applyEffect(batch);
        batch.begin();
        player.filpCheck(player.getPlayerSprite(), player.getPlayerController(), batch).draw(batch);
        batch.end();
        effectManager.removeEffect(batch);

        // UI渲染也应用效果
        effectManager.applyEffect(UIbatch);
        UIbatch.begin();
        player.render(UIbatch,deltaTime);
        UIbatch.end();
        effectManager.removeEffect(UIbatch);
        enemyGenerate.update(Game.world,player,batch);
    }
    public void restart(){
        enemyGenerate.dispose();
        enemyGenerate = new EnemyGenerate();
    }
    public void dispose() {
    }
}
