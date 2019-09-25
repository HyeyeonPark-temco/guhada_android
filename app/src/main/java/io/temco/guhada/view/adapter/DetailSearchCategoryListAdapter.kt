package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnCategoryListListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.Category
import io.temco.guhada.databinding.ItemCategoryLayoutBinding
import io.temco.guhada.view.holder.base.BaseViewHolder
import java.io.Serializable

class CategoryTitle(var title : String, var fullTitle : String) : Serializable{
    override fun toString(): String {
        return "CategoryTitle(title='$title', fullTitle='$fullTitle')"
    }
}

class DetailSearchCategoryListAdapter : RecyclerView.Adapter<CategoryBaseListViewHolder<Category,ItemCategoryLayoutBinding>>() {
    var mList: MutableList<Category> = arrayListOf()
    lateinit var mDepthTitle: MutableMap<Int,MutableMap<Int, Category>>
    var parentCategoryCount = 0

    var mCategoryListener: OnCategoryListListener? = null
    var mDepthIndex : HashMap<Int, Int> = hashMapOf()


    /*override fun getItemViewType(position: Int): Int {
        when (mList[position].id > 0){
            true -> return 1
            false -> return 0
        }
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryBaseListViewHolder<Category,ItemCategoryLayoutBinding> {
        val layoutInflater = LayoutInflater.from(parent.context)
        var res = R.layout.item_category_layout
        val binding: ItemCategoryLayoutBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
        return CategoryListViewHolder(binding.root)

    }

    override fun onBindViewHolder(holder: CategoryBaseListViewHolder<Category,ItemCategoryLayoutBinding>, position: Int) {
        holder.bind(this@DetailSearchCategoryListAdapter, position, mList[position])
    }


    fun setItems(list: MutableList<Category>) {
        this.mList = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = mList.size

}


open abstract class CategoryBaseListViewHolder<T,  VB : ViewDataBinding>(containerView: View) : BaseViewHolder<VB>(containerView){
    abstract fun bind(adapter : DetailSearchCategoryListAdapter, position : Int, data : T)
}


class CategoryListViewHolder(containerView: View) : CategoryBaseListViewHolder<Category, ItemCategoryLayoutBinding>(containerView) {
    override fun bind(adapter : DetailSearchCategoryListAdapter, position: Int, data: Category) {
        mBinding.count = ""
        mBinding.depth = data.depth
        mBinding.hasChild = !data.children.isNullOrEmpty()
        mBinding.isExpandable = !data.children.isNullOrEmpty()
        mBinding.isSelect = data.isSelected
        mBinding.title = data.title
        mBinding.setClickListener {
            synchronized(adapter){
                if(CustomLog.flag)CustomLog.L("CategoryListViewHolder","data",data.toString())
                if(!data.isSelected){
                    data.isSelected = true
                    if(!adapter.mDepthTitle.contains(data.depth)){
                        var map : MutableMap<Int, Category> = mutableMapOf()
                        map.put(data.id, /*CategoryTitle(data.title,data.fullDepthName)*/data)
                        adapter.mDepthTitle.put(data.depth, map)
                    }else{
                        var map : MutableMap<Int, Category> = adapter.mDepthTitle.get(data.depth)!!
                        map.put(data.id, data/*CategoryTitle(data.title,data.fullDepthName)*/)
                        adapter.mDepthTitle.put(data.depth, map)
                    }
                    if(!data.children.isNullOrEmpty()){
                        if(!adapter.mDepthIndex.containsKey(data.depth)){
                            adapter.mDepthIndex.put(data.depth, position)
                            addChild(adapter, position, data)
                        }else{
                            var removeIndex = adapter.mDepthIndex.get(data.depth) as Int
                            if(CustomLog.flag)CustomLog.L("CategoryListViewHolder","removeIndex",removeIndex , "adapter size",adapter.mList.size)
                            var count = removeChild(adapter, removeIndex!!, data)
                            if(CustomLog.flag)CustomLog.L("CategoryListViewHolder","position",position ,"adapter size",adapter.mList.size)
                            var modPosition = position
                            if(position > removeIndex){
                                modPosition = position - count
                            }
                            adapter.mDepthIndex.put(data.depth, modPosition)
                            addChild(adapter, modPosition, data)
                        }
                    }
                }else{
                    var boolean = true
                    if(data.parentId == -1 && adapter.parentCategoryCount == 1) boolean = false
                    if(boolean){
                        data.isSelected = false
                        selectTitleRemove(adapter, data)
                        if(!data.children.isNullOrEmpty()){
                            adapter.mDepthIndex.remove(data.depth)
                            removeChild(adapter, position, data)
                        }
                    }
                }
                adapter.mCategoryListener?.onEvent(position, data)
                adapter.notifyDataSetChanged()
            }
        }
        mBinding.executePendingBindings()
    }


    private fun addChild(adapter : DetailSearchCategoryListAdapter, position : Int , data : Category){
        if(position == adapter.mList.size-1){
            adapter.mList.addAll(data.children)
        }else{
            adapter.mList.addAll(position+1, data.children)
        }
    }

    private fun removeChild(adapter : DetailSearchCategoryListAdapter, position : Int , data : Category) : Int {
        var count = 0
        if(CustomLog.flag)CustomLog.L("CategoryListViewHolder","adapter.mList.size",adapter.mList.size,"position",position)
        for (i in adapter.mList.size-1 downTo position+1){
            if(data.depth < adapter.mList[i].depth){
                unSelectCategory(adapter, adapter.mList[i])
                if(CustomLog.flag)CustomLog.L("CategoryListViewHolder remove "+i,adapter.mList[i])
                count++
                adapter.mList.remove(adapter.mList[i])
            }
        }
        adapter.mList[position].isSelected = false
        selectTitleRemove(adapter, adapter.mList[position])
        return count
    }

    private fun selectTitleRemove(adapter : DetailSearchCategoryListAdapter,item : Category){
        if(adapter.mDepthTitle.containsKey(item.depth) && adapter.mDepthTitle.get(item.depth)!!.contains(item.id)){
            adapter.mDepthTitle.get(item.depth)!!.remove(item.id)
            if(adapter.mDepthTitle[item.depth]?.size ?: 0 == 0) adapter.mDepthTitle.remove(item.depth)
        }
    }

    private fun unSelectCategory(adapter : DetailSearchCategoryListAdapter,item : Category){
        synchronized(adapter){
            item.isSelected = false
            selectTitleRemove(adapter, item)
            /*adapter.mCategorySelectedList.iterator().apply {
                loop@while (hasNext()){
                    var r = next()
                    if(r.id == item.id ) {
                        adapter.mCategorySelectedList.remove(r)
                        break@loop
                    }
                }
            }*/
            //for (r in adapter.mCategorySelectedList) if(item.id == r.id) adapter.mCategorySelectedList.remove(r)

            if(!item.children.isNullOrEmpty()){
                if(CustomLog.flag)CustomLog.L("CategoryListViewHolder unSelectCategory ",item)
                for (c in item.children) {
                    unSelectCategory(adapter, c)
                }
            }
        }
    }



}

