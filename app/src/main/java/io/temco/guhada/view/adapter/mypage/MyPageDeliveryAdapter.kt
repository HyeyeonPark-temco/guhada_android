package io.temco.guhada.view.adapter.mypage

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.EventBusData
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.enum.PurchaseStatus
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.data.model.DeliveryButton
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.review.ReviewAvailableOrder
import io.temco.guhada.databinding.ItemDeliveryBinding
import io.temco.guhada.view.activity.*
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 마이페이지 주문배송, 취소교환반품 리스트 adapter
 * @author Hyeyeon Park
 */
class MyPageDeliveryAdapter : RecyclerView.Adapter<MyPageDeliveryAdapter.Holder>() {
    var type = 1
    var list: MutableList<PurchaseOrder> = mutableListOf()
    var editShippingAddressTask: (purchaseId: Long) -> Unit = {}
    var requestCancelOrderTask: (purchaseOrder: PurchaseOrder) -> Unit = {}

    enum class Type(val type: Int) {
        Delivery(1),
        DeliveryCer(2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_delivery, parent, false))
    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(list[position])

    fun setItems(list: MutableList<PurchaseOrder>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemDeliveryBinding) : BaseViewHolder<ItemDeliveryBinding>(binding.root) {
        fun bind(item: PurchaseOrder) {
            mBinding.item = item

            // set buttons
            val buttons = getButtons(item)
            mBinding.recyclerviewDeliveryButton.adapter = MyPageDeliveryButtonAdapter().apply { this.list = buttons }
            (mBinding.recyclerviewDeliveryButton.layoutManager as GridLayoutManager).spanCount = if (buttons.size > 1) 2 else 1

            // image click listener
            mBinding.imageviewDeliveryProfile.setOnClickListener {
                val data = EventBusData(requestCode = RequestCode.PRODUCT_DETAIL.flag, data = item.dealId)
                EventBusHelper.sendEvent(data)
            }
            mBinding.imageviewDeliveryOrdernumber.setOnClickListener { redirectDeliveryDetailActivity(item.purchaseId, false, item.orderProdGroupId) }
            mBinding.textviewDeliveryOrdernumber.setOnClickListener { redirectDeliveryDetailActivity(item.purchaseId, false, item.orderProdGroupId) }
            mBinding.buttonDeliveryClaim.setOnClickListener { redirectWriteClaimActivity(item.productId) }

            mBinding.executePendingBindings()
        }

        private fun redirectDeliveryDetailActivity(purchaseId: Long, isDeliveryCer: Boolean, orderProdGroupId: Long) {
            val intent = Intent(binding.root.context, DeliveryDetailActivity::class.java)
            intent.putExtra("purchaseId", purchaseId)
            intent.putExtra("isDeliveryCer", isDeliveryCer)
            intent.putExtra("orderProdGroupId", orderProdGroupId)
            binding.root.context.startActivity(intent)
        }

        private fun redirectWriteClaimActivity(productId: Long) {
            val intent = Intent(binding.root.context, WriteClaimActivity::class.java)
            intent.putExtra("productId", productId)
            binding.root.context.startActivity(intent)
        }

        private fun getButtons(item: PurchaseOrder): MutableList<DeliveryButton> {
            val buttons = mutableListOf<DeliveryButton>()
            val status = if (type == Type.Delivery.type) item.purchaseStatus else item.claimStatus
            when (status) {
                PurchaseStatus.WAITING_PAYMENT.status,
                PurchaseStatus.COMPLETE_PAYMENT.status -> {
                    buttons.add(DeliveryButton().apply {
                        text = "주문내역"
                        task = View.OnClickListener { redirectDeliveryDetailActivity(item.purchaseId, false, item.orderProdGroupId) }
                    })
                    buttons.add(DeliveryButton().apply {
                        text = "주문수정"
                    })
                    buttons.add(DeliveryButton().apply {
                        text = "주문취소"
                        task = View.OnClickListener { requestCancelOrderTask(item) }
                    })
                    buttons.add(DeliveryButton().apply {
                        text = "배송지변경"
                        task = View.OnClickListener { editShippingAddressTask(item.purchaseId) }
                    })
                }

                PurchaseStatus.SELLER_IDENTIFIED.status,
                PurchaseStatus.RELEASE_PRODUCT.status -> {
                    buttons.add(DeliveryButton().apply {
                        text = "주문내역"
                        task = View.OnClickListener { redirectDeliveryDetailActivity(item.purchaseId, false, item.orderProdGroupId) }
                    })
                }

                PurchaseStatus.RESEND_EXCHANGE.status,
                PurchaseStatus.COMPLETE_EXCHANGE.status,
                PurchaseStatus.DELIVERING.status,
                PurchaseStatus.DELIVERED.status -> {
                    if (item.purchaseStatus == PurchaseStatus.RESEND_EXCHANGE.status || item.purchaseStatus == PurchaseStatus.COMPLETE_EXCHANGE.status)
                        buttons.add(DeliveryButton().apply { text = "재배송조회" })
                    else buttons.add(DeliveryButton().apply { text = "배송조회" })

                    if (item.purchaseConfirm) {
                        if (item.reviewId != null) buttons.add(DeliveryButton().apply {
                            text = "리뷰수정"
                            task = View.OnClickListener {
                                val intent = Intent(binding.root.context, ReviewWriteActivity::class.java)
                                (binding.root.context as AppCompatActivity).startActivityForResult(intent, RequestCode.DELIVERY.flag)
                            }
                        })
                        else buttons.add(DeliveryButton().apply {
                            text = "리뷰작성"
                            task = View.OnClickListener {
                                val review = ReviewAvailableOrder().apply {
                                    this.purchaseId = item.purchaseId
                                    this.productId = item.productId
                                    this.season = item.season
                                    this.brandName = item.brandName
                                    this.prodName = item.productName
                                    this.quantity = item.quantity
                                    this.orderPrice = item.orderPrice
                                    this.imageUrl = item.imageUrl
                                }

                                val intent = Intent(binding.root.context, ReviewWriteActivity::class.java)
                                intent.putExtra("reviewData", review)
                                (binding.root.context as AppCompatActivity).startActivityForResult(intent, RequestCode.DELIVERY.flag)
                            }
                        })
                    } else {
                        buttons.add(DeliveryButton().apply {
                            text = "구매확정"
                            task = View.OnClickListener {
                                val intent = Intent(binding.root.context, ConfirmPurchaseActivity::class.java)
                                intent.putExtra("purchaseOrder", item)
                                (binding.root.context as AppCompatActivity).startActivityForResult(intent, RequestCode.DELIVERY.flag)
                            }
                        })
                        buttons.add(DeliveryButton().apply {
                            text = "교환신청"
                            task = View.OnClickListener {
                                val intent = Intent(binding.root.context, RequestExchangeActivity::class.java)
                                intent.putExtra("orderProdGroupId", item.orderProdGroupId)
                                (binding.root.context as AppCompatActivity).startActivityForResult(intent, RequestCode.DELIVERY.flag)
                            }
                        })
                        buttons.add(DeliveryButton().apply {
                            text = "반품신청"
                            task = View.OnClickListener {
                                val intent = Intent(binding.root.context, RequestRefundActivity::class.java)
                                intent.putExtra("orderProdGroupId", item.orderProdGroupId)
                                (binding.root.context as AppCompatActivity).startActivityForResult(intent, RequestCode.DELIVERY.flag)
                            }
                        })
                    }
                }

                PurchaseStatus.REQUEST_CANCEL.status -> {
                    buttons.add(DeliveryButton().apply {
                        text = "취소정보"
                        task = View.OnClickListener { redirectDeliveryDetailActivity(item.purchaseId, true, item.orderProdGroupId) }
                    })
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
                    buttons.add(DeliveryButton().apply {
                        text = "교환정보"
                        task = View.OnClickListener { redirectDeliveryDetailActivity(item.purchaseId, true, item.orderProdGroupId) }
                    })
                    buttons.add(DeliveryButton().apply { text = "교환철회" })
                }

                PurchaseStatus.PICKING_RETURN.status -> {
                    buttons.add(DeliveryButton().apply {
                        text = "반품정보"
                        task = View.OnClickListener { redirectDeliveryDetailActivity(item.purchaseId, true, item.orderProdGroupId) }
                    })
                    buttons.add(DeliveryButton().apply { text = "반품철회" })
                }

                PurchaseStatus.COMPLETE_CANCEL.status,
                PurchaseStatus.SALE_CANCEL.status -> {
                    buttons.add(DeliveryButton().apply {
                        text = "취소정보"
                        task = View.OnClickListener { redirectDeliveryDetailActivity(item.purchaseId, true, item.orderProdGroupId) }
                    })
                }

                PurchaseStatus.COMPLETE_PICK_EXCHANGE.status -> {
                    buttons.add(DeliveryButton().apply {
                        text = "교환정보"
                        task = View.OnClickListener { redirectDeliveryDetailActivity(item.purchaseId, true, item.orderProdGroupId) }
                    })
                }

                PurchaseStatus.COMPLETE_RETURN.status,
                PurchaseStatus.COMPLETE_PICK_RETURN.status -> {
                    buttons.add(DeliveryButton().apply {
                        text = "반품정보"
                        task = View.OnClickListener { redirectDeliveryDetailActivity(item.purchaseId, true, item.orderProdGroupId) }
                    })
                }

                PurchaseStatus.WITHDRAW_EXCHANGE.status,
                PurchaseStatus.WITHDRAW_RETURN.status -> {
                    /* NONE */
                }
            }
            return buttons
        }
    }

}
