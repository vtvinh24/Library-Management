package group1.util;

import group1.libmgmt.backend.Book;
import group1.util.lists.LinkedList;
import group1.util.lists.LinkedNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Data {

    public Book[] loadBooks() {
        String[] books = loadFile("books.txt").split(System.lineSeparator());
        Book[] bookArr = new Book[books.length];
        for(int i = 0; i < bookArr.length; i++) {
            String[] bookData = books[i].split("\\|\\|");
            String bcode = bookData[0];
            String title = bookData[1];
            double price = Double.parseDouble(bookData[2]);
            Book b = new Book(bcode, title, price);
            bookArr[i] = b;
        }
        return bookArr;
    }

    public void saveBooks(Book[] books) {
        StringBuilder data = new StringBuilder();
        for(Book book : books) {
            String bookData = book.getCode() + "|" + book.getTitle() + "|" + book.getPrice();
            data.append(bookData).append(System.lineSeparator());
        }
        writeFile("books.txt", data.toString());
    }

    public Reader loadReaders() {
        
    }


    public String loadFile(String fileName) {
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void fileNotFound(String fileName) {
        System.out.println("File " + fileName + " is not found.");
    }

    public void createFile(String fileName) {
        File file = new File(fileName);
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("Error creating file.");
        }
    }

    public void writeFile(String fileName, String data) {
        try {
            FileWriter myWriter = new FileWriter("filename.txt");
            myWriter.write(data);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("Error writing file.");
        }
    }
}
