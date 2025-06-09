package io.github.SimpleGame.Tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.SimpleGame.Character.Enemy.Enemy;
import io.github.SimpleGame.Character.Enemy.EnemyState;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Item.Weapon;
import io.github.SimpleGame.Magic.Dark;
import io.github.SimpleGame.Magic.Magic;
import io.github.SimpleGame.Magic.Thunder;
import io.github.SimpleGame.Resource.SoundManager;
import io.github.SimpleGame.Resource.WorldManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static io.github.SimpleGame.Character.Enemy.Enemy.State.HURT;

public class Listener{
    public static boolean equip;
    public static boolean LightningMagic_Flag;
    public static boolean attack_Flag;
    public static boolean wall_Flag;
    public static float lightningTimer=0;
    public static float lightningCooldown=0.15f;
    public static float DarkTimer=0;
    public static float DarkCooldown=0.5f;
    public static void Bound(World world, Player player){
        world.setContactListener(new ContactListener() {
            final float Time = (float) Math.min(Gdx.graphics.getDeltaTime(),0.25);
            @Override
            public void beginContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

//                System.out.println("Collision detected!");
//                System.out.println("BodyA: " + (bodyA != null ? bodyA.getUserData() : "null"));
//                System.out.println("BodyB: " + (bodyB != null ? bodyB.getUserData() : "null"));
//
//                if (bodyA == null || bodyB == null) {
//                    System.out.println("One of the bodies is null!"); // 调试信息
//                    return;
//

                // 检测玩家是否与墙壁接触
                wall_Flag = (bodyA.getUserData() != null && bodyA.getUserData().equals("player") &&
                    bodyB.getUserData() != null && bodyB.getUserData().equals("wall")) ||
                    (bodyA.getUserData() != null && bodyA.getUserData().equals("wall") &&
                        bodyB.getUserData() != null && bodyB.getUserData().equals("player"));
                if ((bodyA.getUserData() != null && bodyA.getUserData().equals("player") &&
                    bodyB.getUserData() != null && bodyB.getUserData().equals("weapon")) ||
                    (bodyA.getUserData() != null && bodyA.getUserData().equals("weapon") &&
                        bodyB.getUserData() != null && bodyB.getUserData().equals("player"))) {
                    Flag_equip();
                }

                if ((bodyA.getUserData() != null && bodyA.getUserData().equals("player") &&
                    bodyB.getUserData() != null && bodyB.getUserData().equals("Lightning")) ||
                    (bodyA.getUserData() != null && bodyA.getUserData().equals("Lightning") &&
                        bodyB.getUserData() != null && bodyB.getUserData().equals("player"))) {
                    Flag_LightningMagic();
                }

                if ((bodyA.getUserData() != null && bodyA.getUserData().equals("player") &&
                    bodyB.getUserData() != null && bodyB.getUserData().equals("enemy")) ||
                    (bodyA.getUserData() != null && bodyA.getUserData().equals("enemy") &&
                    bodyB.getUserData() != null && bodyB.getUserData().equals("player"))) {
                    Flag_attack();
                }
                if ((bodyA.getUserData() != null && bodyA.getUserData() instanceof Magic&&
                     bodyB.getUserData() != null && bodyB.getUserData() instanceof Enemy) ||
                    (bodyA.getUserData() != null && bodyA.getUserData() instanceof Enemy &&
                     bodyB.getUserData() != null && bodyB.getUserData() instanceof Magic)) {
                    Enemy enemy = null;
                    Magic magic = null;
                    if (bodyA.getUserData() instanceof Enemy) {
                        enemy = (Enemy) bodyA.getUserData();
                    } else if (bodyB.getUserData() instanceof Enemy) {
                        enemy = (Enemy) bodyB.getUserData();
                    }
                    if (bodyA.getUserData() instanceof Magic) {
                        magic = (Magic) bodyA.getUserData();
                    } else if (bodyB.getUserData() instanceof Magic) {
                        magic = (Magic) bodyB.getUserData();
                    }
                    if (magic instanceof Thunder) {
                        if (enemy != null) {
                            enemy.getEnemyState().currentState = HURT;
                        }
                        if (enemy.getEnemyState().currentState == HURT) {
                            SoundManager.playSound("enemyHit");
                        }
                        if (lightningTimer < 0) {
                            if (enemy != null) {
                                enemy.setHP(magic);
                            }
                            lightningTimer = lightningCooldown;
                        }
                        lightningTimer -= Time;
                    }
                    if (magic instanceof Dark) {
                        if (enemy != null) {
                            enemy.getEnemyState().currentState = HURT;
                        }
                        if (enemy.getEnemyState().currentState == HURT) {
                            SoundManager.playSound("enemyHit");
                        }
                        if (DarkTimer < 0) {
                            if (enemy != null) {
                                enemy.setHP(magic);
                            }
                            DarkTimer = DarkCooldown;
                        }
                        DarkTimer -= Time;
                    }
                }
            }
            @Override
            public void endContact(Contact contact) {}

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
    }
    private static void Flag_equip() {equip=true;}
    private static void Flag_LightningMagic() {LightningMagic_Flag=true;}
    private static void  Flag_attack(){attack_Flag=true;};
}
