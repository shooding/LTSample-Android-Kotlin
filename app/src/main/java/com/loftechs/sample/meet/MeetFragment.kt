package com.loftechs.sample.meet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.loftechs.sample.R
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.model.AccountHelper
import com.loftechs.sdk.meet.LTMeetErrorInfo
import com.loftechs.sdk.meet.LTMeetListener
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate

class MeetFragment : AbstractFragment(), MeetContract.View,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        LTMeetListener
{

    private var mPresenter: MeetContract.Presenter<MeetContract.View>? = null

    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mMainfab: FloatingActionButton

    private lateinit var mNameEditText: EditText
    private lateinit var mMeetIDEditText: EditText
    private lateinit var mMeetIDTextView: TextView
    private lateinit var mEnterMeetingBtn: Button
    private lateinit var mSwitch: Switch
    private lateinit var mSwitchPublic: Switch
    private lateinit var mIdentityView: TextView
    private lateinit var mIsPublicTextView: TextView
    private lateinit var mCallUserIDTextView: TextView
    private lateinit var mCallUserIDEditText: EditText

    private val subject = "this is a demo"
    private var bIsPublic: Boolean = true
    private var bIsAdmin: Boolean = true
    private var bIsMemberOnly: Boolean = false

    companion object {
        val newInstance
            get() = MeetFragment()

        private val TAG = MeetFragment::class.java.simpleName
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.activity_meet, container, false)
        initView(root)
        return root
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: MeetPresenter().apply {
            mPresenter = this
        }
    }

    override fun clearPresenter() {
        mPresenter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter?.bindView(this)
        initView(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.unbindView()


    }

    private fun initView(root: View) {

        // TODO Fix this. Before attached to activity_main, top_bar and main_fab is still null
        var parentView = parentFragment?.view
        if(parentView != null){
            mToolbar = parentView.findViewById(R.id.top_bar)
            mToolbar.setNavigationOnClickListener {
                finishView()
            }

            mMainfab = parentView.findViewById(R.id.main_fab)
            mMainfab.visibility = View.GONE
        }

        mIsPublicTextView = root.findViewById(R.id.ispublic_text_view)
        mSwitchPublic = root.findViewById(R.id.id_ispublic_switch)
        mSwitch = root.findViewById(R.id.id_switch)
        mNameEditText = root.findViewById(R.id.name)
        mIdentityView = root.findViewById(R.id.id_text_view)
        mMeetIDEditText = root.findViewById(R.id.meet_id)
        mMeetIDTextView = root.findViewById(R.id.meet_id_tv)
        mEnterMeetingBtn = root.findViewById(R.id.enter_meeting_btn)
        mCallUserIDTextView = root.findViewById(R.id.call_userid_tv)
        mCallUserIDEditText = root.findViewById(R.id.call_userid)
        mEnterMeetingBtn.setOnClickListener(this)
        mSwitch.setOnCheckedChangeListener(this)
        mSwitchPublic.setOnCheckedChangeListener(this)

        // By default switch to public
        onSwitchPublic(true)
        // By default switch to host (admin)
        onSwitchIdentity(true)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.enter_meeting_btn -> {

                if (bIsPublic) {
                    //Public meeting
                    if (bIsAdmin) {

                        mPresenter?.doStartPublicMeeting(requireContext(), subject, this)

                    } else {

                        var meetID = getMeetID()

                        if (meetID == null) {
                            Toast.makeText(requireContext(), "Enter meetID to join", Toast.LENGTH_SHORT).show()
                            return
                        }
                        mPresenter?.doJoinPublicMeeting(requireContext(), meetID, this)
                    }

                } else {

                    val userID = AccountHelper.firstAccount?.userID

                    if (userID == null) {
                        Toast.makeText(requireContext(), "Not login yet", Toast.LENGTH_SHORT).show()
                        return
                    }

                    var receiverID = mCallUserIDEditText.text

                    if (receiverID.isEmpty()) {
                        Toast.makeText(requireContext(), "Enter userID to invite", Toast.LENGTH_SHORT).show()
                        return
                    }

                    var channelID = createSingleChannelID(receiverID.toString(), userID.toString()).toString()

                    //Private meeting
                    if (bIsAdmin) {

                        mPresenter?.doStartPrivateMeeting(requireContext(), subject, channelID, this)

                    } else {

                        var meetID = getMeetID()

                        if (meetID == null) {
                            Toast.makeText(requireContext(), "Enter meetID to join", Toast.LENGTH_SHORT).show()
                            return
                        }
                        mPresenter?.doJoinPrivateMeeting(requireContext(), meetID, channelID, this)
                    }

                }
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (buttonView.id == R.id.id_switch) {
            onSwitchIdentity(isChecked)
        } else if (buttonView.id == R.id.id_ispublic_switch) {
            onSwitchPublic(isChecked)
        }
    }

    override fun onSwitchIdentity(isAdmin: Boolean) {
        bIsAdmin = isAdmin
        if (isAdmin) {
            mMeetIDTextView.visibility = View.GONE
            mMeetIDEditText.visibility = View.GONE
            mIdentityView.text = "Host"
            mEnterMeetingBtn.text = "Start Meeting"
            mEnterMeetingBtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_background)
        } else {
            mMeetIDTextView.visibility = View.VISIBLE
            mMeetIDEditText.visibility = View.VISIBLE
            mIdentityView.text = "Participant"
            mEnterMeetingBtn.text = "Join Meeting"
            mEnterMeetingBtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_background_dark)
        }
    }

    override fun onSwitchPublic(isPublic: Boolean) {
        bIsPublic = isPublic
        if (bIsPublic) {
            mIsPublicTextView.text = "Public"
            mCallUserIDEditText.visibility = View.INVISIBLE
            mCallUserIDTextView.visibility = View.INVISIBLE
        } else {
            mIsPublicTextView.text = "Private"
            mCallUserIDEditText.visibility = View.VISIBLE
            mCallUserIDTextView.visibility = View.VISIBLE
        }
    }

    private fun getMeetID(): String {
        return mMeetIDEditText.text.toString() ?: ""
    }

    private fun getUserName(): String {
        return mNameEditText.text.toString() ?: ""
    }

    override fun finishView() {
        activity?.onBackPressed()
    }

    // Utility to build 1-to-1 channelID from receiverID and userID
    private fun createSingleChannelID(receiverID: String, userID: String): String? {
        if (receiverID == userID) {
            return null
        }
        return if (receiverID < userID) "$receiverID:$userID" else "$userID:$receiverID"
    }

    // implement LTMeetListener
    override fun onMeetTerminate() {
        Log.d(TAG, "onMeetTerminate")
        mEnterMeetingBtn.isEnabled = true
    }

    // implement LTMeetListener
    override fun onMeetError(errorInfo: LTMeetErrorInfo?) {
        Log.e(TAG, "onMeetError")
        Toast.makeText(requireContext(), errorInfo?.errorMessage, Toast.LENGTH_SHORT).show()
        mEnterMeetingBtn.isEnabled = true
    }

}