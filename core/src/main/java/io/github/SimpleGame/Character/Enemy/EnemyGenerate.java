package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Item.Weapon;
import io.github.SimpleGame.Resource.Game;
import io.github.SimpleGame.Resource.MapGeneration;

import java.util.*;

public class EnemyGenerate {
    private final List<Enemy> enemies = new ArrayList<>();
    private final Random random = new Random();
    public int currentRoomx = -1;
    public int currentRoomy = -1;
    private final Map<String, Integer> enemyDeathCounts = new HashMap<>();
    private int killCount = 0; // 使用更具描述性的变量名
    private String enemyName;
    private static List<Weapon> weapons = new ArrayList<>();

    public void addEnemy(World world, Player player, float x, float y) {
        Enemy enemy = EnemyFactory.createRandomEnemy(world, player, x, y);
        enemies.add(enemy);
        enemyName = enemy.getClassName();
    }

    public void render(SpriteBatch batch, Player player) {
        for (Enemy enemy : enemies) {
            enemy.render(batch, player);
        }
    }

    public void update(World world, Player player, SpriteBatch batch) {
        Vector2 position = player.getBody().getPosition();
        int newRoomX = (int) (position.x / MapGeneration.ROOM_WIDTH * MapGeneration.TILE_SIZE);
        int newRoomY = (int) (position.y / MapGeneration.ROOM_HEIGHT * MapGeneration.TILE_SIZE);

        if (newRoomX != currentRoomx || newRoomY != currentRoomy) {
            currentRoomx = newRoomX;
            currentRoomy = newRoomY;
            generateEnemiesInRoom(world, player, newRoomX, newRoomY);
        }

        // 检测敌人是否死亡
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();

            if (enemy.getHP() <= 0 && !enemy.hasDied()) {
                enemy.setHP(0);
            }

            if (enemy.shouldRemove()) {
                String type = enemy.getClassName();
                enemyDeathCounts.put(type, enemyDeathCounts.getOrDefault(type, 0) + 1);

                // 增加击杀计数
                killCount++;

                // 每击杀5个敌人掉落一把武器
                if (killCount % 5 == 0) {
                    float dropX = enemy.getX();
                    float dropY = enemy.getY();
                    Weapon droppedWeapon = new Weapon(world, dropX, dropY, 1);
                    weapons.add(droppedWeapon);
                }

                iterator.remove();
            }
        }

        // 渲染所有掉落的武器
        for (Weapon weapon : weapons) {
            weapon.render(batch, Game.UIbatch, player);
        }

        // 打印当前敌人数
        Gdx.app.log("EnemyCount", "Current Enemies: " + enemies.size());
        System.out.println("Total kills: " + killCount);

        batch.begin();
        render(batch, player);
        batch.end();
    }

    public void generateEnemiesInRoom(World world, Player player, int roomX, int roomY) {
        int enemyCount = random.nextInt(3);
        if (enemies.size() <= 3) {
            for (int i = 0; i < enemyCount; i++) {
                addEnemy(world, player,roomX,roomY);
            }
        }
    }

    public void dispose() {
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        enemies.clear();

        for (Weapon weapon : weapons) {
            weapon.dispose();
        }
        weapons.clear();

        enemyDeathCounts.clear();
    }

    public Map<String, Integer> getEnemyDeathCounts() {
        return new HashMap<>(enemyDeathCounts);
    }

    public int getTotalKills() {
        return killCount;
    }
}
