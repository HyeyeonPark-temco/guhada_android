package io.temco.guhada.common.listener;

import io.temco.guhada.common.Type;
import io.temco.guhada.data.model.Category;

public interface OnCategoryListener {
    //void onEvent(Type.Category type, Category category);
    void onEvent(Category category);
}