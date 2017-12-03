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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.github.piotr_rusin.yule.domain.Article;
import com.github.piotr_rusin.yule.exception.ResourceNotFoundException;
import com.github.piotr_rusin.yule.repository.ArticleRepository;

/**
 * A service encapsulating logic behind operations updating article repository.
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 */
@Service
public class ArticleRepositoryUpdater {
    private static final Logger logger = LoggerFactory
            .getLogger(ArticleRepositoryUpdater.class);

    private ArticleRepository articleRepository;
    private AutoPublicationScheduler publicationScheduler;
    private ArticleProvider articleProvider;

    public ArticleRepositoryUpdater(ArticleRepository articleRepository,
            AutoPublicationScheduler publicationScheduler, ArticleProvider articleProvider) {
        this.articleRepository = articleRepository;
        this.publicationScheduler = publicationScheduler;
        this.articleProvider = articleProvider;
    }

    /**
     * Save a new article or update an existing one.
     * <p>
     * After the article is successfully saved, auto-publication task is
     * rescheduled in case this article is scheduled for auto-publication and
     * the next auto-publication time needs to be updated.
     *
     * @param article
     *            is an article to be saved or updated.
     * @return an article object representing the version of the article in the
     *         database, after it was successfully saved.
     */
    public Article save(Article article) {
        logger.info("Attempting to save an article: {}.", article);
        Article saved = null;
        try {
            saved = articleRepository.save(article);
            logger.info("The article {} was successfully saved.", saved);
        } catch (ObjectOptimisticLockingFailureException e) {
            logger.info(
                    "The article {} was edited concurrently. The changes are "
                            + "being merged based on an assumption that the concurrent edit "
                            + "was only an automatic publication.",
                    saved);
            Article mostRecentlySaved = articleRepository
                    .findOne(article.getId());
            mostRecentlySaved.setAdminAlterableData(article);
            saved = articleRepository.save(mostRecentlySaved);
        }
        publicationScheduler.scheduleNew();
        return saved;
    }

    /**
     * Delete an article from the repository.
     * <p>
     * After the article is successfully deleted, auto-publication task is
     * rescheduled, in case the article was the only one scheduled for next
     * auto-publication.
     * 
     * @param id
     *            is an identifier of the article to be deleted.
     * @throws ResourceNotFoundException
     *             if the article couldn't be found.
     * @return an article object representing the deleted article.
     */
    public Article delete(long id) {
        Article article = articleProvider.getArticleById(id);
        articleRepository.delete(id);
        logger.info("The article {} has been successfully deleted.", article);
        publicationScheduler.scheduleNew();
        return article;
    }

}
