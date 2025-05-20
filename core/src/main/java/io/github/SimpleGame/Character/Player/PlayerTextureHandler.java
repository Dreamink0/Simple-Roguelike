package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface PlayerTextureHandler {
    void load();
    void get();
    void render(SpriteBatch batch,float deltaTime);
    void dispose();
}
