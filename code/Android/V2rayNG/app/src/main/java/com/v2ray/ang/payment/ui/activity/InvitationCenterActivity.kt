package com.v2ray.ang.payment.ui.activity

import android.os.Bundle
import com.v2ray.ang.R
import com.v2ray.ang.ui.BaseActivity

class InvitationCenterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWithToolbar(
            R.layout.activity_invitation_center,
            showHomeAsUp = true,
            title = getString(R.string.title_invitation_center)
        )
    }
}
