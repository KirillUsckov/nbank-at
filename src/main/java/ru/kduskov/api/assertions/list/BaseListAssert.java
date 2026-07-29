package ru.kduskov.api.assertions.list;

import org.assertj.core.api.SoftAssertions;
import ru.kduskov.common.assertions.BaseAssert;

import java.util.List;

public class BaseListAssert<S extends BaseListAssert<S, T>, T>
        extends BaseAssert<S, List<T>> {

    protected BaseListAssert(List<T> actual, Class<S> selfType, SoftAssertions softly) {
        super(actual, selfType, softly);
    }

    public S sizeEquals(int expectedSize) {
        softly(() ->
                softly.assertThat(actual.size())
                        .withFailMessage("Expected %s elements but found %s",
                                expectedSize, actual.size())
                        .isEqualTo(expectedSize)
        );
        return (S) this;
    }
}