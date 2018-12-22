package com.foodapp.app.home.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RelativeLayout
import com.foodapp.app.R
import com.foodapp.app.home.model.FoodListModel
import com.foodapp.app.home.view.adapter.ViewPagerAdapter
import com.foodapp.app.home.viewmodel.HomeVM
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class HomeActivity : AppCompatActivity(){
    lateinit var homeVM: HomeVM
    var price: Int = 0
    val productArr: ArrayList<String> = ArrayList()
    val priceArr: ArrayList<String> = ArrayList()
    var productName: String = ""
    var priceDetail = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        val behaviour = BottomSheetBehavior.from(bottom_sheet)
        behaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(p0: View, p1: Float) {
                Log.e("onSlide", "onSlide")
            }

            override fun onStateChanged(p0: View, p1: Int) {
                Log.e("onStateChanged", "onStateChanged:" )
                if(p1 == BottomSheetBehavior.STATE_COLLAPSED){
                    summery.visibility = GONE
                    val params = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                    total_amt_layout.layoutParams = params
                }
                if(p1 == BottomSheetBehavior.STATE_EXPANDED){
                    summery.visibility = VISIBLE
                    val params = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    total_amt_layout.layoutParams = params
                }
            }

        })
        pay_button.setOnClickListener{v ->
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            behaviour.isHideable = false
        }
        behaviour.peekHeight = 100
    }

    private fun initViews(){
        setSupportActionBar(toolbar)
        toolbar.toolbar_title.text = getString(R.string.title)

        homeVM = ViewModelProviders.of(this).get(HomeVM::class.java)
        homeVM.apiCall()
        homeVM.response.observe(this, object: Observer<FoodListModel>{
            override fun onChanged(t: FoodListModel?) {
                val str: String = Gson().toJson(t)
                Log.e("MainActivity", Gson().toJson(t))
                val foodList: FoodListModel = Gson().fromJson(str, FoodListModel::class.java)
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, applicationContext, foodList)
                viewpager_main.adapter = viewPagerAdapter
                tabs_main.setupWithViewPager(viewpager_main)
            }
        })
        total_amount.text = "AED "+price+" ^"
        registerReceiver(broadcastPriceReduce, IntentFilter("price_reduce_receiver"))
        registerReceiver(broadcastPriceAdd, IntentFilter("price_receiver"))
    }

    val broadcastPriceAdd = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            if(p1 != null){
                price += p1.getIntExtra("price", -1)
                total_amount.text = "AED "+price+"  ^"
                var strlist = p1.getStringExtra("product_name").split("?")

                if(productArr.contains(strlist.get(0))){
                    productArr.set(productArr.indexOf(strlist.get(0)), strlist.get(0))
                    priceArr.set(productArr.indexOf(strlist.get(0)), strlist.get(1))
                }else {
                    productArr.add(strlist.get(0))
                    priceArr.add(strlist.get(1))
                }
                productName = ""
                priceDetail = ""
                for(i in productArr.indices){
                    productName = productName + productArr.get(i)+ "\n"
                    priceDetail = priceDetail + priceArr.get(i)+ "\n"
                }
                product_detail.text = productName
                price_detail.text = priceDetail
                Log.e("Activity", productName)
            }
        }

    }
    val broadcastPriceReduce = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            if(p1 != null){
                price -= p1.getIntExtra("price", -1)
                total_amount.text = "AED "+price+"  ^"
                var strlist = p1.getStringExtra("product_name").split("?")

                if(productArr.contains(strlist.get(0))){
                    productArr.set(productArr.indexOf(strlist.get(0)), strlist.get(0))
                    priceArr.set(productArr.indexOf(strlist.get(0)), strlist.get(1))
                }else {
                    productArr.add(strlist.get(0))
                    priceArr.add(strlist.get(1))
                }
                productName = ""
                priceDetail = ""
                for(i in productArr.indices){
                    productName = productName + productArr.get(i)+ "\n"
                    priceDetail = priceDetail + priceArr.get(i)+ "\n"
                }
                product_detail.text = productName
                price_detail.text = priceDetail
                Log.e("Activity", productName)
                Log.e("Activity", productName)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastPriceAdd)
        unregisterReceiver(broadcastPriceReduce)
    }

}
