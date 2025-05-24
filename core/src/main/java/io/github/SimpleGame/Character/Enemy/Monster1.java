package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Tool.AnimationTool;

public class Monster1 extends Enemy{
    private Sprite enemySprite;
    private State currentState;
    private Player player;
    protected Body enemyBody;
    private monster1Animation animation;
    public Monster1(){}
    public Monster1(World world, Player player, float x, float y)
    {
        super(world,player,x,y);
        this.player = player;
        this.currentState = State.PATROL;
        this.animation = new monster1Animation(x,y);

    }
    public void render(SpriteBatch batch,Player player,World world){
        animation.render(batch,player,world);
    }
}
class monster1Animation extends Monster1 implements EnemyAnimationHandler{
    private Texture PatrolTexture;
    private Texture ChaseTexture;
    private Texture AttackTexture;
    private Texture DieTexture;
    private Texture totaltexture;
    private AnimationTool[] animationTools;
    protected float X;
    protected float Y;
    public monster1Animation(float x,float y){
        this.X=x;
        this.Y=y;
        animationTools  = new AnimationTool[4];
        load();
    }
    @Override
    public void load() {
        assetManager.load("Enemy/NightBorne-Normal-sheet.png", Texture.class);
        assetManager.load("Sprites/BasePlayer/Player-run.png", Texture.class);
        assetManager.load("Enemy/NightBorne-Attack-sheet.png",Texture.class);
        assetManager.finishLoading();
        PatrolTexture = assetManager.get("Enemy/NightBorne-Normal-sheet.png", Texture.class);
        ChaseTexture = assetManager.get("Sprites/BasePlayer/Player-run.png", Texture.class);
        AttackTexture = assetManager.get("Enemy/NightBorne-Attack-sheet.png",Texture.class);
        animationTools[0] = new AnimationTool();
        animationTools[0].create("Patrol",PatrolTexture,1,9,0.15f);
        animationTools[1] = new AnimationTool();
        animationTools[1].create("Chase",ChaseTexture,5,6,0.1f);
        animationTools[2] = new AnimationTool();
        animationTools[2].create("Attack",AttackTexture,1,12,0.05f);
    }

    @Override
    public void render(SpriteBatch batch,Player player,World world) {
        if(Gdx.input.isKeyPressed(Input.Keys.L)){
            animationTools[1].render(batch,X,Y,0.1f,true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            animationTools[2].render(batch,X,Y,0.2f,true);
        } else{
            animationTools[0].render(batch,X, Y, 0.2f,true);
        }
    }

    @Override
    public void dispose() {

    }
}
