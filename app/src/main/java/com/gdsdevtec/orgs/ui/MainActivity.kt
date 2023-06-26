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
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.gdsdevtec.orgs.R
import com.gdsdevtec.orgs.data.database.db.AppDatabase
import com.gdsdevtec.orgs.databinding.ActivityMainBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.utils.ext.DialogUtils
import com.gdsdevtec.orgs.utils.ext.nextScreen
import com.gdsdevtec.orgs.utils.ext.onClick


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val preferences by lazy { OrgsPreferences.getInstance(this) }
    private lateinit var productAdapter: ProductsAdapter
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
           img?.let { binding.includeToolbar.appBarImageView.setImageBitmap(img) }
        }
    }

    private val database by lazy { AppDatabase.getInstance(this) }
    private lateinit var dialogUtils: DialogUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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
    }
    private fun productsAdapter() = ProductsAdapter(
        listProducts = database.productDao().getAllProducts(),
        imageLoader = DialogUtils(this@MainActivity).imageLoader,
        itemSelected = { itemSelected ->
            nextScreen(DetailsProductActivity(), Pair("PRODUCT", itemSelected))
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
                nextScreen(FormActivity(), Pair("PRODUCT", product))
            }
            R.id.menu_excluded-> {
                database.productDao().deleteProduct(product)
                productAdapter.updateList(database.productDao().getAllProducts())
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        productAdapter.updateList(database.productDao().getAllProducts())
        productAdapter.updateList(database.productDao().getAllProducts())
    }

    private fun showDialogChangeApp() {
        dialogUtils.showDialog(
            title = "Deseja personalizar seu app?",
            message = "Voce pode alterar s cores do seu aplicativo",
            textPositiveButton = "Sim",
            textNegativeButton = "Não",
            positiveButton = {
                selectedOptionsChangeAppBar()
            },
        )
    }

    private fun selectedOptionsChangeAppBar() {
        dialogUtils.showDialog(
            title = "Escolha um opcao",
            message = "Como deseja prosseguir?",
            textPositiveButton = "Camera",
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
            titleDialogColor = "Selecione a cor da status Bar",
            actionPositiveButton = {newColorStatusBar->
                window.statusBarColor = newColorStatusBar
                preferences.saveColorStatusBar(newColorStatusBar)
                showDialogChangeAppBar()
            },
            actionNegativeButton = {
                selectedOptionsChangeAppBar()
            }
        )
    }

    private fun showDialogChangeAppBar() {
        dialogUtils.colorDialog(
            titleDialogColor = "Selecione a Cor da AppBar",
            actionPositiveButton = {newColorAppBar->
                binding.includeToolbar.apply {
                    appBarImageView.isVisible = false
                    appbarContainer.setBackgroundColor(newColorAppBar)
                    OrgsPreferences(this@MainActivity).saveColorAppBar(newColorAppBar)

                }
                showDialogColorFAB()
            },
            actionNegativeButton = {
                changeColorStatusBar()
            }
        )
    }

    private fun showDialogColorFAB() {
        dialogUtils.colorDialog(
            titleDialogColor = "Selecione a Cor do botao",
            actionPositiveButton = {newColorFab->
                binding.mainFabAdd.setBackgroundColor(newColorFab)
                preferences.saveColorBtn(newColorFab)

                showDialogColorLetters()
            },
            actionNegativeButton = {
                showDialogChangeApp()
            }
        )
    }

    private fun showDialogColorLetters() {
        dialogUtils.colorDialog(
            titleDialogColor = "Selecione a Cor das letras",
            actionPositiveButton = {newColorLetters->
                binding.includeToolbar.collapsingToolbarLayout.setExpandedTitleColor(newColorLetters)
                binding.mainFabAdd.setTextColor(newColorLetters)
                preferences.saveColorLetters(newColorLetters)
                showBackGroundApp()
            },
            actionNegativeButton = {
                showDialogColorFAB()
            }
        )
    }

    private fun showBackGroundApp() {
        dialogUtils.colorDialog(
            titleDialogColor = "Selecione a Cor de fundo",
            actionPositiveButton = {newBackgroundColor->
                binding.containerRootActivityMain.setBackgroundColor(newBackgroundColor)
                preferences.saveColorBackground(newBackgroundColor)

            },
            actionNegativeButton = {
                showDialogColorLetters()
            }
        )
    }


}