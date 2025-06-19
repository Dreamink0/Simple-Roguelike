package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Tool.AnimationTool;

public class Skeleton extends Enemy{

    public Skeleton(World world, Player player, float x, float y) {
        super(world, player, x, y);
        animation=new EnemyAnimation();
        animation.load("Skeleton");
        enemyPhysic = new EnemyPhysic(x, y, 0.4f, 1.5f);
        Body enemyBody = enemyPhysic.createBody(enemyPhysic.getEnemyBody());
        enemyBody.setUserData(this);
        attribute = new EnemyAttribute(55, 8, 4, 15, 1.5f);
        enemyState = new EnemyState(enemyBody, currentState, player, enemyPhysic, attribute);
        enemyState.hurtAnimationDuration=0.1f;
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
        }
    }
}
