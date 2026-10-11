package fuzs.puzzleslib.api.event.v1.core;

import com.google.common.reflect.AbstractInvocationHandler;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A helper class for avoiding boilerplate code when defining new events on Fabric.
 * <p>
 * Especially useful when converting events from common to dedicated {@link Event Fabric Events}.
 * <p>
 * Thanks a lot to <a
 * href="https://github.com/architectury/architectury-api/blob/1.19.3/common/src/main/java/dev/architectury/event/EventFactory.java">EventFactory.java</a>
 * from <a href="https://github.com/architectury/architectury-api">Architectury API</a> for this implementation.
 * <p>
 * Listener invocation is based on <a href="https://github.com/architectury/architectury-api/pull/747">architectury-api
 * PR #747</a>: instead of resolving a {@link MethodHandle} for every callback, one handle is unreflected, adapted with
 * {@link MethodHandle#asSpreader(Class, int)} and cached per {@link Method}, then invoked with
 * {@link MethodHandle#invokeExact(Object...)}. This keeps listener exceptions unwrapped and stack traces free of
 * reflection frames while removing the previous per-callback overhead.
 */
@SuppressWarnings("unchecked")
public final class FabricEventFactory {
    private static final MethodType INVOKER_TYPE = MethodType.methodType(Object.class, Object.class, Object[].class);
    private static final Map<Method, MethodHandle> INVOKERS = new ConcurrentHashMap<>();

    /**
     * Creates a new event from a functional interface without a result, meaning the interface method returns {@link Void}.
     *
     * @param type event type
     * @param <T>  event type
     * @return the Fabric event
     */
    public static <T> Event<T> create(Class<? super T> type) {
        return EventFactory.createArrayBacked(type, events -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(), new Class[]{type}, new AbstractInvocationHandler() {

            @Override
            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                for (Object event : events) {
                    invokeFast(event, method, args);
                }
                return null;
            }
        }));
    }

    /**
     * Creates a new event from a functional interface with an event result, meaning the interface method returns {@link EventResult}.
     * <p>The implementation stops for the first callback that does not return {@link EventResult#PASS}.
     *
     * @param type event type
     * @param <T>  event type
     * @return the Fabric event
     */
    public static <T> Event<T> createResult(Class<? super T> type) {
        return EventFactory.createArrayBacked(type, events -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(), new Class[]{type}, new AbstractInvocationHandler() {

            @Override
            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                for (Object event : events) {
                    EventResult result = invokeFast(event, method, args);
                    if (result.isInterrupt()) {
                        return result;
                    }
                }
                return EventResult.PASS;
            }
        }));
    }

    /**
     * Creates a new event from a functional interface with an event result holder, meaning the interface method returns an instance of {@link EventResultHolder}.
     * <p>The implementation stops for the first callback that does not return {@link EventResultHolder#pass()}.
     *
     * @param type event type
     * @param <T>  event type
     * @return the Fabric event
     */
    public static <T> Event<T> createResultHolder(Class<? super T> type) {
        return EventFactory.createArrayBacked(type, events -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(), new Class[]{type}, new AbstractInvocationHandler() {

            @Override
            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                for (Object event : events) {
                    EventResultHolder<?> holder = invokeFast(event, method, args);
                    if (holder.isInterrupt()) {
                        return holder;
                    }
                }
                return EventResultHolder.pass();
            }
        }));
    }

    /**
     * Creates a new event from a functional interface with a boolean result, meaning the interface method returns a primitive boolean.
     * <p>The implementation stops for the first callback that returns the same value as provided by <code>inverted</code>.
     * <p>While our own implementations are encouraged to use the custom {@link EventResult} class for returning a result,
     * this helper method exists to aid in creating events similar to implementations in Fabric Api.
     *
     * @param type     event type
     * @param inverted should boolean result be inverted when determining when to interrupt event invocation
     * @param <T>      event type
     * @return the Fabric event
     */
    public static <T> Event<T> createSimpleResult(Class<? super T> type, boolean inverted) {
        return EventFactory.createArrayBacked(type, events -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(), new Class[]{type}, new AbstractInvocationHandler() {

            @Override
            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                for (Object event : events) {
                    boolean result = invokeFast(event, method, args);
                    if (result == inverted) {
                        return result;
                    }
                }
                return !inverted;
            }
        }));
    }

    /**
     * Creates a new event from a functional interface with a vanilla result, meaning the interface method returns {@link InteractionResult}.
     * <p>The implementation stops for the first callback that does not return {@link InteractionResult#PASS}.
     * <p>While our own implementations are encouraged to use the custom {@link EventResult} class for returning a result,
     * this helper method exists to aid in creating events similar to implementations in Fabric Api.
     *
     * @param type event type
     * @param <T>  event type
     * @return the Fabric event
     */
    public static <T> Event<T> createVanillaResult(Class<? super T> type) {
        return EventFactory.createArrayBacked(type, events -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(), new Class[]{type}, new AbstractInvocationHandler() {

            @Override
            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                for (Object event : events) {
                    InteractionResult result = invokeFast(event, method, args);
                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }
                return InteractionResult.PASS;
            }
        }));
    }

    private static <T> T invokeFast(Object object, Method method, Object[] args) throws Throwable {
        MethodHandle invoker = INVOKERS.get(method);
        if (invoker == null) {
            invoker = MethodHandles.lookup()
                    .unreflect(method)
                    .asSpreader(Object[].class, method.getParameterCount())
                    .asType(INVOKER_TYPE);
            INVOKERS.putIfAbsent(method, invoker);
        }

        return (T) invoker.invokeExact((Object) object, args);
    }
}
