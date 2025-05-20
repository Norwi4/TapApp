package com.norwi.tapapp2

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductsAdapter(
    private val items: MutableList<Product>,
    private val onCheckedChanged: (Product, Boolean) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkBoxBought)
        val textName: TextView = view.findViewById(R.id.textProductName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = items[position]
        holder.textName.text = product.name
        holder.checkBox.isChecked = product.isBought

        // Чтобы не срабатывал слушатель при повторном биндинге
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = product.isBought
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChanged(product, isChecked)
        }
    }

    override fun getItemCount() = items.size

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun add(product: Product) {
        items.add(product)
        notifyItemInserted(items.size - 1)
    }
}
