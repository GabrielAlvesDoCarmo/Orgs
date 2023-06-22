package com.gdsdevtec.orgs.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gdsdevtec.orgs.dao.ProductDao
import com.gdsdevtec.orgs.databinding.ActivityMainBinding
import com.gdsdevtec.orgs.utils.ext.DialogUtils
import com.gdsdevtec.orgs.utils.ext.nextScreen
import com.gdsdevtec.orgs.utils.ext.onClick

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var productAdapter : ProductsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        bindingSetup()
    }
    private fun bindingSetup() = binding.run {
        productAdapter = productsAdapter()
        rvMain.adapter = productAdapter
        mainFabAdd.onClick{
           nextScreen(FormActivity())
        }
    }

    private fun productsAdapter() = ProductsAdapter(
        listProducts = getProducts(),
        imageLoader = DialogUtils(this@MainActivity).imageLoader,
        itemSelected = {itemSelected->
            nextScreen(DetailsProductActivity(),Pair("PRODUCT",itemSelected))
        },

    )

    override fun onResume() {
        super.onResume()
        productAdapter.updateList(getProducts())
    }
    private fun getProducts() = ProductDao.getAllProducts()
}