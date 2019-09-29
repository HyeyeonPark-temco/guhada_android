package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCategoryListListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.Category
import io.temco.guhada.databinding.ItemCategoryLayoutBinding
import io.temco.guhada.view.custom.dialog.DetailSearchDialog
import io.temco.guhada.view.holder.base.BaseViewHolder

class DetailSearchCategoryListAdapter : RecyclerView.Adapter<CategoryBaseListViewHolder<Category,ItemCategoryLayoutBinding>>() {
    var mList: MutableList<Category> = arrayListOf()
    lateinit var mDepthTitle: MutableMap<Int,MutableMap<Int, Category>>
    lateinit var mDepthIndex : HashMap<Int, Int>
    var parentCategoryCount = 0

    var mCategoryListener: OnCategoryListListener? = null


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
        if(data.isExpand){
            data.isExpand = false
            if(!data.children.isNullOrEmpty()) addChild(adapter, position, data)
        }
        mBinding.setClickListener {
            if(data.type == Type.Category.ALL){
                clickAll(adapter, position, data)
            }else{
                clickNormal(adapter, position, data)
            }
        }
        mBinding.executePendingBindings()
    }


    private fun clickNormal(adapter : DetailSearchCategoryListAdapter, position: Int, data: Category){
        synchronized(adapter){
            if(CustomLog.flag)CustomLog.L("CategoryListViewHolder","0---- start clickNormal data",data.toString())
            if(!data.isSelected){
                data.isSelected = true
                var depthIndex = adapter.mDepthTitle.keys.iterator()
                if(depthIndex.hasNext()){
                    var di = depthIndex.next()
                    if(di > data.depth)adapter.mDepthTitle.remove(di)
                }
                if(!adapter.mDepthTitle.contains(data.depth)){
                    var map : MutableMap<Int, Category> = mutableMapOf()
                    map.put(data.id, data)
                    adapter.mDepthTitle.put(data.depth, map)
                }else{
                    if((data.children != null && !data.children.isEmpty())) {
                        adapter.mDepthTitle.remove(data.depth)
                        var map : MutableMap<Int, Category> = mutableMapOf()
                        map.put(data.id, data)
                        adapter.mDepthTitle.put(data.depth, map)
                    }else{
                        var map : MutableMap<Int, Category> = adapter.mDepthTitle.get(data.depth)!!
                        map.put(data.id, data)
                        adapter.mDepthTitle.put(data.depth, map)
                    }
                }
                if(CustomLog.flag)CustomLog.L("CategoryListViewHolder","0---- start clickNormal mDepthTitle",adapter.mDepthTitle.toString())
                if(CustomLog.flag)CustomLog.L("CategoryListViewHolder","1---- start clickNormal data",data.toString())
                if(adapter.mDepthTitle.containsKey(data.depth-1)){
                    for (c in adapter.mList){
                        if(c.children != null && c.children.size > 1 && adapter.mDepthTitle.get(data.depth-1)?.containsKey(c.id)!!){
                            c.children[0].isSelected = false
                        }
                    }
                }
                if(data.children != null && data.children.size > 0){
                    var depthIndex = adapter.mDepthIndex.keys.iterator()
                    if(depthIndex.hasNext()){
                        var di = depthIndex.next()
                        if(di > data.depth)adapter.mDepthIndex.remove(di)
                    }
                    if(CustomLog.flag)CustomLog.L("CategoryListViewHolder","1---- start clickNormal data",data.toString())
                    if(!adapter.mDepthIndex.containsKey(data.depth)){
                        adapter.mDepthIndex.put(data.depth, position)
                        addChild(adapter, position, data)
                    }else{
                        var removeIndex = adapter.mDepthIndex.get(data.depth) as Int
                        var count = removeChild(adapter, removeIndex, data)
                        var modPosition = position
                        if(position > removeIndex){
                            modPosition = position - count
                        }
                        adapter.mDepthIndex.put(data.depth, modPosition)
                        addChild(adapter, modPosition, data)
                    }
                }
                if(CustomLog.flag)CustomLog.L("CategoryListViewHolder","11---- clickNormal data",data.toString())
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
            if(CustomLog.flag)CustomLog.L("CategoryListViewHolder","mDepthIndex",adapter.mDepthIndex.toString())
            if(CustomLog.flag)CustomLog.L("CategoryListViewHolder -- end mDepthTitle",adapter.mDepthTitle.toString())
            adapter.mCategoryListener?.onEvent(position, data)
            adapter.notifyDataSetChanged()
        }
    }


    private fun clickAll(adapter : DetailSearchCategoryListAdapter, index: Int, tmp: Category){
        if(CustomLog.flag)CustomLog.L("CategoryListViewHolder","clickAll tmp",tmp.toString())
        if(!tmp.isSelected){
            var position = index-1
            var data = adapter.mList[position]
            if(CustomLog.flag)CustomLog.L("CategoryListViewHolder","clickAll tmp",data.toString())
            selectTitleRemove(adapter, data)
            if(!data.children.isNullOrEmpty()){
                var depthIndex = adapter.mDepthIndex.keys.iterator()
                if(depthIndex.hasNext()){
                    var di = depthIndex.next()
                    if(di > data.depth)adapter.mDepthIndex.remove(di)
                }
                adapter.mDepthIndex.remove(data.depth)
                removeChild(adapter, position, data)
            }
            if(!data.children.isNullOrEmpty()){
                if(!adapter.mDepthIndex.containsKey(data.depth)){
                    adapter.mDepthIndex.put(data.depth, position)
                    addChild(adapter, position, data)
                }else{
                    var removeIndex = adapter.mDepthIndex.get(data.depth) as Int
                    var count = removeChild(adapter, removeIndex!!, data)
                    if(count == -99) {
                        (itemView.context as DetailSearchDialog).dismiss()
                    }
                    var modPosition = position
                    if(position > removeIndex){
                        modPosition = position - count
                    }
                    adapter.mDepthIndex.put(data.depth, modPosition)
                    addChild(adapter, modPosition, data)
                }
            }

            data.isSelected = true
            var depthIndex = adapter.mDepthTitle.keys.iterator()
            if(depthIndex.hasNext()){
                var di = depthIndex.next()
                if(di > data.depth)adapter.mDepthTitle.remove(di)
            }
            if(!adapter.mDepthTitle.contains(data.depth)){
                var map : MutableMap<Int, Category> = mutableMapOf()
                map.put(data.id, data)
                adapter.mDepthTitle.put(data.depth, map)
            }else{
                var map : MutableMap<Int, Category> = adapter.mDepthTitle.get(data.depth)!!
                map.put(data.id, data)
                adapter.mDepthTitle.put(data.depth, map)
            }
            if(CustomLog.flag)CustomLog.L("CategoryListViewHolder -- clickAll mDepthTitle",adapter.mDepthTitle.toString())
            tmp.isSelected = true
            adapter.mCategoryListener?.onEvent(position, data)
            adapter.notifyDataSetChanged()
        }
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
        try{
            for (i in adapter.mList.size-1 downTo position+1){
                if(data.depth < adapter.mList[i].depth){
                    unSelectCategory(adapter, adapter.mList[i])
                    count++
                    if(CustomLog.flag)CustomLog.L("CategoryListViewHolder -- clickAll removeChild 1",adapter.mList[i].toString())
                    adapter.mList.remove(adapter.mList[i])
                }
            }
            adapter.mList[position].isSelected = false
            if(CustomLog.flag)CustomLog.L("CategoryListViewHolder -- clickAll removeChild 2",adapter.mList[position].toString())
            selectTitleRemove(adapter, adapter.mList[position])
        }catch (e : Exception){
            count = -99
        }
        return count
    }

    /**
     * 오류 있음 수정해야됨
     * java.lang.IndexOutOfBoundsException: Index: 19, Size: 16
    at java.util.ArrayList.get(ArrayList.java:437)
    at io.temco.guhada.view.adapter.CategoryListViewHolder.removeChild(DetailSearchCategoryListAdapter.kt:206)
    at io.temco.guhada.view.adapter.CategoryListViewHolder.clickNormal(DetailSearchCategoryListAdapter.kt:113)
    at io.temco.guhada.view.adapter.CategoryListViewHolder.access$clickNormal(DetailSearchCategoryListAdapter.kt:61)
    at io.temco.guhada.view.adapter.CategoryListViewHolder$bind$1.onClick(DetailSearchCategoryListAdapter.kt:77)
     */

    private fun selectTitleRemove(adapter : DetailSearchCategoryListAdapter,item : Category){
        if(adapter.mDepthTitle.containsKey(item.depth) && adapter.mDepthTitle[item.depth]!!.contains(item.id)){
            adapter.mDepthTitle[item.depth]!!.remove(item.id)
            if(adapter.mDepthTitle[item.depth]?.size ?: 0 == 0) adapter.mDepthTitle.remove(item.depth)
        }
    }

    private fun unSelectCategory(adapter : DetailSearchCategoryListAdapter,item : Category){
        synchronized(adapter){
            item.isSelected = false
            selectTitleRemove(adapter, item)
            if(!item.children.isNullOrEmpty()){
                //if(CustomLog.flag)CustomLog.L("CategoryListViewHolder unSelectCategory ",item)
                for (c in item.children) {
                    unSelectCategory(adapter, c)
                }
            }
        }
    }



}

