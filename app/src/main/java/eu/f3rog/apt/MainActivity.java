package eu.f3rog.apt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import eu.f3rog.log.Logged;
import eu.f3rog.log.MainThread;
import eu.f3rog.log.WorkerThread;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doSomething("Hello", 3, 4L, new CustomParam());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                doSomething2("Hello", 3, 4L, new CustomParam());
            }
        }, "2222").start();
    }

    @Logged(level = Log.WARN)
    @WorkerThread
    private void doSomething(String text, int num, long num2, CustomParam customParam) {
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("MainActivity", "doSomething1111 执行中..." + Thread.currentThread());
    }

    @Logged(level = Log.ERROR)
    @MainThread
    private void doSomething2(String text, int num, long num2, CustomParam customParam) {
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.w("MainActivity", "doSomething22222 执行中..." + Thread.currentThread());
    }

    public static class CustomParam {
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CustomParam{");
            sb.append("自定义参数");
            sb.append('}');
            return sb.toString();
        }
    }
}
