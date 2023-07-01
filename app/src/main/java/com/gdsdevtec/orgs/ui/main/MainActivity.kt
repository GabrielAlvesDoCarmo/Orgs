package com.gdsdevtec.orgs.ui.main

import OrgsPreferences
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.gdsdevtec.orgs.R
import com.gdsdevtec.orgs.databinding.ActivityMainBinding
import com.gdsdevtec.orgs.domain.products.Product
import com.gdsdevtec.orgs.ui.detail.DetailsProductActivity
import com.gdsdevtec.orgs.ui.form.FormActivity
import com.gdsdevtec.orgs.utils.const.Constants
import com.gdsdevtec.orgs.utils.ext.DialogUtils
import com.gdsdevtec.orgs.utils.ext.nextScreen
import com.gdsdevtec.orgs.utils.ext.onClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val preferences by lazy { OrgsPreferences.getInstance(this) }
    private val dialogUtils by lazy { DialogUtils(this) }
    private val requestCamera = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) launchCam.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    private val launchCam = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) setImageCamAppBar(result)
    }

    @Inject
    lateinit var viewModel: MainViewModel
    private lateinit var productAdapter: ProductsAdapter
    private var allProducts: List<Product> = listOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getAllProductsDb()
        bindingSetup()
        observers()
    }

    private fun getAllProductsDb() = viewModel.submitActions(
        MainActions.GetAllProducts
    )

    private fun observers() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is MainState.Success -> successStateList(state.listProducts)
                    is MainState.SuccessDelete -> TODO()
                    is MainState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
                    is MainState.Empty -> productAdapter.updateList(listOf())
                    is MainState.Error -> TODO()
                }
            }
        }
    }

    private fun successStateList(listProducts: List<Product>) {
        binding.swipeRefreshLayout.isRefreshing = false
        productAdapter.updateList(listProducts)
    }

    private fun bindingSetup() = binding.run {
        setupAppBar()
        productAdapter = productsAdapter()
        rvMain.adapter = productAdapter
        mainFabAdd.onClick { nextScreen(FormActivity()) }
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.submitActions(MainActions.GetAllProducts)
        }
    }

    private fun setupAppBar() = with(binding.includeToolbar.toolbar) {
        binding.includeToolbar.appbarContainer.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isExpanding = verticalOffset == 0
            val isCollapsing = abs(verticalOffset) >= appBarLayout.totalScrollRange
            if (isExpanding) this.menu.forEach {
                it.isVisible = true
            }
            if (isCollapsing) this.menu.forEach {
                it.isVisible = false
            }
        }
        changeIconsAppBarColor(menu, getColor(R.color.color_primary_variant))
        navigationIcon?.setTint(getColor(R.color.color_primary_variant))
        setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_name_desc -> {
                    allProducts = allProducts.sortedByDescending { it.name }
                    productAdapter.updateList(allProducts)
                    true
                }

                R.id.menu_name_asc -> {
                    allProducts = allProducts.sortedBy { it.name }
                    productAdapter.updateList(allProducts)
                    true
                }

                R.id.menu_description_desc -> {
                    allProducts = allProducts.sortedByDescending { it.description }
                    productAdapter.updateList(allProducts)
                    true
                }

                R.id.menu_description_asc -> {
                    allProducts = allProducts.sortedBy { it.description }
                    productAdapter.updateList(allProducts)
                    true
                }

                R.id.menu_value_desc -> {
                    allProducts = allProducts.sortedByDescending { it.value }
                    productAdapter.updateList(allProducts)
                    true
                }

                R.id.menu_value_asc -> {
                    allProducts = allProducts.sortedBy { it.value }
                    productAdapter.updateList(allProducts)
                    true
                }

                R.id.menu_date_desc -> {
                    allProducts = allProducts.sortedByDescending { it.date }
                    productAdapter.updateList(allProducts)
                    true
                }

                R.id.menu_date_asc -> {
                    allProducts = allProducts.sortedBy { it.date }
                    productAdapter.updateList(allProducts)
                    true
                }

                R.id.menu_timer_desc -> {
                    allProducts = allProducts.sortedByDescending { it.time }
                    productAdapter.updateList(allProducts)
                    true
                }

                R.id.menu_timer_asc -> {
                    allProducts = allProducts.sortedBy { it.time }
                    productAdapter.updateList(allProducts)
                    true
                }

                R.id.menu_not_order -> {
                    getAllProductsDb()
                    productAdapter.updateList(allProducts)
                    true
                }

                R.id.change_picture_app_menu -> takePictureAppBar()
                R.id.change_color_app_menu -> changeColorStatusBar()
                else -> false
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun changeIconsAppBarColor(
        menu: Menu,
        color: Int
    ) {
        menu.forEach { menuItem ->
            menuItem.icon?.let { icon ->
                val drawable = icon.mutate()
                drawable.setTint(color)
                menuItem.icon = drawable
            }
        }
    }

    private fun takePictureAppBar(): Boolean {
        requestCamera.launch(Manifest.permission.CAMERA)
        return true
    }

    private fun productsAdapter() = ProductsAdapter(
        listProducts = allProducts,
        imageLoader = dialogUtils.imageLoader,
        itemSelected = { itemSelected ->
            nextScreen(DetailsProductActivity(), Pair(Constants.PRODUCT_ID, itemSelected.id))
        },
        onLongItemClick = ::onLongItemClick
    )

    private fun onLongItemClick(view: View?, product: Product): Boolean {
        val popUp = PopupMenu(this, view)
        val inflater: MenuInflater = popUp.menuInflater
        inflater.inflate(R.menu.menu_description_producs, popUp.menu)
        popUp.setOnMenuItemClickListener { itemMenu ->
            onClickMenuPopUpItem(itemMenu, product)
        }
        popUp.show()
        return true
    }

    private fun onClickMenuPopUpItem(itemMenu: MenuItem?, product: Product): Boolean {
        when (itemMenu?.itemId) {
            R.id.menu_editable -> nextScreen(FormActivity(), Pair(Constants.PRODUCT_ID, product.id))
            R.id.menu_excluded -> viewModel.submitActions(MainActions.DeleteProduct(product))
        }
        return true
    }

    private fun setImageCamAppBar(result: ActivityResult) {
        val img = result.data?.extras?.get("data") as? Bitmap
        img?.let {
            binding.includeToolbar.appBarImageView.apply {
                isVisible = true
                binding.includeToolbar.appBarImageView.layoutParams.height =
                    ViewGroup.LayoutParams.MATCH_PARENT
                binding.includeToolbar.appBarImageView.setImageBitmap(img)
            }
        }
    }

    private fun changeColorStatusBar(): Boolean {
        dialogUtils.colorDialog(
            titleDialogColor = getString(R.string.selecione_a_cor_da_status_bar),
            actionPositiveButton = { newColorStatusBar ->
                window.statusBarColor = newColorStatusBar
                window.navigationBarColor = newColorStatusBar
                preferences.saveColorStatusBar(newColorStatusBar)
                changeIconsAppBarColor(binding.includeToolbar.toolbar.menu, newColorStatusBar)
                showDialogChangeAppBar()
            },
        )
        return true
    }

    private fun showDialogChangeAppBar() {
        dialogUtils.colorDialog(
            titleDialogColor = getString(R.string.selecione_a_cor_da_appbar),
            actionPositiveButton = { newColorAppBar ->
                binding.includeToolbar.apply {
                    appBarImageView.isVisible = false
                    appbarContainer.setBackgroundColor(newColorAppBar)
                    OrgsPreferences(this@MainActivity).saveColorAppBar(newColorAppBar)
                }
                showDialogColorFAB()
            },
        )
    }

    private fun showDialogColorFAB() {
        dialogUtils.colorDialog(
            titleDialogColor = getString(R.string.selecione_a_cor_do_botao),
            actionPositiveButton = { newColorFab ->
                binding.mainFabAdd.setBackgroundColor(newColorFab)
                preferences.saveColorBtn(newColorFab)
                showDialogColorLetters()
            }
        )
    }

    private fun showDialogColorLetters() {
        dialogUtils.colorDialog(
            titleDialogColor = getString(R.string.selecione_a_cor_das_letras),
            actionPositiveButton = { newColorLetters ->
                binding.includeToolbar.collapsingToolbarLayout.setExpandedTitleColor(newColorLetters)
                binding.mainFabAdd.setTextColor(newColorLetters)
                preferences.saveColorLetters(newColorLetters)
                showBackGroundApp()
            }
        )
    }

    private fun showBackGroundApp() {
        dialogUtils.colorDialog(
            titleDialogColor = getString(R.string.selecione_a_cor_de_fundo),
            actionPositiveButton = { newBackgroundColor ->
                binding.containerRootActivityMain.setBackgroundColor(newBackgroundColor)
                preferences.saveColorBackground(newBackgroundColor)
            }
        )
    }
}