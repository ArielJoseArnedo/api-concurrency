package co.com.ajac.concurrency;

import io.vavr.CheckedFunction1;
import io.vavr.Function1;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public class FutureEither<L, R> {

    public static <L, R> FutureEither<L, R> of(Future<Either<L, R>> value) {
        return new FutureEither<>(value);
    }

    public static <L, R> FutureEither<L, R> fromEither(Either<L, R> either) {
        return new FutureEither<>(Future.successful(either));
    }

    public static <L, R> FutureEither<L, R> right(R r) {
        return new FutureEither<>(Future.successful(Either.right(r)));
    }

    public static <L, R> FutureEither<L, R> left(L l) {
        return new FutureEither<>(Future.successful(Either.left(l)));
    }

    private final Future<Either<L, R>> value;

    public <U> FutureEither<L, U> map(Function1<R, U> f) {
        return new FutureEither<>(this.value.map(e -> e.map(f)));
    }

    public <U> FutureEither<L, U> flatMap(Function1<R, FutureEither<L, U>> f) {
        return new FutureEither<>(
          this.value.flatMap(either -> either
            .fold(
              left -> Future.successful(Either.left(left)),
              right -> f.apply(right).getValue()
            )
          ));
    }

    public <U> FutureEither<U, R> flatMapLeft(Function1<L, FutureEither<U, R>> f) {
        return new FutureEither<>(
          this.value.flatMap(either -> either
            .fold(
              left -> f.apply(left).getValue(),
              right -> Future.successful(Either.right(right))
            )
          ));
    }

    public <U> FutureEither<U, R> mapLeft(Function<? super L, ? extends U> leftMapper) {
        Objects.requireNonNull(leftMapper, "leftMapper is null");
        return new FutureEither<>(value.map(either -> either.mapLeft(leftMapper)));
    }

    public <U> FutureEither<L, U> mapTry(CheckedFunction1<R, U> mapper) {
        Future<Either<L, U>> res =
          this.value.flatMapTry(
            e -> {
                if (e.isLeft()) {
                    return Future.successful(Either.left(e.getLeft()));
                }
                return Future.successful(Either.right(mapper.apply(e.get())));
            });
        return new FutureEither<>(res);
    }

    public <U> FutureEither<U, R> mapLeftTry(CheckedFunction1<L, U> mapper) {
        Future<Either<U, R>> res =
          this.value.flatMapTry(
            e -> {
                if (e.isRight()) {
                    return Future.successful(Either.right(e.get()));
                }
                return Future.successful(Either.left(mapper.apply(e.getLeft())));
            });
        return new FutureEither<>(res);
    }

    public <U> FutureEither<L, U> flatMapFuture(Function<? super R, ? extends Future<Either<L, U>>> mapper) {
        return new FutureEither<>(value.flatMap(either -> either
          .fold(
            left -> Future.successful(Either.left(left)),
            mapper
          )));
    }

    public <C> Future<C> fold(Function1<L, C> fl, Function1<R, C> fr) {
        return this.value.map(e -> e.fold(fl, fr));
    }

    public FutureEither<L, R> peek(Consumer<R> consumer) {
        this.value.peek(e -> e.peek(consumer));
        return this;
    }

    public FutureEither<L, R> peekLeft(Consumer<L> consumer) {
        this.value.peek(e -> e.peekLeft(consumer));
        return this;
    }

    public FutureEither<L, R> peekBoth(Consumer<L> leftConsumer, Consumer<R> rightConsumer) {
        return this.peekLeft(leftConsumer).peek(rightConsumer);
    }

    public <X, Y> FutureEither<X, Y> bimap(Function<? super L, ? extends X> leftMapper, Function<? super R, ? extends Y> rightMapper) {
        Objects.requireNonNull(leftMapper, "leftMapper is null");
        Objects.requireNonNull(rightMapper, "rightMapper is null");
        return new FutureEither<>(value.map(either -> either.bimap(leftMapper, rightMapper)));
    }

    public FutureEither<L, R> recover(Function<? super Throwable, ? extends Either<L, R>> function) {
        Objects.requireNonNull(function, "function is null");
        return new FutureEither<>(value.recover(function));
    }

    public FutureEither<L, R> onFailure(Consumer<? super Throwable> consumer) {
        return new FutureEither<>(this.value.onFailure(consumer));
    }

    public FutureEither<L, R> onSuccess(Consumer<? super Either<L, R>> consumer) {
        return new FutureEither<>(this.value.onSuccess(consumer));
    }
}
