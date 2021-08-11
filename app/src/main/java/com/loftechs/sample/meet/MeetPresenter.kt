package com.loftechs.sample.meet

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.loftechs.sample.BuildConfig
import com.loftechs.sample.model.AccountHelper
import com.loftechs.sdk.meet.*
import com.loftechs.sdk.user.UserEntity
import com.loftechs.sdk.utils.LTLog
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class MeetPresenter : MeetContract.Presenter<MeetContract.View> {

    private val TAG = MeetPresenter::class.java.simpleName
    private var mView: MeetContract.View? = null
    private lateinit var meetSDK:LTMeetSDK

    private val mDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun create() {
        meetSDK = LTMeetSDK.getInstance()
    }

    override fun resume() {
    }

    override fun pause() {
        mDisposable.clear()
    }

    override fun destroy() {
        if (!mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }

    override fun bindView(view: MeetContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun initBundle(arguments: Bundle) {

    }

    override fun doStartPublicMeeting(context: Context, subject: String, listener: LTMeetListener ) {

            // Public Meeting
            val meetInfo = LTMeetInfo(
                    subject, System.currentTimeMillis(), 3600
            )
            meetInfo.lang = LTMeetInfo.LANG.ZH_TW
            val initParams = LTMeetSDKInitParam(
                    BuildConfig.License_Key,
                    BuildConfig.Auth_API,
                    BuildConfig.Public_Meet_Domain,  //public
                    "no use",
                    "no use"
            )
            meetSDK.initialize(null)

            //init SDK and create meeting
            meetSDK.createMeeting(context, initParams, meetInfo, listener).subscribe(handleMeetSDK(meetInfo))

    }

    override fun doJoinPublicMeeting(context: Context, meetID: String, listener: LTMeetListener) {

        // Public Meeting
        val meetInfo = LTMeetInfo(
                "no use", System.currentTimeMillis(), 3600
        )
        meetInfo.lang = LTMeetInfo.LANG.ZH_TW
        val initParams = LTMeetSDKInitParam(
                BuildConfig.License_Key,
                BuildConfig.Auth_API,
                BuildConfig.Public_Meet_Domain,  //public
                "no use",
                "no use"
        )
        meetSDK.initialize(null)

        //init SDK and enter meeting, subject/duration/memberOnly param will be ignored
        meetInfo.meetID = meetID
        meetSDK.enterMeeting(context, initParams, meetInfo, listener).subscribe(handleMeetSDK(meetInfo))

    }

    override fun doStartPrivateMeeting(context: Context, subject: String, channelID: String, listener: LTMeetListener) {

            // Private Meeting

            // Only admin can decide subject/start time/duration parameters etc, participants not
            // Private Meeting
            val meetInfo = LTMeetPrivate(
                    subject, System.currentTimeMillis(), 3600
            )
            meetInfo.lang = LTMeetInfo.LANG.ZH_TW
            meetInfo.memberOnly = true
            meetInfo.chID = channelID
            meetInfo.customAttr = 0

            val initParams = LTMeetSDKInitParam(
                    BuildConfig.License_Key,
                    BuildConfig.Auth_API,
                    BuildConfig.Private_Meet_Domain,  //private
                    BuildConfig.IM_API,
                    AccountHelper.firstAccount?.account
            )

            val user = UserEntity()
            user.userID = AccountHelper.firstAccount?.userID
            user.uuid = AccountHelper.firstAccount?.uuid

            meetSDK.initialize(user)

            //init SDK and create meeting
            meetSDK.createMeeting(context, initParams, meetInfo, listener).subscribe(handleMeetSDK(meetInfo))

    }

    override fun doJoinPrivateMeeting(context: Context, meetID: String, channelID: String, listener: LTMeetListener) {

        // Private Meeting

        // Only admin can decide subject/start time/duration parameters etc, participants not
        // Private Meeting
        val meetInfo = LTMeetPrivate(
                "no use", System.currentTimeMillis(), 3600
        )
        meetInfo.lang = LTMeetInfo.LANG.ZH_TW
        meetInfo.memberOnly = true
        meetInfo.chID = channelID
        meetInfo.customAttr = 0

        val initParams = LTMeetSDKInitParam(
                BuildConfig.License_Key,
                BuildConfig.Auth_API,
                BuildConfig.Private_Meet_Domain,  //private
                BuildConfig.IM_API,
                AccountHelper.firstAccount?.account
        )

        val user = UserEntity()
        user.userID = AccountHelper.firstAccount?.userID
        user.uuid = AccountHelper.firstAccount?.uuid

        meetSDK.initialize(user)

        //init SDK and enter meeting, subject/duration/memberOnly param will be ignored
        meetInfo.meetID = meetID
        meetSDK.enterMeeting(context, initParams, meetInfo, listener).subscribe(handleMeetSDK(meetInfo))

    }

    private fun handleMeetSDK(meetInfo: LTMeetInfo): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                LTLog.d(TAG, "subscribed")
            }

            override fun onNext(meetID: String) {
                meetInfo.meetID = meetID
                LTLog.d(TAG, "onNext got meetID = $meetID")
            }

            override fun onError(e: Throwable) {
                LTLog.e(TAG, e.message)
            }

            override fun onComplete() {
                LTLog.d(TAG, "completed.")
            }
        }
    }
}