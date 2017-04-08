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
package com.github.piotr_rusin.yule.entity;

import static org.assertj.core.api.Assertions.*;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase.Replace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration tests for custom queries defined in
 * ArticleRepository interface.
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 *
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Sql("insert_article_repository_test_data.sql")
public class ArticleRepositoryTests {

	@Autowired
	private ArticleRepository articleRepository;

	private Pageable allArticlePageRequest;

	private List<Article> allArticles;

	@Before
	public void setUp() {
		allArticlePageRequest = new PageRequest(0, (int)articleRepository.count());
		allArticles = articleRepository.findAll();
	}

	@Test
	public void testFindPublicPostsOrdersByPublicationDate() {
		Comparator<Article> desc = Comparator
				.comparing(Article::getPublicationDate)
				.reversed();

		List<Article> actualArticles = articleRepository
				.findPublicPosts(allArticlePageRequest)
				.getContent();

		assertThat(actualArticles).isSortedAccordingTo(desc);
	}

	@Test
	public void testFindPublicPostsFindsAllExpectedArticles() {
		List<Article> expectedArticles = filterPublicArticles(Article::isPost);
		Page<Article> actualArticles = articleRepository.findPublicPosts(allArticlePageRequest);

		assertThat(actualArticles).hasSameElementsAs(expectedArticles);
	}

	@Test
	public void testFindPublicPostBy() {
		Article expectedArticle = getRandomPublicArticleBy(Article::isPost);
		Article actualArticle = articleRepository.findPublicPostBy(expectedArticle.getSlug());

		assertThat(actualArticle).isEqualTo(expectedArticle);
	}

	@Test
	public void testFindPublicPageBy() {
		Article expectedArticle = getRandomPublicArticleBy(a -> !a.isPost());
		Article actualArticle = articleRepository.findPublicPageBy(expectedArticle.getSlug());

		assertThat(actualArticle).isEqualTo(expectedArticle);
	}

	/**
	 * Get an article randomly chosen from persistent public
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

	private Article getRandomArticleFrom(List<Article> articles) {
		Random rand = new Random();
		return articles.get(rand.nextInt(articles.size()));
	}

	/**
	 * Get all persistent public articles fulfilling a condition.
	 *
	 * @param condition
	 * @return
	 */
	private List<Article> filterPublicArticles(Predicate<Article> condition) {
		Predicate<Article> isPublic = a -> a.getStatus() == ArticleStatus.PUBLIC;

		List<Article> articles = allArticles
				.stream()
				.filter(condition.and(isPublic)::test)
				.collect(Collectors.toList());

		return articles;
	}
}
