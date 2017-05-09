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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.NotBlank;

import com.github.piotr_rusin.yule.validation.ExistingArticleConstraint;

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

    @NotBlank
    private String title;

    private String slug;

    private String introduction;

    private String content;

    @Column(name = "creation_date")
    @CreationTimestamp
    private Instant creationDate;

    @Column(name = "publication_date")
    private Instant publicationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ArticleStatus status = ArticleStatus.DRAFT;

    @Column(name = "modification_date")
    @UpdateTimestamp
    private Instant modificationDate;

    @Version
    private int version;

    @Transient
    private Article cache;

    /**
     * Specifies whether an article is also a blog post.
     *
     * All published articles on a blog are publicly accessible through
     * their URL addresses.
     *
     * All articles that are also blog posts are additionally listed on
     * one of the pages of a blog, in reverse chronological order.
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

    public String getIntroduction() {
        if (introduction != null)
            return introduction;
        return content.split("<!--more-->")[0];
    }

    public void setIntroduction(String value) {
        introduction = value;
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

    public void setPublicationDate(Instant publicationDate) {
        if (publicationDate != null)
            publicationDate = publicationDate.truncatedTo(ChronoUnit.MINUTES);
        this.publicationDate = publicationDate;
    }

    public ArticleStatus getStatus() {
        return status;
    }

    public void setStatus(ArticleStatus status) {
        this.status = status;
    }

    public boolean isScheduledForPublication() {
        return status == ArticleStatus.SCHEDULED_FOR_PUBLICATION;
    }

    /**
     * Get status constraints violated by this article.
     *
     * @return a List containing all status constraints provided by the
     *         status object of this article that are not fulfilled by
     *         this article.
     */
    public List<ExistingArticleConstraint> getViolatedStatusConstraints() {
        if (status == null)
            return Collections.emptyList();
        return status.constraints
                .stream()
                .filter(c -> !c.isFulfilledFor(this))
                .collect(Collectors.toList());
    }

    public Instant getModificationDate() {
        return modificationDate;
    }

    public int getVersion() {
        return version;
    }

    public boolean isPost() {
        return post;
    }

    public void setPost(boolean post) {
        this.post = post;
    }

    /**
     * Save the current state of the entity into its cache object.
     */
    @PostLoad
    public void cache() {
        Article currentState = new Article(title, content);
        currentState.creationDate = creationDate;
        currentState.id = id;
        currentState.modificationDate = modificationDate;
        currentState.post = post;
        currentState.publicationDate = publicationDate;
        currentState.slug = slug;
        currentState.status = status;
        currentState.version = version;

        currentState.cache = cache;
        cache = currentState;
    }

    /**
     * Get an Article object representing cached state of this article.
     * <p>
     * The cached state represents the state of this article at the
     * moment of the last call to its {@link #cache() cache) method. It
     * may be the state of the entity upon being loaded from database if
     * the cache method wasn't called after this operation.
     *
     * @return an instance of Article representing state of the object
     *         after the last call to the cache() method, or null if the
     *         method wasn't called yet.
     */
    public Article getCache() {
        return cache;
    }

    @Override
    public String toString() {
        return "Article [" + (title != null ? "title=" + title + ", " : "")
                + (creationDate != null ? "creationDate=" + creationDate + ", " : "")
                + (publicationDate != null ? "publicationDate=" + publicationDate + ", " : "")
                + (status != null ? "status=" + status + ", " : "") + "post=" + post + "]";
    }
}
