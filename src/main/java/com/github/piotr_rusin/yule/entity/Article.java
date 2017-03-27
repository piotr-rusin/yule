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
package com.github.piotr_rusin.yule.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * A class representing blog articles.
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 */
@Entity
@Table(name="articles")
public class Article {
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String title;

	private String slug;

	private String content;

	@Column(name="creation_date")
	@CreationTimestamp
	private Date creationDate;

	@Column(name="modification_date")
	@UpdateTimestamp
	private Date modificationDate;

	@Enumerated(EnumType.STRING)
	private ArticleStatus status = ArticleStatus.DRAFT;

	/**
	 * Specifies whether an article is also a blog post.
	 *
	 * All published, non-private articles on a blog are publicly
	 * accessible through their URL addresses.
	 *
	 * All articles that are also blog posts are additionally listed on
	 * one of the pages of a blog, in reverse chronological order.
	 */
	@Column(name="is_blog_post")
	private boolean post = true;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public boolean isPost() {
		return post;
	}

	public void setPost(boolean post) {
		this.post = post;
	}
}
