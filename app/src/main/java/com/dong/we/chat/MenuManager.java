package com.dong.we.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

public class MenuManager {


    private View topView;
    private View bottomView;
    private View view;

    private float lastX;
    private float lastY;
    private float mTouchStartX;
    private float mTouchStartY;

    private long mDownTime;
    private long mUpTime;

    private MenuClickListener listener;


    public MenuManager(Context context) {
        initView(context);
    }

    private void initView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_menu, null);
        topView = view.findViewById(R.id.menu_top);
        bottomView = view.findViewById(R.id.menu_bottom);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastX = event.getRawX();
                lastY = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        mDownTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //计算新的位置
                        int newX = (int) (lastX - mTouchStartX);
                        int newY = (int) (lastY - mTouchStartY);
                        if (listener != null) {
                            listener.onViewMove(newX, newY);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mUpTime = System.currentTimeMillis();
                        if (mUpTime - mDownTime > 200) {
                            return true;
                        } else {
                            if (event.getY() <= topView.getHeight()) {
                                if (listener != null) {
                                    listener.onTopClick();
                                }
                            }
                            if (event.getY() >= (view.getHeight() - bottomView.getHeight()))
                                if (listener != null) {
                                    listener.onBottomClick();
                                }
                        }
                }
                return false;
            }
        });

    }

    public View getView() {
        return view;
    }

    public MenuClickListener getMenuListener() {
        return listener;
    }

    public void setListener(MenuClickListener listener) {
        this.listener = listener;
    }

    public interface MenuClickListener {

        public void onTopClick();

        public void onBottomClick();

        public void onViewMove(int nx, int ny);

    }


}
