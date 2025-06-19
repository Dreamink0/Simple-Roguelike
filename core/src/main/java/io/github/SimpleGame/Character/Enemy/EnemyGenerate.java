package io.github.SimpleGame.Character.Enemy;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.GUI.UI;
import io.github.SimpleGame.Resource.MapGeneration;

import java.util.*;

import static io.github.SimpleGame.Resource.Game.UIbatch;

public class EnemyGenerate {
    private final List<Enemy> enemies = new ArrayList<>();
    private final Map<String, Integer> enemyCounts = new HashMap<>();
    private final Random random = new Random();
    public int currentRoomx = -1;
    public int currentRoomy = -1;
    private static int count=0;
    // 新增死亡计数Map
    private final Map<String, Integer> enemyDeathCounts = new HashMap<>();

    public void addEnemy(World world, Player player,float x,float y) {
        Enemy enemy = EnemyFactory.createRandomEnemy(world, player, x, y);
        enemies.add(enemy);
        String type = enemy.getClassName();
        enemyCounts.put(type, enemyCounts.getOrDefault(type, 0) + 1);
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

        // 检测敌人是否死亡
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.getHP() <= 0) {
                String type = enemy.getClassName();
                // 更新死亡计数
                enemyDeathCounts.put(type, enemyDeathCounts.getOrDefault(type, 0) + 1);
                // 从敌人列表中移除已死亡的敌人
                enemy.getAnimation().dead(iterator);
            }
        }

        // 打印当前敌人数
        Gdx.app.log("EnemyCount", "Current Enemies: " + enemies.size());
        batch.begin();
        render(batch,player);
        batch.end();
    }

    public void generateEnemiesInRoom(World world,Player player,int roomX,int roomY){
        int enemyCount = random.nextInt(5);
        if(enemies.size()<=10){
            for (int i = 0; i < enemyCount; i++) {
                float newRoomX = (player.getX() / MapGeneration.ROOM_WIDTH *  MapGeneration.TILE_SIZE);
                float newRoomY = (player.getY() / MapGeneration.ROOM_HEIGHT * MapGeneration.TILE_SIZE+enemyCount);
                addEnemy(world, player, newRoomX, newRoomY);
                count++;
            }
        }
    }

    public void dispose() {
        for (Enemy enemy : enemies) {
            String type = enemy.getClassName();
            int count = enemyCounts.getOrDefault(type, 0);
            if(count>0){
                enemyCounts.put(type, count - 1);
            }else{
                enemyCounts.remove(type);
            }
            enemy.dispose();
        }
        enemies.clear();
        enemyCounts.clear();
        enemyDeathCounts.clear(); // 清空死亡计数
    }

    public Map<String, Integer> getEnemyCounts() {
        return new HashMap<>(enemyCounts);
    }

    // 获取敌人死亡统计
    public Map<String, Integer> getEnemyDeathCounts() {
        return new HashMap<>(enemyDeathCounts);
    }
}
