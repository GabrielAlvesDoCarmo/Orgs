package com.gdsdevtec.orgs.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.gdsdevtec.orgs.R
import com.gdsdevtec.orgs.data.database.db.AppDatabase
import com.gdsdevtec.orgs.databinding.ActivityDetailsProductBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.utils.const.Constants
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
    private var productId: Long = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActivity()
    }

    private fun setupActivity() {
        productId = getItemSelected()
        product = dao.getProductForId(productId)
    }

    private fun refreshProductSelected() {
        product?.let { safeProduct ->
            product = dao.getProductForId(safeProduct.id)
            setProductDetail(product)
        } ?: run {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshProductSelected()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_description_producs, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_editable -> clickEditableMenu()
            R.id.menu_excluded -> clickExcludedMenu()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clickExcludedMenu(): Boolean {
        product?.let { dao.deleteProduct(it) }
        finish()
        return true
    }

    private fun clickEditableMenu(): Boolean {
        nextScreen(FormActivity(), Pair(Constants.PRODUCT_ID, productId))
        return true
    }

    private fun getItemSelected(): Long {
        return intent.getLongExtra(Constants.PRODUCT_ID, 0L)
    }


    private fun setProductDetail(product: Product?) = with(binding) {
        product?.let { safeProduct ->
            detailsProductImageView.loadImageDataWithUrl(
                DialogUtils(this@DetailsProductActivity).imageLoader,
                safeProduct.image
            )
            detailsProductTextValue.text = safeProduct.value.convertBigDecimalForCurrencyLocale()
            detailsProductTextTitle.text = safeProduct.name
            detailsProductTextDescription.text = safeProduct.description
        }
    }
}