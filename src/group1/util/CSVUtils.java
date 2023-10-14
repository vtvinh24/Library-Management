package group1.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CSVUtils 
{
    public static ArrayList<ArrayList<String>> parse(String input)
    {
        input = input.replace("\r", "");
        
        ArrayList<ArrayList<String>> output = new ArrayList<>();
        ArrayList<String> curLine = new ArrayList<>();
        StringBuilder curCell = new StringBuilder();
        boolean quoted = false;
        boolean inLine = false;
        
        Runnable flushCell = () -> 
        {
            curLine.add(curCell.toString());
            curCell.setLength(0);            
        };
        Runnable flushLine = () -> 
        {
            flushCell.run();
            output.add((ArrayList<String>)(curLine.clone()));
            curLine.clear();            
        };
        
        for (int i = 0; i < input.length(); i++)
        {
            inLine = true;
            
            if (input.charAt(i) == '"')
            {
                if (i == 0 || input.charAt(i - 1) != '\\')
                {
                    quoted = !quoted;
                    continue;
                }
            }
            
            if (!quoted)
            {
                switch (input.charAt(i))
                {
                    case ',': 
                    {
                        flushCell.run();
                        continue;
                    }
                    case '\n':
                    {
                        flushLine.run();
                        inLine = false;
                        continue;
                    }
                }
            }
            
            curCell.append(input.charAt(i));
        }
        
        if (inLine) flushLine.run();        
        return output;
    }
    
    public static String compose(List<List<String>> input)
    {
        return input.stream().map(x -> x.stream().map(y -> "\"" + y.replace("\"", "\\\"") + "\"").collect(Collectors.joining(","))).collect(Collectors.joining("\n"));
    }
}
