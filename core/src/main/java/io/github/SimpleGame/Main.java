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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;

import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Character.Player.PlayerController;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private static final float WORLD_WIDTH = 20f;
    private static final float WORLD_HEIGHT = 15f;
    private static final float PIXELS_PER_METER = 64f;
    private static final float TIME_STEP = 1/60f;
    private static final float PLAYER_SCALE = 8f;

    private AssetManager assetManager;
    private World world;
    private Body playerBody;
    private PlayerController playerController;
    private SpriteBatch batch;
    private TextureAtlas playerTextureAtlas;
    private OrthographicCamera camera;
    private float accumulator = 0f;
    private Animation<TextureRegion> playerIdleAnimation;
    private Animation<TextureRegion> playerRunAnimation;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap tiledMap;
    float stateTime;
    private Animation<TextureRegion> currentAnimation;

    private Sprite playerSprite;
    private Player player;

    @Override
    public void create() {
        Gdx.app.debug("SimpleGame", "DebugMessage");
        Gdx.app.error("SimpleGame", "errorMessage");
        Box2D.init();
        world = new World(new Vector2(0, 0), true);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        camera.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2, 0);
        camera.update();

        assetManager = new AssetManager();
        assetManager.load("Sprites/BasePlayer/BasePlayer.atlas", TextureAtlas.class);
        tiledMap = new TmxMapLoader().load("Maps/TestMap.tmx");
        assetManager.finishLoading();

        playerTextureAtlas = assetManager.get("Sprites/BasePlayer/BasePlayer.atlas", TextureAtlas.class);

        // 设置所有纹理过滤模式为Nearest
        for (Texture texture : playerTextureAtlas.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        // 创建idle动画
        playerIdleAnimation = new Animation<>(0.15f, playerTextureAtlas.findRegions("idle"), Animation.PlayMode.LOOP);
        playerRunAnimation = new Animation<>(0.08f, playerTextureAtlas.findRegions("run"), Animation.PlayMode.LOOP);
        currentAnimation = playerIdleAnimation;

        playerSprite = new Sprite(playerTextureAtlas.findRegion("idle"));

        playerSprite.setSize(
            (playerSprite.getWidth() / PIXELS_PER_METER) * PLAYER_SCALE,
            (playerSprite.getHeight() / PIXELS_PER_METER) * PLAYER_SCALE
        );

        batch = new SpriteBatch();
        stateTime = 0f;
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap,PIXELS_PER_METER/512);
        player = new Player(world, WORLD_WIDTH, WORLD_HEIGHT);
        playerController = player.getPlayerController();
        playerBody = player.getBody();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        stateTime += deltaTime;

        // 根据玩家移动状态选择动画
        boolean isMoving = playerController.isMoving();
        Animation<TextureRegion> newAnimation = isMoving ? playerRunAnimation : playerIdleAnimation;

        if (newAnimation != currentAnimation) {
            stateTime = 0;
            currentAnimation = newAnimation;
        }

        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        playerSprite.setRegion(currentFrame);

        accumulator += deltaTime;
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }

        playerController.update();

        // 更新相机位置
        Vector2 playerPos = playerController.getPosition();
        camera.position.set(playerPos.x, playerPos.y, 0);
        camera.update();
        mapRenderer.setView(camera);

        batch.setProjectionMatrix(camera.combined);
        mapRenderer.render();

        batch.begin();

        boolean isFlipped = playerController.isFlipped();

        playerSprite.setPosition(
            playerPos.x - playerSprite.getWidth() / 2,
            playerPos.y - playerSprite.getHeight() / 2
        );
        playerSprite.setFlip(isFlipped, false);

        playerSprite.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / height;
        camera.viewportWidth = 2*WORLD_WIDTH;
        camera.viewportHeight = 2*WORLD_WIDTH / aspectRatio;
        camera.update();
    }

    @Override
    public void dispose() {
        world.dispose();
        batch.dispose();
        assetManager.dispose();
    }
}
