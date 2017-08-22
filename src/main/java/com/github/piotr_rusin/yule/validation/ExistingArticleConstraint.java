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

import com.github.piotr_rusin.yule.domain.Article;

/**
 * A testable constraint for an article object, associated with one of its
 * fields and a violation message template.
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 *
 */
abstract public class ExistingArticleConstraint {
    private String propertyName;
    protected String violationMessageTemplate;

    /**
     * Create a new constraint.
     *
     * @param propertyName
     *            is a name of a property associated with the constraint
     * @param violationMessageTemplate
     *            is a message template associated with a violation of the
     *            constraint
     */
    protected ExistingArticleConstraint(String propertyName,
            String violationMessageTemplate) {
        this.propertyName = propertyName;
        this.violationMessageTemplate = violationMessageTemplate;
    }

    /**
     * Get the name of a property associated with this constraint.
     *
     * @return the name of the property
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Get the message template associated with a violation of this constraint.
     *
     * @return the message template
     */
    public String getViolationMessageTemplate() {
        return violationMessageTemplate;
    }

    /**
     * Check if an article fulfills the constraint.
     *
     * @param article
     *            is an article to be checked
     * @return true if the article is not null and fulfills the constraint
     */
    public boolean isFulfilledFor(Article article) {
        if (article == null)
            return false;
        return isFulfilledForExisting(article);
    }

    protected abstract boolean isFulfilledForExisting(Article article);
}
