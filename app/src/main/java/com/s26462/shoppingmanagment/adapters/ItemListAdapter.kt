package com.s26462.shoppingmanagment.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.s26462.shoppingmanagment.R
import com.s26462.shoppingmanagment.activities.ItemListActivity
import com.s26462.shoppingmanagment.models.Item
import kotlinx.android.synthetic.main.item_sl_details.view.*

open class ItemListAdapter(private val context: Context, private var list: ArrayList<Item>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_sl_details, parent, false)
//      ustawienie widoku
        val layoutParams = LinearLayout.LayoutParams(
//          wielkość tego ll to 70% ekranu, żeby było widać pozostałe kategorie
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
//      ustawienie marginesów
        layoutParams.setMargins((15.toDp().toPx()), 0, (40.toDp().toPx()),0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder) {
//          zamiana przycisku na okno wprowadzania w widoku i obsługa przycisków
            if(position == list.size - 1){
                holder.itemView.tv_add_item_list.visibility = View.VISIBLE
                holder.itemView.ll_item.visibility = View.GONE
            } else {
                holder.itemView.tv_add_item_list.visibility = View.GONE
                holder.itemView.ll_item.visibility = View.VISIBLE
            }

            holder.itemView.tv_item_list_title.text = model.title
            holder.itemView.tv_add_item_list.setOnClickListener {
                holder.itemView.tv_add_item_list.visibility = View.GONE
                holder.itemView.cv_add_item_list_name.visibility = View.VISIBLE
            }
//          zamnięcie okna dodawania nowej kategorii
            holder.itemView.ib_close_list_name.setOnClickListener {
                holder.itemView.tv_add_item_list.visibility = View.VISIBLE
                holder.itemView.cv_add_item_list_name.visibility = View.GONE
            }
//          dodanie nowej kategorii
            holder.itemView.ib_done_list_name.setOnClickListener {
                val listName = holder.itemView.et_item_list_name.text.toString()

                if(listName.isNotEmpty()){
                    if(context is ItemListActivity){
                        context.createItemList(listName)
                    }
                } else {
                    Toast.makeText(context, "Wprowadź nazwę kategorii.", Toast.LENGTH_SHORT).show()
                }
            }
//          edycja nazwy
            holder.itemView.ib_edit_list_name.setOnClickListener {
                holder.itemView.et_edit_item_list_name.setText(model.title)
                holder.itemView.ll_title_view.visibility = View.GONE
                holder.itemView.cv_edit_item_list_name.visibility = View.VISIBLE
            }
//          zamknięcie edycji nazwy
            holder.itemView.ib_close_editable_view.setOnClickListener {
                holder.itemView.ll_title_view.visibility = View.VISIBLE
                holder.itemView.cv_edit_item_list_name.visibility = View.GONE
            }
//          edycja nazwy kategorii
            holder.itemView.ib_done_edit_list_name.setOnClickListener {
                val listName = holder.itemView.et_edit_item_list_name.text.toString()
                if(listName.isNotEmpty()){
                    if(context is ItemListActivity){
                        context.updateItemList(position, listName, model)
                    }
                } else {
                    Toast.makeText(context, "Wprowadź nazwę kategorii.", Toast.LENGTH_SHORT).show()
                }
            }

//          usunięcie kategorii
            holder.itemView.ib_delete_list.setOnClickListener {
                alertDialogForDeleteList(position, model.title)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
//  metody do do poprania rozdzielczości ekranu i zamiany jej na int, żeby potem móc określić wielkość RV w procentach
//  dp z px
    private fun Int.toDp(): Int = (this/Resources.getSystem().displayMetrics.density).toInt()
//  px z dp
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

//  alert, który ma być wyświetlony, w celu potwierdzenia usunięcia
    private fun alertDialogForDeleteList(position: Int, title: String){
        val builder = AlertDialog.Builder(context)
//      ustawiam tytuł
        builder.setTitle("Usunięcie kategorii")
//      ustawiam treść
        builder.setMessage("Jesteś pewien, że chcesz usunąć kategorię $title?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
//      jeżeli tak
        builder.setPositiveButton("TAK") {
            dialogInterface, which ->
            dialogInterface.dismiss()

            if(context is ItemListActivity) {
                context.deleteItemList(position)
            }
        }
//      jeżeli nie
        builder.setNegativeButton("NIE") {
            dialogInterface, which ->
            dialogInterface.dismiss()
        }
//      utworzenie alertDialog
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }


    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}