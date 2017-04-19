/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Piotr Rusin <piotr.rusin88@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package com.github.piotr_rusin.yule.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * A class representing blog articles.
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 */
@Entity
@EntityListeners(ArticleListener.class)
@Table(name = "articles")
public class Article {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    private String slug;

    private String content;

    @Column(name = "creation_date")
    @CreationTimestamp
    private Instant creationDate;

    @Column(name = "publication_date")
    private Instant publicationDate;

    @Enumerated(EnumType.STRING)
    private ArticleStatus status = ArticleStatus.DRAFT;

    @Column(name = "modification_date")
    @UpdateTimestamp
    private Instant modificationDate;

    /**
     * Specifies whether an article is also a blog post.
     *
     * All published articles on a blog are publicly accessible
     * through their URL addresses.
     *
     * All articles that are also blog posts are additionally
     * listed on one of the pages of a blog, in reverse
     * chronological order.
     */
    @Column(name = "is_blog_post")
    private boolean post = true;

    protected Article() {
    }

    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public Instant getPublicationDate() {
        return publicationDate;
    }

    public ArticleStatus getStatus() {
        return status;
    }

    /**
     * Set the status of the article.
     * <p>
     * If the status value is {@link ArticleStatus#PUBLISHED}, and the
     * current {@link Article#publicationDate} == null, the publication
     * date is set to the current {@link Instant}, truncated to minutes.
     * <p>
     * Otherwise, the status value must be applicable to the current
     * publication date of the article. The applicable values are:
     * <ul>
     * <li>{@link ArticleStatus#DRAFT} - for any publication date,
     * including null</li>
     * <li>{@link ArticleStatus#PUBLICATION_SCHEDULED} - for a future
     * publication date</li>
     * <li>{@link ArticleStatus#PUBLISHED} - for a past or present
     * publication date</li>
     * </ul>
     *
     * @param status
     *            is a status value associated with current life cycle
     *            stage of the article
     *
     * @throws IllegalArgumentException
     *             if the value of status parameter is not applicable
     *             for the current value of publicationDate property
     */
    public void setStatus(ArticleStatus status) {
        if (publicationDate == null && status == ArticleStatus.PUBLISHED) {
            publicationDate = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        } else {
            assertIsApplicable(status);
        }

        this.status = status;
    }

    private void assertIsApplicable(ArticleStatus status) {
        boolean isFuture = Instant.now().isBefore(publicationDate);
        boolean applicable = false;
        switch (status) {
            case DRAFT:
                applicable = true;
            case PUBLICATION_SCHEDULED:
                applicable = isFuture;
            case PUBLISHED:
                applicable = !isFuture;
        }
        if (!applicable) {
            throw new IllegalArgumentException(String.format(
                    "The value of status argument (%s) is not applicable for the publication date of the article (%s)",
                    status, publicationDate));
        }
    }

    public Instant getModificationDate() {
        return modificationDate;
    }

    public boolean isPost() {
        return post;
    }

    public void setPost(boolean post) {
        this.post = post;
    }
}
