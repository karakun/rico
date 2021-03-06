/*
 * Copyright 2018-2019 Karakun AG.
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.rico.internal.core.functional;

import dev.rico.core.functional.ResultWithInput;
import dev.rico.internal.core.Assert;

/**
 * Implementation of a {@link dev.rico.core.functional.Result} that is based
 * on a not sucessfully executed function
 * @param <T> type of the input
 * @param <R> type of the output
 */
public class Fail<T, R> implements ResultWithInput<T, R> {

    private final T input;

    private final Exception exception;

    public Fail(final T input, final Exception exception) {
        this.input = input;
        this.exception = Assert.requireNonNull(exception, "exception");
    }

    public Fail(final Exception exception) {
        this.input = null;
        this.exception = Assert.requireNonNull(exception, "exception");
    }

    @Override
    public boolean isSuccessful() {
        return false;
    }

    @Override
    public T getInput() {
        return input;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public R getResult() {
        throw new IllegalStateException("No result since call failed!");
    }
}
