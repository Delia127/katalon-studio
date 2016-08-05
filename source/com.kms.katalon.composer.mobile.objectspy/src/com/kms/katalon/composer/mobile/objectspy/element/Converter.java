package com.kms.katalon.composer.mobile.objectspy.element;

public interface Converter<A, B> {
    B convert(A a);

    A revert(B b);
}
