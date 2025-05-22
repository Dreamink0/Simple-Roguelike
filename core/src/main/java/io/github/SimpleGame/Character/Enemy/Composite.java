package io.github.SimpleGame.Character.Enemy;

import java.util.LinkedList;

public abstract class Composite extends Behavior {
    protected LinkedList<Behavior> children;
    public Composite()
    {
        children = new LinkedList<Behavior>();
    }
    //添加子节点
    @Override
    public void AddChild(Behavior child)
    {
        children.add(child);
    }
    //删除子节点
    public void RemoveChild(Behavior child){
        children.remove(child);
    }
    //清空子节点
    public void ClearChildren(){
        children.clear();
    }
}
