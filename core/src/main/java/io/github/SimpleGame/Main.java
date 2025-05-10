package io.github.SimpleGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;

import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Character.Player.PlayerController;
import io.github.SimpleGame.Resource.MapManager;
import io.github.SimpleGame.Resource.ResourceManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private World world;
    private PlayerController playerController;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private float accumulator = 0f;
    private Animation<TextureRegion> playerIdleAnimation;
    private Animation<TextureRegion> playerRunAnimation;
    private MapManager mapManager;
    float stateTime;
    private Animation<TextureRegion> currentAnimation;
    private Sprite playerSprite;
    private Player player;
    private ResourceManager resourceManager;

    @Override
    public void create() {
        try {
            Gdx.app.error("SimpleGame", "create error");
            Box2D.init();
            world = new World(new Vector2(0, 0), true);
            camera = new OrthographicCamera();
            camera.setToOrtho(false, Config.WORLD_WIDTH, Config.WORLD_HEIGHT);
            camera.position.set(Config.WORLD_WIDTH/2, Config.WORLD_HEIGHT/2, 0);
            camera.update();

            resourceManager = ResourceManager.getInstance();
            resourceManager.loadResources();

            playerIdleAnimation = resourceManager.getPlayerIdleAnimation();
            playerRunAnimation = resourceManager.getPlayerRunAnimation();
            playerSprite = resourceManager.getPlayerSprite();

            if (playerIdleAnimation == null || playerRunAnimation == null || playerSprite == null) {
                throw new RuntimeException("Failed to initialize player resources");
            }

            currentAnimation = playerIdleAnimation;

            batch = new SpriteBatch();
            stateTime = 0f;
            mapManager = resourceManager.getMapManager(world);
            player = new Player(world, Config.WORLD_WIDTH, Config.WORLD_HEIGHT);
            playerController = player.getPlayerController();
        } catch (Exception e) {
            Gdx.app.error("SimpleGame", "Error during initialization: " + e.getMessage());
            throw new RuntimeException("Failed to initialize game", e);
        }
        //Listener.Bound(world);
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
        while (accumulator >= Config.TIME_STEP) {
            world.step(Config.TIME_STEP, 6, 2);
            accumulator -= Config.TIME_STEP;
        }

        playerController.update();

        // 更新相机位置
        Vector2 playerPos = playerController.getPosition();
        camera.position.set(playerPos.x, playerPos.y, 0);
        camera.update();
        mapManager.setView(camera);

        batch.setProjectionMatrix(camera.combined);
        mapManager.render(batch);

        batch.begin();

        boolean isFlipped = playerController.isFlipped();

        playerSprite.setPosition(
            playerPos.x - playerSprite.getWidth() / 2,
            playerPos.y - playerSprite.getHeight() / 2
        );
        playerSprite.setFlip(isFlipped, false);
        playerSprite.draw(batch);

        batch.end();
//        Box2DDebugRenderer box2DDebugRenderer = new Box2DDebugRenderer();
//        box2DDebugRenderer.render(world,camera.combined.scl(1f/Config.PIXELS_PER_METER));
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / height;
        camera.viewportWidth = 2*Config.WORLD_WIDTH;
        camera.viewportHeight = 2*Config.WORLD_WIDTH / aspectRatio;
        camera.update();
    }

    @Override
    public void dispose() {
        if (world != null) world.dispose();
        if (batch != null) batch.dispose();
        if (playerSprite != null) playerSprite.getTexture().dispose();
        if (mapManager != null) mapManager.dispose();
        if (resourceManager != null) resourceManager.dispose();
    }
}
