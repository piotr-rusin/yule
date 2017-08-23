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

import java.time.Instant;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.piotr_rusin.yule.config.YuleConfig;
import com.github.piotr_rusin.yule.domain.Article;
import com.github.piotr_rusin.yule.exception.PageNotFoundException;
import com.github.piotr_rusin.yule.exception.ResourceNotFoundException;
import com.github.piotr_rusin.yule.repository.ArticleRepository;
import com.github.piotr_rusin.yule.service.AutoPublicationScheduler;
import org.springframework.data.domain.Sort.Direction;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory
            .getLogger(AdminController.class);
    final static String ARTICLE_PAGE_ATTR = "articlePage";
    final static String ARTICLE_ATTR = "article";
    final static String MESSAGE_ATTR = "message";

    private ArticleRepository articleRepository;
    private AutoPublicationScheduler autoPublicationScheduler;
    private YuleConfig config;

    public AdminController(ArticleRepository articleRepository,
            AutoPublicationScheduler autoPublicationScheduler,
            YuleConfig config) {
        this.articleRepository = articleRepository;
        this.autoPublicationScheduler = autoPublicationScheduler;
        this.config = config;
    }

    @GetMapping()
    public String redirectToArticleList() {
        logger.info("Redirecting to article list");
        return "redirect:/admin/articles";
    }

    @GetMapping({ "/articles", "/articles/page/{page:[1-9][0-9]*}" })
    public String articleList(@PathVariable(required = false) Integer page,
            Model model) {
        logger.info("Requesting a page of admin panel article list view");
        if (page == null) {
            logger.info("No page parameter provided, assuming the first page");
            page = 1;
        }
        PageRequest pageRequest = new PageRequest(page - 1,
                config.getAdminArticleListPageSize(), Direction.DESC, "id");
        Page<Article> articles = articleRepository.findAll(pageRequest);
        if (articles.getNumberOfElements() == 0) {
            if (!articles.isFirst()) {
                throw new PageNotFoundException(String.format(
                        "The requested page (%s) of the article list was not found.",
                        page));
            }
            logger.warn("There are no articles in the database");
            model.addAttribute(ARTICLE_PAGE_ATTR, null);
        } else {
            model.addAttribute(ARTICLE_PAGE_ATTR, articles);
        }
        logger.info("Admin panel: showing page {} of the article list.", page);
        return "admin/article-list";
    }

    @GetMapping("/article")
    public String newArticleDisplayForm(Model model) {
        Article article = new Article();
        addArticleToModel(model, article);
        logger.info("Showing new article form.");
        return "admin/edit-article";
    }

    @GetMapping("/article/{id:\\d+}")
    public String loadForEditing(@PathVariable long id, Model model) {
        Article article = articleRepository.findOne(id);

        if (article == null) {
            throw new ResourceNotFoundException(
                    "There is no article with the id = " + id);
        }

        addArticleToModel(model, article);
        logger.info("Loading article {} for editing", article);
        return "admin/edit-article";
    }

    @PostMapping("/article")
    public String saveNewArticle(@Valid Article dto,
            BindingResult bindingResult, RedirectAttributes attributes) {
        logger.info("Beginning a save operation for article " + dto);
        if (bindingResult.hasErrors()) {
            logger.info("Invalid data: {}. Showing validation errors.", dto);
            return "admin/edit-article";
        }

        Article saved = articleRepository.save(dto);
        logger.info("The new article {} has been successfully saved.", saved);

        if (saved.isScheduledForPublication()) {
            logger.info(
                    "The article {} has been scheduled for auto-publication. "
                            + "Rescheduling auto-publication task.",
                    saved);
        }

        attributes.addAttribute("id", saved.getId()).addFlashAttribute(
                MESSAGE_ATTR, "The article has been successfully saved");

        return "redirect:/admin/article/{id}";
    }

    @PostMapping("/article/{id:\\d+}")
    public String updateArticle(@PathVariable long id, @Valid Article dto,
            BindingResult bindingResult, RedirectAttributes attributes) {
        logger.info(
                "Handling update operation for an article (id: {}, new data: {})",
                id, dto);
        if (bindingResult.hasErrors()) {
            logger.info("Invalid data {}. Showing validation errors.", dto);
            return "admin/edit-article";
        }

        Article saved = articleRepository.findOne(id);
        saved.setAdminAlterableData(dto);
        Article previous = new Article(saved);

        try {
            saved = articleRepository.save(saved);
        } catch (HibernateOptimisticLockingFailureException ex) {
            logger.info(
                    "The article {} was edited concurrently. The changes are "
                            + "being merged based on an assumption that the concurrent edit "
                            + "was only an automatic publication.",
                    saved);
            Article mostRecentlySaved = articleRepository.findOne(id);
            mostRecentlySaved.setAdminAlterableData(saved);
            saved = articleRepository.save(mostRecentlySaved);
        }

        boolean isScheduled = saved.isScheduledForPublication();
        Instant publicationTimestamp = saved.getPublicationTimestamp();
        String reschedulingReason = null;

        if (isScheduled != previous.isScheduledForPublication()) {
            reschedulingReason = "new status for the article: "
                    + saved.getStatus();
        } else if (isScheduled && !publicationTimestamp
                .equals(previous.getPublicationTimestamp())) {
            reschedulingReason = "new publication time: "
                    + publicationTimestamp;
        }

        if (reschedulingReason != null) {
            logger.info(
                    "Rescheduling auto-publication for article {}. Reason: {}",
                    saved, reschedulingReason);
            autoPublicationScheduler.scheduleNew();
        }

        logger.info("The article {} was successfully updated.", saved);
        attributes.addAttribute("id", saved.getId()).addFlashAttribute(
                MESSAGE_ATTR, "The article has been successfully updated");
        return "redirect:/admin/article/{id}";
    }

    /**
     * Delete an article and redirect to a page of the admin panel article list
     * <p>
     * If the article was the last element of the last page, the method
     * redirects to the current last page. Otherwise, it redirects to a page of
     * the same number as the one on which the deleted article was listed.
     *
     * @param id
     * @return
     */
    @PostMapping("/article/{id:\\d+}/delete")
    public String deleteArticleAndRedirectToArticleList(@PathVariable long id,
            RedirectAttributes attributes) {
        Article article = articleRepository.findOne(id);

        if (article == null) {
            throw new ResourceNotFoundException(
                    "There is no article with id = " + id);
        }

        int articleNumber = articleRepository
                .getPositionOnAdminPanelArticleList(id);

        articleRepository.delete(id);
        logger.info("The article {} has been successfully deleted.", article);

        if (article.isScheduledForPublication()) {
            logger.info(
                    "The deleted article {} was scheduled for auto-publication. Rescheduling the auto-publication task.",
                    article);
            autoPublicationScheduler.scheduleNew();
        }

        int pageSize = config.getAdminArticleListPageSize();
        int pageNumber = articleNumber / pageSize + 1;

        PageRequest pageRequest = new PageRequest(pageNumber - 1, pageSize);
        Page<Article> articles = articleRepository.findAll(pageRequest);

        if (articles.getNumberOfElements() == 0 && !articles.isFirst()) {
            pageNumber -= 1;
        }

        attributes.addFlashAttribute(MESSAGE_ATTR,
                String.format(
                        "The article \"%s\" has been successfully deleted.",
                        article.getTitle()));
        String baseRedirect = "redirect:/admin/articles";
        return pageNumber == 1 ? baseRedirect
                : baseRedirect + "/page/" + pageNumber;
    }

    private void addArticleToModel(Model model, Article article) {
        model.addAttribute(ARTICLE_ATTR, article);
    }

}