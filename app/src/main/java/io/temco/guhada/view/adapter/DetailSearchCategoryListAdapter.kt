package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.Category
import io.temco.guhada.databinding.ItemCategoryLayoutBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class DetailSearchCategoryListAdapter : RecyclerView.Adapter<CategoryBaseListViewHolder<Category,ItemCategoryLayoutBinding>>() {
    var mList: MutableList<Category> = arrayListOf()

    override fun getItemViewType(position: Int): Int {
        when (mList[position].id > 0){
            true -> return 1
            false -> return 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryBaseListViewHolder<Category,ItemCategoryLayoutBinding> {
        val layoutInflater = LayoutInflater.from(parent.context)
        var res = R.layout.item_category_layout
        val binding: ItemCategoryLayoutBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
        return CategoryListViewHolder(binding.root)

    }

    override fun onBindViewHolder(holder: CategoryBaseListViewHolder<Category,ItemCategoryLayoutBinding>, position: Int) {
        holder.bind(position, mList[position])
    }


    fun setItems(list: MutableList<Category>) {
        this.mList = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = mList.size


}


open abstract class CategoryBaseListViewHolder<T,  VB : ViewDataBinding>(containerView: View) : BaseViewHolder<VB>(containerView){
    abstract fun bind(position : Int, data : T)
}


class CategoryListViewHolder(containerView: View) : CategoryBaseListViewHolder<Category, ItemCategoryLayoutBinding>(containerView) {
    override fun bind(position: Int, data: Category) {
        mBinding.executePendingBindings()
    }

}

