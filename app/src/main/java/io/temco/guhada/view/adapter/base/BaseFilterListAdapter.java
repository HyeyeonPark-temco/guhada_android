package io.temco.guhada.view.adapter.base;

import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseFilterListAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    ////////////////////////////////////////////////
    // ABSTRACT
    ////////////////////////////////////////////////

    public abstract void reset();

    ////////////////////////////////////////////////
}
