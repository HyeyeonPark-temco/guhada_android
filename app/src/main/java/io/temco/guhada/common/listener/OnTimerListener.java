package io.temco.guhada.common.listener;

/**
 * Timer Listener
 * @Author Hyeyeon Park
 */
public interface OnTimerListener {
    void changeSecond(String second);
    void changeMinute(String minute);

    /**
     * Databinding 사용 시, Minute과 Second 동시에 notify
     */
    void notifyMinuteAndSecond();
}

