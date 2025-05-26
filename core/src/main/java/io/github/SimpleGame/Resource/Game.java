package io.github.SimpleGame.Resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Character.Player.PlayerController;
import io.github.SimpleGame.Config;

import static io.github.SimpleGame.Config.WORLD_HEIGHT;
import static io.github.SimpleGame.Config.WORLD_WIDTH;

public class Game{
    //资源
    private WorldManager worldManager;
    private CameraManager cameraManager;
    private ResourceManager resourceManager;
    private MapManager mapManager;
    private MapGeneration mapGeneration;
    public static World world;
    public static Player player;
    public static SpriteBatch batch;
    public static SpriteBatch UIbatch;
    //接口
    private PhysicHandler physicHandler;
    private GameRenderHandler gameRenderHandler;
    // 对象池
    private final Pool<SpriteBatch> batchPool = new Pool<SpriteBatch>(2) {
        @Override
        protected SpriteBatch newObject() {
            return new SpriteBatch();
        }
    };
    //加载资源初始化
    public void initialize() {
        worldManager = new WorldManager();
        cameraManager = new CameraManager();
        resourceManager = ResourceManager.getInstance();
        resourceManager.loadResources();
        batch = batchPool.obtain();
        UIbatch = batchPool.obtain();
        world =  worldManager.getWorld();
        physicHandler = new GameTimeUpdates();
        gameRenderHandler = new GameRender();
    }
    //生成地图，后续生成逻辑可能也在这里实现吧大概
    public void Generation(){
        mapGeneration = new MapGeneration();
        mapManager = resourceManager.getMapManager(world);
   }
   //生成地图完读取玩家信息
   public void readPlayerData(){
        player = new Player(world, WORLD_WIDTH, WORLD_HEIGHT);
   }
   //游戏主要内容的图像渲染
    public void render(){
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(),5);
        //更新物理世界
        physicHandler.update(world,deltaTime);
        PlayerController controller = player.getPlayerController();

        player.setAction(controller,world).update();
        cameraManager.getCamera(controller);
        mapManager.setView(cameraManager.getCamera());
        mapManager.render(batch);

        batch.setProjectionMatrix(cameraManager.getCamera().combined);
        UIbatch.setProjectionMatrix(cameraManager.getUiCamera().combined);

        gameRenderHandler.render(batch,UIbatch);
        TestKey();

    }
    //视野的放缩
    public void resize(int width, int height) {
        float aspectRatio = (float) width / height;
        cameraManager.getCamera().viewportWidth = 2 * WORLD_WIDTH;
        cameraManager.getCamera().viewportHeight = 2 * WORLD_WIDTH / aspectRatio;
        cameraManager.getCamera().update();
    }
    //资源的释放
    public void dispose(){
        if (mapManager != null) mapManager.dispose();
        if (player != null) player.dispose();
        if (resourceManager != null) resourceManager.dispose();
        if (batch != null) {
            batch.dispose();
            batchPool.free(batch);
        }
        if (UIbatch != null) {
            UIbatch.dispose();
            batchPool.free(UIbatch);
        }
        batchPool.clear();
    }
    public void TestKey(){
        //测试内存:
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            long javaHeap = Gdx.app.getJavaHeap() / (1024 * 1024); // 转换为MB
            long nativeHeap = Gdx.app.getNativeHeap() / (1024 * 1024); // 转换为MB
            Gdx.app.log("Memory", "Free memory: " + javaHeap + " MB");
            Gdx.app.log("Memory", "Native heap free: " + nativeHeap + " MB");
            System.gc();
        }
        //模拟掉血:
        if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
            player.getAttributeHandler().setHP(player.getAttributeHandler().getMaxHP() - 10f);
            player.getAttributeHandler().setMP(player.getAttributeHandler().getMaxMP() - 20f);
            player.getAttributeHandler().setDEF(player.getAttributeHandler().getMaxDEF() - 10f);
            player.getPlayerTextureHandler().get();
        }
        //获得血量信息:
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)){
            System.out.println("HP:"+player.getAttributeHandler().getMaxHP());
            System.out.println("MP:"+player.getAttributeHandler().getMaxMP());
            System.out.println("DEF:"+player.getAttributeHandler().getMaxDEF());
            System.out.println("Damage:"+player.getAttributeHandler().getDamage());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            worldManager.getDebugRenderer().render(world, cameraManager.getCamera().combined);
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
}
