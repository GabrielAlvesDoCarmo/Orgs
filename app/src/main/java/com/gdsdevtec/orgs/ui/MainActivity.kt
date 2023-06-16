package com.gdsdevtec.orgs.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gdsdevtec.orgs.dao.ProductDao
import com.gdsdevtec.orgs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val productAdapter : ProductsAdapter = ProductsAdapter(ProductDao.getAllProducts())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        bindingSetup()
    }
    private fun bindingSetup() = binding.run {
        rvMain.adapter = productAdapter
        mainFabAdd.setOnClickListener {
            Intent(this@MainActivity, FormActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        productAdapter.updateList(ProductDao.getAllProducts())
    }
}