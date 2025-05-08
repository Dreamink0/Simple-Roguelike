package io.github.SimpleGame.TextureTool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Texture_Sheet_Tool {
    private TextureRegion FRM;
    //自动处理动画，将Sheet文件拆好,要知道图片的行列大小
    public static Animation cutting( Texture texture,int rows,int cols){
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
        Animation animation= new Animation<>(0.1f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }
}
