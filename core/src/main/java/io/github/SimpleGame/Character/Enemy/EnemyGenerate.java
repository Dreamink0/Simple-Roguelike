package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Resource.MapGeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class EnemyGenerate {
    private final List<Enemy> enemies = new ArrayList<>();
    private final Random random = new Random();
    public int currentRoomx = -1;
    public int currentRoomy = -1;
    public int count=0;
    public void addEnemy(World world, Player player,float x,float y) {
        Enemy enemy = EnemyFactory.createRandomEnemy(world, player, x, y);
        enemies.add(enemy);
    }
    public void render(SpriteBatch batch, Player player){
         for (Enemy enemy : enemies) {
            enemy.render(batch, player);
        }
    }
    public void update(World world,Player player,SpriteBatch batch){
        Vector2 position = player.getBody().getPosition();
        int newRoomX = (int) (position.x / MapGeneration.ROOM_WIDTH *  MapGeneration.TILE_SIZE);
        int newRoomY = (int) (position.y / MapGeneration.ROOM_HEIGHT * MapGeneration.TILE_SIZE);
        if(newRoomY != currentRoomx||newRoomY != currentRoomy){
            currentRoomx = newRoomX;
            currentRoomy = newRoomY;
            generateEnemiesInRoom(world,player,newRoomX,newRoomY);
        }
        batch.begin();
        render(batch,player);
        batch.end();
    }
    public void generateEnemiesInRoom(World world,Player player,int roomX,int roomY){
        int enemyCount = random.nextInt(5);
        if(count>=0&&count<=1500){
            for (int i = 0; i < enemyCount; i++) {
                float x = random.nextFloat() * roomX * MapGeneration.ROOM_WIDTH;
                float y = random.nextFloat() * roomY * MapGeneration.ROOM_HEIGHT;
                addEnemy(world, player, x, y);
                count++;
            }
        }
    }
    public void dispose() {
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        enemies.clear();
    }
}
