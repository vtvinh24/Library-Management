package group1.util;

public class Box<T>
{
    private T value;

    public Box(T value)
    {
        setValue(value);
    }

    public T getValue()
    {
        return value;
    }
    public void setValue(T value)
    {
        this.value = value;
    }
}
