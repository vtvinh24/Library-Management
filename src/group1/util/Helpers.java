package group1.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
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
        if (input == null) return null;
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
        if (input == null) return null;
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
    
    public static String generateString(char c, int count)
    {
        return new String(new char[count]).replace('\0', c);
    }
    
    public static <T> boolean compareTypes(T a, T b)
    {
        return ((a == null) == (b == null)) && a.getClass().equals(b.getClass());
    }
    
    public static String readLine()
    {
        return (new Scanner(System.in)).nextLine();
    }
    
    public static List<String> split(String input, char... separators)
    {
        if (separators.length == 0) return createArrayList(input);
        
        ArrayList<String> elements = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++)
        {
            char cur = input.charAt(i);
            
            boolean isSeparator = false;
            for (char separator : separators)
            {
                if (cur == separator)
                {
                    elements.add(sb.toString());
                    sb.setLength(0);
                    isSeparator = true;
                    break;
                }
            }
            
            if (!isSeparator)
                sb.append(cur);
        }
        
        elements.add(sb.toString());
        return elements;
    }
    
    public static <T> T validatedInputLoop
    (
        String question,
        Function<String, T> validator
    )
    {
        if (!question.endsWith(" ")) question += " ";
        
        while (true)
        {
            System.out.printf(question);
            try
            {
                return validator.apply(readLine());
            } 
            catch (Exception e)
            {
                System.out.printf
                (
                    "Error: %s. Please try again.\n", 
                    e.getMessage() != null 
                        ? String.format("(%s) %s", e.getClass().getSimpleName(), trimEnd(e.getMessage(), '.')) 
                        : e.getClass().getSimpleName()
                );
            }
        }
    }
    public static int askInteger(String question, int min, int max)
    {
        return validatedInputLoop
        (
            question, 
            (s) -> 
            {
                Helpers.throwIfNullOrWhitespace(s, "Input");
                
                int ret = Integer.parseInt(s);
                if (ret < min || ret > max) throw new IllegalArgumentException(String.format("Input must be from %d to %d", min, max));
                return ret;
            }
        );
    }

    public static boolean askYesNo(String question)
    {
        return Helpers.validatedInputLoop
        (
            Helpers.trimEnd(question, ' ') + " [Y/N]", 
            (s) -> 
            {
                Helpers.throwIfNullOrWhitespace(s, "Input");
                
                switch (s.toUpperCase())
                {
                    case "Y": return true;
                    case "N": return false;
                    default: throw new IllegalArgumentException("Input must be either 'Y' for yes or 'N' for no.");
                }
            }
        );
    }
    
    public static <T> ArrayList<T> createArrayList(T... elements)
    {
        ArrayList<T> ret = new ArrayList<>(elements.length);
        for (T elem : elements)
        {
            ret.add(elem);
        }
        
        return ret;
    }
    
    public static ArrayList<Integer> generateRange(int from, int to)
    {
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i = from; i <= to; i++)
            ret.add(i);
        return ret;
    }
}
