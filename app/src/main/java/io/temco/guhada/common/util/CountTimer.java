package io.temco.guhada.common.util;

import java.util.Timer;
import java.util.TimerTask;

import io.temco.guhada.common.listener.OnTimerListener;

public class CountTimer {

    private static TimerTask mTimerTask;
    private static String timerSecond;
    private static String timerMinute;

    ////////////////////////////////////////////////

    private static void initTimer(OnTimerListener listener) {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                int second = Integer.parseInt(timerSecond);
                if (second > 0) {
                    if (second < 10) {
                        timerSecond = "0" + (second - 1);
                    } else {
                        timerSecond = String.valueOf(second - 1);
                    }

                    listener.changeSecond(timerSecond);
                } else {
                    int minute = Integer.parseInt(timerMinute);
                    if (minute > 0) {
                        timerMinute = "0" + (minute - 1);
                        timerSecond = "59";
                        listener.changeMinute(timerMinute);
                        listener.changeSecond(timerSecond);
                    } else {
                        timerMinute = "00";
                        timerSecond = "00";

                        listener.changeMinute(timerMinute);
                        listener.changeSecond(timerSecond);
                        mTimerTask.cancel();
                    }
                }

                listener.notifyMinuteAndSecond();
            }
        };
    }

    public static void startVerifyNumberTimer(String initialSecond, String initialMinute, OnTimerListener listener) {
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }

        timerSecond = initialSecond;
        timerMinute = initialMinute;
        initTimer(listener);

        // 1초마다 반복
        Timer timer = new Timer();
        timer.schedule(mTimerTask, 0, 1000);
    }

    public static void stopTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
    }

    ////////////////////////////////////////////////
}