package src.utilities;

public class Book {
    private long ISBN;
    private String title;
    private String author;
    private String genre;
    private int publicationDate;
    private int pageCount;
    private double rating;

    public Book(long ISBN, String title, String author, String genre, int publicationDate, int pageCount, double rating) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publicationDate = publicationDate;
        this.pageCount = pageCount;
        this.rating = rating;
    }

    // Getter methods
    public long getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public int getPublicationDate() {
        return publicationDate;
    }

    public int getPageCount() {
        return pageCount;
    }

    public double getRating() {
        return rating;
    }

    // Setter methods
    public void setISBN(long ISBN) {
        this.ISBN = ISBN;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setPublicationDate(int publicationDate) {
        this.publicationDate = publicationDate;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}