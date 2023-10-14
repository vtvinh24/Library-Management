package group1.util;

import java.util.Objects;

public abstract class CodeDiscriminated implements Comparable<CodeDiscriminated> 
{
    public abstract String getCode();
    
    @Override
    public int compareTo(CodeDiscriminated other)
    {
        if (!Helpers.compareTypes(this, other))
        {
            throw new UnsupportedOperationException(String.format("Cannot compare objects of different classes (%s vs %s).", this.getClass().getName(), other.getClass().getName()));
        }
        
        return this.getCode().compareTo(other.getCode());
    }
    @Override
    public boolean equals(Object other)
    {
        return Helpers.compareTypes(this, other) && this.getCode().equals(((CodeDiscriminated)other).getCode());
    }
    @Override
    public int hashCode()
    {
        return this.getClass().hashCode() ^ Objects.hashCode(this.getCode());
    }
}
