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
package com.github.piotr_rusin.yule.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.piotr_rusin.yule.domain.Article;
import com.github.piotr_rusin.yule.domain.ArticleStatus;

/**
 * Integration tests for custom queries defined in
 * ArticleRepository interface.
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 *
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql("insert_article_repository_test_data.sql")
public class ArticleRepositoryTests {

    @Autowired
    private ArticleRepository articleRepository;

    private Pageable allArticlePageRequest;

    private List<Article> allArticles;

    @Before
    public void setUp() {
        allArticlePageRequest = new PageRequest(0, (int) articleRepository.count());
        allArticles = articleRepository.findAll();
    }

    @Test
    public void testFindPublishedPostsOrdersByPublicationDate() {
        Comparator<Article> desc = Comparator
                .comparing(Article::getPublicationDate)
                .reversed();

        List<Article> actualArticles = articleRepository
                .findPublishedPosts(allArticlePageRequest)
                .getContent();

        assertThat(actualArticles).isSortedAccordingTo(desc);
    }

    @Test
    public void testFindPublishedPostsFindsAllExpectedArticles() {
        List<Article> expectedArticles = filterPublicArticles(Article::isPost);
        Page<Article> actualArticles = articleRepository.findPublishedPosts(allArticlePageRequest);

        assertThat(actualArticles).hasSameElementsAs(expectedArticles);
    }

    @Test
    public void testFindPublishedPostBy() {
        Article expectedArticle = getRandomPublicArticleBy(Article::isPost);
        Article actualArticle = articleRepository.findPublishedPostBy(expectedArticle.getSlug());

        assertThat(actualArticle).isEqualTo(expectedArticle);
    }

    @Test
    public void testFindPublishedPageBy() {
        Article expectedArticle = getRandomPublicArticleBy(a -> !a.isPost());
        Article actualArticle = articleRepository.findPublishedPageBy(expectedArticle.getSlug());

        assertThat(actualArticle).isEqualTo(expectedArticle);
    }

    /**
     * Get an article randomly chosen from persistent published
     * articles fulfilling given condition.
     *
     * @param condition
     * @return
     */
    private Article getRandomPublicArticleBy(Predicate<Article> condition) {
        List<Article> articles = filterPublicArticles(condition::test);
        return getRandomArticleFrom(articles);
    }

    @Test
    public void testFindOneBySlug() {
        Article expectedArticle = getRandomArticleFrom(allArticles);
        Article actualArticle = articleRepository.findOneBySlug(expectedArticle.getSlug());

        assertThat(actualArticle).isEqualTo(expectedArticle);
    }

    private List<Article> filterPublicArticles(Predicate<Article> condition) {
        return filterArticles(condition, ArticleStatus.PUBLISHED);
    }

    /**
     * Test if the method finds all scheduled articles with given
     * publication date.
     */
    @Test
    public void findScheduledBy() {
        Article randomScheduled = getRandomArticleFrom(getAllScheduledArticles());
        Instant publicationDate = randomScheduled.getPublicationDate();
        List<Article> expected = filterScheduledArticles(
                (a) -> a.getPublicationDate().equals(publicationDate)
        );
        List<Article> actual = articleRepository.findScheduledBy(publicationDate);

        assertThat(actual).hasSameElementsAs(expected);
    }

    @Test
    public void findAllScheduled() {
        List<Article> expected = getAllScheduledArticles();
        List<Article> actual = articleRepository.findAllScheduled();

        assertThat(actual).isEqualTo(expected);
    }

    private Article getRandomArticleFrom(List<Article> articles) {
        Random rand = new Random();
        return articles.get(rand.nextInt(articles.size()));
    }

    private List<Article> getAllScheduledArticles() {
        return filterScheduledArticles(null);
    }

    @Test
    public void findNextScheduledPublicationTime() {
        Instant expected = filterScheduledArticles(null).stream()
                .map(Article::getPublicationDate).min((d1, d2) -> d1.compareTo(d2)).orElse(null);
        Date actual = articleRepository.findNextScheduledPublicationTime();

        assertThat(actual).isEqualTo(Date.from(expected));
    }

    @Test
    public void findCurrentAutoPublicationTargets() {
        List<Article> expected = filterScheduledArticles(a -> !a.getPublicationDate().isAfter(Instant.now()));
        List<Article> actual = articleRepository.findCurrentAutoPublicationTargets();

        assertThat(actual).hasSameElementsAs(expected);
    }

    private List<Article> filterScheduledArticles(Predicate<Article> condition) {
        return filterArticles(condition, ArticleStatus.SCHEDULED_FOR_PUBLICATION);
    }

    private List<Article> filterArticles(Predicate<Article> condition, ArticleStatus status) {
        Predicate<Article> hasStatus = (a) -> a.getStatus() == status;
        condition = condition != null ? condition : (a) -> true;
        return filterArticles(hasStatus.and(condition));
    }

    /**
     * Get all persistent articles fulfilling a condition.
     *
     * @param condition
     * @return a list of articles
     */
    private List<Article> filterArticles(Predicate<Article> condition) {
        return allArticles
                .stream()
                .filter(condition::test)
                .collect(Collectors.toList());
    }
}
