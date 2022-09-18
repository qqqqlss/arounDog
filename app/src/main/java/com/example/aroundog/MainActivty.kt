package com.example.aroundog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.aroundog.databinding.ActivityMainAfterLoginBinding
import com.example.aroundog.fragments.AroundWalkFragment
import com.example.aroundog.fragments.MainFragment
import com.example.aroundog.fragments.ProfileFragment
import com.google.android.material.navigation.NavigationBarView

class MainActivty : AppCompatActivity() {
    private var TAG: String = "MAINTAG"
    private lateinit var binding: ActivityMainAfterLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAfterLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setFragment()

    }
    fun setFragment(){
        val mainFragment: MainFragment = MainFragment()
        val aroundWalkFragment: AroundWalkFragment = AroundWalkFragment()
        val profileFragment: ProfileFragment = ProfileFragment()


        supportFragmentManager.beginTransaction().replace(R.id.main_container, mainFragment,"walk").commitAllowingStateLoss()
        binding.bottomNav.menu.findItem(R.id.statusLayout).setChecked(true)//시작은 산책하기로

        binding.bottomNav.setOnItemSelectedListener(object: NavigationBarView.OnItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.statusLayout->{//산책하기
                        if(supportFragmentManager.findFragmentByTag("walk") != null)
                            supportFragmentManager.beginTransaction().show(supportFragmentManager.findFragmentByTag("walk")!!).commit()
                        else
                            supportFragmentManager.beginTransaction().add(R.id.main_container,mainFragment,"walk").commit()

                        //다른프래그먼트는 가리기
                        if(supportFragmentManager.findFragmentByTag("aroundWalk") != null)
                            supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("aroundWalk")!!).commit()
                        if(supportFragmentManager.findFragmentByTag("profile") != null)
                            supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("profile")!!).commit()
                        return true
                    }
                    R.id.aroundWalk->{//주변 경로
                        if(supportFragmentManager.findFragmentByTag("aroundWalk") != null)
                            supportFragmentManager.beginTransaction().show(supportFragmentManager.findFragmentByTag("aroundWalk")!!).commit()
                        else
                            supportFragmentManager.beginTransaction().add(R.id.main_container,aroundWalkFragment,"aroundWalk").commit()

                        //다른프래그먼트는 가리기
                        if(supportFragmentManager.findFragmentByTag("walk") != null)
                            supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("walk")!!).commit()
                        if(supportFragmentManager.findFragmentByTag("profile") != null)
                            supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("profile")!!).commit()
                        return true
                    }
                    R.id.profile -> {//프로필
                        if(supportFragmentManager.findFragmentByTag("profile") != null)
                            supportFragmentManager.beginTransaction().show(supportFragmentManager.findFragmentByTag("profile")!!).commit()
                        else
                            supportFragmentManager.beginTransaction().add(R.id.main_container,profileFragment,"profile").commit()

                        //다른프래그먼트는 가리기
                        if(supportFragmentManager.findFragmentByTag("walk") != null)
                            supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("walk")!!).commit()
                        if(supportFragmentManager.findFragmentByTag("aroundWalk") != null)
                            supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("aroundWalk")!!).commit()
                        return true
                    }
                }
                return false
            }
        })

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause ON")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop ON")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy ON")
    }
}