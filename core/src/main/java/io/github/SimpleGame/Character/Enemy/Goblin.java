package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Tool.AnimationTool;
public class Goblin extends Enemy {
    private AnimationTool[] animationTools;
    private Body enemyBody;

    public Goblin(World world, Player player, float x, float y) {
        super(world, player, x, y);
        animation.load("Goblin");
        animationTools = animation.getAnimationTools();
        enemyPhysic = new EnemyPhysic(x, y, 0.5f, 1);
        enemyBody = enemyPhysic.createBody(enemyPhysic.getEnemyBody());
        attribute = new EnemyAttribute(25, 3, 4, 10, 2);
        enemyState = new EnemyState(enemyBody, currentState, player, enemyPhysic, attribute);
    }

    @Override
    public void render(SpriteBatch batch, Player player) {
        animation.render(batch,enemyState,player);
        enemyState.update(batch);
    }

    @Override
    public void dispose() {
        animation.dispose();
        enemyPhysic.dispose();
        enemyState.dispose();
    }
}
