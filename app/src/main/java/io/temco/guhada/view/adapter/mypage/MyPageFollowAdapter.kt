package io.temco.guhada.view.adapter.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.BookMark
import io.temco.guhada.data.model.seller.Seller
import io.temco.guhada.databinding.ItemMypageFollowBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 마이페이지 팔로우한 스토어 어댑터
 * @author Hyeyeon Park
 * @since 2019.08.26
 */
class MyPageFollowAdapter : RecyclerView.Adapter<MyPageFollowAdapter.Holder>() {
    var mList = mutableListOf<Seller>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_mypage_follow, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    fun setItems(list : MutableList<Seller>){
        this@MyPageFollowAdapter.mList = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemMypageFollowBinding) : BaseViewHolder<ItemMypageFollowBinding>(binding.root) {
        fun bind(seller: Seller) {
            mBinding.seller = seller
            mBinding.executePendingBindings()
        }
    }
}