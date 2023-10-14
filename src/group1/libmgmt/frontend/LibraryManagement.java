
package group1.libmgmt.frontend;

import group1.libmgmt.backend.Book;
import group1.libmgmt.backend.BookInventoryStatus;
import group1.util.Helpers;

public class LibraryManagement 
{
    public static void main(String[] args) throws Exception 
    {
        Book b = new Book("B231010", "JGJG", 0);
        Book c = new Book("B231009", "JGJG", 0);
        BookInventoryStatus s = new BookInventoryStatus(b, 20);
        
        System.out.println(s.hashCode());
        System.out.println(b.hashCode());
        System.out.println(Helpers.compareTypes(b, null));
        System.out.println(b.compareTo(c));
    }
}
