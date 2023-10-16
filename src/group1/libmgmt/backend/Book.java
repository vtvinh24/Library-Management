package group1.libmgmt.backend;

import group1.util.CodeDiscriminated;
import group1.util.Helpers;
import group1.util.StringListSerializable;
import java.util.ArrayList;
import java.util.List;

public final class Book extends CodeDiscriminated implements StringListSerializable
{
    private String code;
    private String title;
    private double price;
    private int quantity;
    private int lent;
    
    public Book() {;}
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
        if (!Helpers.matchesRegex(code, "^B[0-9]{6}$"))
        {
            throw new IllegalArgumentException("Book code must be the letter B followed by 6 digits.");
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
    
    public int getQuantity()
    {
        return quantity;
    }
    public void setQuantity(int quantity)
    {
        if (quantity < 0)
        {
            throw new IllegalArgumentException("Book quantity cannot be lower than 0.");
        }        
        if (lent > quantity)
        {
            throw new IllegalArgumentException("Not enough copies to maintain current lendings.");
        }        
        this.quantity = quantity;
    }

    public int getLent()
    {
        return lent;
    }
    public void setLent(int lent)
    {
        if (lent < 0)
        {
            throw new IllegalArgumentException("Book lent count cannot be lower than 0.");
        }
        if (lent > quantity)
        {
            throw new IllegalArgumentException("Not enough copies to lend.");
        }
        
        this.lent = lent;
    }    
    public void hasBeenLent()
    {
        this.setLent(this.getLent() + 1);
    }
    public void hasBeenReturned()
    {
        this.setLent(this.getLent() - 1);
    }
    public boolean isLendable()
    {
        return this.getQuantity() > this.getLent();
    }
    public boolean isReturnable()
    {
        return this.getLent() > 0;
    }
    
    public double getValue()
    {
        return this.getPrice() * this.getQuantity();
    }
    
    @Override
    public String toString()
    {
        return String.format("Book (%s): %s", this.getCode(), this.getTitle());
    }

    @Override
    public List<String> serialize()
    {
        ArrayList<String> output = new ArrayList<>();
        output.add(getCode());
        output.add(getTitle());
        output.add(Double.toString(getPrice()));
        output.add(Integer.toString(getQuantity()));
        return output;
    }
    @Override
    public void deserialize(List<String> input)
    {
        if (input.size() != 4)
        {
            throw new IllegalArgumentException("Input didn't contain the correct number of fields.");
        }
        
        setCode(input.get(0));
        setTitle(input.get(1));
        setPrice(Double.parseDouble(input.get(2)));
        setQuantity(Integer.parseInt(input.get(3)));
    }
}
