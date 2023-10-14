
package group1.libmgmt.frontend;

import group1.libmgmt.backend.Book;
import group1.libmgmt.backend.Lending;
import group1.libmgmt.backend.Reader;
import group1.util.Box;
import group1.util.CSVUtils;
import group1.util.Helpers;
import group1.util.StringListSerializable;
import group1.util.Table;
import group1.util.lists.LinkedList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import group1.util.lists.LinkedNode;
import group1.util.lists.ListAddable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class LibraryManagement
{
    private String outputPath = "";
    private boolean dirty = false;

    private final LinkedList<Book> books = new LinkedList<>();
    private final LinkedList<Reader> readers = new LinkedList<>();
    private final LinkedList<Lending> lendings = new LinkedList<>();
    
    private final Table booksTable = new Table();
    private final Table readersTable = new Table();
    private final Table lendingsTable = new Table();
    
    public void execute()
    {
        booksTable.addColumn(3, "#");
        booksTable.addColumn(7, "Code");
        booksTable.addColumn(20, "Title");        
        booksTable.addColumn(6, "Price");        
        booksTable.addColumn(5, "Quantity");        
        booksTable.addColumn(5, "Lent");       
        
        readersTable.addColumn(3, "#");
        readersTable.addColumn(7, "Code");
        readersTable.addColumn(20, "Name");
        readersTable.addColumn(4, "Birth Year");
        
        lendingsTable.addColumn(3, "#");
        lendingsTable.addColumn(7, "Book");
        lendingsTable.addColumn(7, "Reader");
        lendingsTable.addColumn(20, "State");
        
        
        while (true)
        {
            printHeading(1, "Library Management Program");
            System.out.println("1. Load data");
            System.out.println("2. Save data");
            System.out.println("3. Add data");
            System.out.println("4. Update data");
            System.out.println("5. Delete data");
            System.out.println("6. Find data");
            System.out.println("7. Report data");
            System.out.println("0. Quit");
            int option = Helpers.askInteger("Enter your choice: ", 0, 7);

            System.out.println("");
            switch (option)
            {
                case 0:
                {
                    needsSaving();
                    System.out.println("Goodbye.");
                    return;
                }
                case 1:
                {
                    load();
                    break;
                }
                case 2:
                {
                    save();
                    break;
                }                
                case 3:
                {
                    add();
                    break;
                }
                case 4:
                {
                    update();
                    break;
                }
                case 5:
                {
                    delete();
                    break;
                }
                case 6:
                {
                    find();
                    break;
                }
                case 7:
                {
                    report();
                    break;
                }
            }
        }
    }
    
    private void add()
    {
        printHeading(2, "Adding data");

        int option = askWhichRecord();
        System.out.println("");

        switch (option)
        {
            // 1.1.2
            // 2.1.2
            case 1:
            {
                printHeading(3, "Adding a Book");

                Book b = new Book();
                askBookDetails(b, false);
                books.add(b);
                dirty = true;
                    
                break;
            }
            // 1.2.2
            // 2.2.2
            case 2:
            {
                printHeading(3, "Adding a Reader");

                Reader r = new Reader();
                askReaderDetails(r, false);
                readers.add(r);
                dirty = true;

                break;
            }
            // 1.3.1
            // 2.3.1
            case 3:
            {
                printHeading(3, "Lending a Book");

                if (books.count() == 0)
                {
                    System.out.println("No books to lend!");
                    break;
                }
                if (books.find(true, (v) -> v.isLendable()) == null)
                {
                    System.out.println("All books are out of stock!");
                    break;
                }                
                if (readers.count() == 0)
                {
                    System.out.println("No readers to lend to!");
                    break;
                }

                Lending l = new Lending();
                if (askLendingDetails(l, false))
                {
                    lendings.add(l);
                    dirty = true;
                }

                break;
            }
        }

        System.out.println("");
    }

    private void update()
    {
        printHeading(2, "Updating data");

        int option = askWhichRecord();
        System.out.println("");

        class Helper
        {
            public <T> void execute
            (
                // name of records being updated
                String name, 
                // initial candidate list
                List<T> candidates, 
                // function to create narrowed candidates
                Supplier<List<T>> narrowFunc, 
                // function to print candidate records
                Consumer<List<T>> printFunc, 
                // function to modify a record
                Consumer<T> askDetailsFunc
            )
            {
                printHeading(3, String.format("Updating %ss", name));
                
                if (candidates.isEmpty())
                {
                    System.out.printf("No %ss to update!\n", name);
                    return;
                }
                
                if (Helpers.askYesNo(String.format("Would you like to search for the %ss to update?", name)))
                {
                    candidates = narrowFunc.get();
                }
                System.out.println("");
                if (candidates.isEmpty()) 
                    return;
                
                List<Integer> indexes = askToSelectIndexes(candidates, printFunc);
                System.out.println("");
                for (int i : indexes)
                {
                    askDetailsFunc.accept(candidates.get(i));
                    System.out.println("");
                }              
            }
        }
        
        switch (option)
        {
            case 1:
            {
                new Helper().execute
                (
                    "Book", 
                    books.traverse(), 
                    () -> askToFindBooks(), 
                    (l) -> printBooks(l), 
                    (b) -> askBookDetails(b, true)
                );
                break;
            }
            case 2:
            {
                new Helper().execute
                (
                    "Reader", 
                    readers.traverse(), 
                    () -> askToFindReaders(), 
                    (l) -> printReaders(l), 
                    (b) -> askReaderDetails(b, true)
                );
                break;
            }
            case 3:
            {
                new Helper().execute
                (
                    "Lending", 
                    lendings.traverse(), 
                    () -> askToFindLendings(), 
                    (l) -> printLendings(l), 
                    (b) -> askLendingDetails(b, true)
                );
            }
        }

        dirty = true;
        System.out.println("");
    }

    public void delete()
    {
        printHeading(2, "Deleting data");

        int option = askWhichRecord();
        System.out.println("");

        class Helper
        {
            public <T> void execute
            (
                // name of records being updated
                String name, 
                // initial candidate list
                List<T> candidates, 
                // function to create narrowed candidates
                Supplier<List<T>> narrowFunc, 
                // function to print candidate records
                Consumer<List<T>> printFunc, 
                // function to delete a record
                Consumer<Integer> deleteFunc
            )
            {
                printHeading(3, String.format("Deleting %ss", name));
                
                if (candidates.isEmpty())
                {
                    System.out.printf("No %ss to delete!\n", name);
                    return;
                }
                
                if (Helpers.askYesNo(String.format("Would you like to search for the %ss to delete?", name)))
                {
                    candidates = narrowFunc.get();
                }
                System.out.println("");
                if (candidates.isEmpty()) 
                    return;
                                
                // 1.1.10
                List<Integer> indexes = askToSelectIndexes(candidates, printFunc);
                // it's paramount that we sort them descendingly
                // as deletion by index can only be done properly 
                // from furthest to closest
                indexes.sort((a, b) -> Integer.compare(b, a));
                System.out.println("");
                for (int i : indexes)
                {
                    deleteFunc.accept(i);
                }                
            }
        }
        
        switch (option)
        {
            case 1:
            {
                // 1.1.6
                // 2.1.7
                new Helper().execute
                (
                    "Book", 
                    books.traverse(), 
                    () -> askToFindBooks(), 
                    (l) -> printBooks(l), 
                    (i) -> books.delete(i)
                );
                break;
            }
            case 2:
            {
                // 1.2.6
                // 2.2.6
                new Helper().execute
                (
                    "Reader", 
                    readers.traverse(), 
                    () -> askToFindReaders(), 
                    (l) -> printReaders(l), 
                    (i) -> readers.delete(i)
                );
                break;
            }
            case 3:
            {
                new Helper().execute
                (
                    "Lending", 
                    lendings.traverse(), 
                    () -> askToFindLendings(), 
                    (l) -> printLendings(l), 
                    (i) -> books.delete(i)
                );
            }
        }

        dirty = true;
        System.out.println("");
    }

    private void save()
    {
        printHeading(2, "Saving data");

        File output = new File(outputPath);
        if (!output.isDirectory() || !output.exists())
        {
            System.out.println("Existing output path is either missing or invalid.");
            output = Helpers.validatedInputLoop
            (
                "Please enter the output folder path (enter '.' to use this program's directory): ",
                (s) ->
                {
                    Helpers.throwIfNullOrWhitespace(s, "Output folder");
                    if (s.equals(".")) s = URLDecoder.decode(this.getClass().getResource("").getPath());
                    s = Helpers.trimEnd(s, '\\', '/');

                    File newFile = new File(s);

                    if (!newFile.exists()) throw new IllegalArgumentException("Path doesn't exist.");
                    if (!newFile.isDirectory()) throw new IllegalArgumentException("Path doesn't point to a directory.");

                    return newFile;
                }
            );
            outputPath = output.getPath();
        }

        this.dirty = false;
        BiConsumer<String, ArrayList<? extends StringListSerializable>> save = (file, entries) ->
        {
            File f = new File(file);
            try
            {
                if (f.exists())
                {
                    f.delete();
                }

                String table = CSVUtils.compose(entries.stream().map(x -> x.serialize()).collect(Collectors.toList()));
                try (BufferedWriter wr = new BufferedWriter(new FileWriter(file)))
                {
                    wr.write(table);
                }
            }
            catch (Exception ex)
            {
                System.out.printf("Error while writing %s: %s\n", new File(file).getName(), ex.getMessage());
                this.dirty = true;
            }
        };
        // 1.1.4
        save.accept(String.format("%s/books.csv", outputPath), books.traverse());
        // 1.2.4
        // 2.2.4
        save.accept(String.format("%s/readers.csv", outputPath), readers.traverse());
        save.accept(String.format("%s/lendings.csv", outputPath), lendings.traverse());

        System.out.println("Saving complete.");
        
        System.out.println("");
    }

    private void load()
    {
        needsSaving();

        printHeading(2, "Loading data");

        System.out.println("Library data is loaded from 3 comma-separated value files: ");
        System.out.println("- books.csv: Contains book entries.");
        System.out.println("- readers.csv: Contains reader entries.");
        System.out.println("- lending.csv: Contains lending entries.");

        Helpers.validatedInputLoop
        (
            "Please enter the path to the folder that contains these files (enter '.' to use this program's directory): ",
            (s) ->
            {
                books.clear();
                lendings.clear();
                readers.clear();

                Helpers.throwIfNullOrWhitespace(s, "Output folder");
                if (s.equals(".")) s = URLDecoder.decode(this.getClass().getResource("").getPath());
                s = Helpers.trimEnd(s, '\\', '/');

                class ParseHelper
                {
                    private <T extends StringListSerializable> void parseFile
                    (
                        String path,
                        ListAddable<T> list,
                        Supplier<T> instanceCreator
                    )
                    throws IOException
                    {
                        File file = new File(path);
                        if (!file.exists()) return; //throw new IllegalArgumentException(String.format("%s doesn't exist.", path));

                        ArrayList<ArrayList<String>> table = CSVUtils.parse(Files.readAllLines(file.toPath()).stream().collect(Collectors.joining("\n")));
                        for (ArrayList<String> line : table)
                        {
                            T b = instanceCreator.get();
                            b.deserialize(line);
                            list.add(b);
                        }
                    }

                    public ParseHelper(String path) throws IOException
                    {
                        // 1.1.1
                        // 2.1.1
                        parseFile(String.format("%s/books.csv", path), books, () -> new Book());
                        // 1.2.1
                        // 2.2.1
                        parseFile(String.format("%s/readers.csv", path), readers, () -> new Reader());
                        parseFile(String.format("%s/lendings.csv", path), lendings, () -> new Lending());
                    }
                }

                try
                {
                    ParseHelper dummy = new ParseHelper(s);
                }
                catch (Exception e)
                {
                    throw new IllegalArgumentException(String.format("Error: %s.", Helpers.trimEnd(e.getMessage(), '.')));
                }

                for (Lending l : lendings.traverse())
                {
                    if (getReaderByCode(l.getReaderCode()) == null)
                    {
                        throw new IllegalArgumentException(String.format("A lending contains a non-existent reader (%s)", l.getReaderCode()));
                    }
                    Book target = getBookByCode(l.getBookCode());
                    if (target == null)
                    {
                        throw new IllegalArgumentException(String.format("A lending contains a non-existent book (%s)", l.getBookCode()));
                    }
                    target.hasBeenLent();
                }

                outputPath = s;

                return null;
            }
        );

        System.out.printf("Loaded %d books, %d readers, and %d lendings.\n", books.count(), readers.count(), lendings.count());
        System.out.println("");
    }

    private void find()
    {
        printHeading(2, "Finding data");
        
        int option = askWhichRecord();
        System.out.println("");

        switch (option)
        {
            case 1:
            {
                printHeading(3, "Finding a Book");
                askToFindBooks();
                break;
            }
            case 2:
            {
                printHeading(3, "Finding a Reader");
                askToFindReaders();
                break;
            }
            case 3:
            {
                printHeading(3, "Finding a Lending");
                askToFindLendings();
                break;
            }
        }
        
        System.out.println();
    }
    
    private void report()
    {
        printHeading(2, "Report");
        
        // 1.1.3
        printHeading(3, "Books");
        printBooks(books.traverse());
        // 2.1.9
        System.out.println(booksTable.composeRow(new Object[]
        {
            "",
            "Totals",
            books.count(),
            String.format("%.2f", books.traverse().stream().collect(Collectors.summingDouble(x -> x.getPrice()))),
            books.traverse().stream().collect(Collectors.summingInt(x -> x.getQuantity())),
            books.traverse().stream().collect(Collectors.summingInt(x -> x.getLent())),
        }));
        System.out.println("");
        
        // 1.2.3
        // 2.2.3
        printHeading(3, "Readers");
        printReaders(readers.traverse());
        System.out.println("");
        
        // 1.3.2
        // 2.3.2
        printHeading(3, "Lendings");
        printLendings(lendings.traverse());
        System.out.println("");
    }
    
    private List<Book> askToFindBooks()
    {
        if (books.count() == 0)
        {
            System.out.println("No books to search for!");
            return null;
        }
        
        System.out.println("Would you like to search a book by: ");
        System.out.println("1. Code");
        System.out.println("2. Title");
        System.out.println("3. Price range");
        int choice = Helpers.askInteger("Enter your choice: ", 1, 3);
        
        System.out.println("");
        Box<Function<Book, Boolean>> selector = new Box<>(null);
        switch (choice)
        {
            // 1.1.5
            // 1.1.6
            // 2.1.6
            // 2.1.7
            case 1:
            {
                String code = Helpers.validatedInputLoop("Enter the book code to search for: ", (s) -> s.toUpperCase());
                selector.setValue((b) -> b.getCode().contains(code));
                break;
            }
            case 2:
            {
                String title = Helpers.validatedInputLoop("Enter search term: ", (s) -> s.toLowerCase());
                selector.setValue(b -> b.getTitle().toLowerCase().contains(title));
                break;
            }
            case 3:
            {
                double min = Helpers.validatedInputLoop("Enter minimum price: ", s -> Double.parseDouble(s));
                double max = Helpers.validatedInputLoop("Enter maximum price: ", s -> 
                {
                    double ret = Double.parseDouble(s);
                    if (ret < min)
                    {
                        throw new IllegalArgumentException("Maximum price cannot be lower than minimum.");
                    }
                    return ret;
                });
                selector.setValue((b) -> b.getPrice() >= min && b.getPrice() <= max);
                break;
            }
        }
        
        List<Book> results = books.traverse().stream().filter(x -> selector.getValue().apply(x)).collect(Collectors.toList());
        if (results.isEmpty())
        {
            System.out.println("Found no books.");
        }
        else
        {
            System.out.println("");
            System.out.println("Results:");
            printBooks(results);
        }
        
        return results;
    }
    private List<Reader> askToFindReaders()
    {
       if (readers.count() == 0)
        {
            System.out.println("No books to search for!");
            return null;
        }
        
        System.out.println("Would you like to search a book by: ");
        System.out.println("1. Code");
        System.out.println("2. Name");
        System.out.println("3. Birth year period");
        int choice = Helpers.askInteger("Enter your choice: ", 1, 3);
        
        System.out.println("");
        Box<Function<Reader, Boolean>> selector = new Box<>(null);
        switch (choice)
        {
            // 1.2.5
            // 1.2.6
            // 2.2.5
            // 2.2.6
            case 1:
            {
                String code = Helpers.validatedInputLoop("Enter the book code to search for: ", (s) -> s.toUpperCase());
                selector.setValue((b) -> b.getCode().contains(code));
                break;
            }
            case 2:
            {
                String name = Helpers.validatedInputLoop("Enter search term: ", (s) -> s.toLowerCase());
                selector.setValue(b -> b.getName().toLowerCase().contains(name));
                break;
            }
            case 3:
            {
                double min = Helpers.validatedInputLoop("Enter earliest birth year: ", s -> Double.parseDouble(s));
                double max = Helpers.validatedInputLoop("Enter latest birth year:   ", s -> 
                {
                    double ret = Double.parseDouble(s);
                    if (ret < min)
                    {
                        throw new IllegalArgumentException("Latest birth year cannot be earlier than earliest.");
                    }
                    return ret;
                });
                selector.setValue((b) -> b.getBirthYear() >= min && b.getBirthYear() <= max);
                break;
            }
        }
        
        List<Reader> results = readers.traverse().stream().filter(x -> selector.getValue().apply(x)).collect(Collectors.toList());
        if (results.isEmpty())
        {
            System.out.println("Found no readers.");
        }
        else
        {
            System.out.println("");
            System.out.println("Results:");
            printReaders(results);
        }
        
        return results;
    }    
    private List<Lending> askToFindLendings()
    {
       if (lendings.count() == 0)
        {
            System.out.println("No lendings to search for!");
            return null;
        }
        
        System.out.println("Would you like to search a lending by: ");
        System.out.println("1. Book code");
        System.out.println("2. Reader code");
        System.out.println("3. Book & Reader code");
        System.out.println("4. State");
        int choice = Helpers.askInteger("Enter your choice: ", 1, 4);
        
        System.out.println("");
        Box<Function<Lending, Boolean>> selector = new Box<>(null);
        switch (choice)
        {
            case 1: case 2: case 3:
            {
                String book = choice == 1 ? null : Helpers.validatedInputLoop("Enter the book code to search for: ", (s) -> s.toUpperCase());
                String reader = choice == 2 ? null : Helpers.validatedInputLoop("Enter the reader code to search for: ", (s) -> s.toLowerCase());                
                selector.setValue((b) -> (book == null || b.getBookCode().contains(book)) && (reader == null || b.getReaderCode().contains(reader)));
                break;
            }
            case 4:
            {
                printLendingStates();
                int state = Helpers.askInteger("Enter lending state: ", 1, 3);
                selector.setValue((b) -> b.getState() == state);
                break;
            }
        }
        
        List<Lending> results = lendings.traverse().stream().filter(x -> selector.getValue().apply(x)).collect(Collectors.toList());
        if (results.isEmpty())
        {
            System.out.println("Found no lendings.");
        }
        else
        {
            System.out.println("");
            System.out.println("Results:");
            printLendings(results);
        }
        
        return results;
    }
    
    private <T> List<Integer> askToSelectIndexes(List<T> candidates, Consumer<List<T>> printer)
    {
        if (candidates.size() == 1) return Helpers.createArrayList(0);
        else 
        {
            System.out.println("Available records:");
            printer.accept(candidates);

            System.out.println("Please enter the indicies of the records you want to select, separated by a comma (e.g. 4, 2, 3). Enter nothing to select all.");
            return Helpers.validatedInputLoop
            (
                "Input: ",
                (s) -> 
                {
                    ArrayList<Integer> ret = new ArrayList<>();
                    
                    if (s.isEmpty())
                    {
                        ret.addAll(Helpers.generateRange(0, candidates.size() - 1));
                    }
                    else 
                    {
                        Helpers.split(s, ',').stream().map(x -> Helpers.trim(x, ' ', '\t')).forEach(x -> 
                        {
                            int i = Integer.parseInt(x);
                            if (i < 0 || i >= candidates.size())
                            {
                                throw new IllegalArgumentException("Index lied outside of bounds.");
                            }

                            ret.add(i);
                        });
                    }
                    return ret;
                }
            );
        }
    }
    
    private void printBooks(List<Book> books)
    {
        ArrayList<String[]> table = new ArrayList<>();
        for (int i = 0; i < books.size(); i++)
        {
            Book x = books.get(i);
            table.add(new String[]
            {
                Integer.toString(i),
                x.getCode(),
                x.getTitle(),
                String.format("%.2f", x.getPrice()),
                Integer.toString(x.getQuantity()),
                Integer.toString(x.getLent())
            });
        }
        System.out.print(booksTable.compose(table.toArray(new String[][]{})));
    }
    private void printReaders(List<Reader> readers)
    {
        ArrayList<String[]> table = new ArrayList<>();
        for (int i = 0; i < readers.size(); i++)
        {
            Reader x = readers.get(i);
            table.add(new String[]
            {
                Integer.toString(i),
                x.getCode(),
                x.getName(),
                Integer.toString(x.getBirthYear()),
            });
        }
        System.out.print(readersTable.compose(table.toArray(new String[][]{})));
    }
    private void printLendings(List<Lending> lendings)
    {
        ArrayList<String[]> table = new ArrayList<>();
        for (int i = 0; i < lendings.size(); i++)
        {
            Lending x = lendings.get(i);
            table.add(new String[]
            {
                Integer.toString(i),
                x.getBookCode(),
                x.getReaderCode(),
                Lending.stateToString(x.getState())
            });
        }
        System.out.print(lendingsTable.compose(table.toArray(new String[][]{})));
    }

    private void needsSaving()
    {
        if (dirty)
        {
            if (Helpers.askYesNo("You have unsaved changes, would you like to save the library's data to disk?"))
            {
                System.out.println("");
                save();
            }
            this.dirty = false;
        }
    }

    private Book getBookByCode(String code)
    {
        LinkedNode<Book> result = books.find(code, (v) -> v.getCode());
        if (result == null) return null;
        else return result.getValue();
    }
    private Reader getReaderByCode(String code)
    {
        LinkedNode<Reader> result = readers.find(code, (v) -> v.getCode());
        if (result == null) return null;
        else return result.getValue();
    }    
    
    private void updateBookCode(String oldCode, String newCode)
    {
        if (oldCode.equals(newCode)) return;
        lendings.traverse().stream().filter(x -> x.getBookCode().equals(oldCode)).forEach(x -> x.setBookCode(newCode));
    }
    private void updateReaderCode(String oldCode, String newCode)
    {
        if (oldCode.equals(newCode)) return;
        lendings.traverse().stream().filter(x -> x.getReaderCode().equals(oldCode)).forEach(x -> x.setReaderCode(newCode));
    }

    private int askWhichRecord()
    {
        System.out.println("Please choose a record to update:");
        System.out.println("1. Books");
        System.out.println("2. Readers");
        System.out.println("3. Lendings");
        return Helpers.askInteger("Enter your choice", 1, 3);
    }

    private void askBookDetails(Book b, boolean editing)
    {
        if (editing)
        {
            System.out.println("Updating:");
            printBooks(Helpers.createArrayList(b));
            System.out.println("Enter nothing to maintain the requested information of the book.");
        }
        Helpers.validatedInputLoop
        (
            "Enter book's code: ",
            (s) ->
            {
                if (s.isEmpty() && editing) return null;
                if (books.traverse().stream().anyMatch(x -> x != b && x.getCode().equals(s.toUpperCase())))
                {
                    throw new IllegalArgumentException("Another book already has the same code.");
                }

                String oldCode = b.getCode();
                b.setCode(s);
                if (editing) this.updateBookCode(oldCode, b.getCode());

                return null;
            }
        );
        Helpers.validatedInputLoop
        (
            "Enter book's title: ",
            (s) -> { if (!s.isEmpty() || !editing) b.setTitle(s); return null; }
        );
        Helpers.validatedInputLoop
        (
            "Enter book's price: ",
            (s) -> { if (!s.isEmpty() || !editing) b.setPrice(Double.parseDouble(s)); return null; }
        );
        Helpers.validatedInputLoop
        (
            "Enter book's quantity: ",
            (s) -> { if (!s.isEmpty() || !editing) b.setQuantity(Integer.parseInt(s)); return null; }
        );
    }
    private void askReaderDetails(Reader r, boolean editing)
    {
        if (editing)
        {
            System.out.println("Updating:");
            printReaders(Helpers.createArrayList(r));            
            System.out.println("Enter nothing to maintain the requested information of the reader.");
        }
        Helpers.validatedInputLoop
        (
            "Enter reader's code: ",
            (s) ->
            {
                if (s.isEmpty() && editing) return null;
                if (readers.traverse().stream().anyMatch(x -> x != r && x.getCode().equals(s.toUpperCase())))
                {
                    throw new IllegalArgumentException("Another reader already has the same code.");
                }

                String oldCode = r.getCode();
                r.setCode(s);
                if (editing) this.updateReaderCode(oldCode, r.getCode());

                return null;
            }
        );
        Helpers.validatedInputLoop
        (
            "Enter reader's name: ",
            (s) -> { if (!s.isEmpty() || !editing) r.setName(s); return null; }
        );
        Helpers.validatedInputLoop
        (
            "Enter reader's birth year: ",
            (s) -> { if (!s.isEmpty() || !editing) r.setBirthYear(Integer.parseInt(s)); return null; }
        );
    }
    private boolean askLendingDetails(Lending l, boolean editing)
    {
        if (editing)
        {
            System.out.println("Updating:");
            printLendings(Helpers.createArrayList(l));               
            System.out.println("Enter nothing to maintain the requested information of the lending.");
        }
        
        System.out.println("Books still in-stock: ");
        printBooks(books.traverse().stream().filter(x -> x.isLendable()).collect(Collectors.toList()));
        boolean success = Helpers.validatedInputLoop
        (
            "Enter lending's book code: ",
            (s) ->
            {
                Box<String> code = new Box(s.toUpperCase());
                
                if (s.isEmpty() && editing) return null;
                if (!books.traverse().stream().anyMatch(x -> x.getCode().equals(code.getValue())))
                {
                    throw new IllegalArgumentException("This code does not belong to any books.");
                }

                Book newBook = getBookByCode(code.getValue());
                if (!editing && !newBook.isLendable())
                {
                    // do not give an opportunity to retry as there could be a case
                    // where there are no lendable books left, and the user gets stuck
                    // an vain input loop
                    System.out.println("This book is out of stock!");
                    return false;
                }
                
                String oldCode = l.getBookCode();
                l.setBookCode(code.getValue());
                if (editing && !oldCode.equals(l.getBookCode()))
                {
                    Book oldBook = getBookByCode(oldCode);
                    oldBook.hasBeenReturned();
                }
                newBook.hasBeenLent();

                return true;
            }
        );
        if (!success) return false;
        
        System.out.println("");
        System.out.println("Readers: ");
        printReaders(readers.traverse());        
        Helpers.validatedInputLoop
        (
            "Enter lending's reader code: ",
            (s) ->
            {
                if (s.isEmpty() && editing) return null;
                if (!readers.traverse().stream().anyMatch(x -> x.getCode().equals(s.toUpperCase())))
                {
                    throw new IllegalArgumentException("This code does not belong to any reader.");
                }

                l.setReaderCode(s);
                return null;
            }
        );
        
        System.out.println("");
        printLendingStates();
        Helpers.validatedInputLoop
        (
            "Enter lending's state: ",
            (s) ->
            {
                if (s.isEmpty() && editing) return null;
                l.setState(Integer.parseInt(s));
                return null;
            }
        );
        
        return true;
    }
    
    private static void printLendingStates()
    {
        System.out.println("Available lending states: ");
        System.out.printf("1. %s\n", Lending.stateToString(Lending.NOT_DELIVERED));
        System.out.printf("2. %s\n", Lending.stateToString(Lending.NOT_RETURNED));
        System.out.printf("3. %s\n", Lending.stateToString(Lending.RETURNED));
    }

    private static void printHeading(int level, String title)
    {
        String surround = "";
        switch (level)
        {
            case 1: surround = "======"; break;
            case 2: surround = "------"; break;
            case 3: surround = "----"; break;
        }

        System.out.printf("%s %s %s\n\n", surround, title, surround);
    }
}
