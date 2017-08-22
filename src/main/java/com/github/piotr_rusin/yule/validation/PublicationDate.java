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
package com.github.piotr_rusin.yule.validation;

import java.time.Instant;
import java.util.function.BiPredicate;

import com.github.piotr_rusin.yule.domain.Article;

/**
 * A constraint fulfilled if publication date of an article is not null and is
 * in a pre-configured range (either future or non-future).
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 *
 */
public class PublicationDate extends ExistingArticleConstraint {
    /**
     * A function to be used to test the publication date in relation to the
     * current time.
     */
    private BiPredicate<Instant, Instant> propertyTest;

    /**
     * Create the constraint ensuring an article has a future publication date.
     *
     * @param violationMessageTemplate
     *            is a message associated with a violation of this constraint
     * @return a new constraint object
     */
    public static PublicationDate Future(String violationMessageTemplate) {
        return new PublicationDate(true, violationMessageTemplate);
    }

    /**
     * Create a constraint ensuring an article has a non-future publication
     * date.
     *
     * @param violationMessageTemplate
     *            is a message associated with a violation of this constraint
     * @return a new constraint object
     */
    public static PublicationDate NonFuture(String violationMessageTemplate) {
        return new PublicationDate(false, violationMessageTemplate);
    }

    /**
     * Construct the constraint.
     *
     * @param future
     *            if true, the Article fulfilling the constraint must have a
     *            future publication date, otherwise it must have a non-future
     *            one
     */
    private PublicationDate(boolean future, String violationMessageTemplate) {
        super("publicationDate", violationMessageTemplate);
        propertyTest = future ? Instant::isBefore : (n, p) -> !n.isBefore(p);
    }

    @Override
    protected boolean isFulfilledForExisting(Article article) {
        Instant publicationDate = article.getPublicationDate();
        if (publicationDate == null)
            return false;
        return propertyTest.test(Instant.now(), article.getPublicationDate());
    }

}
