package io.github.SimpleGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.SimpleGame.Character.Enemy.Enemy;
import io.github.SimpleGame.Character.Enemy.Goblin;
import io.github.SimpleGame.Resource.Game;
import io.github.SimpleGame.Resource.SoundManager;
import io.github.SimpleGame.Tool.Listener;
import static io.github.SimpleGame.Resource.Game.player;
import static io.github.SimpleGame.Resource.Game.world;
import static io.github.SimpleGame.Resource.Game.batch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private Game Game;
    private Goblin Goblin;
    @Override
    public void create() {
        try {
            Gdx.app.debug("SimpleGame", "DebugMessage");
            Gdx.app.error("SimpleGame", "errorMessage");
            Game = new Game();
            Game.initialize();
            Game.Generation();
            Game.readPlayerData();
            Goblin = new Goblin(world, player,Config.WORLD_WIDTH/2+10,  Config.WORLD_HEIGHT/2+15);

        } catch (Exception e) {
            Gdx.app.error("SimpleGame", "Error during initialization: " + e.getMessage());
            throw new RuntimeException("Failed to initialize game", e);
        }
    }
    @Override
    public void render() {
        Game.render();
        batch.begin();
        Goblin.render(batch, player);
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
