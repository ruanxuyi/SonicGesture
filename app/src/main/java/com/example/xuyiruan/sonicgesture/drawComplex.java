package com.example.xuyiruan.sonicgesture;

/**
 * Created by kofe on 10/25/15.
 */

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Random;

        import android.content.Context;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.os.Handler;
        import android.os.Message;
        import android.util.AttributeSet;
        import android.view.View;


public class drawComplex extends View{
    private int XPoint = 60;
    private int YPoint = 260;
    private int XScale = 8;
    private int YScale = 40;
    private int XLength = 380;
    private int YLength = 240;

    private int MaxDataSize = XLength / XScale;

    private List<Integer> data = MainActivity.datas;



    private String[] YLabel = new String[YLength / YScale];

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == 0x1234){
                drawComplex.this.invalidate();
            }
        };
    };

    public drawComplex(Context context, AttributeSet attrs) {
        super(context, attrs);
        for(int i=0; i<YLabel.length; i++){
            YLabel[i] = (i + 1) + "M/s";
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(data.size() >= MaxDataSize){
                        data.remove(0);
                    }
                    //data.add(new Random().nextInt(4) + 1);
                    handler.sendEmptyMessage(0x1234);
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);

        //draw y aix
        canvas.drawLine(XPoint, YPoint - YLength, XPoint, YPoint, paint);

        //draw y arrow
        canvas.drawLine(XPoint, YPoint - YLength, XPoint - 3, YPoint-YLength + 6, paint);  //arrow
        canvas.drawLine(XPoint, YPoint - YLength, XPoint + 3, YPoint-YLength + 6 ,paint);

        //draw mark and label
        for(int i=0; i * YScale < YLength; i++) {
            canvas.drawLine(XPoint, YPoint - i * YScale, XPoint + 5, YPoint - i * YScale, paint);  //mark

            canvas.drawText(YLabel[i], XPoint - 50, YPoint - i * YScale, paint);//label
        }

        //draw x aix
        canvas.drawLine(XPoint, YPoint, XPoint + XLength, YPoint, paint);
        System.out.println("Data.size = " + data.size());
        if(data.size() > 1){
            for(int i=1; i<data.size(); i++){
                canvas.drawLine(XPoint + (i-1) * XScale, YPoint - data.get(i-1) * YScale,
                        XPoint + i * XScale, YPoint - data.get(i) * YScale, paint);
            }
        }
    }
}
