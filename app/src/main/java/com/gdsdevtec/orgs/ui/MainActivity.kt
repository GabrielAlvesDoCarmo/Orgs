package com.gdsdevtec.orgs.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.gdsdevtec.orgs.dao.ProductDao
import com.gdsdevtec.orgs.databinding.ActivityMainBinding
import com.gdsdevtec.orgs.utils.ext.DialogUtils
import com.gdsdevtec.orgs.utils.ext.nextScreen
import com.gdsdevtec.orgs.utils.ext.onClick


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var productAdapter: ProductsAdapter
    private val requestCamera = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) launchCam.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    private val launchCam = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {

    }

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
            },
            actionNegativeButton = {
                showDialogColorLetters()
            }
        )
    }

    private fun productsAdapter() = ProductsAdapter(
        listProducts = getProducts(),
        imageLoader = DialogUtils(this@MainActivity).imageLoader,
        itemSelected = { itemSelected ->
            nextScreen(DetailsProductActivity(), Pair("PRODUCT", itemSelected))
        }
    )

    override fun onResume() {
        super.onResume()
        productAdapter.updateList(getProducts())
    }

    private fun getProducts() = ProductDao.getAllProducts()
}