package eu.f3rog.apt.runtime;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Trace;
import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import eu.f3rog.log.MainThread;
import eu.f3rog.log.UiThread;
import eu.f3rog.log.WorkerThread;

@Aspect
public class ThreadAspectConfig {
    private static volatile boolean sEnabled = true;

    private static volatile Loggable sLoggable = Loggable.DEFAULT_LOGGER;

    @Pointcut("within(@eu.f3rog.log.MainThread *) || within(@eu.f3rog.log.UiThread *)  || within(@eu.f3rog.log.WorkerThread *)")
    public void withinAnnotatedClass() {
    }

    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    public void methodInsideAnnotatedType() {
    }

    @Pointcut("execution(@eu.f3rog.log.MainThread * *(..)) || execution(@eu.f3rog.log.UiThread * *(..)) || execution(@eu.f3rog.log.WorkerThread * *(..)) || methodInsideAnnotatedType()")
    public void method() {
    }

    public static void setEnabled(boolean enabled) {
        ThreadAspectConfig.sEnabled = enabled;
    }

    public static void setLoggable(Loggable loggable) {
        if (loggable != null) {
            sLoggable = loggable;
        }
    }

    @Around("method()")
    public Object logAndExecute(final ProceedingJoinPoint joinPoint) throws Throwable {
        Annotation annotation = getMethodAnnotation(joinPoint, WorkerThread.class);
        if (annotation == null) {
            annotation = getMethodAnnotation(joinPoint, MainThread.class);
        }
        if (annotation == null) {
            annotation = getMethodAnnotation(joinPoint, UiThread.class);
        }

        final Object[] result = new Object[1];
        boolean isMainLooper = Looper.myLooper() == Looper.getMainLooper();
        if (isMainLooper) {
            if (WorkerThread.class.equals(annotation.annotationType())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            result[0] = ThreadAspectConfig.this.run(joinPoint);
                        } catch (Throwable e) {
                            CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
                            Class<?> cls = codeSignature.getDeclaringType();
                            sLoggable.w(asTag(cls), e);
                        }
                    }
                }, "@eu.f3rog.log.WorkerThread")
                        .start();
            } else {
                result[0] = run(joinPoint);
            }
        } else {
            if (WorkerThread.class.equals(annotation.annotationType())) {
                result[0] = run(joinPoint);
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            result[0] = ThreadAspectConfig.this.run(joinPoint);
                        } catch (Throwable e) {
                            CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
                            Class<?> cls = codeSignature.getDeclaringType();
                            sLoggable.w(asTag(cls), e);
                        }
                    }
                });
            }
        }

        return result[0];
    }

    private Object run(ProceedingJoinPoint joinPoint) throws Throwable {
        enterMethod(joinPoint);

        long startNanos = System.nanoTime();
        Object result = joinPoint.proceed();
        long stopNanos = System.nanoTime();
        long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);

        exitMethod(joinPoint, result, lengthMillis);
        return result;
    }

    private static void enterMethod(JoinPoint joinPoint) {
        if (!sEnabled) return;

        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();

        Class<?> cls = codeSignature.getDeclaringType();
        String methodName = codeSignature.getName();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();

        StringBuilder builder = new StringBuilder("\u21E2 ");
        builder.append(methodName).append('(');
        for (int i = 0; i < parameterValues.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(parameterNames[i]).append('=');
            builder.append(Strings.toString(parameterValues[i]));
        }
        builder.append(')');

        /*if (Looper.myLooper() != Looper.getMainLooper()) {
            builder.append(" [Thread:\"").append(Thread.currentThread().getName()).append("\"]");
        }*/
        builder.append(" [Thread:\"").append(Thread.currentThread().getName()).append("\"]");

        /*
        Logged annotation = getMethodAnnotation(joinPoint, Logged.class);
        if (annotation.printStack()) {
            logWithLevel(annotation.level(), asTag(cls), builder.toString(), new Exception());
        } else {
            logWithLevel(annotation.level(), asTag(cls), builder.toString());
        }
        */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final String section = builder.toString().substring(2);
            Trace.beginSection(section);
        }
    }

    private static void exitMethod(JoinPoint joinPoint, Object result, long lengthMillis) {
        if (!sEnabled) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Trace.endSection();
        }

        Signature signature = joinPoint.getSignature();

        Class<?> cls = signature.getDeclaringType();
        String methodName = signature.getName();
        boolean hasReturnType = signature instanceof MethodSignature
                && ((MethodSignature) signature).getReturnType() != void.class;

        StringBuilder builder = new StringBuilder("\u21E0 ")
                .append(methodName)
                .append(" [")
                .append(lengthMillis)
                .append("ms]");

        if (hasReturnType) {
            builder.append(" = ");
            builder.append(Strings.toString(result));
        }

        /*Logged annotation = getMethodAnnotation(joinPoint, Logged.class);
        logWithLevel(annotation.level(), asTag(cls), builder.toString());*/
    }

    private static String asTag(Class<?> cls) {
        if (cls.isAnonymousClass()) {
            return asTag(cls.getEnclosingClass());
        }
        return cls.getSimpleName();
    }

    /**
     * Get value of annotated method parameter
     */
    private static <T extends Annotation> T getMethodAnnotation(JoinPoint joinPoint, Class<T> clazz) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return method.getAnnotation(clazz);
    }

    private static void logWithLevel(int level, String tag, String msg) {
        switch (level) {
            case Log.DEBUG:
                sLoggable.d(tag, msg);
                break;
            case Log.INFO:
                sLoggable.i(tag, msg);
                break;
            case Log.WARN:
                sLoggable.w(tag, msg);
                break;
            case Log.ERROR:
                sLoggable.e(tag, msg);
                break;
            case Log.ASSERT:
                sLoggable.wtf(tag, msg);
                break;
            case Log.VERBOSE:
            default:
                sLoggable.v(tag, msg);
                break;
        }
    }

    private static void logWithLevel(int level, String tag, String msg, Throwable e) {
        switch (level) {
            case Log.DEBUG:
                sLoggable.d(tag, msg, e);
                break;
            case Log.INFO:
                sLoggable.i(tag, msg, e);
                break;
            case Log.WARN:
                sLoggable.w(tag, msg, e);
                break;
            case Log.ERROR:
                sLoggable.e(tag, msg, e);
                break;
            case Log.ASSERT:
                sLoggable.wtf(tag, msg, e);
                break;
            case Log.VERBOSE:
            default:
                sLoggable.v(tag, msg, e);
                break;
        }
    }
}
