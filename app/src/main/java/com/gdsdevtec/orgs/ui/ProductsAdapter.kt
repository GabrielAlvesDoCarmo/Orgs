package com.gdsdevtec.orgs.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.gdsdevtec.orgs.databinding.ItemProductBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.utils.ext.convertBigDecimalForCurrencyLocale
import com.gdsdevtec.orgs.utils.ext.loadingImage

class ProductsAdapter(
    listProducts: List<Product>,
    private val imageLoader: ImageLoader
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
        }
    }

    private fun bindItemView(binding: ItemProductBinding, product: Product) = with(binding) {
        itemProductName.text = product.name
        itemProductDescription.text = product.description
        itemProductValue.text = product.value.convertBigDecimalForCurrencyLocale()
        val isVisibility = if (product.image != null) View.VISIBLE else View.GONE
        val guidelinePercent = if (product.image == null) 0.0F else 0.3F
        itemProductImage.apply {
            visibility = isVisibility
            loadingImage(product.image, imageLoader)
        }
        guideline.setGuidelinePercent(guidelinePercent)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(products: List<Product>) {
        listProducts.clear()
        listProducts.addAll(products)
        notifyDataSetChanged()
    }

}