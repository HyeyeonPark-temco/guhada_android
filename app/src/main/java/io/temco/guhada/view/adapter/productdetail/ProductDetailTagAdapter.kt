package io.temco.guhada.view.adapter.productdetail

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.databinding.ItemProductdetailTagBinding
import io.temco.guhada.view.activity.ProductFilterListActivity
import io.temco.guhada.view.holder.base.BaseViewHolder

class ProductDetailTagAdapter : RecyclerView.Adapter<ProductDetailTagAdapter.Holder>() {
    private var list: MutableList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate<ItemProductdetailTagBinding>(LayoutInflater.from(parent.context), R.layout.item_productdetail_tag, parent, false))
    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setItems(list: MutableList<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class Holder(val binding: ItemProductdetailTagBinding) : BaseViewHolder<ItemProductdetailTagBinding>(binding.root) {
        fun bind(value: String) {
            binding.value = value
            binding.setClickListener {
                //CommonUtil.startSearchWordActivity(binding.root.context as Activity, value, true)
                if(!TextUtils.isEmpty(value)){
                    var intent = Intent(binding.root.context as Activity, ProductFilterListActivity::class.java)
                    intent.putExtra("type", Type.ProductListViewType.SEARCH)
                    intent.putExtra("search_word", value)
                    (binding.root.context as Activity).startActivityForResult(intent, Flag.RequestCode.BASE)
                }
            }
            binding.executePendingBindings()
        }
    }
}