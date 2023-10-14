package group1.test;

import group1.libmgmt.backend.Book;
import group1.util.Data;
import group1.util.lists.LinkedList;
import group1.util.lists.LinkedNode;

public class UtilTest {
    public static void main(String[] args) {
        Data data = new Data();
//        System.out.println(data.loadFile("books.txt"));
        Book[] books = data.loadBooks();
        LinkedList<Book> bookList = new LinkedList<>();
        for(Book b : books) {
            bookList.add(b);
        }
        bookList.display();
        System.out.println("------------------");
        Book tmpBook = new Book("B2022", "2023", 100);
        System.out.println(bookList.length());
        bookList.add(tmpBook);
        System.out.println(bookList.length());
        bookList.addAfter(5, tmpBook);
        bookList.addAfter(3, tmpBook);
        bookList.addAfter(1, tmpBook);
        System.out.println("get()");
        bookList.display();

    }
}
