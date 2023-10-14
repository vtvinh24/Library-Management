package group1.libmgmt.backend;

import group1.util.CodeOriented;
import group1.util.Helpers;

public final class Reader extends CodeOriented
{
    private String code;
    private String name;
    private int birthYear;

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
            throw new IllegalArgumentException("Reader code must be the letter R followed by 6 numbers.");
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
}
