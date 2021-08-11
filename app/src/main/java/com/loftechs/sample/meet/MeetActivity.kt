package com.loftechs.sample.meet

import android.os.Bundle
import com.loftechs.sample.R
import com.loftechs.sample.base.BaseActivity
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate


class MeetActivity : BaseActivity() {

    private lateinit var fragment: MeetFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            fragment = MeetFragment.newInstance

            /*
            // When FCM got incoming video call Intent
            val receiverID = intent.getStringExtra(IntentKey.EXTRA_RECEIVER_ID)
            val callUserID = intent.getStringExtra(IntentKey.EXTRA_CALL_USER_ID)
            val callState = intent.getIntExtra(IntentKey.EXTRA_CALL_STATE_TYPE, CallState.NONE.ordinal)
            fragment.arguments = Bundle().apply {
                putString(IntentKey.EXTRA_RECEIVER_ID, receiverID)
                putString(IntentKey.EXTRA_CALL_USER_ID, callUserID)
                putInt(IntentKey.EXTRA_CALL_STATE_TYPE, callState)
            }*/

            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commitNow()
        }
    }
}