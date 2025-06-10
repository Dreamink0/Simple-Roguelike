package io.github.SimpleGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.SimpleGame.Character.Enemy.EnemyGenerate;
import io.github.SimpleGame.Character.Enemy.Frog;
import io.github.SimpleGame.Character.Enemy.NightBorne;
import io.github.SimpleGame.Resource.Game;
import io.github.SimpleGame.Resource.SoundManager;
import io.github.SimpleGame.Tool.Listener;

import static io.github.SimpleGame.Config.WORLD_HEIGHT;
import static io.github.SimpleGame.Config.WORLD_WIDTH;
import static io.github.SimpleGame.Resource.Game.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private Game Game;
    private EnemyGenerate[] enemyGenerate;
    private NightBorne nightBorne;
    @Override
    public void create() {
        try {
            Gdx.app.debug("SimpleGame", "DebugMessage");
            Gdx.app.error("SimpleGame", "errorMessage");
            Game = new Game();
            Game.initialize();
            Game.Generation();
            Game.readPlayerData();
            nightBorne = new NightBorne(world, player, WORLD_WIDTH/2+10, WORLD_HEIGHT+5);
            enemyGenerate = new EnemyGenerate[1];
            for(int i = 0; i < enemyGenerate.length; i++){
                enemyGenerate[i] = new EnemyGenerate();
                enemyGenerate[i].addEnemy(world, player, WORLD_WIDTH/2+i*2, WORLD_HEIGHT+i);
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
        nightBorne.render(batch,player);
        for (EnemyGenerate generate : enemyGenerate) {
            generate.render(batch, player);
        }
        batch.end();
        Listener.Bound(world, player);
    }
    @Override
    public void resize(int width, int height) {
        Game.resize(width, height);
    }
    @Override
    public void dispose() {
        SoundManager.dispose();
        Game.dispose();
    }
}
