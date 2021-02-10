/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.baky.idlingResource;


import androidx.annotation.Nullable;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

import java.net.SocketImpl;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A very simple implementation of {IdlingResource}.
 * <p>
 * Consider using CountingIdlingResource from espresso-contrib package if you use this class from
 * multiple threads or need to keep a count of pending operations.
 */

public class SimpleIdlingResource {
    private static final String GLOBAL_RESOURCE = "global-resource";
    private static volatile SimpleIdlingResource sInstance = null;

    private CountingIdlingResource countingIdlingResource =
        new CountingIdlingResource(GLOBAL_RESOURCE);

    public SimpleIdlingResource() {
    }

    public static SimpleIdlingResource getInstance() {
        if (sInstance == null) {
            synchronized (SimpleIdlingResource.class){
                return new SimpleIdlingResource();
            }
        }
        return sInstance;
    }

    public CountingIdlingResource getCountingIdlingResource() {
        return countingIdlingResource;
    }

    public void setCountingIdlingResource(CountingIdlingResource countingIdlingResource) {
        this.countingIdlingResource = countingIdlingResource;
    }

    public void increment() {
        countingIdlingResource.increment();
    }

    public void decrement() {
        if (!countingIdlingResource.isIdleNow()) {
            countingIdlingResource.decrement();
        }
    }
}