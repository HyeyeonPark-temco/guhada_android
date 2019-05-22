package io.temco.guhada.common.listener;

public interface OnFindPasswordListener extends BaseActivityListener {
    void showSnackBar(String message);

    void showResultView();


    void hideKeyboard();

    void startTimer(String minute, String second);
}
