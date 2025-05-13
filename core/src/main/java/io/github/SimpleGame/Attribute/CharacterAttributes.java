package io.github.SimpleGame.Attribute;

public class CharacterAttributes {
    //该类为通用的设置基础属性的类
    private float HP;
    private float MP;
    private float DEF;
    public CharacterAttributes(float HP, float MP, float DEF){this.HP = HP; this.MP = MP; this.DEF = DEF;}
    public float getHP() {return HP;}
    public void setHP(float HP) {this.HP = HP;}
    public float getDEF() {return DEF;}
    public void setDEF(float DEF) {this.DEF = DEF;}
    public float getMP() {return MP;}
    public void setMP(float MP) {this.MP = MP;}
}
