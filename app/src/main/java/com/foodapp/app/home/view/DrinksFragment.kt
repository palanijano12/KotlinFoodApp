package com.foodapp.app.home.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foodapp.app.R
import com.foodapp.app.home.model.FnblistItem
import com.foodapp.app.home.model.FoodListModel
import com.foodapp.app.home.view.adapter.FoodListAdapter
import com.foodapp.app.home.webservice.PriceReduceListener
import com.foodapp.app.home.webservice.PriceUpdateListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_combos.view.*

class DrinksFragment  : Fragment(), PriceUpdateListener, PriceReduceListener{

    override fun updateReducedPrice(price: Int, name: String ){
        val intent = Intent("price_reduce_receiver")
        intent.putExtra("price", price)
        intent.putExtra("product_name", name)
        activity!!.sendBroadcast(intent)
    }

    override fun updatePrice(price: Int, product: String) {
        val intent = Intent("price_receiver")
        intent.putExtra("price", price)
        intent.putExtra("product_name", product)
        activity!!.sendBroadcast(intent)
    }

    var foods: ArrayList<FnblistItem> = ArrayList()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater!!.inflate(R.layout.fragment_drinks, container, false)
        getData(rootView)

        return rootView
    }

    fun getData(rootView: View) {
        foods.clear()
        val args = arguments
        if (args != null) {
            Log.e("Position", ">>"+args.getInt("position"))
            var position: Int = args.getInt("position")
            val fList: FoodListModel = Gson().fromJson(args.getString("data"), FoodListModel::class.java)
            foods = fList.foodList!!.get(position).fnblist!!
            rootView.recycler_view!!.layoutManager = LinearLayoutManager(activity!!.applicationContext)
            rootView.recycler_view!!.addItemDecoration(LinearLayoutSpaceItemDecoration(20))
            rootView.recycler_view!!.adapter = FoodListAdapter(foods, activity!!.applicationContext, this, this)
        }else{

        }


    }
}