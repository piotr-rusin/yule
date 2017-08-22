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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ScheduledFuture;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AutoPublicationSchedulerTest {
    @Mock
    private AutoPublicationTrigger trigger;

    @Mock
    private AutoPublicationTaskFactory taskFactory;

    @Mock
    private AutoPublicationTask task;

    @Mock
    private ScheduledFuture<?> taskFuture;

    @Mock
    private TaskScheduler taskScheduler;

    private AutoPublicationScheduler autoPublicationScheduler;

    @Before
    public void setUp() {
        when(taskFactory.getNew()).thenReturn(task);
        doReturn(taskFuture).when(taskScheduler).schedule(task, trigger);
        autoPublicationScheduler = new AutoPublicationScheduler(trigger,
                taskFactory, taskScheduler);
    }

    @Test
    public void scheduleNewCancelsOldTaskWhenRescheduling() {
        autoPublicationScheduler.scheduleNew();
        autoPublicationScheduler.scheduleNew();
        verify(taskFuture).cancel(true);
    }

    @Test
    public void scheduleNewSchedulesNewTask() {
        autoPublicationScheduler.scheduleNew();
        verify(taskScheduler).schedule(task, trigger);
    }
}
