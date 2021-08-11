package com.loftechs.sample.meet

import android.content.Context
import android.net.Uri
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.model.data.ProfileInfoEntity
import com.loftechs.sdk.meet.LTMeetListener

interface MeetContract {

    interface View : BaseContract.BaseView {

        fun onSwitchPublic(isPublic: Boolean)
        fun onSwitchIdentity(isAdmin: Boolean)
        fun finishView()
    }

    interface Presenter<T> : BaseContract.Presenter<T> {

        fun doStartPublicMeeting(context: Context, subject: String, listener: LTMeetListener)
        fun doJoinPublicMeeting(context: Context, meetID: String, listener: LTMeetListener)
        fun doStartPrivateMeeting(context: Context, subject: String, channelID: String, listener: LTMeetListener)
        fun doJoinPrivateMeeting(context: Context, meetID: String, channelID: String, listener: LTMeetListener)
    }
}