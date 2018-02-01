package eu.f3rog.apt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import eu.f3rog.log.Logged;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doSomething("Hello", 3, 4L, new CustomParam());
    }

    @Logged(Log.ERROR)
    private void doSomething(String text, int num, long num2, CustomParam customParam) {
        Log.e("1111", "doSomething 执行中...");
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
