package io.temco.guhada.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.common.util.DateUtil
import io.temco.guhada.data.model.community.CommunityTempInfo
import io.temco.guhada.databinding.ItemTempbbsinfoListBinding
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.holder.base.BaseViewHolder
import java.util.*

class TempBbsListAdapter : RecyclerView.Adapter<TempBbsListAdapter.Holder>() {
    var mList: MutableList<CommunityTempInfo> = arrayListOf()

    var mClickSelectItemListener : OnClickSelectItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_tempbbsinfo_list, parent, false))

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position],position)
    }

    fun setItems(list: MutableList<CommunityTempInfo>) {
        this.mList = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemTempbbsinfoListBinding) : BaseViewHolder<ItemTempbbsinfoListBinding>(binding.root) {
        fun bind(item: CommunityTempInfo, position: Int) {
            mBinding.title = item.title
            var date = Calendar.getInstance()
            date.timeInMillis = item.createdTimestamp!!
            mBinding.textviewTempbbsinfoDate.text = DateUtil.getCalendarToString(Type.DateFormat.TYPE_4, date)
            mBinding.setOnClickItem {
                mClickSelectItemListener?.clickSelectItemListener(0, position, item)
            }
            mBinding.setOnClickTempDelete {
                CustomMessageDialog(message = "삭제하시겠습니까?", cancelButtonVisible = true,
                        confirmTask = {
                            mClickSelectItemListener?.clickSelectItemListener(1, position, item)
                        }).show(manager = (itemView.context as AppCompatActivity).supportFragmentManager, tag = "TempBbsListActivity")
            }
            mBinding.executePendingBindings()
        }

    }
}