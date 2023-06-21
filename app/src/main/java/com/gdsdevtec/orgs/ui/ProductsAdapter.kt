package com.gdsdevtec.orgs.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.gdsdevtec.orgs.R
import com.gdsdevtec.orgs.databinding.ItemProductBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.utils.ext.convertBigDecimalForCurrencyLocale

class ProductsAdapter(
    listProducts: List<Product>
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
            bindItemView(this,product)
        }
    }

    private fun bindItemView(binding: ItemProductBinding, product: Product) = with(binding) {
        itemProductName.text = product.name
        itemProductDescription.text = product.description
        itemProductValue.text = product.value.convertBigDecimalForCurrencyLocale()
        itemProductImage.load(getImageProduct(product.image))
    }

    private fun getImageProduct(image: String?): Any {
        return if (image.isNullOrEmpty()) R.drawable.ic_not_image_default
        else image
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(products: List<Product>) {
        listProducts.clear()
        listProducts.addAll(products)
        notifyDataSetChanged()
    }

}