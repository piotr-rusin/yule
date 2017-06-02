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

import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

/**
 * A service responsible for scheduling auto-publication tasks.
 *
 * @author Piotr Rusin <piotr.rusin88@gmail.com>
 *
 */
@Service
public class AutoPublicationScheduler {
    private AutoPublicationTrigger trigger;
    private AutoPublicationTaskFactory taskFactory;
    private TaskScheduler scheduler;
    private Future<?> autoPublicationFuture;

    /**
     * Construct a new instance.
     *
     * @param trigger
     *            is an object providing next execution time for an
     *            auto-publication task.
     * @param taskFactory
     *            is an object providing auto-publication task objects.
     * @param scheduler
     *            is an object used to schedule auto-publication tasks.
     */
    @Autowired
    public AutoPublicationScheduler(AutoPublicationTrigger trigger, AutoPublicationTaskFactory taskFactory, TaskScheduler scheduler) {
        this.trigger = trigger;
        this.taskFactory = taskFactory;
        this.scheduler = scheduler;
    }

    /**
     * Schedule a new auto-publication task (and cancel an existing one, if present).
     */
    @PostConstruct
    public void scheduleNew() {
        if (autoPublicationFuture != null)
            autoPublicationFuture.cancel(true);
        Runnable task = taskFactory.getNew();
        autoPublicationFuture = scheduler.schedule(task, trigger);
    }
}
