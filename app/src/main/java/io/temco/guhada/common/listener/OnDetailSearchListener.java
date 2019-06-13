package io.temco.guhada.common.listener;

import java.util.List;

import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.Filter;

public interface OnDetailSearchListener {

    void onChange(boolean change);

    void onCategory(List<Category> categories);

    void onBrand(List<Brand> brands);

    void onFilter(List<Filter> filters);
}