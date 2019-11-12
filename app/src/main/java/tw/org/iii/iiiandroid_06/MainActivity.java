package tw.org.iii.iiiandroid_06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView clock;
    private ListView lapList;
    private boolean isRunning; //預設false
    private Button btnLeft, btnRight;
    private Timer timer;
    private int i;
    private UIHandler uiHandler;
    private SimpleAdapter adapter;
    private LinkedList<HashMap<String,String>> data;
    private String[] from = {"lapItem"};
    private int[] to = {R.id.lapItem};
    private int lapCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clock = findViewById(R.id.clock);
        lapList = findViewById(R.id.lapList);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        Log.v("DCH", "start");
        uiHandler = new UIHandler();
        timer = new Timer();
        timer.schedule(new MyTask(), 0, 10);
        initListView();
    }

    //初始化
    private void initListView() {
        data = new LinkedList<>(); //物件實體化
        adapter = new SimpleAdapter(this,data,R.layout.item,from, to);
        lapList.setAdapter(adapter);
    }

    //按下start開始計時，stop暫停
    private class MyTask extends TimerTask {
        @Override
        public void run() {
            if (isRunning) {
                i++;
                Log.v("DCH", "i=" + i);
                uiHandler.sendEmptyMessage(0);
                //clock.setText(""+i);
            }
        }
    }

    //清空時間
    @Override
    public void finish() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        super.finish();
    }

    //讓舊版本可以在背景執行
    private class UIHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            clock.setText(toClockString());
        }
    }

    //轉換時間單位
    private String toClockString() {
        int ts = i / 100; //總秒數
        int hs = i % 100;
        int ss = ts % 60;
        int hh = ts / (60*60);
        int mm = (ts - hh*60*60)/60;
        return hh + ":" + mm + ":" + ss + "." + hs;
    }

    public void clickLeft(View view) {
        if (isRunning) {
            doLap();
        }
        else {
            doReset();
        }
    }

    //圈數計時功能
    private void doLap() {
        HashMap<String,String> itemData = new HashMap<>();
        lapCount++;
        itemData.put(from[0], "Lap" + lapCount + ":\t\t\t" + clock.getText().toString());
        data.add(0, itemData);
        adapter.notifyDataSetChanged();
    }

    //歸零重來，清空畫面
    private void doReset() {
        i = 0;
        lapCount = 0;
        data.clear();
        adapter.notifyDataSetChanged();
        uiHandler.sendEmptyMessage(0);
    }

    public void clickRight(View view) {
        //轉換按鈕狀態
        isRunning = !isRunning; //第一次true，第二次false，以此類推
        btnLeft.setText(isRunning?"Lap":"Reset");
        btnRight.setText(isRunning?"Stop":"Start");
    }
}
