package io.temco.guhada.common.listener;

import io.temco.guhada.data.model.Token;

public interface OnSnsLoginListener {
    void redirectTermsActivity();
    void redirectMainActivity(Token data);
}
