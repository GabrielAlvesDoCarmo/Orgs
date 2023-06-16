package com.gdsdevtec.orgs.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gdsdevtec.orgs.databinding.ItemProductBinding
import com.gdsdevtec.orgs.model.Product

class ProductsAdapter(
    private val listProducts : List<Product>
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {
    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

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
            this.itemProductName.text = product.name
            this.itemProductDescription.text = product.description
            this.itemProductValue.text = convertBigDecimal(product)
        }
    }

    private fun convertBigDecimal(product: Product) = try {
        product.value.toString()
    }catch (e: Exception) {
        "Erro"
    }

}