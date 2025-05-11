package io.github.SimpleGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Character.Player.PlayerController;
import io.github.SimpleGame.Resource.MapManager;
import io.github.SimpleGame.Resource.ResourceManager;
import io.github.SimpleGame.Tool.Animation_Tool;

import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private World world;
    private PlayerController playerController;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private float accumulator = 0f;
    private Animation<TextureRegion> playerIdleAnimation;
    private Animation<TextureRegion> playerRunAnimation;
    private Animation<TextureRegion> playerAttackAnimation;

    private MapManager mapManager;
    float stateTime;
    private Animation<TextureRegion> currentAnimation;
    private Sprite playerSprite;
    private Player player;
    private ResourceManager resourceManager;
    private Box2DDebugRenderer debugRenderer;
    private final Animation_Tool animation_tool=new Animation_Tool();
    @Override
    public void create() {
        try {
            Gdx.app.debug("SimpleGame", "DebugMessage");
            Gdx.app.error("SimpleGame", "errorMessage");
            Box2D.init();
            world = new World(new Vector2(0, 0), true);
            debugRenderer = new Box2DDebugRenderer();
            camera = new OrthographicCamera();
            camera.setToOrtho(false, Config.WORLD_WIDTH, Config.WORLD_HEIGHT);
            camera.position.set(Config.WORLD_WIDTH/2, Config.WORLD_HEIGHT/2, 0);
            camera.update();

            resourceManager = ResourceManager.getInstance();
            resourceManager.loadResources();

            playerIdleAnimation = resourceManager.getPlayerIdleAnimation();
            playerRunAnimation = resourceManager.getPlayerRunAnimation();
            playerSprite = resourceManager.getPlayerSprite();
            playerAttackAnimation = resourceManager.getPlayerAttackAnimation();

            if (playerIdleAnimation == null || playerRunAnimation == null || playerSprite == null) {
                throw new RuntimeException("Failed to initialize player resources");
            }

            currentAnimation = playerIdleAnimation;

            batch = new SpriteBatch();
            stateTime = 0f;
            mapManager = resourceManager.getMapManager(world);
            player = new Player(world, Config.WORLD_WIDTH, Config.WORLD_HEIGHT);
            playerController = player.getPlayerController();

            animation_tool.Create("TEST",resourceManager.Test_,5,4);
        } catch (Exception e) {
            Gdx.app.error("SimpleGame", "Error during initialization: " + e.getMessage());
            throw new RuntimeException("Failed to initialize game", e);
        }
        //Listener.Bound(world);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        stateTime += deltaTime;
        animation_tool.update("TEST",deltaTime);
        TextureRegion frame = animation_tool.getKeyFrame("TEST",true);
        // 根据玩家移动状态选择动画
        boolean isAttacking = playerController.isAttacking();
        boolean isMoving = playerController.isMoving();
        Animation<TextureRegion> newAnimation;

        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            playerController.startAttack();
        }
        if (isAttacking) {
            newAnimation = playerAttackAnimation;
        }else{
            newAnimation = isMoving ? playerRunAnimation : playerIdleAnimation;
        }

        if (newAnimation != currentAnimation) {
            stateTime = 0;
            currentAnimation = newAnimation;
        }

        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        playerSprite.setRegion(currentFrame);
        if (isAttacking) {
            TextureRegion attackFrame = playerAttackAnimation.getKeyFrame(stateTime, true);
            if (attackFrame != null) {
                playerSprite.setRegion(attackFrame); // 使用攻击动画帧
            }
        }
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
        if (frame != null) {
            if(Gdx.input.isKeyPressed(Input.Keys.F)){
                batch.draw(frame,
                    playerPos.x - playerSprite.getWidth()+3,
                    playerPos.y - playerSprite.getWidth()/2,
                    15,
                    10
                );
                batch.draw(frame,
                    playerPos.x - playerSprite.getWidth()-9,
                    playerPos.y - playerSprite.getWidth()/2+2,
                    15,
                    10,
                    15,
                    10,
                    1,
                    1,
                    90
                );
            }
        }
        playerSprite.draw(batch);
        batch.end();
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            debugRenderer.render(world, camera.combined.scl(10f/Config.PIXELS_PER_METER));
        }
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
        if (mapManager != null) mapManager.dispose();
        if(player!=null) player.dispose();
        if (batch != null) batch.dispose();
        if (world != null) world.dispose();
        if (resourceManager != null) resourceManager.dispose();
        if (debugRenderer != null) debugRenderer.dispose();
    }
}
