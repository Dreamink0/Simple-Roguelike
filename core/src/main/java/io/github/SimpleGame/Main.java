package io.github.SimpleGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;

import io.github.SimpleGame.Player.PlayerController;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private static final float WORLD_WIDTH = 20f;
    private static final float WORLD_HEIGHT = 15f;
    private static final float PIXELS_PER_METER = 32f;
    private static final float TIME_STEP = 1/60f;
    private static final float GRID_SIZE = 1f;
    private static final float PLAYER_SCALE = 8f;

    private AssetManager assetManager;
    private World world;
    private Body playerBody;
    private PlayerController playerController;
    private SpriteBatch batch;
    private Texture playerTexture;
    private OrthographicCamera camera;
    private float accumulator = 0f;
    
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap tiledMap;
    @Override
    public void create() {
        Box2D.init();
        world = new World(new Vector2(0, 0), true);
        tiledMap = new TmxMapLoader().load("Maps/TestMap.tmx");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        camera.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2, 0);
        camera.update();

        assetManager = new AssetManager();
        assetManager.load("Sprites/knight_f_idle_anim_f0.png", Texture.class);
        assetManager.finishLoading();
        playerTexture = assetManager.get("Sprites/knight_f_idle_anim_f0.png", Texture.class);

        batch = new SpriteBatch();
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        createPlayer();
    }

    private void createPlayer() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2);
        bodyDef.fixedRotation = true;
        playerBody = world.createBody(bodyDef);

        playerController = new PlayerController(playerBody);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        accumulator += deltaTime;
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }

        playerController.update();

        batch.setProjectionMatrix(camera.combined);
        mapRenderer.render();
        batch.begin();

        float textureWidth = (playerTexture.getWidth() / PIXELS_PER_METER) * PLAYER_SCALE;
        float textureHeight = (playerTexture.getHeight() / PIXELS_PER_METER) * PLAYER_SCALE;

        Vector2 playerPos = playerController.getPosition();
        batch.draw(playerTexture,
            playerPos.x - textureWidth/2,
            playerPos.y - textureHeight/2,
            textureWidth,
            textureHeight);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / height;
        camera.viewportWidth = WORLD_WIDTH;
        camera.viewportHeight = WORLD_WIDTH / aspectRatio;
        camera.update();
    }

    @Override
    public void dispose() {
        world.dispose();
        batch.dispose();
        assetManager.dispose();
    }
}
