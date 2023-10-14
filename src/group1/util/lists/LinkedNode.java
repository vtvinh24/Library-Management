package group1.util.lists;

public class LinkedNode<T extends Comparable> 
{
    private T value;
    private LinkedNode<T> next;
    private LinkedNode<T> prev;

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
    

    public LinkedNode<T> getPrev() 
    {
        return prev;
    }
    public void setPrev(LinkedNode<T> prev) 
    {
        this.prev = prev;
    }
    
}
