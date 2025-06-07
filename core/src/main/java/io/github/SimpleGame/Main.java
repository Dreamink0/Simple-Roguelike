package io.github.SimpleGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.SimpleGame.Character.Enemy.EnemyGenerate;
import io.github.SimpleGame.GUI.UI;
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
    @Override
    public void create() {
        try {
            Gdx.app.debug("SimpleGame", "DebugMessage");
            Gdx.app.error("SimpleGame", "errorMessage");
            Game = new Game();
            Game.initialize();
            Game.Generation();
            Game.readPlayerData();
            enemyGenerate = new EnemyGenerate[15];
//            for(int i = 0; i < enemyGenerate.length; i++){
//                enemyGenerate[i] = new EnemyGenerate();
//                enemyGenerate[i].addEnemy(world, player, WORLD_WIDTH/2+i*2, WORLD_HEIGHT+i);
//            }
        } catch (Exception e) {
            Gdx.app.error("SimpleGame", "Error during initialization: " + e.getMessage());
            throw new RuntimeException("Failed to initialize game", e);
        }
    }
    @Override
    public void render() {
        Game.render();
        batch.begin();
//        for(int i = 0; i < enemyGenerate.length; i++){
//            enemyGenerate[i].render(batch, player);
//        }
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
//        for(int i = 0; i < enemyGenerate.length; i++){
//            enemyGenerate[i].dispose();
//        }
        Game.dispose();
    }
}
