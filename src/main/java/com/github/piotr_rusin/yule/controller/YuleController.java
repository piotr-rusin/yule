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
package com.github.piotr_rusin.yule.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.github.piotr_rusin.yule.domain.Article;
import com.github.piotr_rusin.yule.service.ArticleProvider;

@Controller
public class YuleController {

    private ArticleProvider articleProvider;

    @Autowired
    public YuleController(ArticleProvider articleProvider) {
        this.articleProvider = articleProvider;
    }

    @GetMapping({ "/", "/page/{page:[1-9][0-9]*}" })
    public String getBlogPostListPage(
            @PathVariable(required = false) Integer page, Model model) {
        if (page == null) {
            page = 1;
        }
        Page<Article> articles = articleProvider.getBlogPostPage(page - 1);
        model.addAttribute("articlePage", articles);
        return "index";
    }

    @GetMapping("/{publicationDate:\\d+(?:\\-\\d{2}){2}}/{slug:[a-z-]+}")
    public String showBlogPost(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate publicationDate,
            @PathVariable String slug, Model model) {
        Article article = articleProvider.getPublishedBlogPost(slug, publicationDate);
        model.addAttribute("article", article);
        return "article";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/{slug:[a-z-]+}")
    public String showPage(@PathVariable String slug, Model model) {
        Article article = articleProvider.getPublishedPage(slug);
        model.addAttribute("article", article);
        return "article";
    }
}
