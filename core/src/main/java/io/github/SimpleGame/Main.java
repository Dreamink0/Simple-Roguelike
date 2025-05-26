package io.github.SimpleGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.SimpleGame.Character.Enemy.Goblin;
import io.github.SimpleGame.Resource.Game;
import io.github.SimpleGame.Tool.Listener;

import static io.github.SimpleGame.Config.WORLD_HEIGHT;
import static io.github.SimpleGame.Config.WORLD_WIDTH;
import static io.github.SimpleGame.Resource.Game.player;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private Game Game;
    Goblin[] monster1s;
    @Override
    public void create() {
        try {
            Gdx.app.debug("SimpleGame", "DebugMessage");
            Gdx.app.error("SimpleGame", "errorMessage");
            Game = new Game();
            Game.initialize();
            Game.Generation();
            Game.readPlayerData();
            monster1s = new Goblin[10];
            for (int i = 0; i < monster1s.length; i++) {
                float randomX = WORLD_WIDTH + 2 + (float) Math.random() * 20;
                float randomY = WORLD_HEIGHT + 2 + (float) Math.random() * 15;
                monster1s[i] = new Goblin(Game.world, player, randomX, randomY);
            }
        } catch (Exception e) {
            Gdx.app.error("SimpleGame", "Error during initialization: " + e.getMessage());
            throw new RuntimeException("Failed to initialize game", e);
        }
    }
    @Override
    public void render() {
        Game.render();
        Game.batch.begin();
        for (int i = 0; i < monster1s.length; i++) {
            monster1s[i].render(Game.batch,player,Game.world);
        }
        Game.batch.end();
        Listener.Bound(Game.world,player);
    }
    @Override
    public void resize(int width, int height) {Game.resize(width, height);}
    @Override
    public void dispose() {
        Game.dispose();
        for (int i = 0; i < monster1s.length; i++) {monster1s[i].dispose();}
        monster1s = null;
    }
}
