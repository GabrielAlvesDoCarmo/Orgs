package com.gdsdevtec.orgs.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.gdsdevtec.orgs.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        bindingSetup()
    }

    private fun bindingSetup() = binding.run {
        includeToolbar.appBarImageView.onClick {
            DialogUtils(this@MainActivity).showDialog(
                title = "Escolha um opcao",
                textPositiveButton = "Camera",
                positiveButton = {
                    requestCamera.launch(Manifest.permission.CAMERA)
                },
                textNegativeButton = "Selecionar Cor",
                negativeButton = {
                    ColorPickerDialogBuilder
                        .with(this@MainActivity)
                        .setTitle("Selecionar uma cor")
                        .initialColor(R.color.default_color)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)

                        .setPositiveButton(R.string.dialog_button_confirm) { _, selectedColor, _ ->
                                    binding.includeToolbar.apply {
                                        appBarImageView.isVisible = false
                                        appbarContainer.setBackgroundColor(selectedColor)
                                        window.statusBarColor = selectedColor
                                    }
                        }
                        .setNegativeButton(R.string.dialog_button_cancel) { _, _ ->
                            return@setNegativeButton
                        }
                        .build()
                        .show()

                }
            )
        }
        productAdapter = productsAdapter()
        rvMain.adapter = productAdapter
        mainFabAdd.onClick {
            nextScreen(FormActivity())
        }
    }

    private fun productsAdapter() = ProductsAdapter(
        listProducts = getProducts(),
        imageLoader = DialogUtils(this@MainActivity).imageLoader,
        itemSelected = { itemSelected ->
            nextScreen(DetailsProductActivity(), Pair("PRODUCT", itemSelected))
        },

        )

    override fun onResume() {
        super.onResume()
        productAdapter.updateList(getProducts())
    }
    private fun getProducts() = ProductDao.getAllProducts()
}