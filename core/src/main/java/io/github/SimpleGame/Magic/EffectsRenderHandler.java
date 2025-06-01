package io.github.SimpleGame.Magic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;

public interface EffectsRenderHandler {
    void Effectsrender(SpriteBatch batch, Player player, float x, float y, Boolean flip);
    void createEffectBody(float x, float y);
}
