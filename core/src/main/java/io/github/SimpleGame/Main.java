package io.github.SimpleGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.SimpleGame.Character.Enemy.Goblin;
import io.github.SimpleGame.Resource.Game;
import io.github.SimpleGame.Tool.Listener;
import static io.github.SimpleGame.Resource.Game.player;
import static io.github.SimpleGame.Resource.Game.world;
import static io.github.SimpleGame.Resource.Game.batch;

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
                float randomX = Config.WORLD_WIDTH + 2 + (float) Math.random() * 20;
                float randomY = Config.WORLD_HEIGHT + 2 + (float) Math.random() * 15;
                monster1s[i] = new Goblin(world, player, randomX, randomY);
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
        for (Goblin monster1 : monster1s) {
            monster1.render(batch, player, world);
        }
        batch.end();
        Listener.Bound(world,player);
    }
    @Override
    public void resize(int width, int height) {Game.resize(width, height);}
    @Override
    public void dispose() {
        Game.dispose();
        for (Goblin monster1 : monster1s) {
            monster1.dispose();
        }
        monster1s = null;
    }
}
