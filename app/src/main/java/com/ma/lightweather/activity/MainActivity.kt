package com.ma.lightweather.activity

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.ma.lightweather.R
import com.ma.lightweather.fragment.*
import com.ma.lightweather.utils.CommonUtils
import com.ma.lightweather.widget.WeatherViewPager
import java.util.*
import kotlin.system.exitProcess


class MainActivity : BaseActivity() {


    private var frogWeatherFrag: FrogWeatherFragment? = null
    private var futureDaysFrag: FutureDaysFragment? = null
    private var weatherFrag: WeatherFragment? = null
    private var cityFrag: CityFrgment? = null
    private var photoFrag: PhotoFragment? = null
    private var viewPager:WeatherViewPager? = null
    private var toolBar: Toolbar? = null
    private var searchView:SearchView?=null
    private var tabLayout: TabLayout? = null
    private val fragmentList = ArrayList<Fragment>()
    private val titleList = ArrayList<String>()
    private var clickTime: Long = 0
    private var navigationView: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        //setSearch()
        //initOldData()
        initNewData()
    }

    override fun recreate() {
//        try {//避免重启太快 恢复
//            val fragmentTransaction = supportFragmentManager.beginTransaction()
//            for (fragment in fragmentList) {
//                fragmentTransaction.remove(fragment)
//            }
//            fragmentTransaction.commitAllowingStateLoss()
//        } catch (e: Exception) {
//            CommonUtils.showShortSnackBar(tabLayout,"切换主题失败，请重试")
//        }

        super.recreate()
    }

    private fun initOldData() {
        weatherFrag = WeatherFragment()
        cityFrag = CityFrgment()
        photoFrag = PhotoFragment()
        fragmentList.add(weatherFrag!!)
        fragmentList.add(cityFrag!!)
        fragmentList.add(photoFrag!!)
        titleList.add(getString(R.string.main_weather_text))
        titleList.add(getString(R.string.main_city_text))
        titleList.add(getString(R.string.main_photo_text))
        viewPager?.adapter = ViewAdapter(supportFragmentManager)
        tabLayout?.setupWithViewPager(viewPager)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initNewData() {
        frogWeatherFrag= FrogWeatherFragment()
        futureDaysFrag= FutureDaysFragment()
        fragmentList.add(frogWeatherFrag!!)
        fragmentList.add(futureDaysFrag!!)
        titleList.add(getString(R.string.main_weather_text))
        titleList.add(getString(R.string.main_future_text))
        viewPager?.adapter = ViewAdapter(supportFragmentManager)
        tabLayout?.setupWithViewPager(viewPager)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initView() {
        toolBar = findViewById(R.id.toolBar)
        toolBar?.inflateMenu(R.menu.toolbar_menu)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        viewPager?.offscreenPageLimit = 2
        viewPager?.currentItem = 0
        setSupportActionBar(toolBar)
    }

    private fun setSearch(){
        searchView = findViewById(R.id.toolBarSearch)
        val et = searchView?.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
        et?.textSize = 15f
        et?.hint = getString(R.string.main_search_text)
        et?.setHintTextColor(ContextCompat.getColor(this, R.color.hint_black_text))
        et?.setTextColor(ContextCompat.getColor(this, R.color.primary_black_text))
        et?.setBackgroundResource(R.drawable.bg_weather_sv_solid_grey)
        searchView?.onActionViewExpanded()
        searchView?.clearFocus()
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                refresh(query, true)
                searchView?.clearFocus()
                searchView?.setQuery("", false)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }


    fun refresh(city: String, isSkip: Boolean) {
        if (isSkip) {
            viewPager?.currentItem = 0
        }
        weatherFrag?.loadData(city)
    }

    fun refreshCity() {
        cityFrag?.initData()
    }

    fun setWeatherBack(cond:String) {
        var color=CommonUtils.getColorWeatherTheme(cond)
        toolBar?.setBackgroundColor(ContextCompat.getColor(this,color))
        tabLayout?.setBackgroundColor(ContextCompat.getColor(this,color))
        setStatusColor(color)
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.toolbar_menu, menu)
//        val item = menu.findItem(R.id.toolBarSearch)
//        val searchView = item.actionView as SearchView
//        val et = searchView.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
//        et.textSize = 15f
//        et.hint = getString(R.string.main_search_text)
//        et.setHintTextColor(ContextCompat.getColor(this, R.color.hint_black_text))
//        et.setTextColor(ContextCompat.getColor(this, R.color.primary_black_text))
//        et.setBackgroundResource(R.drawable.bg_weather_sv_solid_grey)
//        searchView.isIconified=false
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                refresh(query, true)
//                searchView.clearFocus()
//                searchView.setQuery("", false)
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                return false
//            }
//        })
//        return super.onCreateOptionsMenu(menu)
//    }

    internal inner class ViewAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titleList[position]
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun exit() {
        if (System.currentTimeMillis() - clickTime > 2000) {
            CommonUtils.showShortSnackBar(tabLayout, getString(R.string.main_exit_text))
            clickTime = System.currentTimeMillis()
        } else {
            this.finish()
            exitProcess(0)
        }
    }


    // 获取点击事件
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // TODO Auto-generated method stub
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (isHideInput(view, ev)) {
                hideSoftInput(view!!.windowToken)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // 判定是否需要隐藏
    private fun isHideInput(v: View?, ev: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(ev.x > left
                    && ev.x < right
                    && ev.y > top
                    && ev.y < bottom)
        }
        return false
    }

    // 隐藏软键盘
    private fun hideSoftInput(token: IBinder?) {
        if (token != null) {
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


    fun setStatusColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6.0以上版本
            // 设置状态栏底色颜色
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor =ContextCompat.getColor(this,color)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0以上版本
            val window: Window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //6.0以下设置默认黑色状态栏
            window.statusBarColor = ContextCompat.getColor(this,color)
        }
    }
}
