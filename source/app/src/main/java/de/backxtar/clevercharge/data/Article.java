package de.backxtar.clevercharge.data;

/**
 * Article container.
 * @author Jörg Quick, Tilo Steinmetzer
 * @version 1.0
 */

public class Article {

    /* Article values */

    /**
     * Article ID.
     */
    private int id;

    /**
     * News category.
     */
    private String category;

    /**
     * News author.
     */
    private String author;

    /**
     * News title.
     */
    private String title;

    /**
     * News description.
     */
    private String description;

    /**
     * Url to news image.
     */
    private String urlToImage;

    /**
     * Date news was published.
     */
    private String publishedAt;

    /**
     * News content.
     */
    private String content;
    //============================

    /**
     * Get news id.
     * @return id as int
     */
    public int getId() {
        return id;
    }

    /**
     * Get news category.
     * @return category as String
     */
    public String getCategory() {
        return category;
    }

    /**
     * Get news author.
     * @return author as String
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Get news title.
     * @return title as String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get news description.
     * @return desc as String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get news image-url.
     * @return url as String
     */
    public String getUrlToImage() {
        return urlToImage;
    }

    /**
     * Get news published date.
     * @return date as String
     */
    public String getPublishedAt() {
        return publishedAt;
    }

    /**
     * Get news content.
     * @return content as String
     */
    public String getContent() {
        return content;
    }
}
