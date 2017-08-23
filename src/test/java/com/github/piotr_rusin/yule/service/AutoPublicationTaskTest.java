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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.piotr_rusin.yule.domain.Article;
import com.github.piotr_rusin.yule.domain.ArticleStatus;
import com.github.piotr_rusin.yule.repository.ArticleRepository;

import slf4jtest.Settings;
import slf4jtest.TestLoggerFactory;

@RunWith(SpringRunner.class)
public class AutoPublicationTaskTest {
    @Mock
    private ArticleRepository repository;

    @Mock
    private Logger mockLogger;

    private AutoPublicationTask task;
    private List<Article> autoPublicationTargets = new ArrayList<>();
    private Instant publicationTime;
    private TestLoggerFactory loggerFactory;

    @Before
    public void setUp() throws Exception {
        setUpAutoPublicationTargetsAndTime();
        Clock clock = Clock.fixed(publicationTime, ZoneId.systemDefault());
        loggerFactory = Settings.instance().enableAll()
                .delegate(AutoPublicationTask.class.getName(), mockLogger)
                .buildLogging();
        task = new AutoPublicationTask(repository, clock, loggerFactory);
    }

    private void setUpAutoPublicationTargetsAndTime() {
        for (int i = 0; i < 4; i++) {
            Article article = getAutoPublicationTarget(Instant.now());
            if (i == 0)
                publicationTime = article.getPublicationTimestamp();
            autoPublicationTargets.add(article);
        }

        when(repository.findCurrentAutoPublicationTargets())
                .thenReturn(autoPublicationTargets);
    }

    private Article getAutoPublicationTarget(Instant publicationDate) {
        int index = autoPublicationTargets.size();
        Article article = new Article("title " + index, "content " + index);
        article.setStatus(ArticleStatus.SCHEDULED_FOR_PUBLICATION);
        article.setPublicationTimestamp(publicationDate);
        return article;
    }

    @Test
    public void runLogsInitialMessage() {
        task.run();
        verify(mockLogger)
                .info("Executing scheduled automatic publication task");
    }

    @Test
    public void runLogsWarningIfNoArticlesToPublish() {
        autoPublicationTargets.clear();
        task.run();
        verify(mockLogger).warn("No articles to publish");
    }

    @Test
    public void runSetsPublishedStatus() {
        task.run();
        Condition<Article> published = new Condition<>(
                a -> a.getStatus() == ArticleStatus.PUBLISHED, "published");
        assertThat(autoPublicationTargets).are(published);
    }

    @Test
    public void runSavesArticle() {
        task.run();
        ArgumentCaptor<Article> article = ArgumentCaptor
                .forClass(Article.class);
        verify(repository, times(autoPublicationTargets.size()))
                .save(article.capture());
        List<Article> articles = article.getAllValues();
        assertThat(articles).containsOnlyElementsOf(autoPublicationTargets);
    }

    @Test
    public void runLogsWhenOptimisticLockFails() {
        ObjectOptimisticLockingFailureException ex = mock(
                ObjectOptimisticLockingFailureException.class);
        when(repository.save(any(Article.class))).thenThrow(ex);
        task.run();
        for (Article a : autoPublicationTargets) {
            verify(mockLogger).info(String.format(
                    "Postponing auto-publication of %s due to a concurrent update",
                    a));
        }
    }

    @Test
    public void runLogsAnAttemptDelayedByOverAMinute() {
        Map<Article, Duration> articleAutoPublicationDelay = new HashMap<>();
        for (int d = 2; d <= 4; d++) {
            Duration delay = Duration.ofSeconds(d * 60);
            Article article = getAutoPublicationTarget(
                    publicationTime.minus(delay));
            autoPublicationTargets.add(article);
            articleAutoPublicationDelay.put(article, delay);
        }

        task.run();
        for (Map.Entry<Article, Duration> articleAndDelay : articleAutoPublicationDelay
                .entrySet()) {
            Article article = articleAndDelay.getKey();
            Duration delay = articleAndDelay.getValue();
            verify(mockLogger).warn(String.format(
                    "The auto-publication attempt for %s was delayed - it was executed "
                            + "%s after the scheduled publication time.",
                    article, delay));
        }
    }
}
