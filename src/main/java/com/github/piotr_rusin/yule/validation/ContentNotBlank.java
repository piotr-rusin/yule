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
 * A constraint fulfilled if content of an article is neither null nor empty
 * (after whitespaces have been trimmed from it).
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 *
 */
public class ContentNotBlank extends ExistingArticleConstraint {

    /**
     * Create a new constraint.
     *
     * @param violationMessageTemplate
     *            is a message associated with a violation of the constraint
     */
    public ContentNotBlank(String violationMessageTemplate) {
        super("content", violationMessageTemplate);
    }

    @Override
    public boolean isFulfilledForExisting(Article article) {
        String content = article.getContent();
        return content != null && content.trim().length() > 0;
    }
}
