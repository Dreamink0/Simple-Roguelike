package io.github.SimpleGame.Magic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Fire {
    private TextureAtlas particleAtlas;
    private ParticleEffect effect;

    public Fire() {
        if (!Gdx.files.internal("Magic/particle.png").exists()) {
            Gdx.app.error("Fire", "FR.png not found!");
        }
        particleAtlas = new TextureAtlas(Gdx.files.internal("Magic/particle.png"));
        effect = new ParticleEffect();
    }

    public void create() {
        if (!Gdx.files.internal("Magic/Fire.p").exists()) {
            Gdx.app.error("Fire", "Fire.p not found!");
        }
        effect.load(Gdx.files.internal("Magic/Fire.p"), particleAtlas);
        effect.start();
    }

    public void render(SpriteBatch batch) {
        if (effect != null) {
            effect.update(Gdx.graphics.getDeltaTime());
            effect.draw(batch);
        }
    }

    public void dispose() {
        if (effect != null) effect.dispose();
        if (particleAtlas != null) particleAtlas.dispose();
    }
}
