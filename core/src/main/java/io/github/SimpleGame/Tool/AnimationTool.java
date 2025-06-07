package io.github.SimpleGame.Tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AnimationTool {
    /*
     * 用法说明：
     * 首先需要创建
     * Texture texture:一个sheet图，里面的行和列表示不同帧的图
     * 简化版步骤：
     * 1. 创建纹理图集：Texture texture = new Texture("animation.png");
     * 2. 创建动画：animationTool.Create("动画名称", 纹理对象, 行数, 列数,每帧持续时间，推荐0.08左右即可);
     * 3. 渲染：animationTool.render(SpriteBatch batch,float x,float y,float scale,Boolean loop);
     * 具体版：
     * 1. 创建纹理图集：Texture texture = new Texture("animation.png");
     * 2. 实例化工具类：Animation_Tool animationTool = new Animation_Tool();
     * 3. 创建动画：animationTool.Create("动画名称", 纹理对象, 行数, 列数,每帧持续时间，推荐0.08左右即可);
     *    - "动画名称"：为动画指定一个唯一标识,名称这个可以随便取
     *    - 纹理对象：包含动画帧的纹理资源,从你的资源库获得
     *    - 行数/列数：纹理图集中的行列分割数量,最好稍微输入的比行和列比原图行或者列多1,否则会出现多个帧同时出现在屏幕
     * 4. 渲染: animationTool.render(SpriteBatch batch,float x,float y,float scale,Boolean loop);
     *    - 参数：SpriteBatch batch(来自Main程序的SpriteBatch对象)
     *          float x,float y,(坐标)
     *          float scale,(缩放)
     *          Boolean loop是否循环
     * 5. 资源释放：animationTool.dispose();
     *    - 当不再需要动画时调用以释放纹理资源
     */
    private Map<String, Animation<TextureRegion>> animations=new HashMap<>();
    public Map<String, Float> stateTimes=new HashMap<>();
    private float frameDuration;
    private String framename;
    private Animation<TextureRegion> animation;
    private TextureRegion textureRegion;
    public float stateTime = 0f;
    public void create(String name, Texture texture, int rows, int cols,float frameDuration){
        this.frameDuration=frameDuration;
        this.framename=name;
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
        this.animation= new Animation<>(frameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        animations.put(name,animation);
        stateTimes.put(name,0f);
    }
    public void update() {
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        stateTime+=deltaTime;
        if (stateTimes.containsKey(framename)) {
            stateTimes.put(framename, stateTimes.get(framename) + deltaTime);
        }
    }
    //统一方法名，调用的时候就不用一直写变量名加.getKeyFrame了
    private void getKeyFrame(boolean loop) {

        if (!animations.containsKey(framename)) {
            System.out.println("ERROR: Animation "+framename+" not found!");
        }

        animation = animations.get(framename);
        float stateTime = stateTimes.get(framename);
        this.textureRegion=animation.getKeyFrame(stateTime,loop);
    }
    public TextureRegion getCurrentFrame() {
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        stateTime+=deltaTime;
        return animation.getKeyFrame(stateTime);
    }
    public boolean isAnimationFinished() {
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        stateTime+=deltaTime;
        return animation.isAnimationFinished(stateTime);
    }
    public void render(SpriteBatch batch, float x, float y, float scale, Boolean loop) {
        update();
        getKeyFrame(loop);
        float width = textureRegion.getRegionWidth() * scale;
        float height = textureRegion.getRegionHeight() * scale;
        // 使用中心点作为锚点进行绘制，避免图像偏移
        batch.draw(textureRegion, x - width / 2, y - height / 2, width, height);
    }
    public void render(SpriteBatch batch, float x, float y, float scale, boolean loop, boolean flip){
        update();
        getKeyFrame(loop);
        float width = textureRegion.getRegionWidth() * scale;
        float height = textureRegion.getRegionHeight() * scale;
        if(flip){
            batch.draw(textureRegion, x + width / 2, y - height / 2, -width, height);
        }else{
            batch.draw(textureRegion, x - width / 2, y - height / 2, width, height);
        }
    }
    public void render(SpriteBatch batch,float x,float y,float scale,Boolean loop,float rotationAngle){
        update();
        getKeyFrame(loop);
        float width=textureRegion.getRegionWidth()*scale;
        float height=textureRegion.getRegionHeight()*scale;
        batch.draw(textureRegion, x - width / 2, y - height / 2, width/2, height/2, width, height, scale, scale, rotationAngle);
    }
    public void render(SpriteBatch batch,float x,float y,float scaleX,float scaleY,Boolean loop){
        update();
        getKeyFrame(loop);
        float width=textureRegion.getRegionWidth()*scaleX;
        float height=textureRegion.getRegionHeight()*scaleY;
        batch.draw(textureRegion, x - width / 2, y - height / 2, width, height);
    }
    public void dispose(){
        for(String name:animations.keySet()){
            animations.get(name).getKeyFrames()[0].getTexture().dispose();
        }
        animations.clear();
        stateTimes.clear();
        animations=null;
        stateTimes=null;
    }
    public TextureRegion KeyFrame(boolean loop) {

        if (!animations.containsKey(framename)) {
            System.out.println("ERROR: Animation "+framename+" not found!");
        }

        animation = animations.get(framename);
        float stateTime = stateTimes.get(framename);
        return animation.getKeyFrame(stateTime,loop);
    }
    public TextureRegion getTextureRegion(){
        return textureRegion;
    }
    public Animation<TextureRegion> getAnimation(){
        return animation;
    }
    public String getFramename(){
        return framename;
    }
    public float getFrameDuration(){
        return frameDuration;
    }

    public void resetStateTime() {
        this.stateTime = 0f;
    }
}
