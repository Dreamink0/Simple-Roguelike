package io.github.SimpleGame.Resource;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Pool;

public interface GameRenderHandler {
    void render(SpriteBatch batch,SpriteBatch UIbatch);
}
