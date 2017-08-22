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

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.github.piotr_rusin.yule.domain.Article;

/**
 * A validator ensuring given article violates no constraints put on it by its
 * status.
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 *
 */
public class StatusConstraintValidator
        implements ConstraintValidator<StatusConstraintsFulfilled, Article> {

    @Override
    public void initialize(StatusConstraintsFulfilled annotation) {
    }

    @Override
    public boolean isValid(Article article,
            ConstraintValidatorContext context) {
        if (article == null)
            return true;
        List<ExistingArticleConstraint> violatedStatusConstraints = article
                .getViolatedStatusConstraints();

        for (ExistingArticleConstraint constraint : violatedStatusConstraints) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    constraint.getViolationMessageTemplate())
                    .addPropertyNode(constraint.getPropertyName())
                    .addConstraintViolation();
        }

        return violatedStatusConstraints.isEmpty();
    }

}
