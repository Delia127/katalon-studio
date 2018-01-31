package com.kms.katalon.util.collections;

import java.io.Serializable;
import java.util.Objects;

/**
 * A pair values
 *
 * @param <L> left value
 * @param <R> right value
 */
public class Pair<L, R> implements Serializable {

    private static final long serialVersionUID = 8600937250007049474L;

    private L left;

    private R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public R getRight() {
        return right;
    }

    public void setRight(R right) {
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Pair)) {
            return false;
        }

        Pair<?, ?> that = (Pair<?, ?>) o;
        return Objects.equals(this.left, that.left) && Objects.equals(this.right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.left, this.right);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", this.left, this.right);
    }

    public Pair<L, R> clone() {
        return Pair.of(this.left, this.right);
    }

    /**
     * Convenience method to create a pair.
     *
     * @param left The first value.
     * @param right The second value.
     * @param <L> The type of the 1st item in the pair.
     * @param <R> The type of the 2nd item in the pair.
     * @return A new pair of [left, right].
     */
    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<L, R>(left, right);
    }

}
