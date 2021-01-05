package Model;

import java.util.Random;

public class Book implements Comparable<Book>{
    private int bookId;
    private int pages;
    private double width;
    private double height;
    private double price;
    private String title;
    private String author;
    private int rented;

    public Book () {

    }
    public Book(int id, int pages, double width, double height, double price,String title ,String author, int rented) {
        this.bookId = id;
        this.pages = pages;
        this.width = width;
        this.height = height;
        this.price = price;
        if (title == null || title == "") {
            throw new IllegalArgumentException("Title Cannot be null");
        }
        this.title = title;
        if (author == null || author == "") {
            throw new IllegalArgumentException("Author Cannot be null");
        }
        this.author = author;
        this.rented = rented;
    }

    /**
     * Constructor not used anymore. Was filling the Book instance with random values
     * @param randomValues
     */
    @Deprecated
    public Book(boolean randomValues) {
        if (randomValues) {
            Random random = new Random();
            this.pages = random.nextInt(1000);
            this.width = random.nextFloat() * 50;
            this.height = random.nextFloat() * 50;
            this.price = random.nextFloat() * 50;
            this.title = "No title";
            this.author = "Anonymous author";
        }
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRented() {
        return rented;
    }

    public void setRented(int rented) {
        this.rented = rented;
    }

    @Override
    public String toString() {
        String rented;
        switch (this.rented) {
            case 1 :
                rented = "Ksiazka wypozyczona";
                break;
            case 2 :
                rented = "Książka archiwizowana";
                break;
            default :
                rented = "Ksiazka do wypozyczenia";
                break;
        }
        return bookId + ". " + title + " - " + author + ". " + price + "PLN, ilość stron : " + pages + " wymiary, : " + width + "cm x " + height + " cm - " + rented;
    }

    public int getBookId() {
        return bookId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return pages == book.pages &&
                Double.compare(book.width, width) == 0 &&
                Double.compare(book.height, height) == 0 &&
                Double.compare(book.price, price) == 0 &&
                title.equals(book.title) &&
                author.equals(book.author);
    }


    @Override
    public int compareTo(Book o) {
        final int LOWER = -1;
        final int EQUALS = 0;
        final int HIGHER = 1;
        if (this == o || this.equals(o) || (this.title == o.title && this.author == o.author)) return EQUALS;
        if (this.pages > o.pages && this.price > o.price) {
            return HIGHER;
        } else return LOWER;
    }
}
