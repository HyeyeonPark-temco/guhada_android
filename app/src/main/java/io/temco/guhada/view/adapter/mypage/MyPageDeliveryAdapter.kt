package io.temco.guhada.view.adapter.mypage

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
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
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.DeliveryButton
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.review.MyPageReviewContent
import io.temco.guhada.data.model.review.ReviewAvailableOrder
import io.temco.guhada.data.model.review.ReviewOrder
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.databinding.ItemDeliveryBinding
import io.temco.guhada.view.activity.*
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.holder.base.BaseViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]
        holder.bind(item)

        if (position > 0)
            holder.setDivider(item.purchaseId, list[position - 1].purchaseId)
    }

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
                EventBusData(requestCode = RequestCode.PRODUCT_DETAIL.flag, data = item.dealId).let { data ->
                    EventBusHelper.sendEvent(data)
                }
            }
            mBinding.buttonDeliveryClaim.setOnClickListener {
                CommonUtilKotlin.startActivityUserClaimSeller(itemView.context as AppCompatActivity, item.sellerId.toLong(), item.productId, item.orderProdGroupId)
            }

            val redirectDeliveryDetailListener = View.OnClickListener {
                val refundVisible = if (item.claimStatus.isNullOrEmpty()) false       // [주문배송] 항상 노출하지 않음
                else item.purchaseStatus != PurchaseStatus.WAITING_PAYMENT.status               // [취소교환반품] purchaseStatus가 '입금대기중'인 경우, 노출하지 않음

                redirectDeliveryDetailActivity(purchaseId = item.purchaseId, isDeliveryCer = false, orderProdGroupId = item.orderProdGroupId,
                        status = if (item.claimStatus.isNullOrEmpty()) item.purchaseStatusText else item.claimStatusText, orderClaimGroupId = item.orderClaimGroupId, refundVisible = refundVisible)
            }
            mBinding.imageviewDeliveryOrdernumber.setOnClickListener(redirectDeliveryDetailListener)
            mBinding.textviewDeliveryOrdernumber.setOnClickListener(redirectDeliveryDetailListener)

            mBinding.executePendingBindings()
        }

        fun setDivider(purchaseId: Long, prevPurchaseId: Long) {
            if (purchaseId == prevPurchaseId) {
                mBinding.constraintlayoutDeliveryTop.visibility = View.GONE
                mBinding.viewDeliveryTopline.setBackgroundResource(R.drawable.drawable_dash_line_eb)
                mBinding.viewDeliveryTopline.layoutParams.height = CommonViewUtil.convertDpToPixel(10, mBinding.root.context)
            }
        }

        private fun redirectDeliveryDetailActivity(purchaseId: Long, isDeliveryCer: Boolean, orderProdGroupId: Long, status: String, orderClaimGroupId: Long, refundVisible: Boolean = false) {
            val intent = Intent(binding.root.context, DeliveryDetailActivity::class.java)
            intent.putExtra("purchaseId", purchaseId)
            intent.putExtra("isDeliveryCer", isDeliveryCer)
            intent.putExtra("orderProdGroupId", orderProdGroupId)
            intent.putExtra("orderClaimGroupId", orderClaimGroupId)
            intent.putExtra("status", status)
            intent.putExtra("refundVisible", refundVisible)
            binding.root.context.startActivity(intent)
        }

        private fun redirectWriteClaimActivity(productId: Long) {
            val intent = Intent(binding.root.context, WriteClaimActivity::class.java)
            intent.putExtra("productId", productId)
            binding.root.context.startActivity(intent)
        }

        private fun withdrawRefund(orderClaimId: Long) =
                ServerCallbackUtil.callWithToken(task = { token ->
                    ClaimServer.withdrawExchange(OnServerListener { success, o ->
                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = { EventBusData(requestCode = RequestCode.WITHDRAW.flag, data = null).let { data -> EventBusHelper.sendEvent(data) } },
                                claimNotFoundTask = { ToastUtil.showMessage(it.message) })
                    }, accessToken = token, orderClaimId = orderClaimId)
                })

        private fun withdrawExchange(orderClaimId: Long) = ServerCallbackUtil.callWithToken(task = { token ->
            ClaimServer.withdrawRefund(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = { EventBusData(requestCode = RequestCode.WITHDRAW.flag, data = null).let { data -> EventBusHelper.sendEvent(data) } },
                        claimNotFoundTask = { ToastUtil.showMessage(it.message) })
            }, accessToken = token, orderClaimId = orderClaimId)
        })

        private fun redirectWriteReviewActivity(item: PurchaseOrder) {
            val review = ReviewAvailableOrder().apply {
                this.purchaseId = item.purchaseId
                this.productId = item.productId
                this.season = if (TextUtils.isEmpty(item.season)) "" else item.season
                this.brandName = item.brandName
                this.prodName = item.productName
                this.quantity = item.quantity
                this.orderPrice = item.orderPrice
                this.imageUrl = item.imageUrl
                this.orderProdGroupId = item.orderProdGroupId.toInt()
                this.sellerId = item.sellerId.toLong()
            }

            val intent = Intent(binding.root.context, ReviewWriteActivity::class.java)
            intent.putExtra("reviewData", review)
            (binding.root.context as AppCompatActivity).startActivityForResult(intent, RequestCode.DELIVERY.flag)
        }

        private fun redirectWriteReviewActivityForModify(item: PurchaseOrder) {
            if (item.reviewId != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    val reviewContent = getReviewAsync(item.productId, item.reviewId?.toLong()
                            ?: 0)
                    reviewContent.review.profileImageUrl = item.imageUrl
                    reviewContent.order = ReviewOrder().apply {
                        this.purchaseId = item.purchaseId
                        this.productId = item.productId
                        this.orderTimestamp = item.orderTimestamp
                        this.brandName = item.brandName ?: ""
                        this.season = item.season ?: ""
                        this.prodName = item.productName ?: ""
                        this.imageName = item.imageName ?: ""
                        this.imageUrl = item.imageUrl ?: ""
                        this.optionAttribute1 = item.optionAttribute1 ?: ""
                        this.optionAttribute2 = item.optionAttribute2 ?: ""
                        this.optionAttribute3 = item.optionAttribute3 ?: ""
                        this.quantity = item.quantity
                        this.discountPrice = item.discountPrice
                        this.originalPrice = item.originalPrice
                        this.orderPrice = item.orderPrice
                        this.shipPrice = item.shipPrice
                        this.sellerId = item.sellerId.toLong()
                        this.sellerName = item.sellerName ?: ""
                        this.purchaseStatus = item.purchaseStatus ?: ""
                        this.purchaseStatusText = item.purchaseStatusText ?: ""
                        this.statusMessage = item.statusMessage ?: ""
                        this.orderProdGroupId = item.orderProdGroupId.toInt()
                        this.purchaseConfirm = item.purchaseConfirm
                        //     this.expireTimestamp = item.expireTimestamp
                        //     this.shipCompleteTimestamp = item.shipCompleteTimestamp
                        this.reviewId = item.reviewId ?: 0
                        this.dealId = item.dealId
                    }
                    val intent = Intent(binding.root.context, ReviewWriteActivity::class.java)
                    intent.putExtra("reviewData", reviewContent)
                    (binding.root.context as Activity).startActivityForResult(intent, RequestCode.DELIVERY.flag)
                }
            }
        }

        private fun redirectShippingTrackingActivity(invoiceNo: String?, companyNo: String?) {
            val intent = Intent(mBinding.root.context, ShippingTrackingActivity::class.java)
            intent.putExtra("invoiceNo", invoiceNo)
            intent.putExtra("companyNo", companyNo)
            mBinding.root.context.startActivity(intent)
        }

        private suspend fun getReviewAsync(productId: Long, reviewId: Long): MyPageReviewContent = UserServer.getReviewAsync(productId, reviewId).await().data

        private fun getButtons(item: PurchaseOrder): MutableList<DeliveryButton> {
            val buttons = mutableListOf<DeliveryButton>()
            val status = if (item.claimStatus != null) item.claimStatus else item.purchaseStatus //if (type == Type.Delivery.type) item.purchaseStatus else item.claimStatus

            when (status) {
                PurchaseStatus.WAITING_PAYMENT.status,
                PurchaseStatus.COMPLETE_PAYMENT.status -> {
//                    buttons.add(DeliveryButton().apply {
//                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_orderinfo)
//                        task = View.OnClickListener {
//                            redirectDeliveryDetailActivity(item.purchaseId, false, item.orderProdGroupId,
//                                    if (item.claimStatus.isNullOrEmpty()) item.purchaseStatusText else item.claimStatusText, item.orderClaimGroupId)
//                        }
//                    })
//                    buttons.add(DeliveryButton().apply {
//                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_ordermodify)
//                    })
                    buttons.add(DeliveryButton().apply {
                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_ordercancel)
                        task = View.OnClickListener { requestCancelOrderTask(item) }
                    })
                    buttons.add(DeliveryButton().apply {
                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_changeshipping)
                        task = View.OnClickListener { editShippingAddressTask(item.purchaseId) }
                    })
                }
                PurchaseStatus.SELLER_IDENTIFIED.status,
                PurchaseStatus.RELEASE_PRODUCT.status -> {
//                    buttons.add(DeliveryButton().apply {
//                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_orderinfo)
//                        task = View.OnClickListener { redirectDeliveryDetailActivity(item.purchaseId, false, item.orderProdGroupId, if (item.claimStatus.isNullOrEmpty()) item.purchaseStatusText else item.claimStatusText, item.orderClaimGroupId) }
//                    })
                }

                PurchaseStatus.RESEND_EXCHANGE.status,
                PurchaseStatus.COMPLETE_EXCHANGE.status,
                PurchaseStatus.DELIVERING.status,
                PurchaseStatus.DELIVERED.status -> {
                    if (item.claimStatus == PurchaseStatus.RESEND_EXCHANGE.status || item.purchaseStatus == PurchaseStatus.COMPLETE_EXCHANGE.status || item.claimStatus == PurchaseStatus.COMPLETE_EXCHANGE.status)
                        buttons.add(DeliveryButton().apply {
                            text = mBinding.root.context.getString(R.string.mypage_delivery_button_reshippinginfo)
                            task = View.OnClickListener { redirectShippingTrackingActivity(invoiceNo = item.resendInvoiceNo, companyNo = item.resendShipCompany) }
                        })
                    else buttons.add(DeliveryButton().apply {
                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_shippinginfo)
                        task = View.OnClickListener { redirectShippingTrackingActivity(invoiceNo = item.invoiceNo, companyNo = item.shipCompany) }
                    })

                    if (item.purchaseConfirm) {
                        if (item.reviewId != null) buttons.add(DeliveryButton().apply {
                            text = mBinding.root.context.getString(R.string.mypage_delivery_button_reviewmodify)
                            task = View.OnClickListener {
                                redirectWriteReviewActivityForModify(item)
                            }
                        })
                        else buttons.add(DeliveryButton().apply {
                            text = mBinding.root.context.getString(R.string.mypage_delivery_button_reviewwrite)
                            task = View.OnClickListener { redirectWriteReviewActivity(item) }
                        })
                    } else {
                        buttons.add(DeliveryButton().apply {
                            text = mBinding.root.context.getString(R.string.mypage_delivery_button_confirmpurchase)
                            task = View.OnClickListener {
                                val intent = Intent(binding.root.context, ConfirmPurchaseActivity::class.java)
                                intent.putExtra("purchaseOrder", item)
                                (binding.root.context as AppCompatActivity).startActivityForResult(intent, RequestCode.DELIVERY.flag)
                            }
                        })
                        buttons.add(DeliveryButton().apply {
                            text = mBinding.root.context.getString(R.string.mypage_delivery_button_requestexchange)
                            task = View.OnClickListener {
                                val intent = Intent(binding.root.context, RequestExchangeActivity::class.java)
                                intent.putExtra("orderProdGroupId", item.orderProdGroupId)
                                (binding.root.context as AppCompatActivity).startActivityForResult(intent, RequestCode.DELIVERY.flag)
                            }
                        })
                        buttons.add(DeliveryButton().apply {
                            text = mBinding.root.context.getString(R.string.mypage_delivery_button_requestrefund)
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
                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_cancelinfo)
                        task = View.OnClickListener {
                            redirectDeliveryDetailActivity(purchaseId = item.purchaseId, isDeliveryCer = true, orderProdGroupId = item.orderProdGroupId,
                                    status = if (item.claimStatus.isNullOrEmpty()) item.purchaseStatusText else item.claimStatusText, orderClaimGroupId = item.orderClaimGroupId, refundVisible = true)
                        }
                    })
                    // buttons.add(DeliveryButton().apply { text = "취소철회" })
                }

                PurchaseStatus.REQUEST_RETURN.status -> {
                    buttons.add(DeliveryButton().apply {
                        // 반품 신청서 수정
                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_formmodify)
                        task = View.OnClickListener {
                            val intent = Intent(mBinding.root.context, RequestRefundActivity::class.java)
                            intent.putExtra("orderClaimId", item.orderClaimId)
                            mBinding.root.context.startActivity(intent)
                        }
                    })

                    buttons.add(DeliveryButton().apply {
                        // 반품 철회
                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_withdrawrefund)
                        task = View.OnClickListener {
                            CustomMessageDialog(message = binding.root.resources.getString(R.string.mypage_deliverycer_withdraw_refund), cancelButtonVisible = true,
                                    confirmTask = { withdrawExchange(item.orderClaimId) }).show(manager = (binding.root.context as AppCompatActivity).supportFragmentManager, tag = MyPageDeliveryAdapter::class.java.simpleName)
                        }
                    })
                }

                PurchaseStatus.REQUEST_EXCHANGE.status -> {
                    buttons.add(DeliveryButton().apply {
                        // 교환 신청서 수정
                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_formmodify)
                        task = View.OnClickListener {
                            val intent = Intent(mBinding.root.context, RequestExchangeActivity::class.java)
                            intent.putExtra("orderClaimId", item.orderClaimId)
                            mBinding.root.context.startActivity(intent)
                        }
                    })
                    buttons.add(DeliveryButton().apply {
                        // 교환 철회
                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_withdrawexchange)
                        task = View.OnClickListener {
                            CustomMessageDialog(message = binding.root.resources.getString(R.string.mypage_deliverycer_withdraw_exchange), cancelButtonVisible = true,
                                    confirmTask = { withdrawRefund(item.orderClaimId) }).show(manager = (binding.root.context as AppCompatActivity).supportFragmentManager, tag = MyPageDeliveryAdapter::class.java.simpleName)
                        }
                    })
                }

                PurchaseStatus.PICKING_EXCHANGE.status -> {
                    buttons.add(DeliveryButton().apply {
                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_exchangeinfo)
                        task = View.OnClickListener {
                            redirectDeliveryDetailActivity(purchaseId = item.purchaseId, isDeliveryCer = true, orderProdGroupId = item.orderProdGroupId,
                                    status = if (item.claimStatus.isNullOrEmpty()) item.purchaseStatusText else item.claimStatusText, orderClaimGroupId = item.orderClaimGroupId, refundVisible = false)
                        }
                    })
                    buttons.add(DeliveryButton().apply {
                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_withdrawexchange)
                        task = View.OnClickListener {
                            CustomMessageDialog(message = binding.root.resources.getString(R.string.mypage_deliverycer_withdraw_exchange), cancelButtonVisible = true,
                                    confirmTask = { withdrawRefund(item.orderClaimId) }).show(manager = (binding.root.context as AppCompatActivity).supportFragmentManager, tag = MyPageDeliveryAdapter::class.java.simpleName)
                        }
                    })
                }

                PurchaseStatus.PICKING_RETURN.status -> {
                    buttons.add(DeliveryButton().apply {
                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_refundinfo)
                        task = View.OnClickListener {
                            redirectDeliveryDetailActivity(purchaseId = item.purchaseId, isDeliveryCer = true, orderProdGroupId = item.orderProdGroupId,
                                    status = if (item.claimStatus.isNullOrEmpty()) item.purchaseStatusText else item.claimStatusText, orderClaimGroupId = item.orderClaimGroupId, refundVisible = true)
                        }
                    })
                    buttons.add(DeliveryButton().apply { text = mBinding.root.context.getString(R.string.mypage_delivery_button_withdrawrefund) })
                }

                PurchaseStatus.COMPLETE_CANCEL.status,
                PurchaseStatus.SALE_CANCEL.status -> {
                    buttons.add(DeliveryButton().apply {
                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_cancelinfo)
                        task = View.OnClickListener {
                            redirectDeliveryDetailActivity(purchaseId = item.purchaseId, isDeliveryCer = true, orderProdGroupId = item.orderProdGroupId,
                                    status = if (item.claimStatus.isNullOrEmpty()) item.purchaseStatusText else item.claimStatusText, orderClaimGroupId = item.orderClaimGroupId, refundVisible = true)
                        }
                    })
                }

                PurchaseStatus.COMPLETE_PICK_EXCHANGE.status -> {
                    buttons.add(DeliveryButton().apply {
                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_exchangeinfo)
                        task = View.OnClickListener {
                            redirectDeliveryDetailActivity(purchaseId = item.purchaseId, isDeliveryCer = true, orderProdGroupId = item.orderProdGroupId,
                                    status = if (item.claimStatus.isNullOrEmpty()) item.purchaseStatusText else item.claimStatusText, orderClaimGroupId = item.orderClaimGroupId, refundVisible = false)
                        }
                    })
                }

                PurchaseStatus.COMPLETE_RETURN.status,
                PurchaseStatus.COMPLETE_PICK_RETURN.status -> {
                    buttons.add(DeliveryButton().apply {
                        text = mBinding.root.context.getString(R.string.mypage_delivery_button_refundinfo)
                        task = View.OnClickListener {
                            redirectDeliveryDetailActivity(purchaseId = item.purchaseId, isDeliveryCer = true, orderProdGroupId = item.orderProdGroupId,
                                    status = if (item.claimStatus.isNullOrEmpty()) item.purchaseStatusText else item.claimStatusText, orderClaimGroupId = item.orderClaimGroupId, refundVisible = true)
                        }
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
