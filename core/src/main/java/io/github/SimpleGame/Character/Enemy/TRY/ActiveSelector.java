package io.github.SimpleGame.Character.Enemy.TRY;

public class ActiveSelector extends Selector{
    @Override
    public Status OnUpdate() {
        var prevChild = currentChild.get(currentChildIndex);
        var res = super.OnUpdate();
        if (currentChildIndex < currentChild.size() && prevChild != currentChild.get(currentChildIndex)) {
            prevChild.Abort();
        }
        return res;
    }
}
