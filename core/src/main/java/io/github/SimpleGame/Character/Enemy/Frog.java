package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;

public class Frog extends Enemy{
    public Frog(World world, Player player, float x, float y) {
        super(world, player, x, y);
        animation.load("Frog");
        enemyPhysic = new EnemyPhysic(x, y, 0.5f, 1);
        Body enemyBody = enemyPhysic.createBody(enemyPhysic.getEnemyBody());
        enemyBody.setUserData(this);
        attribute = new EnemyAttribute(35, 3, 4, 15, 2);
        enemyState = new EnemyState(enemyBody, currentState, player, enemyPhysic, attribute);
    }

    @Override
    public void render(SpriteBatch batch, Player player) {
        animation.render(batch,enemyState,player);
        enemyState.update(batch);
    }

    @Override
    public void dispose() {
        if( animation != null){
            animation.dispose();
            animation = null;
        }
    }
}
