package io.temco.guhada.view.fragment.event

import io.temco.guhada.R
import io.temco.guhada.databinding.FragmentLuckyeventBinding
import io.temco.guhada.view.fragment.base.BaseFragment

class LuckyEventFragment : BaseFragment<FragmentLuckyeventBinding>() {

    // -------- LOCAL VALUE --------

    // -----------------------------


    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String {
        return LuckyEventFragment::class.java!!.getSimpleName()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_luckyevent
    }

    override fun init() {


    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////


    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////


}