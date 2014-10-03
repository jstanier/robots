package com.brandwatch.robotstxt.domain;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
public final class OtherDirective implements Directive {

    @Nonnull
    private final String field;
    @Nonnull
    private final String value;

    public OtherDirective(@Nonnull String field, @Nonnull String value) {
        this.field = checkNotNull(field, "field is null");
        this.value = checkNotNull(value, "value is null");
    }

    @Override
    @Nonnull
    public String getField() {
        return field;
    }

    @Override
    @Nonnull
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtherDirective that = (OtherDirective) o;
        return field.equals(that.field) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = field.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("field", field)
                .add("value", value)
                .toString();
    }
}
