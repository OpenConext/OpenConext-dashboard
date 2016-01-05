/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package selfservice.cache;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.google.common.base.Stopwatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public abstract class AbstractCache implements DisposableBean {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractCache.class);

  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  public AbstractCache(long initialDelay, long delay) {
    LOG.info("Starting cache {}, with an initial delay of {} and a delay of {}", getCacheName(), initialDelay, delay);
    executor.scheduleWithFixedDelay(() -> populateCache(), initialDelay, delay, MILLISECONDS);
  }

  protected abstract void doPopulateCache();

  protected abstract String getCacheName();

  private void populateCache() {
    LOG.info("Refreshing {} cache", getCacheName());
    Stopwatch stopwatch = Stopwatch.createStarted();
    try {
      doPopulateCache();
    } catch (Throwable t) {
      LOG.error("Error in the refresh of the cache", t);
    }

    LOG.info("Finished refreshing {} cache ({} milliseconds)", getCacheName(), stopwatch.elapsed(MILLISECONDS));
  }

  @Override
  public void destroy() throws Exception {
    LOG.debug("Cancelling refresh job for {}", getCacheName());
    executor.shutdownNow();
  }

  /**
   * Evicts the cache (asynchronously), effectively by scheduling a one time populate-job.
   */
  public void evict() {
    executor.execute(() -> populateCache());
  }

  /**
   * Clears the cache (synchronously)
   */
  public void evictSynchronously() {
    populateCache();
  }

}
