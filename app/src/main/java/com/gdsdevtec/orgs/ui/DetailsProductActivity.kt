package com.gdsdevtec.orgs.ui

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.gdsdevtec.orgs.R
import com.gdsdevtec.orgs.data.database.db.AppDatabase
import com.gdsdevtec.orgs.databinding.ActivityDetailsProductBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.utils.ext.DialogUtils
import com.gdsdevtec.orgs.utils.ext.convertBigDecimalForCurrencyLocale
import com.gdsdevtec.orgs.utils.ext.loadImageDataWithUrl
import com.gdsdevtec.orgs.utils.ext.nextScreen

class DetailsProductActivity : AppCompatActivity() {
    private val binding: ActivityDetailsProductBinding by lazy {
        ActivityDetailsProductBinding.inflate(layoutInflater)
    }
    private val dao by lazy { AppDatabase.getInstance(this).productDao() }
    private var product: Product? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActivity()
    }

    private fun setupActivity() {
        product = getItemSelected()
        product?.let { product ->
            setProductDetail(product)
        } ?: run { finish() }
    }

    override fun onResume() {
        super.onResume()
        product?.let { safeProduct -> product = dao.getProductForId(safeProduct.id) }
        product?.let { setProductDetail(it) } ?: run { finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_description_producs, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_editable -> {
                nextScreen(FormActivity(), Pair("PRODUCT", getItemSelected()!!))
                true
            }

            R.id.menu_excluded -> {
                product?.let { dao.deleteProduct(it) }
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getItemSelected(): Product? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("PRODUCT", Product::class.java)
        } else {
            intent.extras?.get("PRODUCT") as? Product
        }
    }


    private fun setProductDetail(product: Product) = with(binding) {
        detailsProductImageView.loadImageDataWithUrl(
            DialogUtils(this@DetailsProductActivity).imageLoader,
            product.image
        )
        detailsProductTextValue.text = product.value.convertBigDecimalForCurrencyLocale()
        detailsProductTextTitle.text = product.name
        detailsProductTextDescription.text = product.description
    }
}