package com.gdsdevtec.orgs.ui.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gdsdevtec.orgs.R
import com.gdsdevtec.orgs.databinding.ActivityDetailsProductBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.ui.form.FormActivity
import com.gdsdevtec.orgs.utils.const.Constants
import com.gdsdevtec.orgs.utils.ext.DialogUtils
import com.gdsdevtec.orgs.utils.ext.convertBigDecimalForCurrencyLocale
import com.gdsdevtec.orgs.utils.ext.loadImageDataWithUrl
import com.gdsdevtec.orgs.utils.ext.nextScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailsProductActivity : AppCompatActivity() {
    private val binding: ActivityDetailsProductBinding by lazy {
        ActivityDetailsProductBinding.inflate(layoutInflater)
    }
    @Inject
    lateinit var viewModel: DetailsViewModel
    private var productId: Long = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActivity()
        observers()
    }

    private fun observers() {
        lifecycleScope.launch {
            viewModel.state.collect{state->
                when(state){
                    is DetailsState.Success -> setProductDetail(state.product)
                    is DetailsState.Error -> TODO()
                    is DetailsState.Empty -> TODO()
                }
            }
        }
    }

    private fun setupActivity() {
        productId = intent.getLongExtra(Constants.PRODUCT_ID, 0L)
        viewModel.submitActions(
            DetailsActions.GetProductForId(productId)
        )
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

        finish()
        return true
    }

    private fun clickEditableMenu(): Boolean {
        nextScreen(FormActivity(), Pair(Constants.PRODUCT_ID, productId))
        return true
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