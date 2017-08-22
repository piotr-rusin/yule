package com.github.piotr_rusin.yule.controller;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.github.piotr_rusin.yule.config.YuleConfig;
import com.github.piotr_rusin.yule.domain.Article;
import com.github.piotr_rusin.yule.repository.ArticleRepository;

@ControllerAdvice
public class CommonModelAttributeController {
    private YuleConfig config;
    private ArticleRepository articleRepository;

    @Autowired
    public CommonModelAttributeController(YuleConfig config,
            ArticleRepository articleRepository) {
        this.config = config;
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
}
