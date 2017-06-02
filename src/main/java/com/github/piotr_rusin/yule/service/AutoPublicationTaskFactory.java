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
package com.github.piotr_rusin.yule.service;

import java.time.Clock;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.piotr_rusin.yule.repository.ArticleRepository;

/**
 * Creates objects representing auto-publication tasks.
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 *
 */
@Service
public class AutoPublicationTaskFactory {
    private ArticleRepository repository;

    /**
     * Create a new instance
     *
     * @param repository
     *            is an article repository to be passed to each
     *            auto-publication task.
     */
    @Autowired
    public AutoPublicationTaskFactory(ArticleRepository repository) {
        this.repository = repository;
    }

    public AutoPublicationTask getNew() {
        return new AutoPublicationTask(repository, Clock.systemUTC(),
                LoggerFactory.getILoggerFactory());
    }
}
