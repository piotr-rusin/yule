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
package com.github.piotr_rusin.yule.config;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("yule")
@Validated
public class YuleConfig {
    @NotEmpty(message = "The configured blog title must not be empty. "
            + "The default value is \"Default blog title\".")
    private String title = "Default blog title";

    @Size(min = 1, message = "Blog description is optional, but when "
            + "specified, it must not be an empty value.")
    private String description;

    @Min(value = 5, message = "The number of articles presented on a single "
            + "page of the published blog post list (indexPageSize) must not "
            + "be smaller than 5 (the default value)")
    private int indexPageSize = 5;

    @Min(value = 5, message = "The number of articles visible on a page of "
            + "the article list on administrator's panel "
            + "(adminArticleListPageSize) must not be smaller than 5 "
            + "(the default value is 10)")
    private int adminArticleListPageSize = 10;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setIndexPageSize(int size) {
        indexPageSize = size;
    }

    public int getIndexPageSize() {
        return indexPageSize;
    }

    public void setAdminArticleListPageSize(int size) {
        adminArticleListPageSize = size;
    }

    public int getAdminArticleListPageSize() {
        return adminArticleListPageSize;
    }
}
