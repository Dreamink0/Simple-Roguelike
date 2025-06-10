package io.github.SimpleGame.Resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private Weapon item;
    private Weapon item2;
//    private Thunder thunder;
//    private Thunder thunder2;
//    private Dark dark;
    private UI ui;
    public GameRender() {
//        dark = new Dark(Game.world, player, WORLD_WIDTH+15,WORLD_HEIGHT+5);
//        thunder = new Thunder(Game.world, player, WORLD_WIDTH+10,WORLD_HEIGHT+2);
        item = new Weapon(Game.world, WORLD_WIDTH+10, WORLD_HEIGHT, 1f);
        item2 = new Weapon(Game.world, WORLD_WIDTH-2, WORLD_HEIGHT, 1f);
//        thunder2 = new Thunder(Game.world, player, WORLD_WIDTH+5,WORLD_HEIGHT+2);
        ui=new UI();
        ui.create();
    }
    @Override
    public void render(SpriteBatch batch,SpriteBatch UIbatch) {
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(),5);
        ui.render(UIbatch,player);
        EffectManager effectManager = EffectManager.getInstance();

        //在这里渲染各种东东
        effectManager.applyEffect(batch);
//        dark.render(batch, UIbatch, player);
//        thunder.render(batch, UIbatch, player);
//        thunder2.render(batch, UIbatch, player);
        batch.begin();
        player.filpCheck(player.getPlayerSprite(), player.getPlayerController(), batch).draw(batch);
        batch.end();
        item2.render(batch, UIbatch, player);
        item.render(batch, UIbatch, player);
        effectManager.removeEffect(batch);

        // UI渲染也应用效果
        effectManager.applyEffect(UIbatch);
        UIbatch.begin();
        player.render(UIbatch,deltaTime);
        UIbatch.end();
        effectManager.removeEffect(UIbatch);
    }
    public void dispose() {
        item.dispose();
        item2.dispose();
    }
}
