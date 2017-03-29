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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.piotr_rusin.yule.entity.Article;
import com.github.slugify.Slugify;

/**
 * Integration tests for ArticleListener class.
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 *
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
public class ArticleListenerTests {

	@Autowired
	private TestEntityManager entityManager;

	private Slugify slg = new Slugify();

	private Article article;

	@Before
	public void prepareArticle() {
		article = new Article();
		article.setTitle("Lorem ipsum: dolor sit amet");
		article.setContent(
			"Lorem ipsum dolor sit amet, consectetur adipiscing" +
			" elit, sed do eiusmod tempor incididunt ut labore et" +
			" dolore magna aliqua."
		);
	}

	private void testExpectedSlug(String expectedSlug) {
		Article persistedArticle = entityManager.persistFlushFind(article);
		assertThat(persistedArticle.getSlug()).isEqualTo(expectedSlug);
	}

	@Test
	public void testSlugIsCreatedFromTitle() {
		String slugFromTitle = slg.slugify(article.getTitle());
		testExpectedSlug(slugFromTitle);
	}

	@Test
	public void testSlugIsPreservedWhenNotNull() {
		String customSlug = slg.slugify("My custom slug");
		article.setSlug(customSlug);
		testExpectedSlug(customSlug);
	}

	/**
	 * Test if a slug based on the original title of a persisted
	 * article is preserved after the title has been modified and
	 * the change has been persisted.
	 */
	@Test
	public void testSlugIsPreservedAfterUpdatingTitle() {
		Article persistedArticle = entityManager.persistFlushFind(article);
		String oldSlug = persistedArticle.getSlug();
		persistedArticle.setTitle("A new title");
		article = persistedArticle;

		testExpectedSlug(oldSlug);
	}

	/**
	 * Test if a new slug value is created for a persisted article
	 * after its slug value has been set to null and the updated
	 * article has been persisted.
	 */
	@Test
	public void testSlugIsCreatedFromTitleAfterBeingUnset() {
		String initialSlug = slg.slugify("My custom slug");
		article.setSlug(initialSlug);
		Article persistedArticle = entityManager.persistFlushFind(article);
		String newSlug = slg.slugify(persistedArticle.getTitle());
		persistedArticle.setSlug(null);
		article = persistedArticle;

		testExpectedSlug(newSlug);
	}

}
