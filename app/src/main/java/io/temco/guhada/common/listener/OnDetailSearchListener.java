package io.temco.guhada.common.listener;

import java.util.List;
import java.util.Map;

import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.Filter;

public interface OnDetailSearchListener {

    void onChange(boolean change);

    void onCategoryResult(Map<Integer, Map<Integer, Category>> map);

    void onCategory(List<Category> categories);

    void onBrand(List<Brand> brands);

    void onFilter(List<Filter> filters);
}