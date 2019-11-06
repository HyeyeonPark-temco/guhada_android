package io.temco.guhada.view.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.CardInterest
import io.temco.guhada.data.viewmodel.CardInterestViewModel
import io.temco.guhada.databinding.ActivityCardinterestBinding
import io.temco.guhada.databinding.ItemCardinterestBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 무이자 할부 카드 정보 리스트
 * @author Hyeyeon Park
 * @since 2019..11.06
 */
class CardInterestActivity : BindActivity<ActivityCardinterestBinding>() {
    private lateinit var mViewModel: CardInterestViewModel

    override fun getBaseTag(): String = CardInterestActivity::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.activity_cardinterest

    override fun getViewType(): Type.View = Type.View.CARD_INTEREST

    override fun init() {
        initHeader()
        initViewModel()
    }

    private fun initHeader() {
        mBinding.includeCardinterestHeader.title = getString(R.string.cardinterest_header)
        mBinding.includeCardinterestHeader.setOnClickCloseButton { finish() }
    }

    private fun initViewModel() {
        mViewModel = CardInterestViewModel()
        mViewModel.mCardInterest.observe(this, Observer {
            this@CardInterestActivity.mBinding.recyclerviewCardinterest.adapter = CardInterestAdapter().apply { this.mList = it }
        })
        mViewModel.getCardInterest()
    }

    inner class CardInterestAdapter : RecyclerView.Adapter<CardInterestAdapter.Holder>() {
        var mList = mutableListOf<CardInterest>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
                Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_cardinterest, parent, false))

        override fun getItemCount(): Int = mList.size

        override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(mList[position])

        inner class Holder(binding: ItemCardinterestBinding) : BaseViewHolder<ItemCardinterestBinding>(binding.root) {
            fun bind(card: CardInterest) {
                binding.card = card
                binding.executePendingBindings()
            }
        }
    }
}