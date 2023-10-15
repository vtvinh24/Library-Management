package group1.libmgmt.backend;

import group1.util.CodeDiscriminated;
import group1.util.Helpers;
import group1.util.StringListSerializable;
import java.util.ArrayList;
import java.util.List;

public final class Lending implements StringListSerializable, Comparable<Lending>
{
    private String bookCode;
    private String readerCode;
    // initially not delivered
    private int state = NOT_DELIVERED;
    
    public static final int NOT_DELIVERED = 1;
    public static final int NOT_RETURNED = 2;
    public static final int RETURNED = 3;

    public Lending() {;}
    public Lending(String bookCode, String readerCode, int state)
    {
        this.setBookCode(bookCode);
        this.setReaderCode(readerCode);
        this.setState(state);
    }
    public Lending(Book book, Reader reader, int state)
    {
        this.setBookCode(book.getCode());
        this.setReaderCode(reader.getCode());
        this.setState(state);
    }

    public String getBookCode()
    {
        return bookCode;
    }
    public void setBookCode(String bookCode)
    {
        Book.throwIfInvalidCode(bookCode);
        bookCode = bookCode.toUpperCase();
        this.bookCode = bookCode;
    }

    public String getReaderCode()
    {
        return readerCode;
    }
    public void setReaderCode(String readerCode)
    {
        Reader.throwIfInvalidCode(readerCode);
        readerCode = readerCode.toUpperCase();
        this.readerCode = readerCode;
    }

    public int getState()
    {
        return state;
    }
    public void setState(int state)
    {
        throwIfInvalidState(state);
        this.state = state;
    }
    public static void throwIfInvalidState(int state)
    {
        if (state < 1 || state > 3)
        {
            throw new IllegalArgumentException("Invalid lending state.");
        }
    }
    public static String stateToString(int state)
    {
        switch (state)
        {
            case NOT_DELIVERED: return "Not delivered";
            case RETURNED: return "Returned";
            case NOT_RETURNED: return "Not returned";
            default: throw new IllegalArgumentException("Invalid lending state.");
        }
    }
    
    @Override
    public String toString()
    {
        return String.format("Lending: %s to %s", this.getBookCode(), this.getReaderCode());
    }   
    
    @Override
    public List<String> serialize()
    {
        ArrayList<String> output = new ArrayList<>();
        output.add(getBookCode());
        output.add(getReaderCode());
        output.add(Integer.toString(getState()));
        return output;
    }
    @Override
    public void deserialize(List<String> input)
    {
        if (input.size() != 3)
        {
            throw new IllegalArgumentException("Input didn't contain the correct number of fields.");
        }
        
        setBookCode(input.get(0));
        setReaderCode(input.get(1));
        setState(Integer.parseInt(input.get(2)));
    }     

    @Override
    public int compareTo(Lending other)
    {
        if (!Helpers.compareTypes(this, other))
        {
            throw new UnsupportedOperationException(String.format("Cannot compare objects of different classes (%s vs %s).", this.getClass().getName(), other.getClass().getName()));
        }
        
        int reader = this.getBookCode().compareTo(other.getBookCode());
        if (reader != 0) return reader;
        return this.getReaderCode().compareTo(other.getReaderCode());
    }
}
