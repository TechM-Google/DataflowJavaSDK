/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.dataflow.sdk.util;

import com.google.cloud.dataflow.sdk.annotations.Experimental;

import org.joda.time.Instant;

import javax.annotation.Nullable;

/**
 * Interface for interacting with time.
 */
@Experimental(Experimental.Kind.TIMERS)
public interface Timers {
  /**
   * Sets a timer to fire when the event time watermark, the current processing time, or
   * the synchronized processing time watermark surpasses a given timestamp.
   *
   * <p>See {@link TimeDomain} for details on the time domains available.
   *
   * <p>Timers are not guaranteed to fire immediately, but will be delivered at some time
   * afterwards.
   *
   * <p>An implementation of {@link Timers} implicitly scopes timers that are set - they may
   * be scoped to a key and window, or a key, window, and trigger, etc.
   *
   * @param timestamp the time at which the timer should be delivered
   * @param timeDomain the domain that the {@code timestamp} applies to
   */
  public abstract void setTimer(Instant timestamp, TimeDomain timeDomain);

  /** Removes the timer set in this context for the {@code timestmap} and {@code timeDomain}. */
  public abstract void deleteTimer(Instant timestamp, TimeDomain timeDomain);

  /** Returns the current processing time. */
  public abstract Instant currentProcessingTime();

  /** Returns the current synchronized processing time or {@code null} if unknown. */
  @Nullable
  public abstract Instant currentSynchronizedProcessingTime();

  /** Returns the current event time or {@code null} if unknown. */
  @Nullable
  public abstract Instant currentEventTime();
}
