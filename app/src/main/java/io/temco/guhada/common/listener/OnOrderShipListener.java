package io.temco.guhada.common.listener;

import io.temco.guhada.data.model.MyOrderItem;
import io.temco.guhada.view.adapter.mypage.OrderShipListAdapter;

public interface OnOrderShipListener {

    void onEvent();

    // void onReview(OrderShipListAdapter adapter, int position, MyOrderItem data);

    void onReward(OrderShipListAdapter adapter, int position, MyOrderItem data, String message);
}