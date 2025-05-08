package io.github.SimpleGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Magic.Fire;
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
    private Animation<TextureRegion> Fire_Animation;
    private Texture Fire_texture;
    float stateTime;

    @Override
    public void create() {
        Box2D.init();
        world = new World(new Vector2(0, 0), true);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        camera.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2, 0);
        camera.update();

        assetManager = new AssetManager();
        assetManager.load("Sprites/knight_f_idle_anim_f0.png", Texture.class);
        assetManager.load("Magic/FR.png", Texture.class);
        tiledMap = new TmxMapLoader().load("Maps/TestMap.tmx");
        assetManager.finishLoading();
        playerTexture = assetManager.get("Sprites/knight_f_idle_anim_f0.png", Texture.class);
        Fire_texture = assetManager.get("Magic/FR.png", Texture.class);
        int frameWidth = (int) (Fire_texture.getWidth() / 4);
        int frameHeight = (int) (Fire_texture.getHeight() / 4);
        TextureRegion[][] tmp = TextureRegion.split(Fire_texture,frameWidth,frameHeight);
        TextureRegion[] walkFrames = new TextureRegion[tmp.length * tmp[0].length];
        int index = 0;
        for (TextureRegion[] row : tmp) {
            for (TextureRegion region : row) {
                walkFrames[index++] = region;
            }
        }
        Fire_Animation = new Animation<>(0.1f, walkFrames);
        Fire_Animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        batch = new SpriteBatch();
        stateTime = 0f;
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
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion frame = Fire_Animation.getKeyFrame(stateTime);
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
        if (frame != null) { // 防止 NullPointerException
            batch.draw(frame,
                playerPos.x - textureWidth/2,
                playerPos.y - textureHeight/2,
                textureWidth,
                textureHeight);
        }
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
        Fire_texture.dispose();
        assetManager.dispose();
    }
}
