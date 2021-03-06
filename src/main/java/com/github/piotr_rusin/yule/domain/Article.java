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
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import com.github.piotr_rusin.yule.validation.ExistingArticleConstraint;
import com.github.piotr_rusin.yule.validation.StatusConstraintsFulfilled;

/**
 * A class representing blog articles.
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 */
@Entity
@EntityListeners(ArticleListener.class)
@Table(name = "articles")
@StatusConstraintsFulfilled
public class Article {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    private String slug;

    private String customIntroduction;

    private String content;

    @Column(name = "creation_timestamp")
    @CreationTimestamp
    private Instant creationTimestamp;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "publication_timestamp")
    private Instant publicationTimestamp;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ArticleStatus status = ArticleStatus.DRAFT;

    @Column(name = "modification_timestamp")
    @UpdateTimestamp
    private Instant modificationTimestamp;

    @Version
    private int version;

    @Column(name = "is_blog_post")
    private boolean post = true;

    public Article() {
    }

    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * Create a new article as a copy of an existing one.
     *
     * @param article
     *            is an article to copy
     */
    public Article(Article article) {
        this.setAdminAlterableData(article);
        this.id = article.id;
        this.creationTimestamp = article.creationTimestamp;
        this.modificationTimestamp = article.modificationTimestamp;
        this.slug = article.slug;
        this.version = article.version;
    }

    /**
     * Set data allowed to be alterable by the blog administrator to the values
     * provided by a data transfer object.
     * <p>
     * We assume admin-alterable data are all properties of the Article, except:
     * {@link Article#id id}, {@link Article#creationTimestamp creation
     * timestamp}, {@link Article#modificationTimestamp modification timestamp},
     * {@link Article#slug slug} and {@link Article#version version}.
     *
     * @param dto
     *            is the object providing new data for this article.
     */
    public void setAdminAlterableData(Article dto) {
        this.title = dto.title;
        this.content = dto.content;
        this.post = dto.post;
        this.publicationTimestamp = dto.publicationTimestamp;
        this.status = dto.status;
        this.customIntroduction = dto.customIntroduction;
    }

    public Long getId() {
        return id;
    }

    public boolean isNew() {
        return id == null;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the article.
     *
     * @param title
     *            is the value to be set. It can't be null or empty.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the current value representing the article in URL addresses.
     *
     * @return the value, either a custom one or one generated by
     *         {@link ArticleListener#makeSureHasSlug(Article) a pre-persist and
     *         pre-update event handler}
     * @see #setSlug(String)
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Set a custom value representing the article in URL addresses.
     *
     * @param slug
     *            is a value to be set as a slug. If it's null, and no other
     *            value will be set before saving the article in the database, a
     *            value based on the title at the time of persist or update
     *            operation will be generated and set instead.
     * @see ArticleListener#makeSureHasSlug(Article)
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * Get a value used as an introduction to the article
     *
     * @return the custom introduction value, if set, otherwise a value derived
     *         from the current content. If the content is not null and has a
     *         <!--more--> tag, the part of the content before the tag will be
     *         returned, otherwise the whole content will.
     */
    public String getIntroduction() {
        if (customIntroduction != null && !customIntroduction.isEmpty())
            return customIntroduction;
        if (content == null)
            return null;
        String[] segments = content.split("<!--more-->");
        if (segments.length > 1) {
            return segments[0];
        }
        return null;
    }

    /**
     * Get a custom introduction value for the article
     *
     * @return the custom introduction value
     */
    public String getCustomIntroduction() {
        return customIntroduction;
    }

    /**
     * Set a custom value to be used as an introduction to the article.
     *
     * @param value
     *            is the value to be set
     * @see #getCustomIntroduction()
     */
    public void setCustomIntroduction(String value) {
        customIntroduction = value;
    }

    public String getContent() {
        return content;
    }

    /**
     * Set the content of the article.
     *
     * @param content
     *            is a value to be set as the content. Using an invalid value
     *            will result in leaving the article in an invalid state. The
     *            choice of a correct value depends on
     *            {@link Article#getStatus() the current status} of the article.
     * @see ArticleStatus
     * @see StatusConstraintsFulfilled
     */
    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreationTimestamp() {
        return creationTimestamp;
    }

    public Instant getPublicationTimestamp() {
        return publicationTimestamp;
    }

    /**
     * Set the publication timestamp of the article
     *
     * @param publicationTimestamp
     *            is a publication date and time value (in UTC) to be set. Using
     *            an incorrect value will result in leaving the article object
     *            in an invalid state. The choice of a correct value depends on
     *            {@link Article#getStatus() the current status} of the article.
     * @see ArticleStatus
     * @see StatusConstraintsFulfilled
     */
    public void setPublicationTimestamp(Instant publicationTimestamp) {
        if (publicationTimestamp != null)
            publicationTimestamp = publicationTimestamp
                    .truncatedTo(ChronoUnit.MINUTES);
        this.publicationTimestamp = publicationTimestamp;
    }

    /**
     * Get the status of the article.
     *
     * @return the current status.
     * @see ArticleStatus
     */
    public ArticleStatus getStatus() {
        return status;
    }

    /**
     * Set the status of the article
     * <p>
     * The status object may provide some additional constraints for its owner,
     * so setting it may leave the article in an invalid state.
     * <p>
     * If the status being set is {@link ArticleStatus#PUBLISHED "PUBLISHED"}
     * and the article has no {@link Article#publicationTimestamp publication
     * timestamp} set, the current moment will be set as the publication
     * timestamp.
     *
     * @param status
     *            is a status value to be set
     * @see ArticleStatus
     * @see StatusConstraintsFulfilled
     */
    public void setStatus(ArticleStatus status) {
        if (status == ArticleStatus.PUBLISHED && publicationTimestamp == null)
            setPublicationTimestamp(Instant.now());
        this.status = status;
    }

    public boolean isScheduledForPublication() {
        return status == ArticleStatus.SCHEDULED_FOR_PUBLICATION;
    }

    /**
     * Get status constraints violated by this article.
     *
     * @return a List containing all status constraints provided by the status
     *         object of this article that are not fulfilled by this article.
     */
    public List<ExistingArticleConstraint> getViolatedStatusConstraints() {
        if (status == null)
            return Collections.emptyList();
        return status.constraints.stream().filter(c -> !c.isFulfilledFor(this))
                .collect(Collectors.toList());
    }

    public Instant getModificationTimestamp() {
        return modificationTimestamp;
    }

    /**
     * Get the version number of the article.
     *
     * @return a version number used by optimistic locking mechanism.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Check if the article is also a blog post.
     * <p>
     * All published articles on a blog are publicly accessible through their
     * URL addresses.
     * <p>
     * All articles that are also blog posts are additionally listed on one of
     * the pages of a blog, in reverse chronological order.
     *
     * @return true if the article is a blog post
     */
    public boolean isPost() {
        return post;
    }

    /**
     * Set a value marking the article as a blog post.
     *
     * @param post
     *            is true if the article is to become a blog post, false
     *            otherwise
     */
    public void setPost(boolean post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "Article [" + (title != null ? "title=" + title + ", " : "")
                + (creationTimestamp != null
                        ? "creationTimestamp=" + creationTimestamp + ", "
                        : "")
                + (publicationTimestamp != null
                        ? "publicationTimestamp=" + publicationTimestamp + ", "
                        : "")
                + (status != null ? "status=" + status + ", " : "") + "post="
                + post + "]";
    }
}
