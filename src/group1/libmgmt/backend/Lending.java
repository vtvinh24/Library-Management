package group1.libmgmt.backend;

import group1.util.CodeOriented;
import group1.util.Helpers;

public final class Lending extends CodeOriented
{
    private String bookCode;
    private String readerCode;
    private int state;
    
    public static final int NOT_DELIVERED = 1;
    public static final int NOT_RETURNED = 2;
    public static final int RETURNED = 3;

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
        if (state < 1 || state > 3)
        {
            throw new IllegalArgumentException("Invalid lending state.");
        }
        
        this.state = state;
    }
    
    @Override
    public String getCode()
    {
        return String.format("%s->%s", this.bookCode, this.readerCode);
    }
    @Override
    public String toString()
    {
        return String.format("Lending: %s to %s", this.getBookCode(), this.getReaderCode());
    }    
}
