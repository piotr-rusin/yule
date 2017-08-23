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
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.github.piotr_rusin.yule.config.YuleConfig;
import com.github.piotr_rusin.yule.domain.Article;
import com.github.piotr_rusin.yule.exception.PageNotFoundException;
import com.github.piotr_rusin.yule.exception.ResourceNotFoundException;
import com.github.piotr_rusin.yule.repository.ArticleRepository;

@Controller
public class YuleController {

    private static final Logger logger = LoggerFactory
            .getLogger(YuleController.class);

    private YuleConfig config;
    private ArticleRepository articleRepository;

    @Autowired
    public YuleController(YuleConfig config,
            ArticleRepository articleRepository) {
        this.config = config;
        this.articleRepository = articleRepository;
    }

    @GetMapping({ "/", "/page/{page:[1-9][0-9]*}" })
    public String getBlogPostListPage(
            @PathVariable(required = false) Integer page, Model model) {
        logger.info("Requesting a page of index view");
        if (page == null) {
            logger.info("No page parameter provided, assuming the first page");
            page = 1;
        }
        PageRequest pageRequest = new PageRequest(page - 1,
                config.getIndexPageSize());
        Page<Article> articles = articleRepository
                .findPublishedPosts(pageRequest);
        if (articles.getNumberOfElements() == 0) {
            if (!articles.isFirst()) {
                throw new PageNotFoundException(String.format(
                        "The requested blog post list page (%s) was not found.",
                        page));
            }
            logger.warn("There are no published blog posts in the database");
            model.addAttribute("articlePage", null);
        } else {
            model.addAttribute("articlePage", articles);
        }
        logger.info(
                String.format("Returning page %d of the blog post list", page));
        return "index";
    }

    @GetMapping("/{publicationDate:\\d+(?:\\-\\d{2}){2}}/{slug:[a-z-]+}")
    public String showBlogPost(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate publicationDate,
            @PathVariable String slug, Model model) {
        Article article = articleRepository.findPublishedPostBy(slug);
        LocalDate actualPublicationDate = LocalDateTime
                .ofInstant(article.getPublicationTimestamp(), ZoneOffset.UTC)
                .toLocalDate();

        if (article == null || !publicationDate.equals(actualPublicationDate)) {
            throw new ResourceNotFoundException(String.format(
                    "The article '%s', published on %s, was not found.", slug,
                    publicationDate));
        }
        logger.info("Returning the article '{}', published on {}", slug,
                publicationDate);
        model.addAttribute("article", article);
        return "article";
    }

    @GetMapping("/{slug:[a-z-]+}")
    public String showPage(@PathVariable String slug, Model model) {
        Article article = articleRepository.findPublishedPageBy(slug);
        if (article == null) {
            throw new ResourceNotFoundException(String.format(
                    "The requested blog page (%s) was not found.", slug));
        }
        logger.info("Returning requested blog page: " + slug);
        model.addAttribute("article", article);
        return "article";
    }
}
