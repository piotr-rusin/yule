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
package com.github.piotr_rusin.yule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.piotr_rusin.yule.config.YuleConfig;
import com.github.piotr_rusin.yule.domain.Article;
import com.github.piotr_rusin.yule.exception.PageNotFoundException;
import com.github.piotr_rusin.yule.exception.ResourceNotFoundException;
import com.github.piotr_rusin.yule.repository.ArticleRepository;

/**
 * A service responsible for querying for articles to be displayed.
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 *
 */
@Service
public class ArticleProvider {
    private static final Logger logger = LoggerFactory
            .getLogger(ArticleProvider.class);

    private ArticleRepository repository;
    private YuleConfig config;

    @Autowired
    public ArticleProvider(ArticleRepository repository, YuleConfig config) {
        this.repository = repository;
        this.config = config;
    }

    /**
     * Get a requested page of articles.
     *
     * @param query
     *            is a query to be used to find the page.
     * @param queryFor
     *            is a string describing the exact type of article we query for.
     * @param pageRequest
     *            is a page request to be passed to the query.
     * @throws PageNotFoundException
     *             if the page is empty and not first.
     * @return the requested page
     */
    private Page<Article> getArticlePage(
            Function<Pageable, Page<Article>> query, String queryFor,
            Pageable pageRequest) {
        logger.info("Requesting a page of {}, with page request: {}", queryFor,
                pageRequest);
        Page<Article> articles = query.apply(pageRequest);

        if (articles.hasContent() || articles.isFirst()) {
            logger.info("The page was successfully found.");
            return articles;
        }

        throw new PageNotFoundException(
                String.format("No %s could be found for page request %s.",
                        queryFor, pageRequest));
    }

    /**
     * Get a page of published blog posts.
     *
     * @param page
     *            the number of the requested page, starting from 0.
     * @throws PageNotFoundException
     *             if the page is empty and not first.
     * @return the requested page.
     */
    public Page<Article> getBlogPostPage(int page) {
        PageRequest pageRequest = new PageRequest(page,
                config.getIndexPageSize());

        return getArticlePage(repository::findPublishedPosts,
                "published blog posts", pageRequest);
    }

    /**
     * Get a page of all existing articles.
     *
     * @param pageRequest
     *            is a page request to be passed to the query.
     * @throws PageNotFoundException
     *             if the page is empty and not first.
     * @return the requested page.
     */
    public Page<Article> getAdminArticleListPage(Pageable pageRequest) {
        return getArticlePage(repository::findAll, "articles", pageRequest);
    }

    private Article getArticleBy(String slug, Function<String, Article> query) {
        logger.info("Requesting \"{}\" article.", slug);
        Article article = query.apply(slug);
        if (article == null) {
            throw new ResourceNotFoundException(String.format(
                    "The requested article \"%s\" was not found.", slug));
        }

        logger.info("The request was handled succesfully, returning {}",
                article);
        return article;
    }

    /**
     * Get a published blog post.
     *
     * @param slug
     *            is a string used in a URL to identify the requested article.
     * @param publicationDate
     *            is a UTC-based publication date of the requested article.
     * @throws ResourceNotFoundException
     *             if the post wasn't found or it wasn't found under specified
     *             publication date.
     * @return the requested blog post.
     */
    public Article getPublishedBlogPost(String slug,
            LocalDate publicationDate) {
        logger.info("Requesting a blog post \"{}\", published on {}.", slug,
                publicationDate);
        Article article = repository.findPublishedPostBy(slug);
        if (article == null) {
            throw new ResourceNotFoundException(String.format(
                    "The requested blog post \"%s\" was not found.", slug,
                    publicationDate));
        }

        LocalDate actualPublicationDate = LocalDateTime
                .ofInstant(article.getPublicationTimestamp(), ZoneOffset.UTC)
                .toLocalDate();
        if (!publicationDate.equals(actualPublicationDate)) {
            throw new ResourceNotFoundException(String.format(
                    "Incorrect publication date for blog post %s - the requested date was %s.",
                    article, publicationDate));
        }

        logger.info("The request was handled succesfully, returning {}.",
                article);
        return article;
    }

    /**
     * Get a published blog page.
     *
     * @param slug
     *            is a string used in a URL to identify the requested article.
     * @throws ResourceNotFoundException
     *             if the page couldn't be found.
     * @return the requested blog page.
     */
    public Article getPublishedPage(String slug) {
        logger.info("Requesting \"{}\" blog page.", slug);
        Article article = repository.findPublishedPageBy(slug);
        if (article == null) {
            throw new ResourceNotFoundException(String.format(
                    "The requested blog page \"%s\" was not found.", slug));
        }

        logger.info("The request was handled succesfully, returning {}",
                article);
        return article;
    }

}
