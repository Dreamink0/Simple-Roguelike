package io.github.SimpleGame.Resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.World;

public class GameTimeUpdates implements PhysicHandler{
    private float stateTime = 0f;
    private static final float MAX_STEP_TIME = 0.25f;
    private static final float MIN_STEP_TIME = 1/60f;
    private static final int MAX_STEPS = 5;
    @Override
    public void update(World world,float deltaTime) {
        Gdx.gl.glClearColor(37/255f, 19/255f, 26/255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        stateTime += deltaTime;
        int steps = 0;
        while ( stateTime >= MIN_STEP_TIME && steps < MAX_STEPS) {
           world.step(MIN_STEP_TIME, 6, 2);
            stateTime -= MIN_STEP_TIME;
            steps++;
        }
        if (stateTime > 0 && steps < MAX_STEPS) {
            world.step( stateTime, 6, 2);
            stateTime = 0;
        }
        if ( stateTime > MAX_STEP_TIME) {
            stateTime = MAX_STEP_TIME;
        }
    }
}
