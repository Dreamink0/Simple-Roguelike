package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemyFactory {
    @FunctionalInterface
     public interface EnemyCreator {
        Enemy create(World world, Player player, float x, float y);
    }
    private static final List<EnemyCreator> ENEMY_CREATORS = new ArrayList<>();
    private static final Random RANDOM = new Random();
    //注册所有EnemyCreator,手动创建;
    static {
        ENEMY_CREATORS.add((Goblin::new));
        ENEMY_CREATORS.add((Flyingeye::new));
        ENEMY_CREATORS.add((Skeleton::new));
        ENEMY_CREATORS.add((Frog::new));
        ENEMY_CREATORS.add((NightBorne::new));
        ENEMY_CREATORS.add((BadCat::new));
    }
    public static Enemy createRandomEnemy(World world, Player player, float x, float y) {
        if (ENEMY_CREATORS.isEmpty()) {
            throw new IllegalStateException("No enemy creators registered.");
        }
        int index = RANDOM.nextInt(ENEMY_CREATORS.size());
        return ENEMY_CREATORS.get(index).create(world, player, x, y);
    }
}
