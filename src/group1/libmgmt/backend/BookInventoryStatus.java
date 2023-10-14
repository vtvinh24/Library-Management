package group1.libmgmt.backend;

import group1.util.CodeOriented;

public final class BookInventoryStatus extends CodeOriented 
{
    private String bookCode;
    private int quantity;
    private int lended;

    public BookInventoryStatus(String bookCode, int quantity)
    {
        this.bookCode = bookCode;
        this.quantity = quantity;
    }
    public BookInventoryStatus(Book book, int quantity)
    {
        this.bookCode = book.getCode();
        this.quantity = quantity;
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

    public int getQuantity()
    {
        return quantity;
    }
    public void setQuantity(int quantity)
    {
        if (lended > quantity)
        {
            throw new IllegalArgumentException("Not enough books to maintain lendings.");
        }        
        this.quantity = quantity;
    }

    public int getLended()
    {
        return lended;
    }
    public void setLended(int lended)
    {
        if (lended > quantity)
        {
            throw new IllegalArgumentException("Not enough books to lend.");
        }
        this.lended = lended;
    }
    
    @Override
    public String getCode()
    {
        return bookCode;
    }
    @Override
    public String toString()
    {
        return String.format("Status: %s (%d/%d)", this.getBookCode(), this.getLended(), this.getQuantity());
    }
}
