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

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.piotr_rusin.yule.config.YuleConfig;
import com.github.piotr_rusin.yule.domain.Article;
import com.github.piotr_rusin.yule.domain.ArticleStatus;
import com.github.piotr_rusin.yule.exception.PageNotFoundException;
import com.github.piotr_rusin.yule.exception.ResourceNotFoundException;
import com.github.piotr_rusin.yule.repository.ArticleRepository;

@RunWith(SpringRunner.class)
public class ArticleProviderTest {
    @Mock
    private ArticleRepository repository;

    @Mock
    private YuleConfig config;

    private ArticleProvider articleProvider;

    @Before
    public void setUp() {
        articleProvider = new ArticleProvider(repository, config);
    }

    /**
     * Get a page of articles to be used during a test.
     *
     * @param pageRequest
     *            is a page request used to query for the page.
     * @param empty
     *            if true, the returned collection will be empty.
     * @return the page.
     */
    private Page<Article> getArticlePage(PageRequest pageRequest,
            boolean empty) {
        List<Article> content = new ArrayList<>();
        int actualCount = empty ? 0 : pageRequest.getPageSize();
        for (int i = 0; i < actualCount; i++) {
            content.add(new Article("Title " + i, "Content " + i));
        }
        return new PageImpl<>(content, pageRequest, actualCount * 10);
    }

    /**
     * Configure the repository to return a page of blog posts.
     * <p>
     * The configuration mock object is also configured so that the number of
     * posts listed on the index page will be equal to the size of the requested
     * page.
     *
     * @param page
     *            is the page to be configured as the return value of a query
     *            for published blog posts.
     * @param pageRequest
     *            is a page request to be configured as a request for the page.
     */
    private void setUpAsPublishedBlogPostPage(Page<Article> page,
            PageRequest pageRequest) {
        doReturn(pageRequest.getPageSize()).when(config).getIndexPageSize();
        doReturn(page).when(repository).findPublishedPosts(pageRequest);
    }

    @Test
    public void testGetBlogPostPageReturnsRequestedPage() {
        PageRequest pageRequest = new PageRequest(2, 10);
        boolean empty = false;
        Page<Article> expected = getArticlePage(pageRequest, empty);
        setUpAsPublishedBlogPostPage(expected, pageRequest);

        Page<Article> actual = articleProvider.getBlogPostPage(2);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGetBlogPostPageReturnsNull() {
        PageRequest pageRequest = new PageRequest(0, 10);
        boolean empty = true;
        Page<Article> expected = getArticlePage(pageRequest, empty);
        setUpAsPublishedBlogPostPage(expected, pageRequest);

        Page<Article> actual = articleProvider.getBlogPostPage(0);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGetBlogPostPageRaisesPageNotFound() {
        PageRequest pageRequest = new PageRequest(2, 10);
        boolean empty = true;
        Page<Article> expected = getArticlePage(pageRequest, empty);
        setUpAsPublishedBlogPostPage(expected, pageRequest);

        assertThatExceptionOfType(PageNotFoundException.class)
                .isThrownBy(() -> articleProvider.getBlogPostPage(2))
                .withMessage(String.format(
                        "No published blog posts could be found for page request %s.",
                        pageRequest));
    }

    @Test
    public void testGetAdminArticleListPageReturnsRequestedPage() {
        PageRequest pageRequest = new PageRequest(2, 10);
        boolean empty = false;
        Page<Article> expected = getArticlePage(pageRequest, empty);
        doReturn(expected).when(repository).findAll(pageRequest);

        Page<Article> actual = articleProvider
                .getAdminArticleListPage(pageRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGetAdminArticleListPageReturnsNull() {
        PageRequest pageRequest = new PageRequest(0, 10);
        boolean empty = true;
        Page<Article> expected = getArticlePage(pageRequest, empty);
        doReturn(expected).when(repository).findAll(pageRequest);

        Page<Article> actual = articleProvider
                .getAdminArticleListPage(pageRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGetAdminArticleListPageRaisesPageNotFound() {
        PageRequest pageRequest = new PageRequest(2, 10);
        boolean empty = true;
        Page<Article> expected = getArticlePage(pageRequest, empty);
        doReturn(expected).when(repository).findAll(pageRequest);

        assertThatExceptionOfType(PageNotFoundException.class).isThrownBy(
                () -> articleProvider.getAdminArticleListPage(pageRequest))
                .withMessage(String.format(
                        "No articles could be found for page request %s.",
                        pageRequest));
    }

    /**
     * Get a published article to be used during tests.
     * <p>
     * The article is configured to be published almost immediately at the
     * moment of creation, so it can be queried by a current local UTC-based
     * date.
     *
     * @param post
     *            if true, the article will also be recognized as a blog post,
     *            otherwise it will be recognized as a blog page.
     * @return the article.
     */
    private Article getPublishedArticle(boolean post) {
        Article expected = new Article("Title", "Content");
        expected.setStatus(ArticleStatus.PUBLISHED);
        expected.setPost(post);
        return expected;
    }

    @Test
    public void testGetPublishedBlogPostReturnsABlogPost() {
        String slug = "title";
        LocalDate publicationDate = LocalDate.now(ZoneOffset.UTC);
        boolean post = true;
        Article expected = getPublishedArticle(post);
        doReturn(expected).when(repository).findPublishedPostBy(slug);

        Article actual = articleProvider.getPublishedBlogPost(slug,
                publicationDate);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGetPublishedBlogPostThrowsResourceNotFoundForNullArticle() {
        String slug = "non-existing-article";
        LocalDate publicationDate = LocalDate.now(ZoneOffset.UTC);

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> articleProvider.getPublishedBlogPost(slug,
                        publicationDate))
                .withMessage(String.format(
                        "The requested article \"%s\" was not found.", slug));
    }

    @Test
    public void testGetPublishedBlogPostThrowsResourceNotFoundForIncorrectPublicationDate() {
        String slug = "title";
        boolean post = true;
        Article expected = getPublishedArticle(post);
        doReturn(expected).when(repository).findPublishedPostBy(slug);

        LocalDate publicationDate = LocalDate.now(ZoneOffset.UTC).minusDays(1);

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> articleProvider.getPublishedBlogPost(slug,
                        publicationDate))
                .withMessage(String.format(
                        "Incorrect publication date for blog post %s - the requested date was %s.",
                        expected, publicationDate));
    }

    @Test
    public void testGetPublishedPageReturnsArticle() {
        String slug = "title";

        boolean post = false;
        Article expected = getPublishedArticle(post);
        doReturn(expected).when(repository).findPublishedPageBy(slug);

        Article actual = articleProvider.getPublishedPage(slug);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGetPublishedPageThrowsResourceNotFound() {
        String slug = "non-existing-page";
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> articleProvider.getPublishedPage(slug))
                .withMessage(String.format(
                        "The requested blog page \"%s\" was not found.", slug));
    }

}
