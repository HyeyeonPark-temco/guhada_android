package io.temco.guhada.common.listener;

import io.temco.guhada.common.Type;

public interface OnCategoryListener {
    void onEvent(Type.Category type, int[] hierarchies);
}