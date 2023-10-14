package group1.util;

import group1.libmgmt.backend.Book;
import group1.libmgmt.backend.Reader;

import java.io.*;
import java.util.ArrayList;

public class Data {

    public ArrayList loadBooks() {
        String[] books = loadFile("books.txt").split(System.lineSeparator());
        ArrayList bookList = new ArrayList<Book>();

        for (String book : books) {
            if (isValidBook(book)) {
                String[] bookData = book.split("\\|\\|");
                String bCode = bookData[0];
                String title = bookData[1];
                double price = Double.parseDouble(bookData[2]);
                Book b = new Book(bCode, title, price);
                bookList.add(b);
            }
        }
        return bookList;
    }

    private boolean isValidBook(String b) {
        return b.matches("B\\d+\\|\\|.*");
    }

    public void saveBooks(Book[] books) {
        StringBuilder data = new StringBuilder();
        for(Book book : books) {
            String bookData = book.getCode() + "|" + book.getTitle() + "|" + book.getPrice();
            data.append(bookData).append(System.lineSeparator());
        }
        writeFile("books.txt", data.toString());
    }

    public Reader[] loadReaders() {
        String[] readers = loadFile("readers.txt").split(System.lineSeparator());
        Reader[] readerArr = new Reader[readers.length];
        for(int i = 0; i < readerArr.length; i++) {
            String[] readerData = readers[i].split("\\|\\|");
            String rcode = readerData[0];
            String name = readerData[1];
            int birthYear = Integer.parseInt(readerData[2]);
            Reader r = new Reader(rcode, name, birthYear);
            readerArr[i] = r;
        }
        return readerArr;
    }


    public String loadFile(String fileName) {
        if(!foundFile(fileName)) createFile(fileName);
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

    private boolean foundFile(String fileName) {
        File f = new File(fileName);
        return f.exists() && !f.isDirectory();
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
