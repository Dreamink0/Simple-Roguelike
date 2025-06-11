package io.github.SimpleGame.Item;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import io.github.SimpleGame.Magic.BodyPool;
import io.github.SimpleGame.Tool.AnimationTool;

public class WeaponEffectsTextureManager {
    protected AnimationTool[] animation;
    protected Texture textures;
    private static final String[] prePath={
        "Effects/WeaponEffects/Weapon0/",
        "Effects/WeaponEffects/Weapon1/",
    };

    public void create(int ID,int weaponID){
        if(ID==0){
            if(weaponID==0){
                Texture effects = new Texture(prePath[0] + 0 + ".png");
                animation = new AnimationTool[1];
                animation[0] = new AnimationTool();
                animation[0].create("Weapon0", effects,1,28, 0.05f);
            }
            if(weaponID==1){
                Texture effects = new Texture(prePath[0] + 1 + ".png");
                animation = new AnimationTool[1];
                animation[0] = new AnimationTool();
                animation[0].create("Weapon1", effects,3,4, 0.08f);
            }
            if(weaponID==2){
                Texture texture0 = new Texture(prePath[0]+2+"b.png");
                texture0.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                animation = new AnimationTool[1];
                animation[0] = new AnimationTool();
                animation[0].create("Weapon2",texture0,3,4,0.05f);
            }
            if(weaponID==3){
                Texture texture0 = new Texture(prePath[0]+3+"a.png");
                texture0.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                animation = new AnimationTool[1];
                animation[0] = new AnimationTool();
                animation[0].create("Weapon3",texture0,1,10,0.05f);
            }
            if(weaponID==4){
                Texture texture0 = new Texture(prePath[0]+4+"a.png");
                Texture texture1 = new Texture(prePath[0]+4+"b.png");
                texture0.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                texture1.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                animation = new AnimationTool[2];
                animation[0] = new AnimationTool();
                animation[1] = new AnimationTool();
                animation[0].create("Weapon4",texture0,1,37,0.05f);
                animation[1].create("Weapon4",texture1,1,24,0.05f);
            }
            if(weaponID==5){
                Texture texture0 = new Texture(prePath[0]+5+".png");
                texture0.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                animation = new AnimationTool[1];
                animation[0] = new AnimationTool();
                animation[0].create("Weapon5",texture0,1,4,0.1f);
            }
            if(weaponID==6){
                Texture texture0 = new Texture(prePath[0]+6+".png");
                texture0.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                animation = new AnimationTool[10];
                for(int i=0;i<10;i++){
                    animation[i] = new AnimationTool();
                }
                for(int i=0;i<10;i++){
                    animation[i].create("Weapon6_"+i,texture0,1,8,0.7f*0.01f);
                }
            }
            if(weaponID==7){
                Texture texture0 = new Texture(prePath[0]+7+"a.png");
                Texture texture1 = new Texture(prePath[0]+7+"b.png");
                Texture texture2 = new Texture(prePath[0]+7+"c.png");
                Texture texture3 = new Texture(prePath[0]+7+"d.png");
                texture1.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                texture0.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                texture2.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                texture3.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                animation = new AnimationTool[4];
                animation[0] = new AnimationTool();
                animation[1] = new AnimationTool();
                animation[2] = new AnimationTool();
                animation[3] = new AnimationTool();
                animation[0].create("Weapon7_"+0,texture0,11,1,1f);
                animation[1].create("Weapon7_"+1,texture1,4,5,1f);
                animation[2].create("Weapon7_"+2,texture2,4,5,1f);
                animation[3].create("Weapon7_"+3,texture3,20,1,1f);
            }
            if(weaponID==8){}
            if(weaponID==9){}
            if(weaponID==10){}
            if(weaponID==11){}
            if(weaponID==12){
                Texture texture0 = new Texture(prePath[0]+12+"a.png");
                texture0.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                animation = new AnimationTool[2];
                animation[0] = new AnimationTool();
                animation[1] = new AnimationTool();
                animation[0].create("Weapon12",texture0,3,3,0.03f);
            }
            if(weaponID==13){}
            if(weaponID==14){}
            if(weaponID==15){}
            if(weaponID==16){}
            if(weaponID==17){}
            if(weaponID==18){}
            if(weaponID==19){}
            if(weaponID==20){}
            if(weaponID==21){}
            if(weaponID==22){}
            if(weaponID==23){
                Texture texture0 = new Texture(prePath[0]+23+"a.png");
                Texture texture1 = new Texture(prePath[0]+23+"b.png");
                Texture texture2 = new Texture(prePath[0]+23+"c.png");
                Texture texture3 = new Texture(prePath[0]+23+"d.png");
                Texture texture4 = new Texture(prePath[0]+23+"e.png");
                texture0.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                texture1.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                texture2.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                texture3.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                texture4.unsafeSetFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                animation = new AnimationTool[5];
                animation[0] = new AnimationTool();
                animation[1] = new AnimationTool();
                animation[2] = new AnimationTool();
                animation[3] = new AnimationTool();
                animation[4] = new AnimationTool();
                animation[0].create("Weapon23",texture0,5,2,0.05f);
                animation[1].create("Weapon23",texture1,5,2,0.06f);
                animation[2].create("Weapon23",texture2,4,2,0.077f);
                animation[3].create("Weapon23",texture3,5,2,0.05f);
                animation[4].create("Weapon23",texture4,4,2,0.081f);
            }
            if(weaponID==24){}
            if(weaponID==25){}
            if(weaponID==26){}
            if(weaponID==27){}
            if(weaponID==28){}
            if(weaponID==29){}
        }
    }

    public AnimationTool[] getAnimation() {
        return animation;
    }
}
