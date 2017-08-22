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

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.github.piotr_rusin.yule.domain.Article;
import com.github.piotr_rusin.yule.domain.ArticleStatus;
import com.github.piotr_rusin.yule.repository.ArticleRepository;

/**
 * Represents an auto-publication task performed for all scheduled articles
 * whose publication date has come or passed.
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 *
 */
public class AutoPublicationTask implements Runnable {
    private ArticleRepository repository;
    private Clock clock;
    private final Logger logger;

    /**
     * Create a new instance.
     *
     * @param repository
     *            is an object to be used to query for articles to publish
     * @param clock
     *            is a clock object to be used for detecting delayed executions
     *            of the task
     * @param loggerFactory
     */
    public AutoPublicationTask(ArticleRepository repository, Clock clock,
            ILoggerFactory loggerFactory) {
        this.repository = repository;
        this.clock = clock;
        logger = loggerFactory.getLogger(AutoPublicationTask.class.getName());
    }

    /**
     * Publish all due articles, each in a separate transaction.
     */
    @Override
    public void run() {
        logger.info("Executing scheduled automatic publication task");
        List<Article> toBePublished = repository
                .findCurrentAutoPublicationTargets();
        if (toBePublished.isEmpty())
            logger.warn("No articles to publish");
        for (Article a : toBePublished) {
            publish(a);
        }
    }

    /**
     * Publish an article.
     * <p>
     * If the article is being concurrently edited, the method fails and logs a
     * warning.
     * <p>
     * In any case, the method calculates the difference between scheduled
     * auto-publication time and the actual time of the attempt. If this time is
     * longer than a minute, it may indicate some problems preventing executing
     * the task on time (possibly server failures).
     *
     * @param article
     *            is an article to be published
     */
    private void publish(Article article) {
        article.setStatus(ArticleStatus.PUBLISHED);

        try {
            repository.save(article);
            logger.info("Completed autopublication for " + article);
        } catch (ObjectOptimisticLockingFailureException e) {
            logger.info(String.format(
                    "Postponing auto-publication of %s due to a concurrent update",
                    article));
        } finally {
            Duration delay = Duration.ofSeconds(article.getPublicationDate()
                    .until(Instant.now(clock), ChronoUnit.SECONDS));
            if (delay.toMinutes() >= 1) {
                logger.warn(String.format(
                        "The auto-publication attempt for %s was delayed - it was executed "
                                + "%s after the scheduled publication time.",
                        article, delay));
            }
        }
    }
}
