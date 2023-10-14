package group1.util;

import java.util.function.Function;
import java.util.regex.Pattern;

public final class Helpers 
{
    public static boolean isNullOrWhitespace(String input)
    {
        return input == null || input.isEmpty() || input.matches("[ \\t\\r\\n]+");
    }
    
    public static String trimStart(String input, char... trimChars)
    {
        if (trimChars.length == 0) return input;
        
        Function<Character, Boolean> isTrimChar = (c) -> 
        {
            for (int i = 0; i < trimChars.length; i++)
                if (trimChars[i] == c) return true;
            return false;
        };
        StringBuffer sb = new StringBuffer(input);
        // trim from start
        while (sb.length() > 0)
        {
            if (isTrimChar.apply(sb.charAt(0))) sb.deleteCharAt(0);
            else break;
        }
        
        return sb.toString();
    }
    public static String trimEnd(String input, char... trimChars)
    {
        if (trimChars.length == 0) return input;
        
        Function<Character, Boolean> isTrimChar = (c) -> 
        {
            for (int i = 0; i < trimChars.length; i++)
                if (trimChars[i] == c) return true;
            return false;
        };
        StringBuffer sb = new StringBuffer(input);
        // trim from ebd
        while (sb.length() > 0)
        {
            int last = sb.length() - 1;
            if (isTrimChar.apply(sb.charAt(last))) sb.deleteCharAt(last);
            else break;
        }
        
        return sb.toString();
    }    
    public static String trim(String input, char... trimChars)
    {
        return trimStart(trimEnd(input, trimChars), trimChars);
    }
    
    public static class InputNullOrWhitespaceException extends IllegalArgumentException
    {
        public InputNullOrWhitespaceException(String varName)
        {
            super(String.format("%s cannot be null or empty, and cannot only contain whitespaces.", varName));
        }
    }
    public static void throwIfNullOrWhitespace(String input, String varName)
    {
        if (isNullOrWhitespace(input))
        {
            throw new InputNullOrWhitespaceException(varName);
        }
    }
    
    public static boolean matchesRegex(String input, String regex)
    {
        Pattern p = Pattern.compile(regex);
        return p.matcher(input).find();
    }
    
    public static <T> boolean compareTypes(T a, T b)
    {
        return ((a == null) == (b == null)) && a.getClass().equals(b.getClass());
    }
}
