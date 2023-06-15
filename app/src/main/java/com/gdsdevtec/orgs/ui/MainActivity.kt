package com.gdsdevtec.orgs.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gdsdevtec.orgs.dao.ProductDao
import com.gdsdevtec.orgs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var productAdapter : ProductsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        productAdapter = ProductsAdapter(ProductDao.getAllProducts())
        binding.rvMain.adapter = productAdapter
    }
}