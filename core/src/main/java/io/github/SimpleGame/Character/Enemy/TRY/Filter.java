package io.github.SimpleGame.Character.Enemy.TRY;

public class Filter extends Sequence{
    public void addCondition(Behavior condition){
        AddChild(condition);
    }
    public void addAction(Behavior action){
        AddChild(action);
    }
}
