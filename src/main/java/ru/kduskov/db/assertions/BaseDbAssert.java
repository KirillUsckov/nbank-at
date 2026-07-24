package ru.kduskov.db.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.SoftAssertions;
import ru.kduskov.db.models.dao.BaseDao;

import java.time.LocalDateTime;

public abstract class BaseDbAssert<S extends BaseDbAssert<S, A>, A extends BaseDao>
        extends AbstractAssert<S, A> {

    protected final SoftAssertions softly;

    protected BaseDbAssert(A actual, Class<S> selfType, SoftAssertions softly) {
        super(actual, selfType);
        this.softly = softly;
    }

    protected void softly(Runnable assertion) {
        try {
            assertion.run();
        } catch (AssertionError ignored) {
        }
    }

    protected <T> void isEqualTo(T actual, T expected, String message) {
        softly(() ->
                softly.assertThat(actual)
                        .withFailMessage(message)
                        .isEqualTo(expected)
        );
    }

    protected BaseDbAssert<S, A> dateCreatedEquals(LocalDateTime expected) {
        isEqualTo(
                actual.getCreatedAt(),
                expected,
                String.format("Expected date created %s but was %s", expected, actual.getCreatedAt())
        );
        return this;
    }

    protected BaseDbAssert<S, A> dateUpdatedEquals(LocalDateTime expected) {
        isEqualTo(
                actual.getUpdatedAt(),
                expected,
                String.format("Expected date updated %s but was %s", expected, actual.getUpdatedAt())
        );
        return this;
    }

    protected BaseDbAssert<S, A> dateUpdatedAfter(LocalDateTime expected) {
        softly.assertThat(actual.getUpdatedAt())
                .withFailMessage(
                        String.format("Expected date updated %s should be after %s", expected, actual.getUpdatedAt())
                )
                .isAfter(expected);
        return this;
    }

    protected BaseDbAssert<S, A> dateCreatedAfter(LocalDateTime expected) {
        softly.assertThat(actual.getCreatedAt())
                .withFailMessage(
                        String.format("Expected date created %s should be before %s", expected, actual.getCreatedAt())
                )
                .isAfter(expected);
        return this;
    }

    protected BaseDbAssert<S, A> dateCreatedBefore(LocalDateTime expected) {
        softly.assertThat(actual.getCreatedAt())
                .withFailMessage(
                        String.format("Expected date created %s should be after %s", expected, actual.getCreatedAt())
                )
                .isBefore(expected);
        return this;
    }

    protected BaseDbAssert<S, A> idEquals(Long expectedId) {
        isEqualTo(
                actual.getId(),
                expectedId,
                String.format("Expected id %s but was %s", expectedId, actual.getId())
        );
        return this;
    }
}