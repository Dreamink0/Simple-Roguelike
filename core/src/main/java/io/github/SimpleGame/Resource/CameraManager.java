package io.github.SimpleGame.Resource;

import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.SimpleGame.Character.Player.PlayerController;
import io.github.SimpleGame.Config;

import static io.github.SimpleGame.Config.WORLD_HEIGHT;
import static io.github.SimpleGame.Config.WORLD_WIDTH;

public class CameraManager {
    private OrthographicCamera camera;
    public OrthographicCamera uiCamera;
    public CameraManager() {
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, Config.WORLD_WIDTH, Config.WORLD_HEIGHT);
        camera.position.set(Config.WORLD_WIDTH/2, Config.WORLD_HEIGHT/2, 0);
        camera.update();
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        uiCamera.position.set(Config.WORLD_WIDTH/2, Config.WORLD_HEIGHT/2, 0);
        uiCamera.update();
    }
    public OrthographicCamera getCamera(PlayerController playerController) {
        this.camera.position.set(playerController.getPosition().x, playerController.getPosition().y, 0);
        this.camera.update();
        return camera;
    }
    public OrthographicCamera getCamera() {
        return camera;
    }
    public OrthographicCamera getUiCamera() {return uiCamera;}
}
