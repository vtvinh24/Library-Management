package group1.libmgmt.backend;

import group1.util.CodeDiscriminated;
import group1.util.Helpers;
import group1.util.StringListSerializable;
import java.util.ArrayList;
import java.util.List;

public final class Reader extends CodeDiscriminated implements StringListSerializable
{
    private String code;
    private String name;
    private int birthYear;

    public Reader() {;}
    public Reader(String code, String name, int birthYear)
    {
        this.setCode(code);
        this.setName(name);
        this.setBirthYear(birthYear);
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
        Helpers.throwIfNullOrWhitespace(code, "Reader code");
        code = code.toUpperCase();
        if (!Helpers.matchesRegex(code, "^R[0-9]{6}$"))
        {
            throw new IllegalArgumentException("Reader code must be the letter R followed by 6 digits.");
        }
    }    

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        Helpers.throwIfNullOrWhitespace(name, "Reader's name");
        this.name = name;
    }

    public int getBirthYear()
    {
        return birthYear;
    }
    public void setBirthYear(int birthYear)
    {
        if (birthYear < 1900 || birthYear > 2010)
        {
            throw new IllegalArgumentException("Reader's birth year must be between 1900 and 2010.");
        }
        
        this.birthYear = birthYear;
    }
    
    @Override
    public String toString()
    {
        return String.format("Reader (%s): %s (%d)", this.getCode(), this.getName(), this.getBirthYear());
    }  
    
    @Override
    public List<String> serialize()
    {
        ArrayList<String> output = new ArrayList<>();
        output.add(getCode());
        output.add(getName());
        output.add(Integer.toString(getBirthYear()));
        return output;
    }
    @Override
    public void deserialize(List<String> input)
    {
        if (input.size() != 3)
        {
            throw new IllegalArgumentException("Input didn't contain the correct number of fields.");
        }
        
        setCode(input.get(0));
        setName(input.get(1));
        setBirthYear(Integer.parseInt(input.get(2)));
    }    
}
