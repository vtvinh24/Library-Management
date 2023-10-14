package group1.libmgmt.backend;

import group1.util.CodeOriented;
import group1.util.Helpers;

public final class Book extends CodeOriented
{
    private String code;
    private String title;
    private double price;

    public Book(String code, String title, double price)
    {
        this.setCode(code);
        this.setTitle(title);
        this.setPrice(price);
    }

    @Override
    public String getCode()
    {
        return code;
    }
    public void setCode(String code)
    {
        throwIfInvalidCode(code);
        
        code = code.toUpperCase();
        this.code = code;
    }
    public static void throwIfInvalidCode(String code)
    {
        code = code.toUpperCase();
        Helpers.throwIfNullOrWhitespace(code, "Book code");
        if (!Helpers.matchesRegex(code, "^B\\d+$"))
        {
            throw new IllegalArgumentException("Book code must be the letter B followed by a numeric identifier.");
        }
    }

    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        Helpers.throwIfNullOrWhitespace(code, "Book title");
        this.title = title;
    }

    public double getPrice()
    {
        return price;
    }
    public void setPrice(double price)
    {
        if (price < 0)
        {
            throw new IllegalArgumentException("Book price cannot be lower than 0.");
        }
        
        this.price = price;
    }
    
    @Override
    public String toString()
    {
        return String.format("Book (%s): %s", this.getCode(), this.getTitle());
    }
}
