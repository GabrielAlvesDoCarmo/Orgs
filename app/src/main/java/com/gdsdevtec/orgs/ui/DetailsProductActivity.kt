package com.gdsdevtec.orgs.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gdsdevtec.orgs.databinding.ActivityDetailsProductBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.utils.ext.DialogUtils
import com.gdsdevtec.orgs.utils.ext.convertBigDecimalForCurrencyLocale
import com.gdsdevtec.orgs.utils.ext.loadImageDataWithUrl
class DetailsProductActivity : AppCompatActivity() {
    private val binding: ActivityDetailsProductBinding by lazy {
        ActivityDetailsProductBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActivity()
    }


    private fun setupActivity() {
        val itemSelected = getItemSelected()
        itemSelected?.let {product->
            setProductDetail(product)
        }?: run {
            finish()
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
        detailsProductImageView.loadImageDataWithUrl(DialogUtils(this@DetailsProductActivity).imageLoader,product.image)
        detailsProductTextValue.text = product.value.convertBigDecimalForCurrencyLocale()
        detailsProductTextTitle.text = product.name
        detailsProductTextDescription.text= product.description
    }
}