package io.github.SimpleGame.Character.Enemy.TRY;

public class Monitor extends Parallel{
    public Monitor(Policy mSuccessPolicy, Policy mFailurePolicy) {
        super(mSuccessPolicy, mFailurePolicy);
    }
    public void AddCondition(Behavior condition) {
        children.addFirst(condition);
    }
    public void AddAction(Behavior action){
        children.addLast(action);
    }
}
