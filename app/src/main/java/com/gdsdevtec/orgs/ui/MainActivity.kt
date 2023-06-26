package com.gdsdevtec.orgs.ui

import OrgsPreferences
import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.gdsdevtec.orgs.R
import com.gdsdevtec.orgs.data.database.db.AppDatabase
import com.gdsdevtec.orgs.databinding.ActivityMainBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.utils.const.Constants
import com.gdsdevtec.orgs.utils.ext.DialogUtils
import com.gdsdevtec.orgs.utils.ext.nextScreen
import com.gdsdevtec.orgs.utils.ext.onClick


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
    ) {result->
        if (result.resultCode == RESULT_OK) {
            val img = result.data?.extras?.get("data") as? Bitmap
           img?.let {
               binding.includeToolbar.appBarImageView.apply {
                   isVisible = true
                   binding.includeToolbar.appBarImageView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                   binding.includeToolbar.appBarImageView.setImageBitmap(img)
               }
           }
        }
    }
    private lateinit var productAdapter: ProductsAdapter
    private lateinit var dialogUtils: DialogUtils
    private var allProducts : List<Product> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        allProducts = dao.getAllProducts()
        bindingSetup()
    }

    private fun bindingSetup() = binding.run {
        dialogUtils = DialogUtils(this@MainActivity)
        includeToolbar.appbarContainer.onClick {
            showDialogChangeApp()
        }
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
    private fun productsAdapter() = ProductsAdapter(
        listProducts = allProducts,
        imageLoader = DialogUtils(this@MainActivity).imageLoader,
        itemSelected = { itemSelected ->
            nextScreen(DetailsProductActivity(), Pair(Constants.PRODUCT_ID, itemSelected.id))
        },
        onLongItemClick = ::onLongItemClick
    )

    private fun onLongItemClick(view: View?,product: Product) : Boolean {
        val popUp = PopupMenu(this,view)
        val inflater : MenuInflater = popUp.menuInflater
        inflater.inflate(R.menu.menu_description_producs,popUp.menu)
        popUp.setOnMenuItemClickListener{itemMenu->
            onClickMenuPopUpItem(itemMenu,product)
        }
        popUp.show()
        return true
    }

    private fun onClickMenuPopUpItem(itemMenu: MenuItem?, product: Product): Boolean {
        when(itemMenu?.itemId){
            R.id.menu_editable-> {
                nextScreen(FormActivity(), Pair(Constants.PRODUCT_ID, product.id))
            }
            R.id.menu_excluded-> {
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

    private fun showDialogChangeApp() {
        dialogUtils.showDialog(
            title = getString(R.string.deseja_personalizar_seu_app),
            message = getString(R.string.voce_pode_alterar_s_cores_do_seu_aplicativo),
            textPositiveButton = getString(R.string.sim),
            textNegativeButton = getString(R.string.n_o),
            positiveButton = {
                selectedOptionsChangeAppBar()
            },
        )
    }

    private fun selectedOptionsChangeAppBar() {
        dialogUtils.showDialog(
            title = getString(R.string.escolha_um_opcao),
            message = getString(R.string.como_deseja_prosseguir),
            textPositiveButton = getString(R.string.camera),
            positiveButton = {
                requestCamera.launch(Manifest.permission.CAMERA)
            },
            textNegativeButton = "Selecionar Cor",
            negativeButton = {
                changeColorStatusBar()
            }
        )
    }

    private fun changeColorStatusBar() {
        dialogUtils.colorDialog(
            titleDialogColor = getString(R.string.selecione_a_cor_da_status_bar),
            actionPositiveButton = {newColorStatusBar->
                window.statusBarColor = newColorStatusBar
                window.navigationBarColor = newColorStatusBar
                preferences.saveColorStatusBar(newColorStatusBar)
                showDialogChangeAppBar()
            },
        )
    }

    private fun showDialogChangeAppBar() {
        dialogUtils.colorDialog(
            titleDialogColor = getString(R.string.selecione_a_cor_da_appbar),
            actionPositiveButton = {newColorAppBar->
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
            actionPositiveButton = {newColorFab->
                binding.mainFabAdd.setBackgroundColor(newColorFab)
                preferences.saveColorBtn(newColorFab)

                showDialogColorLetters()
            }
        )
    }

    private fun showDialogColorLetters() {
        dialogUtils.colorDialog(
            titleDialogColor = getString(R.string.selecione_a_cor_das_letras),
            actionPositiveButton = {newColorLetters->
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
            actionPositiveButton = {newBackgroundColor->
                binding.containerRootActivityMain.setBackgroundColor(newBackgroundColor)
                preferences.saveColorBackground(newBackgroundColor)

            }
        )
    }
}