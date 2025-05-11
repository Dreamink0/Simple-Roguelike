package io.github.SimpleGame.Tool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class Animation_Tool {
    private Map<String,Animation<TextureRegion>> animations=new HashMap<>();
    private Map<String, Float> stateTimes=new HashMap<>();
    public Animation<TextureRegion> Create(String name,Texture texture, int rows, int cols){
        int Width = (int)(texture.getWidth()/cols);
        int Height = (int)(texture.getHeight()/rows);
        TextureRegion[][] tmp = TextureRegion.split(texture, Width, Height);
        TextureRegion[] frames = new TextureRegion[tmp.length*tmp[0].length];
        int index = 0;
        for(TextureRegion[] Row:tmp){
            for(TextureRegion region:Row){
                frames[index++] = region;
            }
        }
        Animation<TextureRegion> animation= new Animation<>(0.02f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        animations.put(name,animation);
        stateTimes.put(name,0f);
        return animation;
    }
    public void update(String name, float delta) {
        if (stateTimes.containsKey(name)) {
            stateTimes.put(name, stateTimes.get(name) + delta);
        }
    }
    //统一方法名，调用的时候就不用一直写变量名加.getKeyFrame了
    public TextureRegion getKeyFrame(String name,boolean loop) {

        if (!animations.containsKey(name)) {
            System.out.println("ERROR: Animation "+name+" not found!");
        }

       Animation<TextureRegion> animation = animations.get(name);
        float stateTime = stateTimes.get(name);
        return animation.getKeyFrame(stateTime,loop);
    }
}
