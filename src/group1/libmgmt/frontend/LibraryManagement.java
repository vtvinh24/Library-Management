
package group1.libmgmt.frontend;

import group1.libmgmt.backend.Book;
import group1.libmgmt.backend.Lending;
import group1.libmgmt.backend.Reader;
import group1.util.Box;
import group1.util.CSVUtils;
import group1.util.Helpers;
import group1.util.StringListSerializable;
import group1.util.Table;
import group1.util.lists.BinarySearchNode;
import group1.util.lists.BinarySearchTree;
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

    private static final boolean BOOKS_BST = false;

    private final LinkedList<Book> llBooks = new LinkedList<>();
    private final BinarySearchTree<Book> bstBooks = new BinarySearchTree<>();
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
        booksTable.addColumn(10, "Price");
        booksTable.addColumn(5, "Quantity");
        booksTable.addColumn(5, "Lent");
        booksTable.addColumn(10, "Value");

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
            System.out.println("4. Edit data");
            System.out.println("5. Sort/Balance data");
            System.out.println("6. Delete data");
            System.out.println("7. Find data");
            System.out.println("8. Report data");
            System.out.println("0. Quit");
            int option = Helpers.askInteger("Enter your choice: ", 0, 8);

            System.out.println("");
            switch (option)
            {
                case 0:
                {
                    needsSaving();
                    System.out.println("");
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
                    edit();
                    break;
                }
                case 5:
                {
                    sort();
                    break;
                }
                case 6:
                {
                    delete();
                    break;
                }
                case 7:
                {
                    find();
                    break;
                }
                case 8:
                {
                    report();
                    break;
                }
            }

            System.out.println("Press enter to continue.");
            Helpers.readLine();
        }
    }

    private void add()
    {
        printHeading(2, "Adding data");

        int option = askWhichRecord();
        System.out.println("");

        switch (option)
        {
            case 1:
            {
                printHeading(3, "Adding a Book");

                Book b = new Book();
                askBookDetails(b, false);

                if (!BOOKS_BST)
                {
                    if (llBooks.count() > 0)
                    {
                        System.out.println("");
                        System.out.println("Would you like to add this Book:");
                        System.out.println("1. To the front of the list");
                        System.out.println("2. To the back of the list");
                        if (llBooks.count() > 2)
                        {
                            System.out.println("3. After an index of choice");
                        }
                        switch (Helpers.askInteger("Enter your choice: ", 1, llBooks.count() > 2 ? 3 : 2))
                        {
                            // 1.1.8
                            case 1: llBooks.addToHead(b); break;
                            // 1.1.2
                            // 2.1.2
                            case 2: llBooks.add(b); break;
                            // 1.1.9
                            case 3:
                            {
                                int index = Helpers.askInteger("Enter the index after which the book should be inserted", 0, llBooks.count() - 2);
                                llBooks.addAfter(index, b);
                                break;
                            }
                        }
                    }
                    else llBooks.add(b);
                }
                else
                {
                    bstBooks.add(b);
                }


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

                if (countBooks() == 0)
                {
                    System.out.println("No books to lend!");
                    System.out.println("");
                    return;
                }
                if (findBooks(x -> x.isLendable()).isEmpty())
                {
                    System.out.println("All books are out of stock!");
                    System.out.println("");
                    return;
                }
                if (readers.count() == 0)
                {
                    System.out.println("No readers to lend to!");
                    System.out.println("");
                    return;
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
        System.out.println("Successfully added data.");
        System.out.println("");
    }

    private void edit()
    {
        printHeading(2, "Editing data");

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
                printHeading(3, String.format("Editing %ss", name));

                if (candidates.isEmpty())
                {
                    System.out.printf("No %ss to edit!\n", name);
                    return;
                }

                if (Helpers.askYesNo(String.format("Would you like to search for the %ss to edit?", name)))
                {
                    candidates = narrowFunc.get();
                }
                System.out.println("");
                if (candidates.isEmpty())
                {
                    System.out.printf("Found no %ss to edit!\n", name);
                    return;
                }

                List<T> selected = askToSelect(candidates, printFunc);
                System.out.println("");
                for (T s : selected)
                {
                    askDetailsFunc.accept(s);
                    System.out.println("");
                    dirty = true;
                }

                System.out.printf("Successfully edited %s(s).\n", name);
            }
        }

        switch (option)
        {
            case 1:
            {
                new Helper().execute
                (
                    "Book",
                    traverseBooks(),
                    () -> askToFindBooks(false),
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
                    () -> askToFindReaders(false),
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
                    () -> askToFindLendings(false),
                    (l) -> printLendings(l),
                    (b) -> askLendingDetails(b, true)
                );
            }            
        }
        System.out.println("");
    }

    private void sort()
    {
        printHeading(2, "Sorting/Balancing data");

        int option = askWhichRecord();
        System.out.println("");

        switch (option)
        {
            case 1:
            {
                printHeading(3, "Sorting/Balancing Books");
                if (countBooks() == 0)
                {
                    System.out.println("No Books to balance!");
                    System.out.println("");
                    return;
                }
                else
                {
                    if (BOOKS_BST) bstBooks.balance(); // 2.1.8
                    else llBooks.sort();  // 1.1.7
                    dirty = true;
                }
                break;
            }
            case 2:
            {
                printHeading(3, "Sorting Readers");
                if (readers.count() == 0)
                {
                    System.out.println("No Readers to sort!");
                    System.out.println("");
                    return;
                }
                else
                {
                    readers.sort();
                    dirty = true;
                }
                break;
            }
            case 3:
            {
                // 2.3.3
                // 1.3.3
                printHeading(3, "Sorting Lendings");
                if (lendings.count() == 0)
                {
                    System.out.println("No Lendings to sort!");
                    System.out.println("");
                    return;
                }
                else
                {
                    lendings.sort();
                    dirty = true;
                }
                break;
            }
        }

        System.out.println("Sorting/Balancing complete.");
        System.out.println("");
    }

    private void delete()
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
                Consumer<List<T>> deleteFunc
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
                {
                    System.out.printf("Found no %ss to delete!\n", name);
                    return;
                }

                List<T> selected = askToSelect(candidates, printFunc);
                deleteFunc.accept(selected);
                dirty = true;

                System.out.println("");
                System.out.printf("Sucessfully deleted %s(s)\n", name);
            }

            public void deleteLending(Lending target)
            {
                if (target.getState() == Lending.NOT_RETURNED)
                {
                    getBookByCode(target.getBookCode()).hasBeenReturned();
                }
                lendings.delete(target);
            }

            public void deleteLendings(Function<Lending, Boolean> evaluator)
            {
                List<Lending> targets = lendings.findValues(evaluator);
                for (Lending target : targets)
                {
                    deleteLending(target);
                }
            }
        }

        Helper helper = new Helper();

        switch (option)
        {
            case 1:
            {
                // 2.1.7
                helper.execute
                (
                    "Book",
                    traverseBooks(),
                    () -> askToFindBooks(false),
                    (l) -> printBooks(l),
                    (list) ->
                    {
                        for (Book b : list)
                        {
                            helper.deleteLendings((v) -> v.getBookCode().equals(b.getCode()));
                        }

                        // 2.1.7
                        if (BOOKS_BST)
                        {
                            for (Book b : list)
                            {
                                bstBooks.delete(b);
                            }
                        }
                        else
                        {
                            // 1.1.10
                            /*
                            for (Book b : list)
                            {
                                llBooks.delete(b);
                            }
                            */

                            // 1.1.6
                            List<Integer> indexes = list.stream().map(x -> llBooks.findIndexOf(x)).collect(Collectors.toList());
                            indexes.sort((a, b) -> b.compareTo(a));
                            for (int i : indexes)
                            {
                                llBooks.delete(i);
                            }
                        }
                    }
                );
                break;
            }
            case 2:
            {
                // 1.2.6
                // 2.2.6
                helper.execute
                (
                    "Reader",
                    readers.traverse(),
                    () -> askToFindReaders(false),
                    (l) -> printReaders(l),
                    (l) ->
                    {
                        for (Reader r : l)
                        {
                            helper.deleteLendings((v) -> v.getReaderCode().equals(r.getCode()));
                            readers.delete(r);
                        }
                    }
                );
                break;
            }
            case 3:
            {
                helper.execute
                (
                    "Lending",
                    lendings.traverse(),
                    () -> askToFindLendings(false),
                    (l) -> printLendings(l),
                    (l) ->
                    {
                        for (Lending lend : l)
                        {
                            helper.deleteLending(lend);
                        }
                    }
                );
            }            
        }
        System.out.println("");
    }

    private void save()
    {
        printHeading(2, "Saving data");

        if (!outputPath.equals(""))
        {
            if (!Helpers.askYesNo("Would you like to save to the same location?"))
            {
                outputPath = Helpers.validatedInputLoop("Please enter new output path (enter '.' to use this program's directory): ", (s) -> s);
            }
        }

        File newPath = new File(outputPath);
        if (!newPath.isDirectory() || !newPath.exists())
        {
            System.out.println("Output path is either missing or invalid.");
            outputPath = Helpers.validatedInputLoop
            (
                "Please enter the output folder path (enter '.' to use this program's directory): ",
                (s) ->
                {
                    Helpers.throwIfNullOrWhitespace(s, "Output folder");
                    if (s.equals(".")) s = getMainJarDirectory();
                    s = Helpers.trimEnd(s, '\\', '/');

                    File newFile = new File(s);

                    if (!newFile.exists()) throw new IllegalArgumentException("Path doesn't exist.");
                    if (!newFile.isDirectory()) throw new IllegalArgumentException("Path doesn't point to a directory.");

                    return s;
                }
            );
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
        // 2.1.5
        save.accept(String.format("%s/books.csv", outputPath), new ArrayList<>(traverseBooks()));
        // 1.2.4
        // 2.2.4
        save.accept(String.format("%s/readers.csv", outputPath), readers.traverse());
        save.accept(String.format("%s/lendings.csv", outputPath), lendings.traverse());

        System.out.printf("Saved data to %s.\n", outputPath);

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

        String newPath = Helpers.validatedInputLoop
        (
            "Please enter the path to the folder that contains one of these files (enter '.' to use this program's directory): ",
            (s) -> s
        );
        boolean tolerateErrors = Helpers.askYesNo("Should errors be tolerated?");
        Box<Integer> errors = new Box<>(0);   
        
        System.out.println("");
        System.out.println("Loading data.");
        try
        {
            clearBooks();
            lendings.clear();
            readers.clear();

            Helpers.throwIfNullOrWhitespace(newPath, "Output folder");
            if (newPath.equals(".")) newPath = getMainJarDirectory();
            newPath = Helpers.trimEnd(newPath, '\\', '/');

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
                    if (!file.exists()) 
                    {
                        System.out.printf("%s doesn't exist. Ignoring.\n", path);
                        errors.setValue(errors.getValue() + 1);
                        return;
                    }

                    ArrayList<ArrayList<String>> table = CSVUtils.parse(Files.readAllLines(file.toPath()).stream().collect(Collectors.joining("\n")));
                    for (ArrayList<String> line : table)
                    {
                        T b = instanceCreator.get();
                        try
                        { 
                            b.deserialize(line); 
                        }
                        catch (Exception e)
                        {
                            if (tolerateErrors)
                            {
                                System.out.printf("Error while deserializing data line: %s. Skipping.\n", Helpers.trimEnd(e.getMessage(), '.'));
                                errors.setValue(errors.getValue() + 1);
                                continue;
                            }
                            else 
                            {
                                throw e;
                            }
                        }
                        list.add(b);
                    }
                }

                public void execute(String path) throws IOException
                {
                    // 1.1.1
                    // 2.1.1
                    parseFile(String.format("%s/books.csv", path), BOOKS_BST ? bstBooks : llBooks, () -> new Book());
                    // 1.2.1
                    // 2.2.1
                    parseFile(String.format("%s/readers.csv", path), readers, () -> new Reader());
                    parseFile(String.format("%s/lendings.csv", path), lendings, () -> new Lending());
                }
            }
            new ParseHelper().execute(newPath);

            for (Lending l : lendings.traverse())
            {
                try
                {
                    if (getReaderByCode(l.getReaderCode()) == null)
                    {
                        throw new IllegalArgumentException(String.format("A lending was issued to a non-existent reader (%s)", l.getReaderCode()));
                    }
                    Book target = getBookByCode(l.getBookCode());
                    if (target == null)
                    {
                        throw new IllegalArgumentException(String.format("A lending was for a non-existent book (%s)", l.getBookCode()));
                    }

                    // we only care about books still in posession of readers
                    // as returned books don't necessarily represent stock
                    // due to possible slimming of book stock
                    if (l.getState() == Lending.NOT_RETURNED) 
                    {
                        target.hasBeenLent();
                    }
                }
                catch (Exception e)
                {
                    if (tolerateErrors)
                    {
                        System.out.printf("Error while verifying lending data: %s. Removing lending.\n", Helpers.trimEnd(e.getMessage(), '.'));
                        errors.setValue(errors.getValue() + 1);
                        lendings.deleteObject(l);
                    }
                    else 
                    {
                        throw e;
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.printf("Fatal error while loading data: %s\n", e.getMessage());
            System.out.println("");
            return;
        }
        
        if (errors.getValue() > 0) System.out.println("");
        System.out.printf("Loaded data from %s.\n", newPath);
        System.out.printf("%d books, %d readers, %d lendings, and %d error(s)\n", countBooks(), readers.count(), lendings.count(), errors.getValue());
        System.out.println("");

        outputPath = newPath;
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
                askToFindBooks(true);
                break;
            }
            case 2:
            {
                printHeading(3, "Finding a Reader");
                askToFindReaders(true);
                break;
            }
            case 3:
            {
                printHeading(3, "Finding a Lending");
                askToFindLendings(true);
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
        if (BOOKS_BST)
        {
            if (Helpers.askYesNo("Would you like print through books in level order?"))
            {
                // 2.1.3
                printBooks(bstBooks.levelOrder());
            }
            else
            {
                // 2.1.4
                printBooks(bstBooks.inorder());
            }
        }
        else
        {
            printBooks(llBooks.traverse());
        }
        // 2.1.9
        List<Book> allBooks = traverseBooks();
        System.out.println(booksTable.composeRow(new Object[]
        {
            "",
            "Totals",
            String.format("%d titles", allBooks.size()),
            String.format("%.2f", allBooks.stream().collect(Collectors.summingDouble(x -> x.getPrice()))),
            allBooks.stream().collect(Collectors.summingInt(x -> x.getQuantity())),
            allBooks.stream().collect(Collectors.summingInt(x -> x.getLent())),
            String.format("%.2f", allBooks.stream().collect(Collectors.summingDouble(x -> x.getValue())))
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

    private List<Book> askToFindBooks(boolean printResults)
    {
        if (countBooks() == 0)
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
        List<Book> results = null;
        switch (choice)
        {
            // 1.1.5
            // 1.1.6
            // 2.1.6
            // 2.1.7
            case 1:
            {
                String code = Helpers.validatedInputLoop("Enter the book code to search for: ", (s) -> s.toUpperCase());
                Book b = getBookByCode(code);
                results = (b != null) ? Helpers.createArrayList(b) : new ArrayList<>();

                break;
            }
            case 2:
            {
                String title = Helpers.validatedInputLoop("Enter search term: ", (s) -> s.toLowerCase());
                results = findBooks(b -> b.getTitle().toLowerCase().contains(title));
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
                results = findBooks((b) -> b.getPrice() >= min && b.getPrice() <= max);
                break;
            }
        }

        if (printResults)
        {
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
        }


        return results;
    }
    private List<Reader> askToFindReaders(boolean printResults)
    {
       if (readers.count() == 0)
        {
            System.out.println("No readers to search for!");
            return null;
        }

        System.out.println("Would you like to search a reader by: ");
        System.out.println("1. Code");
        System.out.println("2. Name");
        System.out.println("3. Birth year period");
        int choice = Helpers.askInteger("Enter your choice: ", 1, 3);

        System.out.println("");
        List<Reader> results = null;
        switch (choice)
        {
            // 1.2.5
            // 1.2.6
            // 2.2.5
            // 2.2.6
            case 1:
            {
                String code = Helpers.validatedInputLoop("Enter the reader code to search for: ", (s) -> s.toUpperCase());
                results = Helpers.createArrayList(getReaderByCode(code));
                break;
            }
            case 2:
            {
                String name = Helpers.validatedInputLoop("Enter search term: ", (s) -> s.toLowerCase());
                results = readers.findValues(r -> r.getName().toLowerCase().contains(name));
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
                results = readers.findValues((r) -> r.getBirthYear() >= min && r.getBirthYear() <= max);
                break;
            }
        }

        if (printResults)
        {
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
        }

        return results;
    }
    private List<Lending> askToFindLendings(boolean printResults)
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

        Box<Function<Lending, Boolean>> selector = new Box<>(null);
        switch (choice)
        {
            case 1: case 2: case 3:
            {
                String book = choice == 1 
                    ? null 
                    : Helpers.validatedInputLoop("Enter the book code to search for:   ", (s) -> s.toUpperCase());
                String reader = choice == 2 
                    ? null 
                    : Helpers.validatedInputLoop("Enter the reader code to search for: ", (s) -> s.toLowerCase());
                selector.setValue((b) -> (book == null || b.getBookCode().contains(book)) && (reader == null || b.getReaderCode().contains(reader)));
                break;
            }
            case 4:
            {
                printLendingStates();
                int state = Helpers.askInteger("Enter lending state: ", 0, 2);
                selector.setValue((b) -> b.getState() == state);
                break;
            }
        }

        List<Lending> results = lendings.findValues(x -> selector.getValue().apply(x));
        if (printResults)
        {
            System.out.println("");

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
        }

        return results;
    }

    private <T> List<T> askToSelect(List<T> candidates, Consumer<List<T>> printer)
    {
        if (candidates.size() == 1) return candidates;
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
                    ArrayList<T> ret = new ArrayList<>();

                    if (s.isEmpty())
                    {
                        return candidates;
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

                            ret.add(candidates.get(i));
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
                Integer.toString(x.getLent()),
                String.format("%.2f", x.getValue())
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
        }
    }

    private Book getBookByCode(String code)
    {
        if (BOOKS_BST)
        {
            BinarySearchNode<Book> result = bstBooks.find((b) -> code.compareTo(b.getCode()));
            if (result == null) return null;
            else return result.getValue();
        }
        else
        {
            List<LinkedNode<Book>> result = llBooks.find((v) -> v.getCode().compareTo(code) == 0);
            if (result.isEmpty()) return null;
            else return result.get(0).getValue();
        }
    }
    private Reader getReaderByCode(String code)
    {
        LinkedNode<Reader> result = readers.find((v) -> v.getCode().compareTo(code) == 0).get(0);
        if (result == null) return null;
        else return result.getValue();
    }


    private int askWhichRecord()
    {
        System.out.println("Please select a collection:");
        System.out.println("1. Books");
        System.out.println("2. Readers");
        System.out.println("3. Lendings");
        return Helpers.askInteger("Enter your choice: ", 1, 3);
    }

    private void askBookDetails(Book b, boolean editing)
    {
        if (editing)
        {
            System.out.println("C:");
            printBooks(Helpers.createArrayList(b));
            System.out.println("Enter nothing to maintain the requested information of the book.");
            System.out.println("");
        }
        Helpers.validatedInputLoop
        (
            "Enter book's code: ",
            (s) ->
            {
                if (s.isEmpty() && editing) return null;
                if (getBookByCode(s.toUpperCase()) != null)
                {
                    throw new IllegalArgumentException("Another book already has the same code.");
                }

                String oldCode = b.getCode();
                b.setCode(s);
                if (editing && !oldCode.equals(b.getCode()))
                {
                    lendings.traverse().stream().filter(x -> x.getBookCode().equals(oldCode)).forEach(x -> x.setBookCode(b.getCode()));
                }

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
            System.out.println("");
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
                if (editing && !oldCode.equals(r.getCode()))
                {
                    lendings.traverse().stream().filter(x -> x.getReaderCode().equals(oldCode)).forEach(x -> x.setReaderCode(r.getCode()));
                }

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
            System.out.println("");
        }
        
        System.out.println("Available books:");
        printBooks(traverseBooks());
        Book book = Helpers.validatedInputLoop
        (
            "Enter lending's book code: ", 
            (s) ->
            {
                Box<String> code = new Box(s.toUpperCase());
                if (editing && s.isEmpty()) code.setValue(l.getBookCode());
                Book.throwIfInvalidCode(code.getValue());
                return getBookByCode(code.getValue());
            }
        );
        if (book == null)
        {
            System.out.println("No book exists with such a code.");
            return false;
        }
        
        System.out.println("");
        System.out.println("Readers: ");
        printReaders(readers.traverse());
        Reader reader = Helpers.validatedInputLoop
        (
            "Enter lending's reader code: ", 
            (s) -> 
            {
                Box<String> code = new Box(s.toUpperCase());
                if (editing && s.isEmpty()) code.setValue(l.getReaderCode());
                Reader.throwIfInvalidCode(code.getValue());
                return getReaderByCode(code.getValue());
            }
        );
        if (reader == null)
        {
            System.out.println("No reader exists with such a code.");
            return false;
        }
        
        if (!editing)
        {
            l.setBookCode(book.getCode());
            l.setReaderCode(reader.getCode());

            if (book.isLendable())
            {
                book.hasBeenLent();
                l.setState(Lending.NOT_RETURNED);
            }
            else
            {
                System.out.println("");
                System.out.println("This book is out of stock, this lending will be marked as undelivered");
                l.setState(Lending.NOT_DELIVERED);
            }
            
            return true;
        }
        else
        {
            System.out.println("");
            printLendingStates();
            int state = Helpers.validatedInputLoop
            (
                "Enter lending's reader code: ", 
                (s) -> 
                {
                    if (editing && s.isEmpty()) return l.getState();
                    int ret = Integer.parseInt(s);
                    Lending.throwIfInvalidState(ret);
                    return ret;
                }
            );

            // assume the lending takes the book away from the reader
            if (l.getState() == Lending.NOT_RETURNED)
            {
                getBookByCode(l.getBookCode()).hasBeenReturned();
            }
            if (state == Lending.NOT_RETURNED)
            {
                if (!book.isLendable())
                {
                    System.out.println("");
                    System.out.println("Specified book is out of stock!");
                    return false;
                }             
                book.hasBeenLent();
            }
            l.setBookCode(book.getCode());
            l.setReaderCode(reader.getCode());             
            l.setState(state);
        }
        
        return true;
    }


    private int countBooks()
    {
        if (BOOKS_BST) return bstBooks.count();
        return llBooks.count();
    }
    private List<Book> traverseBooks()
    {
        if (BOOKS_BST) return bstBooks.inorder();
        return llBooks.traverse();
    }
    private List<Book> findBooks(Function<Book, Boolean> evaluator)
    {
        if (BOOKS_BST) return bstBooks.inorder().stream().filter(x -> evaluator.apply(x)).collect(Collectors.toList());
        return llBooks.findValues(evaluator);
    }
    private void clearBooks()
    {
        if (BOOKS_BST) bstBooks.clear();
        else llBooks.clear();
    }

    private static void printLendingStates()
    {
        System.out.println("Available lending states: ");
        System.out.printf("0. %s\n", Lending.stateToString(Lending.NOT_DELIVERED));
        System.out.printf("1. %s\n", Lending.stateToString(Lending.NOT_RETURNED));
        System.out.printf("2. %s\n", Lending.stateToString(Lending.RETURNED));
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
    
    private static String getMainJarDirectory()
    {
        String jarPath = URLDecoder.decode(LibraryManagement.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File file = new File(jarPath);
        if (!file.isDirectory()) return file.getParent();
        return file.getPath();
    }
}
