package io.github.SimpleGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;

import io.github.SimpleGame.Character.Player.Player;
import static io.github.SimpleGame.Config.WORLD_HEIGHT;
import static io.github.SimpleGame.Config.WORLD_WIDTH;
import io.github.SimpleGame.Item.Weapon;
import io.github.SimpleGame.Magic.Lightning_Magic;
import io.github.SimpleGame.Resource.CameraManager;
import io.github.SimpleGame.Resource.MapGeneration;
import io.github.SimpleGame.Resource.MapManager;
import io.github.SimpleGame.Resource.ResourceManager;
import io.github.SimpleGame.Resource.WorldManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private WorldManager worldManager;
    private CameraManager cameraManager;
    private SpriteBatch batch;
    private SpriteBatch uiBatch;
    private OrthographicCamera uiCamera;
    private MapManager mapManager;
    private MapGeneration mapGeneration;
    private float stateTime = 0f;
    private Player player;
    private ResourceManager resourceManager;
    private Weapon item;
    private Lightning_Magic lightningMagic;

    // 物理更新相关常量
    private static final float MAX_STEP_TIME = 0.25f;
    private static final float MIN_STEP_TIME = 1/60f;
    private static final int MAX_STEPS = 5;
    private float accumulator = 0f;

    // 对象池
    private final Pool<SpriteBatch> batchPool = new Pool<SpriteBatch>(2) {
        @Override
        protected SpriteBatch newObject() {
            return new SpriteBatch();
        }
    };

    @Override
    public void create() {
        try {
            Gdx.app.debug("SimpleGame", "DebugMessage");
            Gdx.app.error("SimpleGame", "errorMessage");

            // 1. 核心资源加载
            worldManager = new WorldManager();
            cameraManager = new CameraManager();
            resourceManager = ResourceManager.getInstance();

            // 2. 加载资产
            resourceManager.loadResources();

            // 3. 初始化玩家和游戏对象
            player = new Player(worldManager.getWorld(), WORLD_WIDTH, WORLD_HEIGHT);

            // 4. 初始化渲染器
            batch = batchPool.obtain();
            uiBatch = batchPool.obtain();

            // 5. 初始化地图
            mapGeneration = new MapGeneration();
            mapManager = resourceManager.getMapManager(worldManager.getWorld());
            item = new Weapon(worldManager.getWorld(), WORLD_WIDTH, WORLD_HEIGHT + 5, 1f);
            lightningMagic = new Lightning_Magic();
            lightningMagic.magicCreate(worldManager.getWorld(), WORLD_WIDTH+10, WORLD_HEIGHT+6);
        } catch (Exception e) {
            Gdx.app.error("SimpleGame", "Error during initialization: " + e.getMessage());
            throw new RuntimeException("Failed to initialize game", e);
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(37/255f, 19/255f, 26/255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), MAX_STEP_TIME);
        stateTime += deltaTime;

        accumulator += deltaTime;
        int steps = 0;
        while (accumulator >= MIN_STEP_TIME && steps < MAX_STEPS) {
            worldManager.getWorld().step(MIN_STEP_TIME, 6, 2);
            accumulator -= MIN_STEP_TIME;
            steps++;
        }

        if (accumulator > 0 && steps < MAX_STEPS) {
            worldManager.getWorld().step(accumulator, 6, 2);
            accumulator = 0;
        }

        if (accumulator > MAX_STEP_TIME) {
            accumulator = MAX_STEP_TIME;
        }
        // 测试按键
        if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
            player.getAttributeHandler().setHP(player.getAttributeHandler().getMaxHP() - 10f);
            player.getAttributeHandler().setMP(player.getAttributeHandler().getMaxMP() - 20f);
            player.getAttributeHandler().setDEF(player.getAttributeHandler().getMaxDEF() - 10f);
            player.getPlayerTextureHandler().get();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)){
            System.out.println("HP:"+player.getAttributeHandler().getMaxHP());
            System.out.println("MP:"+player.getAttributeHandler().getMaxMP());
            System.out.println("DEF:"+player.getAttributeHandler().getMaxDEF());
        }
        player.setAction(player.getPlayerController(), player, worldManager.getWorld()).update();

        cameraManager.getCamera(player.getPlayerController());
        mapManager.setView(cameraManager.getCamera());

        mapManager.render(batch);

        batch.setProjectionMatrix(cameraManager.getCamera().combined);
        uiBatch.setProjectionMatrix(cameraManager.getUiCamera().combined);

        batch.begin();
        player.filpCheck(player.getPlayerSprite(), player.getPlayerController(), batch).draw(batch);
        item.render(batch, player);
        lightningMagic.magicRender(batch, player);
        batch.end();
        lightningMagic.magicObtain(batch,uiBatch,player);
        uiBatch.begin();
        player.render(uiBatch,deltaTime);
        uiBatch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            worldManager.getDebugRenderer().render(worldManager.getWorld(), cameraManager.getCamera().combined);
        }
        // 重新生成地图
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            regenerateMap();
        }
    }

    private void regenerateMap() {
        try {
            if (mapManager != null) {
                mapManager.dispose();
            }
            mapManager = new MapManager(mapGeneration.generateRandomMap(), Config.PIXELS_PER_METER/512, worldManager.getWorld());
            player.getBody().setTransform(WORLD_WIDTH, WORLD_HEIGHT, 0);
            Gdx.app.debug("SimpleGame", "New map generated successfully");
        } catch (Exception e) {
            Gdx.app.error("SimpleGame", "Error generating new map: " + e.getMessage());
        }
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / height;
        cameraManager.getCamera().viewportWidth = 2 * WORLD_WIDTH;
        cameraManager.getCamera().viewportHeight = 2 * WORLD_WIDTH / aspectRatio;
        cameraManager.getCamera().update();
    }

    @Override
    public void dispose() {
        if (mapManager != null) mapManager.dispose();
        if (player != null) player.dispose();
        if (batch != null) {
            batch.dispose();
            batchPool.free(batch);
        }
        if (uiBatch != null) {
            uiBatch.dispose();
            batchPool.free(uiBatch);
        }
        if (worldManager.getWorld() != null) worldManager.getWorld().dispose();
        if (resourceManager != null) resourceManager.dispose();
        if (worldManager.getDebugRenderer() != null) worldManager.getDebugRenderer().dispose();

        // 清理对象池
        batchPool.clear();
    }
}
