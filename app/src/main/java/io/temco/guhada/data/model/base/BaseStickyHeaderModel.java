package io.temco.guhada.data.model.base;


import androidx.annotation.LayoutRes;

import io.temco.guhada.common.Type;

public class BaseStickyHeaderModel {

    public Type.List type = Type.List.CONTENTS;

    public int position;

    @LayoutRes
    public int layoutRes;
}