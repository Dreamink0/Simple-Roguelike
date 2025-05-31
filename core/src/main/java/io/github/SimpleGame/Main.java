package io.github.SimpleGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.SimpleGame.Character.Enemy.Goblin;
import io.github.SimpleGame.Resource.Game;
import io.github.SimpleGame.Resource.ResourceManager;
import io.github.SimpleGame.Resource.SoundManager;
import io.github.SimpleGame.Tool.AnimationTool;
import io.github.SimpleGame.Tool.Listener;

import java.util.Random;

import static io.github.SimpleGame.Config.WORLD_HEIGHT;
import static io.github.SimpleGame.Config.WORLD_WIDTH;
import static io.github.SimpleGame.Resource.Game.player;
import static io.github.SimpleGame.Resource.Game.world;
import static io.github.SimpleGame.Resource.Game.batch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private Game Game;
    private Goblin[] Goblin;
    private AnimationTool animationTools=new AnimationTool();
    @Override
    public void create() {
        try {
            Gdx.app.debug("SimpleGame", "DebugMessage");
            Gdx.app.error("SimpleGame", "errorMessage");
            Game = new Game();
            Game.initialize();
            Game.Generation();
            Game.readPlayerData();
            Goblin =  new Goblin[150];
            Random random = new Random();
            for (int i = 0; i < Goblin.length; i++) {
                float randomX = random.nextFloat() * WORLD_WIDTH/2;
                float randomY = random.nextFloat() * WORLD_HEIGHT/2;
                Goblin[i] = new Goblin(world, player, randomX+25, randomY+25);
            }


        } catch (Exception e) {
            Gdx.app.error("SimpleGame", "Error during initialization: " + e.getMessage());
            throw new RuntimeException("Failed to initialize game", e);
        }
    }
    @Override
    public void render() {
        Game.render();
        batch.begin();
        for (Goblin goblin : Goblin) {
            goblin.render(batch, player);
        }

        batch.end();
        Listener.Bound(world,player);
    }
    @Override
    public void resize(int width, int height) {Game.resize(width, height);}
    @Override
    public void dispose() {
        SoundManager.dispose();
    }
}
