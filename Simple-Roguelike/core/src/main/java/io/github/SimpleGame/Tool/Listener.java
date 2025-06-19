
// 修改后的逻辑
if (enemy != null && magic != null) {
    // 直接应用伤害避免递归触发
    enemy.setHP(magic);
    // 单次播放音效
    if (enemy.getEnemyState().currentState != HURT) {
        enemy.getEnemyState().currentState = HURT;
        SoundManager.playSound("enemyHit");
    }
}
