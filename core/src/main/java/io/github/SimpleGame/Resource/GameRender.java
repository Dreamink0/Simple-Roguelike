package io.github.SimpleGame.Resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Item.Weapon;
import io.github.SimpleGame.Magic.GravityMagic;
import io.github.SimpleGame.Magic.LightningMagic;
import io.github.SimpleGame.Tool.Listener;

import java.util.ArrayList;

import static io.github.SimpleGame.Config.WORLD_HEIGHT;
import static io.github.SimpleGame.Config.WORLD_WIDTH;
import static io.github.SimpleGame.Resource.Game.player;


public class GameRender implements GameRenderHandler{
    private Weapon item;
    private Weapon item2;
    private LightningMagic lightningMagic;
    private GravityMagic gravityMagic;

    public GameRender() {
        item = new Weapon(Game.world, WORLD_WIDTH, WORLD_HEIGHT + 5, 1f);
         item2 = new Weapon(Game.world, WORLD_WIDTH+10, WORLD_HEIGHT + 10, 1f);
        lightningMagic = new LightningMagic();
        lightningMagic.magicCreate(Game.world, WORLD_WIDTH+10, WORLD_HEIGHT+6);
        gravityMagic = new GravityMagic();
    }
    @Override
    public void render(SpriteBatch batch,SpriteBatch UIbatch) {
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(),5);
        //在这里渲染各种东东
        item2.render(batch, UIbatch, player);
        lightningMagic.magicObtain(Game.batch,Game.UIbatch,player);
        batch.begin();
        player.filpCheck(player.getPlayerSprite(), player.getPlayerController(), batch).draw(batch);
        lightningMagic.magicRender(Game.batch, player);
        gravityMagic.magicRender(Game.batch, player);
        batch.end();
        item.render(batch, UIbatch, player);
        UIbatch.begin();
        player.render(UIbatch,deltaTime);
        UIbatch.end();
    }
    public void dispose() {
        lightningMagic.dispose();
        gravityMagic.dispose();
    }
}
