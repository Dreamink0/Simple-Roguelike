package io.github.SimpleGame.Resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Item.Weapon;
import io.github.SimpleGame.Magic.Thunder;
import io.github.SimpleGame.Tool.Listener;

import java.util.ArrayList;

import static io.github.SimpleGame.Config.WORLD_HEIGHT;
import static io.github.SimpleGame.Config.WORLD_WIDTH;
import static io.github.SimpleGame.Resource.Game.player;


public class GameRender implements GameRenderHandler{
    private Weapon item;
    private Weapon item2;
    private Thunder thunder;
    private Thunder thunder2;
    private Thunder thunder3;
    private Thunder thunder4;

    public GameRender() {
        thunder = new Thunder(Game.world, player, WORLD_WIDTH+2,WORLD_HEIGHT+2);
        item = new Weapon(Game.world, WORLD_WIDTH, WORLD_HEIGHT + 5, 1f);
        item2 = new Weapon(Game.world, WORLD_WIDTH+10, WORLD_HEIGHT + 10, 1f);
    }
    @Override
    public void render(SpriteBatch batch,SpriteBatch UIbatch) {
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(),5);
        //在这里渲染各种东东
        thunder.render(batch, UIbatch, player);
        item2.render(batch, UIbatch, player);
        batch.begin();
        player.filpCheck(player.getPlayerSprite(), player.getPlayerController(), batch).draw(batch);
        batch.end();
        item.render(batch, UIbatch, player);
        UIbatch.begin();
        player.render(UIbatch,deltaTime);
        UIbatch.end();
    }
    public void dispose() {
    }
}
