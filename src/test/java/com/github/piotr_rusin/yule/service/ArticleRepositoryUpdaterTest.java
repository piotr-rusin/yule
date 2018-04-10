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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.piotr_rusin.yule.domain.Article;
import com.github.piotr_rusin.yule.exception.ResourceNotFoundException;
import com.github.piotr_rusin.yule.repository.ArticleRepository;

@RunWith(SpringRunner.class)
public class ArticleRepositoryUpdaterTest {
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private AutoPublicationScheduler autoPublicationScheduler;

    @Mock
    private ArticleProvider articleProvider;

    private ArticleRepositoryUpdater articleManager;

    @Before
    public void setUp() {
        articleManager = new ArticleRepositoryUpdater(articleRepository,
                autoPublicationScheduler, articleProvider);
    }

    private void assertSaves(Article article) {
        verify(articleRepository).save(article);
    }

    private Article getArticleToSave() {
        return new Article("New title", "New content");
    }

    @Test
    public void testSaveSavesTheArticle() {
        Article articleToSave = getArticleToSave();

        articleManager.save(articleToSave);

        assertSaves(articleToSave);
    }

    private void assertSchedulesAutoPublication() {
        verify(autoPublicationScheduler).scheduleNew();
    }

    @Test
    public void testSaveReschedulesAutoPublication() {
        Article articleToSave = getArticleToSave();

        articleManager.save(articleToSave);

        assertSchedulesAutoPublication();
    }

    @Test
    public void testSaveReturnsSavedArticle() {
        Article articleToSave = getArticleToSave();
        Article expected = new Article(articleToSave);
        doReturn(expected).when(articleRepository).save(articleToSave);

        Article actual = articleManager.save(articleToSave);

        assertThat(actual).isEqualTo(expected);
    }

    private Article getExistingArticle(Long id) {
        Article existing = new Article("Existing title", "Existing content");
        doReturn(existing).when(articleRepository).findOne(id);
        return existing;
    }

    /**
     * Versions of the same article involved in a concurrent update operation.
     *
     * @author Piotr Rusin <piotr.rusin88@gmail.com>
     */
    private class ConcurrentlyUpdated {
        /**
         * The version of the article to be saved.
         */
        Article toSave;
        /**
         * The newer version of the article, existing in the database at the
         * moment of calling {@link ArticleRepositoryUpdater#save(Article)} on
         * {@link ConcurrentlyUpdated#toSave}, as if the article is being
         * concurrently updated.
         */
        Article existing;

        ConcurrentlyUpdated() {
            toSave = getArticleToSave();
            doThrow(ObjectOptimisticLockingFailureException.class)
                    .when(articleRepository).save(toSave);

            existing = getExistingArticle(toSave.getId());
        }
    }

    private ConcurrentlyUpdated getConcurrentlyUpdatedArticlePair() {
        return new ConcurrentlyUpdated();
    }

    @Test
    public void testSaveUpdatesConcurrentlyUpdatedArticle() {
        ConcurrentlyUpdated pair = getConcurrentlyUpdatedArticlePair();

        articleManager.save(pair.toSave);

        assertThat(pair.existing).isEqualToComparingFieldByField(pair.toSave);
    }

    @Test
    public void testSaveSavesConcurrentlyUpdatedArticle() {
        ConcurrentlyUpdated pair = getConcurrentlyUpdatedArticlePair();

        articleManager.save(pair.toSave);

        assertSaves(pair.existing);
    }

    @Test
    public void testSaveReschedulesAutoPublicationAfterSavingConcurrentlyUpdatedArticle() {
        ConcurrentlyUpdated pair = getConcurrentlyUpdatedArticlePair();

        articleManager.save(pair.toSave);

        assertSchedulesAutoPublication();
    }

    @Test
    public void testSaveReturnsConcurrentlyUpdatedArticle() {
        ConcurrentlyUpdated pair = getConcurrentlyUpdatedArticlePair();

        Article expected = new Article(pair.existing);
        doReturn(expected).when(articleRepository).save(pair.existing);

        Article actual = articleManager.save(pair.toSave);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testDeleteDeletesAnArticle() {
        long id = 10;
        Article article = getExistingArticle(id);
        doReturn(article).when(articleProvider).getArticleById(id);

        articleManager.delete(id);
        verify(articleRepository).delete(id);
    }

    @Test
    public void testDeleteReschedulesAutoPublication() {
        long id = 10;
        Article article = getExistingArticle(id);
        doReturn(article).when(articleProvider).getArticleById(id);

        articleManager.delete(id);
        assertSchedulesAutoPublication();
    }

    @Test
    public void testDeleteReturnsExpectedArticle() {
        long id = 10;
        Article expected = getExistingArticle(id);
        doReturn(expected).when(articleProvider).getArticleById(id);

        Article actual = articleManager.delete(id);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testDeleteThrowsResourceNotFound() {
        long id = 10;
        doThrow(ResourceNotFoundException.class).when(articleProvider).getArticleById(id);
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> articleManager.delete(id));
    }
}
