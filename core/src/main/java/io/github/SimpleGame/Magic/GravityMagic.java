//package io.github.SimpleGame.Magic;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.physics.box2d.World;
//import io.github.SimpleGame.Character.Player.Player;
//import io.github.SimpleGame.Tool.AnimationTool;
//
//public class GravityMagic extends Magic{
//    private AnimationTool animationTool;
//    private Texture gravityMagicTexture;
//    private Texture smokeTexture;
//    float totalTime = 0;
//    public GravityMagic()
//    {
//        animationTool=new AnimationTool();
//        load();
//        animationTool.create("GravityMagic",gravityMagicTexture,5,4,0.2f);
//    }
//
//
//    @Override
//    public void Obtain(SpriteBatch batch, SpriteBatch UIbatch, Player player) {
//
//    }
//
//    @Override
//    public void Render(SpriteBatch batch, Player player) {
//        if(Gdx.input.isKeyPressed(Input.Keys.G)) {
//            totalTime += Gdx.graphics.getDeltaTime();
//            float r = (float) Math.abs(Math.sin(totalTime * Math.PI * 1.5));
//            float g = (float) Math.abs(Math.sin(totalTime * Math.PI * 1.5 + 2 * Math.PI / 3));
//            float b = (float) Math.abs(Math.sin(totalTime * Math.PI * 1.5 + 4 * Math.PI / 3));
//            Color dynamicColor = new Color(r, g, b, 1);
//            batch.setColor(dynamicColor);
//            animationTool.render(batch, player.getX(), player.getY(), 0.1f, true);
//            animationTool.render(batch, player.getX(), player.getY(), 0.3f, true,90);
//            batch.setColor(Color.WHITE);
//        }
//    }
//    public void load(){
//        assetManager.load("Magic/Gravity/Gravity-Sheet.png", Texture.class);
//        assetManager.finishLoading();
//        gravityMagicTexture=assetManager.get("Magic/Gravity/Gravity-Sheet.png",Texture.class);
//
//    }
//    public void dispose(){
//        gravityMagicTexture.dispose();
//    }
//}
