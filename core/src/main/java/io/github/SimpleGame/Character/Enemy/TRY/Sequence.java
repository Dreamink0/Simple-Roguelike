package io.github.SimpleGame.Character.Enemy.TRY;

import java.util.LinkedList;

public class Sequence extends Behavior{
    protected LinkedList<Behavior> currentChild = new LinkedList<>();
    protected int currentChildIndex = 0;

    @Override
    protected void OnInitialize() {
        currentChildIndex = 0;
    }

    @Override
    protected Status OnUpdate() {
        while (true) {
            //记录当前节点返回状态
            //如果当前节点返回失败，则返回失败
            //如果当前节点返回成功，则进入下一个节点
            //如果当前节点返回运行中，则返回运行中,就直接返回该结果
            var s = currentChild.get(currentChildIndex).Tick();
            if(s != Status.Success) return s;currentChildIndex++;
            if (currentChildIndex >= currentChild.size()) return Status.Success;
        }
    }

    @Override
    public void AddChild(Behavior child) {
        if (child != null) {
            currentChild.add(child);
        }
    }
}
