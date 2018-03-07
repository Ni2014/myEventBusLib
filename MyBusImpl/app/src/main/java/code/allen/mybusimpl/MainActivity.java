package code.allen.mybusimpl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import code.allen.mylibrary.v1.Bus;
import code.allen.mylibrary.v1.BusReceiver;

public class MainActivity extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.btn);
        Bus.getDefault().register(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event event = new Event();
                event.setUserId("111");
                Bus.getDefault().post(event);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bus.getDefault().unRegister(this);
    }

    // 接收事件
    @BusReceiver
    public void onEvent(Event event){
        System.out.println("getEvent " + event.getUserId());
    }
}
