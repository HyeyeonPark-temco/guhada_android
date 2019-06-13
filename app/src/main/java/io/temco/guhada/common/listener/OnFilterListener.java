package io.temco.guhada.common.listener;

import java.util.List;

import io.temco.guhada.data.model.Attribute;

public interface OnFilterListener {
    void onFilter(int id, List<Attribute> attributes);
}
