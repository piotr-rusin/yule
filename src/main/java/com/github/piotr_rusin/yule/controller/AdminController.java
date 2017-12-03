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

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.piotr_rusin.yule.domain.Article;
import com.github.piotr_rusin.yule.exception.PageNotFoundException;
import com.github.piotr_rusin.yule.service.ArticleProvider;
import com.github.piotr_rusin.yule.service.ArticleRepositoryUpdater;

@Controller
@SessionAttributes({AdminController.PAGE_REQUEST_ATTR, AdminController.ARTICLE_ATTR})
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory
            .getLogger(AdminController.class);
    final static String ARTICLE_PAGE_ATTR = "articlePage";
    final static String ARTICLE_ATTR = "article";
    final static String MESSAGE_ATTR = "message";
    final static String ARTICLE_NOT_VALIDATED = "articleNotValidated";
    final static String PAGE_REQUEST_ATTR = "pageRequest";

    final static int DEFAULT_PAGE_SIZE = 10;

    private ArticleRepositoryUpdater articleRepositoryUpdater;
    private ArticleProvider articleProvider;

    public AdminController(ArticleRepositoryUpdater articleRepositoryUpdater,
            ArticleProvider articleProvider) {
        this.articleRepositoryUpdater = articleRepositoryUpdater;
        this.articleProvider = articleProvider;
    }

    @GetMapping()
    public String redirectToArticleList() {
        logger.info("Redirecting to article list");
        return "redirect:/admin/articles";
    }

    @GetMapping("/articles")
    public String articleList(
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "creationTimestamp", direction = Sort.Direction.DESC) Pageable pageRequest,
            Model model) {
        model.addAttribute(PAGE_REQUEST_ATTR, pageRequest);
        Page<Article> articles = articleProvider.getAdminArticleListPage(pageRequest);
        model.addAttribute(ARTICLE_PAGE_ATTR, articles);
        return "admin/article-list";
    }

    @GetMapping("/article")
    public String newArticleDisplayForm(Model model) {
        Article article = new Article();
        addArticleToModel(model, article);
        model.addAttribute(ARTICLE_NOT_VALIDATED, true);
        logger.info("Showing new article form.");
        return "admin/edit-article";
    }

    @GetMapping("/article/{id:\\d+}")
    public String loadForEditing(@PathVariable long id, Model model) {
        Article article = articleProvider.getArticleById(id);
        addArticleToModel(model, article);
        logger.info("Loading article {} for editing", article);
        return "admin/edit-article";
    }

    @PostMapping("/article")
    public String saveOrUpdate(@Valid Article article,
            BindingResult bindingResult, RedirectAttributes attributes) {
        String executing = "updating";
        String executed = "updated";
        if (article.isNew()) {
            executing = "saving";
            executed = "saved";
        }
        logger.info("Performing {} operation for article {}...", executing, article);
        if (bindingResult.hasErrors()) {
            logger.info("Invalid data: {}. Showing validation errors.", article);
            return "admin/edit-article";
        }

        Article saved = null;
        try {
            saved = articleRepositoryUpdater.save(article);
        } catch (DataIntegrityViolationException e) {
            logger.info("The article {} was not {} - its name is already in use.", article, executed);
            bindingResult.rejectValue("title", "error.duplicate-title",
                    String.format(
                            "An article named \"%s\" already exists.",
                            article.getTitle()));
            return "admin/edit-article";
        }

        attributes.addAttribute("id", saved.getId()).addFlashAttribute(
                MESSAGE_ATTR, String.format("The article has been successfully %s.", executed) );

        return "redirect:/admin/article/{id}";
    }

    /**
     * Redirect to a page of admin article list.
     *
     * @param attributes
     *            is a container for redirect attributes.
     * @param pageRequest
     *            is an object representing page request used to query for the
     *            last displayed admin article list page, if any. If null, the
     *            redirection target is assumed to be the first page of the
     *            list, with default sorting order and page size (see
     *            {@link AdminController#articleList(Pageable, Model)} and
     *            {@link AdminController#DEFAULT_PAGE_SIZE})
     * @return a value interpreted as redirection request
     */
    @GetMapping("/articles/redirect")
    public String redirectToArticleList(RedirectAttributes attributes,
            @SessionAttribute(value = PAGE_REQUEST_ATTR, required = false) Pageable pageRequest) {
        String redirect = "redirect:/admin/articles";
        if (pageRequest == null) {
            return redirect;
        }

        Sort sort = pageRequest.getSort();
        if (sort != null) {
            for (Sort.Order o : pageRequest.getSort()) {
                String property = o.getProperty();
                String direction = o.isAscending() ? "asc" : "desc";
                attributes.addAttribute("sort", property + "," + direction);
            }
        }

        int pageNumber = pageRequest.getPageNumber();
        if (pageNumber != 0) {
            attributes.addAttribute("page", pageNumber);
        }

        int pageSize = pageRequest.getPageSize();
        if (pageSize != DEFAULT_PAGE_SIZE) {
            attributes.addAttribute("size", pageSize);
        }

        return redirect;
    }

    /**
     * Delete an article and redirect to a page of the admin panel article list
     * <p>
     * If the article was the last element of the last page, the method
     * redirects to the current last page. Otherwise, it redirects to a page of
     * the same number as the one on which the deleted article was displayed.
     * 
     * @param id
     *            is an identifier of the article to be deleted
     * @param attributes
     *            is a container for redirect attributes
     * @param pageRequest
     *            is an object representing page request used to display the
     *            page on which the article was presented
     * @return
     */
    @PostMapping("/article/{id:\\d+}/delete")
    public String deleteArticleAndRedirectToArticleList(@PathVariable long id,
            RedirectAttributes attributes,
            @SessionAttribute(PAGE_REQUEST_ATTR) Pageable pageRequest) {
        Article deleted = articleRepositoryUpdater.delete(id);

        attributes.addFlashAttribute(MESSAGE_ATTR,
                String.format(
                        "The article \"%s\" has been successfully deleted.",
                        deleted.getTitle()));

        try {
            articleProvider.getAdminArticleListPage(pageRequest);
        } catch (PageNotFoundException e) {
            pageRequest = pageRequest.previousOrFirst();
        }

        return redirectToArticleList(attributes, pageRequest);
    }

    private void addArticleToModel(Model model, Article article) {
        model.addAttribute(ARTICLE_ATTR, article);
    }

}
