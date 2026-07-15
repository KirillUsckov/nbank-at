package ru.kduskov.api.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.SoftAssertions;

public abstract class BaseAssert<S extends BaseAssert<S, A>, A>
        extends AbstractAssert<S, A> {

    protected final SoftAssertions softly;

    protected BaseAssert(A actual, Class<S> selfType, SoftAssertions softly) {
        super(actual, selfType);
        this.softly = softly;
    }

    protected void softly(Runnable assertion) {
        try {
            assertion.run();
        } catch (AssertionError e) {
        }
    }

    protected <T> void isEqualTo(T actual, T expected, String message) {
        softly(() ->
                softly.assertThat(actual)
                        .withFailMessage(message)
                        .isEqualTo(expected)
        );
    }
}