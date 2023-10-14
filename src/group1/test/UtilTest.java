package group1.test;

import group1.libmgmt.backend.Book;
import group1.util.Data;

public class UtilTest {
    public static void main(String[] args) {
        Data data = new Data();
        Book[] books = data.loadBooks();
        for(Book b : books) {
            System.out.println(b);
        }

    }
}
