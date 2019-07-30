package io.temco.guhada.view.adapter.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.enum.PurchaseStatus
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.databinding.ItemDeliveryBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class MyPageDeliveryAdapter : RecyclerView.Adapter<MyPageDeliveryAdapter.Holder>() {
    var list: MutableList<PurchaseOrder> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_delivery, parent, false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    inner class Holder(binding: ItemDeliveryBinding) : BaseViewHolder<ItemDeliveryBinding>(binding.root) {
        fun bind(item: PurchaseOrder) {
            mBinding.item = item

            // space
            //  val pixels = mBinding.root.context.resources.getDimensionPixelSize(R.dimen.margin_grid_space)
            //  mBinding.recyclerviewDeliveryButton.addItemDecoration(GridItemSpaceDecoration(pixels))

            val buttons = mutableListOf<DeliveryButton>()
            when (item.purchaseStatus) {
                PurchaseStatus.WAITING_PAYMENT.status,
                PurchaseStatus.COMPLETE_PAYMENT.status -> {
                    buttons.add(DeliveryButton().apply { text = "주문내역" })
                    buttons.add(DeliveryButton().apply { text = "주문수정" })
                    buttons.add(DeliveryButton().apply { text = "주문취소" })
                }

                PurchaseStatus.SELLER_IDENTIFIED.status,
                PurchaseStatus.RELEASE_PRODUCT.status -> {
                    buttons.add(DeliveryButton().apply { text = "주문내역" })
                }

                PurchaseStatus.RESEND_EXCHANGE.status,
                PurchaseStatus.COMPLETE_EXCHANGE.status,
                PurchaseStatus.DELIVERING.status,
                PurchaseStatus.DELIVERED.status -> {
                    if (item.purchaseStatus == PurchaseStatus.RESEND_EXCHANGE.status || item.purchaseStatus == PurchaseStatus.COMPLETE_EXCHANGE.status)
                        buttons.add(DeliveryButton().apply { text = "재배송조회" })
                    else buttons.add(DeliveryButton().apply { text = "배송조회" })

                    if (item.purchaseConfirm) {
                        if (item.reviewId != null) buttons.add(DeliveryButton().apply { text = "리뷰수정" })
                        else buttons.add(DeliveryButton().apply { text = "리뷰작성" })
                    } else {
                        buttons.add(DeliveryButton().apply { text = "구매확정" })
                        buttons.add(DeliveryButton().apply { text = "교환신청" })
                        buttons.add(DeliveryButton().apply { text = "반품신청" })
                    }
                }

                PurchaseStatus.REQUEST_CANCEL.status -> {
                    buttons.add(DeliveryButton().apply { text = "취소정보" })
                    buttons.add(DeliveryButton().apply { text = "취소철회" })
                }

                PurchaseStatus.REQUEST_RETURN.status -> {
                    buttons.add(DeliveryButton().apply { text = "신청서수정" })
                    buttons.add(DeliveryButton().apply { text = "반품철회" })
                }

                PurchaseStatus.REQUEST_EXCHANGE.status -> {
                    buttons.add(DeliveryButton().apply { text = "신청서수정" })
                    buttons.add(DeliveryButton().apply { text = "교환철회" })
                }

                PurchaseStatus.PICKING_EXCHANGE.status -> {
                    buttons.add(DeliveryButton().apply { text = "교환정보" })
                    buttons.add(DeliveryButton().apply { text = "교환철회" })
                }

                PurchaseStatus.PICKING_RETURN.status -> {
                    buttons.add(DeliveryButton().apply { text = "반품정보" })
                    buttons.add(DeliveryButton().apply { text = "반품철회" })
                }

                PurchaseStatus.COMPLETE_CANCEL.status,
                PurchaseStatus.SALE_CANCEL.status -> {
                    buttons.add(DeliveryButton().apply { text = "취소정보" })
                }

                PurchaseStatus.COMPLETE_PICK_EXCHANGE.status -> {
                    buttons.add(DeliveryButton().apply { text = "교환정보" })
                }

                PurchaseStatus.COMPLETE_RETURN.status,
                PurchaseStatus.COMPLETE_PICK_RETURN.status -> {
                    buttons.add(DeliveryButton().apply { text = "반품정보" })
                }

                PurchaseStatus.WITHDRAW_EXCHANGE.status,
                PurchaseStatus.WITHDRAW_RETURN.status -> { /* NONE */
                }
            }

            mBinding.recyclerviewDeliveryButton.adapter = MyPageDeliveryButtonAdapter().apply { this.list = buttons }
            (mBinding.recyclerviewDeliveryButton.layoutManager as GridLayoutManager).spanCount = if (buttons.size > 1) 2 else 1
            mBinding.executePendingBindings()
        }

    }

    inner class DeliveryButton {
        var text = ""
        var onClickListener: View.OnClickListener = View.OnClickListener { }
    }
}