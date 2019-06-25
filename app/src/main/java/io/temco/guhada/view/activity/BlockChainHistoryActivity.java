package io.temco.guhada.view.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.util.TextUtil;
import io.temco.guhada.data.model.BlockChain;
import io.temco.guhada.data.model.ProductByList;
import io.temco.guhada.data.server.BlockChainServer;
import io.temco.guhada.databinding.ActivityBlockchainHistoryBinding;
import io.temco.guhada.view.activity.base.BindActivity;
import io.temco.guhada.view.adapter.BlockChainListAdapter;

public class BlockChainHistoryActivity extends BindActivity<ActivityBlockchainHistoryBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private RequestManager mRequestManager;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return BlockChainHistoryActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_blockchain_history;
    }

    @Override
    protected Type.View getViewType() {
        return Type.View.BLOCKCHAIN;
    }

    @Override
    protected void init() {
        mBinding.setClickListener(this);
        mRequestManager = Glide.with(this);

        // Data
        if (getIntent() != null) {
            initInfomation((ProductByList) getIntent().getSerializableExtra(Info.INTENT_PRODUCT_DATA));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_close:
                finish();
                break;
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

//    public static void startActivity(Context context, int id) {
//        Intent i = new Intent(context, BlockChainHistoryActivity.class);
//        i.putExtra(Info.INTENT_PRODUCT_ID, id);
//        context.startActivity(i);
//    }

    public static void startActivity(Context context, ProductByList data) {
        Intent i = new Intent(context, BlockChainHistoryActivity.class);
        i.putExtra(Info.INTENT_PRODUCT_DATA, data);
        context.startActivity(i);
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void initInfomation(ProductByList product) {
        if (product != null) {
            // List
            getBlockChainData(product.productId);

            // Brand
            mBinding.textBrand.setText(product.brandName);

            // Product
            mBinding.textProduct.setText(product.productName);

            // Option
            // mBinding.textOption.setText(mProduct.options.);

            // Price
            mBinding.textPrice.setText(String.format(getString(R.string.product_price), TextUtil.getDecimalFormat(product.discountPrice)));
        }
    }

    private String createOptions(String... options) {
        if (options != null && options.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : options) {
                if (TextUtils.isEmpty(s)) continue;
                if (sb.length() > 0) {
                    sb.append(", ").append(s);
                } else {
                    sb.append(s);
                }
            }
            return sb.toString();
        }
        return null;
    }

    private void initList(List<BlockChain> data) {
        // Adapter
        BlockChainListAdapter adapter = new BlockChainListAdapter(this);
        adapter.setItems(data);

        // List
        mBinding.listContents.setHasFixedSize(true);
        mBinding.listContents.setLayoutManager(new LinearLayoutManager(this));
        mBinding.listContents.setAdapter(adapter);
    }

    ////////////////////////////////////////////////
    // SERVER
    ////////////////////////////////////////////////

    /**
     * 상품의 블록채인 정보 가져오기 API
     * 페이지 하단부
     * @param id product id
     */
    private void getBlockChainData(int id) {
        BlockChainServer.getTransactionData(id, (success, o) -> {
            if (success) {
                initList((List<BlockChain>) o);
            } else {

            }
        });
    }

    ////////////////////////////////////////////////
}