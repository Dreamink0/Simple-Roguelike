package io.github.SimpleGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import io.github.SimpleGame.Tool.Animation_Tool;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainTest extends ApplicationAdapter {
    private WorldManager worldManager;
    private CameraManager cameraManager;
    private SpriteBatch batch;
    private MapManager mapManager;
    private MapGeneration mapGeneration;
    float stateTime=0f;
    private Player player;
    private ResourceManager resourceManager;
    private Weapon item;
    private Lightning_Magic lightningMagic;
    private SpriteBatch uiBatch;
    private OrthographicCamera uiCamera;

    @Override
    public void create() {
        try {
            Gdx.app.debug("SimpleGame", "DebugMessage");
            Gdx.app.error("SimpleGame", "errorMessage");
            //1核心资源加载
            worldManager=new WorldManager();
            cameraManager=new CameraManager();
            uiBatch = new SpriteBatch();
            uiCamera = new OrthographicCamera();
            resourceManager = ResourceManager.getInstance();
            //2加载资产
            resourceManager.loadResources();
            //3初始化玩家
            player = new Player(worldManager.getWorld(), WORLD_WIDTH, WORLD_HEIGHT);
            item=new Weapon(worldManager.getWorld(), WORLD_WIDTH, WORLD_HEIGHT+5,1f);
            lightningMagic=new Lightning_Magic();
            lightningMagic.magicCreate(worldManager.getWorld(), WORLD_WIDTH+4, WORLD_HEIGHT);
            //5其他初始化
            batch = new SpriteBatch();
            mapGeneration = new MapGeneration();
            mapManager = resourceManager.getMapManager(worldManager.getWorld());
        } catch (Exception e) {
            Gdx.app.error("SimpleGame", "Error during initialization: " + e.getMessage());
            throw new RuntimeException("Failed to initialize game", e);
        }
    }
    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            System.gc(); // 主动触发GC
            long freeMem = Runtime.getRuntime().freeMemory() / (1024*1024);
            Gdx.app.log("Memory", "Free memory: " + freeMem + "MB");
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        stateTime += deltaTime;
        // 根据玩家移动状态选择动画
        player.setAction(player.getPlayerController(), player, worldManager.getWorld()).update();
        // 更新相机位置
        // 主摄像机跟随玩家
        cameraManager.getCamera(player.getPlayerController());
        mapManager.setView(cameraManager.getCamera());
        // 设置主游戏画面渲染
        mapManager.render(batch);
        // 新增UI摄像机管理UI元素
        uiCamera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        uiCamera.position.set(Config.WORLD_WIDTH/2, Config.WORLD_HEIGHT/2, 0);
        uiCamera.update();
        batch.setProjectionMatrix(cameraManager.getCamera().combined);
        uiBatch.setProjectionMatrix(uiCamera.combined);
        // 此处可以添加具体的UI渲染代码，如血条、技能栏等
        batch.begin();
        player.filpCheck(player.getPlayerSprite(), player.getPlayerController(), batch).draw(batch);
        item.render(batch, player);
        lightningMagic.magicRender(batch, player);
        batch.end();
        uiBatch.begin();
        lightningMagic.magicObtain(uiBatch, player); //检测拾取
        player.render(uiBatch,deltaTime);
        uiBatch.end();
        // 调试渲染
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            worldManager.getDebugRenderer().render(worldManager.getWorld(), cameraManager.getCamera().combined);
        }
//        if(Gdx.input.isKeyPressed(Input.Keys.P)){
//            System.out.println("HP"+player.HP);
//            System.out.println("MP"+player.MP);
//            System.out.println("DEF"+player.DEF);
//        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.K)){
//                // 触发纹理更新
//                player.HPtexture = null;
//                player.MPtexture = null;
//                player.DEFtexture = null;
//            player.HP-=10;
//            player.DEF-=4;
//            player.MP+=10;
//            if(player.MP>50){
//                player.MP=50;
//            }
//            if(player.HP<=0){
//                player.HP=0;
//            }
//            if(player.DEF<=0){
//                player.DEF=0;
//            }
//        }
        // 按R键生成新地图
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            try {
                // 清理旧地图
                if (mapManager != null) {
                    mapManager.dispose();
                }
                // 生成新地图
                mapManager = new MapManager(mapGeneration.generateRandomMap(), Config.PIXELS_PER_METER/512, worldManager.getWorld());
                // 重置玩家位置到地图中心
                player.getBody().setTransform(WORLD_WIDTH, WORLD_HEIGHT, 0);
                Gdx.app.debug("SimpleGame", "New map generated successfully");
            } catch (Exception e) {
                Gdx.app.error("SimpleGame", "Error generating new map: " + e.getMessage());
            }
        }
    }
    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / height;
        cameraManager.getCamera().viewportWidth = 2* WORLD_WIDTH;
        cameraManager.getCamera().viewportHeight = 2* WORLD_WIDTH / aspectRatio;
        cameraManager.getCamera().update();
    }
    @Override
    public void dispose() {
        if (mapManager != null) mapManager.dispose();
        if(player!=null) player.dispose();
        if (batch != null) batch.dispose();
        if(uiBatch!=null) uiBatch.dispose();
        if (worldManager.getWorld() != null) worldManager.getWorld().dispose();
        if (resourceManager != null) resourceManager.dispose();
        if (worldManager.getDebugRenderer() != null) worldManager.getDebugRenderer().dispose();
        if(lightningMagic!=null){lightningMagic.dispose();}
    }
}
