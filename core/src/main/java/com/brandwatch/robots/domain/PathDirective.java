package com.brandwatch.robots.domain;

/*
 * #%L
 * Robots (core)
 * %%
 * Copyright (C) 2015 Brandwatch
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Brandwatch nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import com.brandwatch.robots.matching.Matchable;
import com.brandwatch.robots.matching.Matcher;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
public final class PathDirective implements Directive, Matchable<String> {

    @Nonnull
    private final Field field;

    @Nonnull
    private final String value;

    @Nonnull
    private final Matcher<String> pathMatcher;

    public PathDirective(@Nonnull Field field, @Nonnull String value, @Nonnull Matcher<String> pathMatcher) {
        this.pathMatcher = checkNotNull(pathMatcher, "pathMatcher is null");
        this.field = checkNotNull(field, "field is null");
        this.value = checkNotNull(value, "value is null");
    }

    @Nonnull
    @Override
    public String getField() {
        return field.toString();
    }

    @Nonnull
    @Override
    public String getValue() {
        return value;
    }


    @Override
    public Matcher<String> getMatcher() {
        return pathMatcher;
    }

    public boolean isAllowed() {
        return field.isAllowed();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathDirective that = (PathDirective) o;
        return Objects.equal(field, that.field)
                && Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(field, value);
    }

    @Nonnull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("field", field)
                .add("value", value)
                .toString();
    }


    public enum Field {
        allow {
            @Override
            public boolean isAllowed() {
                return true;
            }
        },
        disallow {
            @Override
            public boolean isAllowed() {
                return false;
            }
        };

        public abstract boolean isAllowed();
    }
}
