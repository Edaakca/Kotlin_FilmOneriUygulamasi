package com.edaakca.beyazperdeprojesi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edaakca.beyazperdeprojesi.R
import com.edaakca.beyazperdeprojesi.databinding.MenuItemBinding

class MenuAdapter(
    private val menuItems: List<Pair<String, Int>>, // Başlık ve icon olarak çift
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val (title, icon) = menuItems[position]
        holder.bind(title, icon)
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String, icon: Int) {
            binding.itemTitle.text = title
            binding.itemIcon.setImageResource(icon) // Iconu dinamik olarak set ediyoruz

            itemView.setOnClickListener {
                onItemClick(title)
            }
        }
    }
}
