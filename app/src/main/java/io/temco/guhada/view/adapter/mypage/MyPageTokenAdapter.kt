package io.temco.guhada.view.adapter.mypage

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.data.model.blockchain.TokenList
import io.temco.guhada.databinding.ItemMypageTokenBinding
import io.temco.guhada.view.activity.TokenHistoryActivity
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 마이페이지-토큰 리스트 Adapter
 * @author Hyeyeon Park
 * @since 2019.11.27
 */
class MyPageTokenAdapter : RecyclerView.Adapter<MyPageTokenAdapter.Holder>() {
    var mList = listOf<TokenList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_mypage_token, parent, false)).apply {
                this.binding.linearlayoutTokenContainer.setOnClickListener {
                    val context = this.binding.root.context
                    val intent = Intent(context, TokenHistoryActivity::class.java)
                    intent.putExtra("token", mList[this@apply.adapterPosition])
                    context.startActivity(intent)
                }
                this.binding.buttonTokenDeposit.setOnClickListener { CommonUtilKotlin.moveGuhadaTokenAddress(this.binding.root.context as Activity, mList[this@apply.adapterPosition].tokenName) }
            }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(mList[position])

    inner class Holder(binding: ItemMypageTokenBinding) : BaseViewHolder<ItemMypageTokenBinding>(binding.root) {
        fun bind(token: TokenList) {
            mBinding.token = token
            mBinding.executePendingBindings()
        }
    }
}