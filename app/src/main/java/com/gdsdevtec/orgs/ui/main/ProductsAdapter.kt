package com.gdsdevtec.orgs.ui.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.gdsdevtec.orgs.databinding.ItemProductBinding
import com.gdsdevtec.orgs.domain.products.Product
import com.gdsdevtec.orgs.utils.ext.convertBigDecimalForCurrencyLocale
import com.gdsdevtec.orgs.utils.ext.loadImageDataWithUrl
import com.gdsdevtec.orgs.utils.ext.onClick

class ProductsAdapter(
    listProducts: List<Product>,
    private val imageLoader: ImageLoader,
    private val itemSelected: (Product) -> Unit,
    private val onLongItemClick : (View?, Product)-> Boolean
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {
    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val listProducts = listProducts.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = listProducts.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = listProducts[position]
        holder.binding.apply {
            bindItemView(this, product)
            root.onClick { itemSelected.invoke(product) }
            root.setOnLongClickListener {
                onLongItemClick.invoke(it,product)
            }
        }
    }

    private fun bindItemView(binding: ItemProductBinding, product: Product) = with(binding) {
        itemProductName.text = product.name
        itemProductDescription.text = product.description
        itemProductValue.text = product.value.convertBigDecimalForCurrencyLocale()
        itemDateEvent.text = product.date
        itemTimeEvent.text = product.time
        imageSetup(itemProductImage, product)
        guidelineSetup(guideline, product)
    }

    private fun guidelineSetup(guideline: Guideline, product: Product) {
        val guidelinePercent = if (product.image == null) 0.0F else 0.3F
        guideline.setGuidelinePercent(guidelinePercent)
    }

    private fun imageSetup(image: AppCompatImageView, product: Product) {
        image.visibility = if (product.image != null) View.VISIBLE else View.GONE
        image.loadImageDataWithUrl(imageLoader, product.image)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(products: List<Product>) {
        listProducts.clear()
        listProducts.addAll(products)
        notifyDataSetChanged()
    }

}