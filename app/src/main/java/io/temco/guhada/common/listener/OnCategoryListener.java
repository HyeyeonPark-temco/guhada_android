package io.temco.guhada.common.listener;

import io.temco.guhada.data.model.Category;

public interface OnCategoryListener {
    // void onEvent(Type.Category type, int[] hierarchies);
    void onEvent(Category category);
}