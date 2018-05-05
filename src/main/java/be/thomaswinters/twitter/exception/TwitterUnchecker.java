package be.thomaswinters.twitter.exception;

import twitter4j.TwitterException;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TwitterUnchecker {


    @FunctionalInterface
    public interface Consumer_WithTwitterExceptions<T, E extends TwitterException> {
        void accept(T t) throws E;
    }

    @FunctionalInterface
    public interface BiConsumer_WithTwitterExceptions<T, U, E extends TwitterException> {
        void accept(T t, U u) throws E;
    }

    @FunctionalInterface
    public interface Function_WithTwitterExceptions<T, R, E extends TwitterException> {
        R apply(T t) throws TwitterException;
    }
    @FunctionalInterface
    public interface BiFunction_WithTwitterExceptions<T, S, R, E extends TwitterException> {
        R apply(T t, S s) throws E;
    }

    @FunctionalInterface
    public interface Supplier_WithTwitterExceptions<T, E extends TwitterException> {
        T get() throws E;
    }

    @FunctionalInterface
    public interface Runnable_WithTwitterExceptions<E extends TwitterException> {
        void run() throws E;
    }

    /**
     * .forEach(rethrowConsumer(name ->
     * System.out.println(Class.forName(name)))); or
     * .forEach(rethrowConsumer(ClassNameUtil::println));
     */
    public static <T, E extends TwitterException> Consumer<T> rethrowConsumer(Consumer_WithTwitterExceptions<T, E> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (TwitterException exception) {
                throwAsUnchecked(exception);
            }
        };
    }

    public static <T, U, E extends TwitterException> BiConsumer<T, U> rethrowBiConsumer(
            BiConsumer_WithTwitterExceptions<T, U, E> biConsumer) {
        return (t, u) -> {
            try {
                biConsumer.accept(t, u);
            } catch (TwitterException exception) {
                throwAsUnchecked(exception);
            }
        };
    }

    /**
     * .map(rethrowFunction(name -> Class.forName(name))) or
     * .map(rethrowFunction(Class::forName))
     */
    public static <T, R, E extends TwitterException> Function<T, R> rethrowFunction(
            Function_WithTwitterExceptions<T, R, E> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (TwitterException exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    /**
     * rethrowSupplier(() -> new StringJoiner(new String(new byte[]{77, 97, 114,
     * 107}, "UTF-8"))),
     */
    public static <T, E extends TwitterException> Supplier<T> rethrowSupplier(Supplier_WithTwitterExceptions<T, E> function) {
        return () -> {
            try {
                return function.get();
            } catch (TwitterException exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    /**
     * uncheck(() -> Class.forName("xxx"));
     */
    public static void uncheck(@SuppressWarnings("rawtypes") Runnable_WithTwitterExceptions t) {
        try {
            t.run();
        } catch (TwitterException exception) {
            throwAsUnchecked(exception);
        }
    }

    /**
     * uncheck(() -> Class.forName("xxx"));
     */
    public static <R, E extends TwitterException> R uncheck(Supplier_WithTwitterExceptions<R, E> supplier) {
        try {
            return supplier.get();
        } catch (TwitterException exception) {
            throwAsUnchecked(exception);
            return null;
        }
    }

    /**
     * uncheck(Class::forName, "xxx");
     */
    public static <T, R, E extends TwitterException> R uncheck(Function_WithTwitterExceptions<T, R, E> function, T t) {
        try {
            return function.apply(t);
        } catch (TwitterException exception) {
            throwAsUnchecked(exception);
            return null;
        }
    }
    /**
     * uncheck(Class::forName, "xxx");
     */
    public static <T, E extends TwitterException> void uncheckConsumer(Consumer_WithTwitterExceptions<T, E> consumer, T t) {
        try {
            consumer.accept(t);
        } catch (TwitterException exception) {
            throwAsUnchecked(exception);
        }
    }
    /**
     * uncheck(Class::forName, "xxx");
     */
    public static <T, S, R, E extends TwitterException> R uncheck(BiFunction_WithTwitterExceptions<T, S, R, E> function, T t, S s) {
        try {
            return function.apply(t,s);
        } catch (TwitterException exception) {
            throwAsUnchecked(exception);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwAsUnchecked(TwitterException exception) throws E {
        throw new UncheckedTwitterException(exception);
    }

}
