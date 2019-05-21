package io.temco.guhada.common.listener;

public interface OnFindAccountListener extends BaseActivityListener {
    void showSnackBar(String message);

    void hideKeyboard();
}
