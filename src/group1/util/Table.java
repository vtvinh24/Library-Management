package group1.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Table 
{
    private class Column
    {
        private String header;
        private int width;

        public Column(int width, String header)
        {
            setHeader(header);
            setWidth(width);
        }

        public String getHeader()
        {
            return header;
        }
        public void setHeader(String header)
        {
            Helpers.throwIfNullOrWhitespace(header, "Column header");
            this.header = header;
        }

        public int getWidth()
        {
            if (width < header.length()) return header.length();
            return width;
        }
        public void setWidth(int width)
        {
            this.width = width;
        }
    }
    
    private ArrayList<Column> columns = new ArrayList<>();
    
    public void addColumn(int width, String header)
    {
        columns.add(new Column(width, header));
    }
    
    public String composeRow(Object[] rowContent)
    {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < columns.size(); i++)
        {
            String content = i >= rowContent.length ? "" : rowContent[i].toString();
            int width = columns.get(i).getWidth();
            
            if (content.length() > width) content = content.substring(0, width);
            else content += Helpers.generateString(' ', width - content.length());
            
            content = " " + content + " ";
            
            if (i == 0) sb.append("|");
            sb.append(content);
            sb.append("|");
        }
        
        return sb.toString();
    }
    public String composeRow(List<Object> rowContent)
    {
        return composeRow(rowContent.toArray(new Object[]{}));
    }
    
    public String compose(Object[][] rowContents)
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append(composeRow(columns.stream().map(x -> x.getHeader()).collect(Collectors.toList()))).append("\n");
        sb.append("|").append(columns.stream().map(x -> Helpers.generateString('-', x.getWidth() + 2)).collect(Collectors.joining("|"))).append("|");
        for (int i = 0; i < rowContents.length; i++)
        {
            sb.append("\n");
            sb.append(composeRow(rowContents[i]));
        }
        
        sb.append("\n");
        return sb.toString();
    }
    public String compose(List<Object[]> rowContents)
    {
        return compose(rowContents.toArray(new Object[][]{}));
    }
}
