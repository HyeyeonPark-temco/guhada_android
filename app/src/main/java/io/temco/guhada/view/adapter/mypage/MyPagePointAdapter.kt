package io.temco.guhada.view.adapter.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.PointStatus
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.data.model.point.PointHistory
import io.temco.guhada.data.viewmodel.mypage.MyPagePointViewModel
import io.temco.guhada.databinding.ItemMypagePointBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class MyPagePointAdapter(val mViewModel: MyPagePointViewModel) : RecyclerView.Adapter<MyPagePointAdapter.Holder>() {
    private var list = mutableListOf<PointHistory.PointHistoryContent>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_mypage_point, parent, false)).apply {
        this.binding.imagebuttonMypagepointDelete.setOnClickListener {
            val item = list[this@apply.adapterPosition]
            mViewModel.onClickDelete(OnServerListener { success, o ->
                this@MyPagePointAdapter.list.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                mViewModel.mEmptyVisible.set(this@MyPagePointAdapter.list.isEmpty())
            }, item.id)
        }
    }

    override fun getItemCount(): Int = this.list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setItems(items: MutableList<PointHistory.PointHistoryContent>) {
        this.list = items
        notifyDataSetChanged()
    }

    fun addAllItems(items: MutableList<PointHistory.PointHistoryContent>) {
        val startPos = list.size
        this.list.addAll(items)
        notifyItemRangeChanged(startPos, items.size)
    }

    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemMypagePointBinding) : BaseViewHolder<ItemMypagePointBinding>(binding.root) {
        fun bind(item: PointHistory.PointHistoryContent) {
            mBinding.item = item
            mBinding.colorRes = when (item.status) {
                PointStatus.CONSUMPTION_CANCEL.status,
                PointStatus.SAVED.status,
                PointStatus.RESTORE.status -> BaseApplication.getInstance().resources.getColor(R.color.common_blue_purple)

                PointStatus.CONSUMPTION.status,
                PointStatus.SAVED_CANCEL.status,
                PointStatus.CONSUMPTION_CANCEL.status,
                PointStatus.RESTORE_CANCEL.status,
                PointStatus.EXPIRED_CANCEL.status,
                PointStatus.EXPIRED.status -> BaseApplication.getInstance().resources.getColor(R.color.brick)

                PointStatus.DUE_CONSUMPTION_CANCEL.status,
                PointStatus.DUE_CONSUMPTION.status,
                PointStatus.DUE_SAVE_CANCEL.status,
                PointStatus.DUE_SAVE.status -> BaseApplication.getInstance().resources.getColor(R.color.warm_grey) // 적립 예정
                else -> BaseApplication.getInstance().resources.getColor(R.color.perrywinkle)
            }

            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }
}