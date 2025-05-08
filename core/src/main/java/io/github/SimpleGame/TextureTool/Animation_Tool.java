package io.github.SimpleGame.TextureTool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animation_Tool {
    private Animation<TextureRegion> animation;
    private float stateTime;
    public Animation_Tool(Texture texture, int rows,int cols,float stateTime){
        animation = Texture_Sheet_Tool.cutting(texture,rows,cols);
        this.stateTime = 0f;
    }
    public void update(float delta){
        stateTime += delta;
    }
    public TextureRegion Current_Frame() {
        return animation.getKeyFrame(stateTime, true);
    }
}
