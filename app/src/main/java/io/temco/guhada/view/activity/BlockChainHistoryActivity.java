package io.temco.guhada.view.activity;

import android.view.View;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.databinding.ActivityBlockchainHistoryBinding;
import io.temco.guhada.view.activity.base.BindActivity;

public class BlockChainHistoryActivity extends BindActivity<ActivityBlockchainHistoryBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    ////////////////////////////////////////////////
}
