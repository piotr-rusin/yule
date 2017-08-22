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

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for validation constraints of Article class and for some of its methods
 */
public class ArticleTests {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    /**
     * An article to be used during tests
     */
    private Article article;

    @BeforeClass
    public static void prepareValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterClass
    public static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Before
    public void prepareValidArticle() {
        prepareValidArticle(ArticleStatus.DRAFT);
    }

    /**
     * Prepare a valid article.
     *
     * @param status
     *            is a status assigned to the article
     */
    public void prepareValidArticle(ArticleStatus status) {
        article = new Article("Example title", "Example content");

        Instant publicationDate;

        switch (status) {
        case PUBLISHED:
            publicationDate = Instant.MIN;
            break;
        case SCHEDULED_FOR_PUBLICATION:
            publicationDate = Instant.MAX;
            break;
        case DRAFT:
        default:
            publicationDate = null;
        }

        article.setPublicationDate(publicationDate);
        article.setStatus(status);
    }

    @Test
    public void articleIsValid() {
        Set<ConstraintViolation<Article>> constraints = validator
                .validate(article);
        assertThat(constraints).isEmpty();
    }

    @Test
    public void shouldDetectBlankOrNullTitle() {
        String[] titles = { null, "", " " };
        for (String t : titles) {
            article.setTitle(t);
            assertDetectedOneExpectedViolation("title", t, "may not be empty");
        }
    }

    @Test
    public void shouldDetectBlankAndNullContent() {
        Object[][] statusAndMessagePairs = { {
                ArticleStatus.SCHEDULED_FOR_PUBLICATION,
                "this value must not be blank for scheduled publication" },
                { ArticleStatus.PUBLISHED,
                        "this value must not be blank for publication" } };

        String[] contentValues = { null, "", "  " };
        for (String content : contentValues) {
            for (Object[] data : statusAndMessagePairs) {
                prepareValidArticle((ArticleStatus) data[0]);
                article.setContent(content);
                assertDetectedOneExpectedViolation("content", article,
                        (String) data[1]);
            }
        }
    }

    @Test
    public void shouldDetectInvalidPublicationDate() {
        Object[][] statusPublicationDateAndMessageCombinations = { {
                ArticleStatus.SCHEDULED_FOR_PUBLICATION, Instant.MIN,
                "this value must be a future one for scheduled publication" },
                { ArticleStatus.PUBLISHED, Instant.MAX,
                        "this value must be a current or a past one for publication" } };

        for (Object[] data : statusPublicationDateAndMessageCombinations) {
            Instant publicationDate = (Instant) data[1];
            article.setStatus((ArticleStatus) data[0]);
            article.setPublicationDate(publicationDate);
            assertDetectedOneExpectedViolation("publicationDate", article,
                    (String) data[2]);
        }
    }

    @Test
    public void shouldDetectNullStatus() {
        article.setStatus(null);
        assertDetectedOneExpectedViolation("status", null, "may not be null");
    }

    /**
     * Assert that one expected violation was detected.
     *
     * @param propertyName
     *            is a name of a property for which we expect a violation
     * @param expectedValue
     *            is an expected value associated with the violation
     * @param expectedMessage
     *            is an expected message associated with the violation
     */
    private void assertDetectedOneExpectedViolation(String propertyName,
            Object expectedValue, String expectedMessage) {
        Set<ConstraintViolation<Article>> constraints = validator
                .validate(article);

        assertThat(constraints).hasSize(1);
        ConstraintViolation<Article> violation = constraints.iterator().next();
        assertThat(violation.getMessage()).isEqualTo(expectedMessage);
        assertThat(violation.getPropertyPath().toString())
                .isEqualTo(propertyName);
        assertThat(violation.getInvalidValue()).isEqualTo(expectedValue);
    }
}
