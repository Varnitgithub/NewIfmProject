package com.example.ifmapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.ifmapp.databinding.MainActivityBinding
import com.example.ifmapp.fragments.DocsFragment
import com.example.ifmapp.fragments.HomeFragment
import com.example.ifmapp.fragments.MenuFragment
import com.example.ifmapp.fragments.MustersFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding

    private lateinit var hashMap: HashMap<String, String>
    private lateinit var homeFragment: HomeFragment
    private lateinit var mustersFragment: MustersFragment
    private lateinit var docsfragment: DocsFragment
    private lateinit var menuFragment: MenuFragment

    private var otp: String? = null

    private var otpFromLogin: String? = null

    private var otpFromsignUp: String? = null
    private var mOTP: String? = null

    private var mobileNumber: String? = null

    private var empNumber: String? = null

    private var locationAutoId: String? = null

    private var pinFromSignin: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        otp = intent.getStringExtra("mPIN")
        otpFromLogin = intent.getStringExtra("mPINFromLogin")
        mobileNumber = intent.getStringExtra("mobileNumber")
        otpFromsignUp = intent.getStringExtra("userPin")
        pinFromSignin = intent.getStringExtra("pinFromSignin")
        empNumber = intent.getStringExtra("empId")
        mOTP = intent.getStringExtra("mOTP")



        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        if (otp!=null){
            homeFragment = HomeFragment(this, otp.toString(), mobileNumber.toString(),empNumber.toString())
        }else if (otpFromLogin!=null){
            homeFragment = HomeFragment(this, otpFromLogin.toString(), mobileNumber.toString(),empNumber.toString())
        }else if (otpFromsignUp!=null){
            homeFragment = HomeFragment(this, otpFromsignUp.toString(), mobileNumber.toString(),empNumber.toString())
        }else if (pinFromSignin!=null){
            homeFragment = HomeFragment(this, pinFromSignin.toString(), mobileNumber.toString(),empNumber.toString())

        }else{
            homeFragment = HomeFragment(this, mOTP.toString(), mobileNumber.toString(),empNumber.toString())

        }


        val bundle = Bundle()
        bundle.putString("mPIN", otp)
        homeFragment.arguments = bundle
        addFragment(homeFragment)

        mustersFragment = MustersFragment(this)
        docsfragment = DocsFragment()
        menuFragment = MenuFragment()

        hashMap = HashMap()


        // Set listener for item clicks
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    addFragment(homeFragment)
                    true
                }

                R.id.navigation_musters -> {
                    addFragment(mustersFragment)

                    true
                }

                R.id.navigation_mydocs -> {
                    addFragment(docsfragment)

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

}
