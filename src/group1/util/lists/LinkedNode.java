package group1.util.lists;

public class LinkedNode<T extends Comparable<T>> 
{
    private T value;
    private LinkedNode<T> next;

    public LinkedNode(T value, LinkedNode<T> next)
    {
        this.value = value;
        this.next = next;
    }

    public T getValue()
    {
        return value;
    }
    public void setValue(T value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("Node's value cannot be null.");
        }
        
        this.value = value;
    }

    public LinkedNode<T> getNext()
    {
        return next;
    }
    public void setNext(LinkedNode<T> next)
    {
        this.next = next;
    }
}
