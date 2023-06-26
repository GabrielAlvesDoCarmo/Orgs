package com.gdsdevtec.orgs.ui

import OrgsPreferences
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.gdsdevtec.orgs.R
import com.gdsdevtec.orgs.data.database.db.AppDatabase
import com.gdsdevtec.orgs.databinding.ActivityMainBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.utils.const.Constants
import com.gdsdevtec.orgs.utils.ext.DialogUtils
import com.gdsdevtec.orgs.utils.ext.nextScreen
import com.gdsdevtec.orgs.utils.ext.onClick
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val preferences by lazy { OrgsPreferences.getInstance(this) }
    private val dao by lazy { AppDatabase.getInstance(this).productDao() }
    private val requestCamera = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) launchCam.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    private val launchCam = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
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
    }
    private lateinit var productAdapter: ProductsAdapter
    private lateinit var dialogUtils: DialogUtils
    private var allProducts: List<Product> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        allProducts = dao.getAllProducts()
        bindingSetup()
    }

    private fun bindingSetup() = binding.run {
        dialogUtils = DialogUtils(this@MainActivity)
        setupAppBar()
        productAdapter = productsAdapter()
        rvMain.adapter = productAdapter
        mainFabAdd.onClick {
            nextScreen(FormActivity())
        }
//        swipeRefreshLayout.setOnRefreshListener {
//            allProducts = dao.getAllProducts()
//            productAdapter.updateList(allProducts)
//        }
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
        setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
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
                    allProducts = dao.getAllProducts()
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
        imageLoader = DialogUtils(this@MainActivity).imageLoader,
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
            R.id.menu_editable -> {
                nextScreen(FormActivity(), Pair(Constants.PRODUCT_ID, product.id))
            }

            R.id.menu_excluded -> {
                dao.deleteProduct(product)
                productAdapter.updateList(dao.getAllProducts())
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        allProducts = dao.getAllProducts()
        productAdapter.updateList(allProducts)
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
                binding.includeToolbar.collapsingToolbarLayout.setExpandedTitleColor(
                    newColorLetters
                )
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