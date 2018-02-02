package eu.f3rog.apt.runtime;

import android.util.Log;

/**
 * @author Administrator
 * @date 2018/2/2 15:15
 */

interface Loggable {

    int v(String tag, String msg);

    int v(String tag, String msg, Throwable tr);

    int d(String tag, String msg);

    int d(String tag, String msg, Throwable tr);

    int i(String tag, String msg);

    int i(String tag, String msg, Throwable tr);

    int w(String tag, String msg);

    int w(String tag, String msg, Throwable tr);

    int w(String tag, Throwable tr);

    int e(String tag, String msg);

    int e(String tag, String msg, Throwable tr);

    int wtf(String tag, String msg);

    int wtf(String tag, Throwable tr);

    int wtf(String tag, String msg, Throwable tr);

    String getStackTraceString(Throwable tr);

    int println(int priority, String tag, String msg);

    Loggable DEFAULT_LOGGER = new Loggable() {
        @Override
        public int v(String tag, String msg) {
            return Log.v(tag, msg);
        }

        @Override
        public int v(String tag, String msg, Throwable tr) {
            return Log.v(tag, msg, tr);
        }

        @Override
        public int d(String tag, String msg) {
            return Log.d(tag, msg);
        }

        @Override
        public int d(String tag, String msg, Throwable tr) {
            return Log.d(tag, msg, tr);
        }

        @Override
        public int i(String tag, String msg) {
            return Log.i(tag, msg);
        }

        @Override
        public int i(String tag, String msg, Throwable tr) {
            return Log.i(tag, msg, tr);
        }

        @Override
        public int w(String tag, String msg) {
            return Log.w(tag, msg);
        }

        @Override
        public int w(String tag, String msg, Throwable tr) {
            return Log.w(tag, msg, tr);
        }

        @Override
        public int w(String tag, Throwable tr) {
            return Log.w(tag, tr);
        }

        @Override
        public int e(String tag, String msg) {
            return Log.e(tag, msg);
        }

        @Override
        public int e(String tag, String msg, Throwable tr) {
            return Log.e(tag, msg, tr);
        }

        @Override
        public int wtf(String tag, String msg) {
            return Log.wtf(tag, msg);
        }

        @Override
        public int wtf(String tag, Throwable tr) {
            return Log.wtf(tag, tr);
        }

        @Override
        public int wtf(String tag, String msg, Throwable tr) {
            return Log.wtf(tag, msg, tr);
        }

        @Override
        public String getStackTraceString(Throwable tr) {
            return Log.getStackTraceString(tr);
        }

        @Override
        public int println(int priority, String tag, String msg) {
            return println(priority, tag, msg);
        }
    };
}
