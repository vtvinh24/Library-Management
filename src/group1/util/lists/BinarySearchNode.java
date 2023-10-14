package group1.util.lists;

import java.util.function.Function;

public class BinarySearchNode<T extends Comparable> implements ListAddable<T>
{
    private T value;
    private BinarySearchNode<T> left;
    private BinarySearchNode<T> right;

    public <U extends Comparable> void add(T value, Function<T, U> selector)
    {
        throw new UnsupportedOperationException();
    }
    @Override
    public void add(T value)
    {
        add(value, (v) -> v);
    }
    
    public <U extends Comparable> BinarySearchNode<T> find(T value, Function<T, U> selector)
    {
        throw new UnsupportedOperationException();
    }
    public BinarySearchNode<T> find(T value) 
    {
        return find(value, (v) -> v);
    }
    
    public <U extends Comparable> void delete(T value, Function<T, U> selector)
    {
        throw new UnsupportedOperationException();
    }
    public void delete(T value) 
    {
        find(value, (v) -> v);
    }
}
