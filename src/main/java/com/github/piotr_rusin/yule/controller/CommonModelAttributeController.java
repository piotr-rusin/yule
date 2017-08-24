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

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.github.piotr_rusin.yule.config.UserConfig;
import com.github.piotr_rusin.yule.config.YuleConfig;
import com.github.piotr_rusin.yule.domain.Article;
import com.github.piotr_rusin.yule.repository.ArticleRepository;

@ControllerAdvice
public class CommonModelAttributeController {
    private YuleConfig config;
    private UserConfig adminConfig;
    private ArticleRepository articleRepository;

    @Autowired
    public CommonModelAttributeController(YuleConfig config,
            UserConfig userConfig, ArticleRepository articleRepository) {
        this.config = config;
        this.adminConfig = userConfig;
        this.articleRepository = articleRepository;
    }

    @ModelAttribute("blogPageNameToSlug")
    public Map<String, String> getBlogPageNameToSlugMap() {
        return articleRepository.findPublishedPages().stream()
                .collect(Collectors.toMap(Article::getTitle, Article::getSlug));
    }

    @ModelAttribute("blogTitle")
    public String getBlogTitle() {
        return config.getTitle();
    }

    @ModelAttribute("blogDescription")
    public String getBlogDescription() {
        return config.getDescription();
    }

    @ModelAttribute("userTimeZone")
    public ZoneId getUserTimezone(ZoneId userTimeZoneId) {
        return userTimeZoneId;
    }

    @ModelAttribute("adminName")
    public String getAdminName() {
        return adminConfig.getName();
    }

    @ModelAttribute("adminEmail")
    public String getAdminEmail() {
        return adminConfig.getEmail();
    }
}
