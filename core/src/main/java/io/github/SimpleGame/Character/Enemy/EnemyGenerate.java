package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;

import java.util.ArrayList;
import java.util.List;

public class EnemyGenerate {
    private final List<Enemy> enemies = new ArrayList<>();
    public void addEnemy(World world, Player player,float x,float y) {
        Enemy enemy = EnemyFactory.createRandomEnemy(world, player, x, y);
        enemies.add(enemy);
    }
    public void render(SpriteBatch batch, Player player){
         for (Enemy enemy : enemies) {
            enemy.render(batch, player);
        }
    }
    public void dispose() {
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
    }
}
