package io.github.SimpleGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Item.Weapon;
import io.github.SimpleGame.Magic.Lightning_Magic;
import io.github.SimpleGame.Resource.CameraManager;
import io.github.SimpleGame.Resource.MapManager;
import io.github.SimpleGame.Resource.ResourceManager;
import io.github.SimpleGame.Resource.WorldManager;
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainTest extends ApplicationAdapter {
    private WorldManager worldManager;
    private CameraManager cameraManager;
    private SpriteBatch batch;
    private MapManager mapManager;
    float stateTime=0f;
    private Player player;
    private ResourceManager resourceManager;
    private Weapon item;
    Lightning_Magic lightningMagic;
    @Override
    public void create() {
        try {
            Gdx.app.debug("SimpleGame", "DebugMessage");
            Gdx.app.error("SimpleGame", "errorMessage");
            //1核心资源加载
            worldManager=new WorldManager();
            cameraManager=new CameraManager();
            resourceManager = ResourceManager.getInstance();
            //2加载资产
            resourceManager.loadResources();
            //3初始化玩家
            player = new Player(worldManager.getWorld(), Config.WORLD_WIDTH, Config.WORLD_HEIGHT);
            player.getSprite(resourceManager);
            item=new Weapon(worldManager.getWorld(),Config.WORLD_WIDTH,Config.WORLD_HEIGHT+5,1f);
            lightningMagic=new Lightning_Magic();
            lightningMagic.Magic_create(worldManager.getWorld(),Config.WORLD_WIDTH+4,Config.WORLD_HEIGHT);
            //5其他初始化
            batch = new SpriteBatch();
            mapManager = resourceManager.getMapManager(worldManager.getWorld());
        } catch (Exception e) {
            Gdx.app.error("SimpleGame", "Error during initialization: " + e.getMessage());
            throw new RuntimeException("Failed to initialize game", e);
        }
    }
    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        stateTime += deltaTime;
        // 根据玩家移动状态选择动画
        player.setAction(player.getPlayerController(),player,worldManager.getWorld()).update();
        // 更新相机位置
        cameraManager.getCamera(player.getPlayerController());
        mapManager.setView(cameraManager.getCamera());
        batch.setProjectionMatrix(cameraManager.getCamera().combined);
        mapManager.render(batch);
        //其他
        batch.begin();
        player.FilpCheck(player.getPlayerSprite(),player.getPlayerController(),batch).draw(batch);
        item.render(batch,player);
        lightningMagic.Magic_obtain(batch, player); //检测拾取
        lightningMagic.Magic_render(batch,player);
        batch.end();
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            worldManager.getDebugRenderer().render(worldManager.getWorld(), cameraManager.getCamera().combined);
        }
    }
    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / height;
        cameraManager.getCamera().viewportWidth = 2*Config.WORLD_WIDTH;
        cameraManager.getCamera().viewportHeight = 2*Config.WORLD_WIDTH / aspectRatio;
        cameraManager.getCamera().update();
    }
    @Override
    public void dispose() {
        if (mapManager != null) mapManager.dispose();
        if(player!=null) player.dispose();
        if (batch != null) batch.dispose();
        if (worldManager.getWorld() != null) worldManager.getWorld().dispose();
        if (resourceManager != null) resourceManager.dispose();
        if (worldManager.getDebugRenderer() != null) worldManager.getDebugRenderer().dispose();
    }
}
