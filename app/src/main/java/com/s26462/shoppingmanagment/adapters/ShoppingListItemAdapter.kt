package com.s26462.shoppingmanagment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.models.ShoppingList
import kotlinx.android.synthetic.main.activity_create_shopping_list.*
import kotlinx.android.synthetic.main.item_shopping_list.view.*

open class ShoppingListItemAdapter(private val context: Context, private var list: ArrayList<ShoppingList>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_shopping_list, parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
//        https://github.com/bumptech/glide
//        ustawienie awataru
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_spnglist_place_holder)
                .into(holder.itemView.iv_spnglist_image);

            holder.itemView.tv_name.text = model.name
            holder.itemView.tv_created_by.text = "Utworzył ${model.createdBy}"

            holder.itemView.setOnClickListener{
                if(onClickListener != null) {
                    onClickListener!!.onClick(position,model)
                }
            }
        }
    }

    interface  OnClickListener{
        fun onClick(position: Int, model: ShoppingList)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}