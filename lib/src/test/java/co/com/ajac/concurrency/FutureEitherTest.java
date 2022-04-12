package co.com.ajac.concurrency;

import io.vavr.CheckedFunction1;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class FutureEitherTest {

    @Test
    void of() {
        final Future<Either<Object, Integer>> successful = Future.successful(Either.right(1));
        final FutureEither<Object, Integer> futureEither = FutureEither.of(successful);
        assertThat(successful.get().get()).isEqualTo(futureEither.getValue().get().get());
    }

    @Test
    void fromEither() {
        final Either<Object, Integer> either = Either.right(1);
        final FutureEither<Object, Integer> futureEither = FutureEither.fromEither(either);
        assertThat(either.get()).isEqualTo(futureEither.getValue().get().get());
    }

    @Test
    void right() {
        final FutureEither<Object, Integer> right = FutureEither.right(1);
        assertThat(right.getValue().get().get()).isEqualTo(1);
    }

    @Test
    void left() {
        final FutureEither<Integer, Object> right = FutureEither.left(1);
        assertThat(right.getValue().get().getLeft()).isEqualTo(1);
    }

    @Test
    void map() {
        final FutureEither<Object, Integer> right = FutureEither.right(1);
        final FutureEither<Object, String> map = right.map(String::valueOf);
        assertThat(map.getValue().get().get()).isEqualTo("1");
    }

    @Test
    void flatMap() {
        final FutureEither<Object, Integer> right = FutureEither.right(1);
        final FutureEither<Object, String> flatMap = right.flatMap(integer -> FutureEither.right("3"));
        assertThat(flatMap.getValue().get().get()).isEqualTo("3");
    }

    @Test
    void flatMapLeft() {
        final FutureEither<Boolean, Integer> right = FutureEither.left(false);
        final FutureEither<String, Integer> flatMapLeft = right.flatMapLeft(bool -> FutureEither.left("5"));
        assertThat(flatMapLeft.getValue().get().getLeft()).isEqualTo("5");
    }

    @Test
    void mapLeft() {
        final FutureEither<Boolean, Integer> right = FutureEither.left(false);
        final FutureEither<String, Integer> mapLeft = right.mapLeft(bool -> "LEFT");
        assertThat(mapLeft.getValue().get().getLeft()).isEqualTo("LEFT");
    }

    @Test
    void mapTry() {
        final FutureEither<Object, Integer> right = FutureEither.right(0);
        final FutureEither<Object, String> mapTry = right.mapTry(CheckedFunction1.of(String::valueOf));
        assertThat(mapTry.getValue().get().get()).isEqualTo("0");
    }

    @Test
    void mapLeftTry() {
        final FutureEither<Integer, Object> right = FutureEither.left(0);
        final FutureEither<String, Object> mapLeftTry = right.mapLeftTry(CheckedFunction1.of(String::valueOf));
        assertThat(mapLeftTry.getValue().get().getLeft()).isEqualTo("0");
    }

    @Test
    void flatMapFuture() {
        final FutureEither<Object, Integer> right = FutureEither.right(0);
        final FutureEither<Object, Integer> flatMapFuture = right.flatMapFuture(integer -> Future.successful(Either.right(integer + 10)));
        assertThat(flatMapFuture.getValue().get().get()).isEqualTo(10);
    }

    @Test
    void fold() {
        final FutureEither<Object, Integer> right = FutureEither.right(0);
        final Future<String> fold = right.fold(Object::toString, String::valueOf);
        assertThat(fold.get()).isEqualTo("0");
    }

    @Test
    void peek() {
        final Consumer<Integer> consumer = System.out::println;
        final FutureEither<Object, Integer> right = FutureEither.right(10);
        final FutureEither<Object, Integer> peek = right.peek(consumer);
        assertThat(peek.getValue().get().get()).isEqualTo(10);
    }

    @Test
    void peekLeft() {
        final Consumer<Integer> consumer = System.out::println;
        final FutureEither<Integer, Object> right = FutureEither.left(20);
        final FutureEither<Integer, Object> peek = right.peekLeft(consumer);
        assertThat(peek.getValue().get().getLeft()).isEqualTo(20);
    }

    @Test
    void peekBoth() {
        final Consumer<Object> consumerLeft = System.out::println;
        final Consumer<Integer> consumerRight = System.out::println;
        final FutureEither<Object, Integer> right = FutureEither.right(30);
        final FutureEither<Object, Integer> peekBoth = right.peekBoth(consumerLeft, consumerRight);
        assertThat(peekBoth.getValue().get().get()).isEqualTo(30);
    }

    @Test
    void bimap() {
        final FutureEither<Object, Integer> right = FutureEither.right(30);
        final FutureEither<String, Boolean> peekBoth = right.bimap(
          Object::toString,
          integer -> Boolean.FALSE
        );
        assertThat(peekBoth.getValue().get().get()).isFalse();
    }

    @Test
    void recover() {
        final FutureEither<Object, String> futureEitherError = FutureEither.of(Future.failed(new RuntimeException("Error")));
        final FutureEither<Object, String> recover = futureEitherError
          .recover(throwable -> Either.right(throwable.getMessage()));
        assertThat(recover.getValue().get().get()).isEqualTo("Error");
    }

    @Test
    void getValue() {
        final FutureEither<Object, String> futureEitherError = FutureEither.of(Future.successful(Either.right("OK")));
        final Future<Either<Object, String>> value = futureEitherError.getValue();
        assertThat(value.get().get()).isEqualTo("OK");

    }
}