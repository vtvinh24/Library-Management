package group1.util.lists;

import java.util.ArrayList;
import java.util.function.Function;

public class BinarySearchNode<T extends Comparable>
{
    private T value;
    private BinarySearchNode<T> left;
    private BinarySearchNode<T> right;

    public BinarySearchNode(T value)
    {
        this.value = value;
        left = right = null;
    }

    public T getValue()
    {
        return value;
    }
    public void setValue(T value)
    {
        this.value = value;
    }

    public BinarySearchNode<T> getLeft()
    {
        return left;
    }
    public void setLeft(BinarySearchNode<T> left)
    {
        this.left = left;
    }

    public BinarySearchNode<T> getRight()
    {
        return right;
    }
    public void setRight(BinarySearchNode<T> right)
    {
        this.right = right;
    }
}
