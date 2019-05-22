package io.temco.guhada.common.listener;

public interface OnTimerListener {
    void changeSecond(String second);
    void changeMinute(String minute);
    void notifyMinuteAndSecond();
}

