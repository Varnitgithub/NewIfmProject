package com.example.ifmapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
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
import com.example.ifmapp.utils.UserObject


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
        Log.d("TAGGGGGGG", "oncreate of main: this is user details ${UserObject.userNames} ${UserObject.userId} ${UserObject.designation} ${UserObject.userPin}")

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
            .replace(R.id.frameLayout_Main, fragment)
            .commit()

    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

        val fragmentManager: FragmentManager = supportFragmentManager
        val currentFragment: Fragment? = fragmentManager.findFragmentById(R.id.frameLayout_Main)

        when (currentFragment) {
            is HomeFragment -> {
               /* dialog = Dialog(this)
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
                }*/

                var dialogBuilder=AlertDialog.Builder(this@MainActivity)
                dialogBuilder.setTitle("GroupL")
                dialogBuilder.setMessage("Are you sure,You want to logout?")

                dialogBuilder.setPositiveButton("Logout",object :DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        startActivity(Intent(this@MainActivity, DashBoardScreen::class.java))
                        dialog?.dismiss()
                    }
                })
                dialogBuilder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.dismiss()
                    }
                })
                dialogBuilder.show()
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

    override fun onResume() {
        super.onResume()
        if (otp != null) {
            Log.d("TAGGGGGGGGGG", "onCreateView: 222........$otp $empNumber")
            homeFragment =
                HomeFragment(this, otp.toString()
                    , empNumber.toString(),userName.toString()
                )
        }
        else if (otpFromLogin != null) {
            homeFragment = HomeFragment(
                this,
                otpFromLogin.toString(),
                empNumber.toString(), userName.toString()
            )
        }
        else if (otpFromsignUp != null) {
            homeFragment = HomeFragment(
                this,
                otpFromsignUp.toString(),
                empNumber.toString(), userName.toString()
            )
        }
        else if (pinFromSignin != null) {
            homeFragment = HomeFragment(
                this,
                pinFromSignin.toString(),
                empNumber.toString(), userName.toString()
            )
        }
        else {
            homeFragment =
                HomeFragment(this, UserObject.userPin,
                    UserObject.userId,UserObject.userNames)

        }

        if (otp != null) {
            menuFragment = MenuFragment(otp.toString(),userName.toString(),empNumber.toString())
            myTaskFragment = MyTaskFragment()
            eRegisterFragment = ERegisterFragment(otp.toString(),empNumber.toString())
        }
        else if (otpFromLogin != null) {
            menuFragment = MenuFragment(otpFromLogin.toString(),userName.toString(),empNumber.toString())
            myTaskFragment = MyTaskFragment()
            eRegisterFragment = ERegisterFragment(otpFromLogin.toString(),empNumber.toString())


        }
        else if (otpFromsignUp != null)
        {
            menuFragment = MenuFragment(otpFromsignUp.toString(),userName.toString(),empNumber.toString())
            myTaskFragment = MyTaskFragment()
            eRegisterFragment = ERegisterFragment(otpFromsignUp.toString(),empNumber.toString())


        }
        else if (pinFromSignin != null) {
            menuFragment = MenuFragment(pinFromSignin.toString(),userName.toString(),empNumber.toString())
            myTaskFragment = MyTaskFragment()
            eRegisterFragment = ERegisterFragment(pinFromSignin.toString(),empNumber.toString())


        }
        else {
            menuFragment = MenuFragment( UserObject.userPin,userName.toString(), UserObject.userId)
            myTaskFragment = MyTaskFragment()
            eRegisterFragment = ERegisterFragment( UserObject.userPin, UserObject.userId)
            Log.d("TAGGGGG", "onCreate: this case is running for others")


        }

        val bundle = Bundle()
        bundle.putString("mPIN", otp)
        homeFragment.arguments = bundle
        addFragment(homeFragment)
        Log.d("TAGGGGGGG", "onresume of main: this is user details ${UserObject.userNames} ${UserObject.userId} ${UserObject.designation} ${UserObject.userPin}")

    }

    override fun onRestart() {
        super.onRestart()
        if (otp != null) {
            Log.d("TAGGGGGGGGGG", "onCreateView: 222........$otp $empNumber")
            homeFragment =
                HomeFragment(this, otp.toString()
                    , empNumber.toString(),userName.toString()
                )
        }
        else if (otpFromLogin != null) {
            homeFragment = HomeFragment(
                this,
                otpFromLogin.toString(),
                empNumber.toString(), userName.toString()
            )
        }
        else if (otpFromsignUp != null) {
            homeFragment = HomeFragment(
                this,
                otpFromsignUp.toString(),
                empNumber.toString(), userName.toString()
            )
        }
        else if (pinFromSignin != null) {
            homeFragment = HomeFragment(
                this,
                pinFromSignin.toString(),
                empNumber.toString(), userName.toString()
            )
        }
        else {
            homeFragment =
                HomeFragment(this, UserObject.userPin,
                    UserObject.userId,UserObject.userNames)

        }

        if (otp != null) {
            menuFragment = MenuFragment(otp.toString(),userName.toString(),empNumber.toString())
            myTaskFragment = MyTaskFragment()
            eRegisterFragment = ERegisterFragment(otp.toString(),empNumber.toString())
        }
        else if (otpFromLogin != null) {
            menuFragment = MenuFragment(otpFromLogin.toString(),userName.toString(),empNumber.toString())
            myTaskFragment = MyTaskFragment()
            eRegisterFragment = ERegisterFragment(otpFromLogin.toString(),empNumber.toString())


        }
        else if (otpFromsignUp != null)
        {
            menuFragment = MenuFragment(otpFromsignUp.toString(),userName.toString(),empNumber.toString())
            myTaskFragment = MyTaskFragment()
            eRegisterFragment = ERegisterFragment(otpFromsignUp.toString(),empNumber.toString())


        }
        else if (pinFromSignin != null) {
            menuFragment = MenuFragment(pinFromSignin.toString(),userName.toString(),empNumber.toString())
            myTaskFragment = MyTaskFragment()
            eRegisterFragment = ERegisterFragment(pinFromSignin.toString(),empNumber.toString())


        }
        else {
            menuFragment = MenuFragment( UserObject.userPin,userName.toString(), UserObject.userId)
            myTaskFragment = MyTaskFragment()
            eRegisterFragment = ERegisterFragment( UserObject.userPin, UserObject.userId)
            Log.d("TAGGGGG", "onCreate: this case is running for others")


        }

        val bundle = Bundle()
        bundle.putString("mPIN", otp)
        homeFragment.arguments = bundle
        addFragment(homeFragment)
        Log.d("TAGGGGGGG", "onresume of main: this is user details ${UserObject.userNames} ${UserObject.userId} ${UserObject.designation} ${UserObject.userPin}")

    }
}
