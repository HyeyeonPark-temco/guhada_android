package io.temco.guhada.common.listener;

import io.temco.guhada.data.model.Category;

public interface OnStateFragmentListener {
    void onReset();

    void onUpdate(Category data);
}