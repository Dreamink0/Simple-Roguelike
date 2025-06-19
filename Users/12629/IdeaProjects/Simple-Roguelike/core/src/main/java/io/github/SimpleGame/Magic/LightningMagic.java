    public float getDamage(){
        // 如果魔法伤害应动态依赖玩家属性（如攻击力），可以改为：
        return player.getAttributeHandler().getDamage(); // 假设 player 是魔法类的成员变量
    }