package src.utilities;

/**
 * Represents an award entry with associated details.
 */
public class Award {
    private String awardId;
    private String categoryId;
    private Long isbn;
    private String title;
    private int ranking;
    private String author;

    /**
     * Constructs a new Award with all details including author.
     *
     * @param awardId    Unique award identifier
     * @param categoryId Category ID this award belongs to
     * @param isbn       ISBN of the related book
     * @param title      Title of the book
     * @param ranking    Ranking position
     * @param author     Author of the book
     */
    public Award(String awardId, String categoryId, Long isbn, String title, int ranking, String author) {
        this.awardId = awardId;
        this.categoryId = categoryId;
        this.isbn = isbn;
        this.title = title;
        this.ranking = ranking;
        this.author = author;
    }

    public String getAwardId() {
        return awardId;
    }

    public void setAwardId(String awardId) {
        this.awardId = awardId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Long getISBN() {
        return isbn;
    }

    public void setISBN(Long isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}