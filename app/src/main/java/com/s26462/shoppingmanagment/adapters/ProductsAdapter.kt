package com.shoppingmanagment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.adapters.ShoppingListItemAdapter
import com.s26462.shoppingmanagment.models.Products
import kotlinx.android.synthetic.main.item_member.view.*
import kotlinx.android.synthetic.main.item_products.view.*

open class ProductsAdapter(private val context: Context,private var list: ArrayList<Products>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_products,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.tv_product_name.text = model.name
            holder.itemView.tv_product_amount.text = model.amount
            holder.itemView.tv_product_price.text = model.price
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, product: Products)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}