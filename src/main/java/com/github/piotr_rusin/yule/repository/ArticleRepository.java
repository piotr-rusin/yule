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
package com.github.piotr_rusin.yule.repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.piotr_rusin.yule.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Long>,
        JpaSpecificationExecutor<Article> {

    @Query("select a from Article a where a.post = true "
            + "and a.status = com.github.piotr_rusin.yule.domain"
            + ".ArticleStatus.PUBLISHED order by a.publicationTimestamp desc")
    Page<Article> findPublishedPosts(Pageable pageRequest);

    @Query("select a from Article a where a.post = false "
            + "and a.status = com.github.piotr_rusin.yule.domain"
            + ".ArticleStatus.PUBLISHED order by a.publicationTimestamp desc")
    List<Article> findPublishedPages();

    @Query("select count(a) from Article a where a.id >= :id")
    int getPositionOnAdminPanelArticleList(@Param("id") long id);

    @Query("select a from Article a where a.post = true "
            + "and a.status = com.github.piotr_rusin.yule.domain"
            + ".ArticleStatus.PUBLISHED and a.slug = :slug")
    Article findPublishedPostBy(@Param("slug") String slug);

    @Query("select a from Article a where a.post = false "
            + "and a.status = com.github.piotr_rusin.yule.domain"
            + ".ArticleStatus.PUBLISHED and a.slug = :slug")
    Article findPublishedPageBy(@Param("slug") String slug);

    Article findOneBySlug(String slug);

    @Query("select a from Article a where "
            + "a.status = com.github.piotr_rusin.yule.domain"
            + ".ArticleStatus.SCHEDULED_FOR_PUBLICATION "
            + "and a.publicationTimestamp = :timestamp")
    List<Article> findScheduledBy(@Param("timestamp") Instant publicationTime);

    @Query("select a from Article a where "
            + "a.status = com.github.piotr_rusin.yule.domain"
            + ".ArticleStatus.SCHEDULED_FOR_PUBLICATION")
    List<Article> findAllScheduled();

    @Query("select min(a.publicationTimestamp) from Article a where "
            + "a.status = com.github.piotr_rusin.yule.domain"
            + ".ArticleStatus.SCHEDULED_FOR_PUBLICATION")
    Date findNextScheduledPublicationTime();

    @Query("select a from Article a where "
            + "a.status = com.github.piotr_rusin.yule.domain"
            + ".ArticleStatus.SCHEDULED_FOR_PUBLICATION "
            + "and a.publicationTimestamp <= CURRENT_TIMESTAMP")
    List<Article> findCurrentAutoPublicationTargets();
}
