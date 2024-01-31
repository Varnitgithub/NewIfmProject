package com.example.ifmapp

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.ifmapp.activities.DashBoardScreen
import com.example.ifmapp.databinding.MainActivityBinding
import com.example.ifmapp.fragments.ERegisterFragment
import com.example.ifmapp.fragments.HomeFragment
import com.example.ifmapp.fragments.MenuFragment
import com.example.ifmapp.fragments.MyTaskFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding
    private lateinit var dialog: Dialog
    private lateinit var hashMap: HashMap<String, String>
    private lateinit var homeFragment: HomeFragment
    private lateinit var myTaskFragment: MyTaskFragment
    private lateinit var eRegisterFragment: ERegisterFragment
    private lateinit var menuFragment: MenuFragment

    private var otp: String? = null

    private var otpFromLogin: String? = null

    private var otpFromsignUp: String? = null
    private var mOTP: String? = null

    private var mobileNumber: String? = null

    private var empNumber: String? = null
    private var inout: String? = null

    private var locationAutoId: String? = null

    private var pinFromSignin: String? = null
    private var userName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        otp = intent.getStringExtra("mPIN")
        otpFromLogin = intent.getStringExtra("mPINFromLogin")
        mobileNumber = intent.getStringExtra("mobileNumber")
        otpFromsignUp = intent.getStringExtra("userPin")
        pinFromSignin = intent.getStringExtra("pinFromSignin")
        empNumber = intent.getStringExtra("empId")
        mOTP = intent.getStringExtra("mOTP")
        userName = intent.getStringExtra("empName")



        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

         inout = intent.getStringExtra("INOUTStatus")

        Log.d("TAGGGGGGGGGGG", "onCreateView: this is in out set222 $inout")

        Log.d("TAGGGGGGG", "onResponse: $otp is the new otp  222")


        if (otp != null && userName != null) {
            homeFragment =
                HomeFragment(this, otp.toString(), mobileNumber.toString()
                    , empNumber.toString(),userName.toString(),inout.toString()
                )
        } else if (otpFromLogin != null) {
            homeFragment = HomeFragment(
                this,
                otpFromLogin.toString(),
                mobileNumber.toString(),
                empNumber.toString(), userName.toString(),inout.toString()
            )
        } else if (otpFromsignUp != null) {
            homeFragment = HomeFragment(
                this,
                otpFromsignUp.toString(),
                mobileNumber.toString(),
                empNumber.toString(), userName.toString(),inout.toString()
            )
        } else if (pinFromSignin != null) {
            homeFragment = HomeFragment(
                this,
                pinFromSignin.toString(),
                mobileNumber.toString(),
                empNumber.toString(), userName.toString(),inout.toString()
            )
        } else {
            homeFragment =
                HomeFragment(this, mOTP.toString(), mobileNumber.toString(),
                    empNumber.toString(),userName.toString(),inout.toString())
        }

        if (otp != null) {
            menuFragment = MenuFragment(otp.toString())
            myTaskFragment = MyTaskFragment(this, otp.toString())
            eRegisterFragment = ERegisterFragment(otp.toString())
        } else if (otpFromLogin != null) {
            menuFragment = MenuFragment(otpFromLogin.toString())
            myTaskFragment = MyTaskFragment(this, otpFromLogin.toString())
            eRegisterFragment = ERegisterFragment(otpFromLogin.toString())


        } else if (otpFromsignUp != null) {
            menuFragment = MenuFragment(otpFromsignUp.toString())
            myTaskFragment = MyTaskFragment(this, otpFromsignUp.toString())
            eRegisterFragment = ERegisterFragment(otpFromsignUp.toString())


        } else if (pinFromSignin != null) {
            menuFragment = MenuFragment(pinFromSignin.toString())
            myTaskFragment = MyTaskFragment(this, pinFromSignin.toString())
            eRegisterFragment = ERegisterFragment(pinFromSignin.toString())


        } else {
            menuFragment = MenuFragment(mOTP.toString())
            myTaskFragment = MyTaskFragment(this, mOTP.toString())
            eRegisterFragment = ERegisterFragment(mOTP.toString())


        }

        val bundle = Bundle()
        bundle.putString("mPIN", otp)
        homeFragment.arguments = bundle
        addFragment(homeFragment)

        hashMap = HashMap()


        // Set listener for item clicks
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    addFragment(homeFragment)
                    true
                }

                R.id.navigation_musters -> {
                    addFragment(myTaskFragment)

                    true
                }

                R.id.navigation_mydocs -> {
                    addFragment(eRegisterFragment)

                    true
                }

                R.id.navigation_menu -> {
                    addFragment(menuFragment)

                    true
                }

                else -> false
            }
        }
    }
    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()

    }
    override fun onBackPressed() {

        val fragmentManager: FragmentManager = supportFragmentManager
        val currentFragment: Fragment? = fragmentManager.findFragmentById(R.id.frameLayout)

        when (currentFragment) {
            is HomeFragment -> {
                dialog = Dialog(this)
                dialog.setContentView(R.layout.back_button_dialog)
                dialog.setCancelable(true)
                dialog.show()
                val dialogText: TextView = dialog.findViewById(R.id.logoutTxt)
                val btnNo: TextView = dialog.findViewById(R.id.btnNo)
                val btnYes: TextView = dialog.findViewById(R.id.btnYes)

                btnYes.setOnClickListener {
                    startActivity(Intent(this@MainActivity, DashBoardScreen::class.java))
                    dialog.dismiss()
                    super.onBackPressed()
                }
                btnNo.setOnClickListener {
                    dialog.dismiss()
                }
            }

            is MyTaskFragment -> {
                addFragment(homeFragment)
                binding.bottomNavigation.selectedItemId = R.id.navigation_home
            }

            is ERegisterFragment -> {
                addFragment(homeFragment)
                binding.bottomNavigation.selectedItemId = R.id.navigation_home

            }

            is MenuFragment -> {
                addFragment(homeFragment)
                binding.bottomNavigation.selectedItemId = R.id.navigation_home

            }

        }
    }
}
