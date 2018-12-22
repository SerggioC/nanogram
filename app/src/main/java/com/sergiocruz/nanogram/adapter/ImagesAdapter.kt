package com.sergiocruz.nanogram.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.model.endpoint.usermedia.Images


class ImagesAdapter(private val imageClickListener: ImageClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var imageList: List<Images>? = null
    private var mRecipeName: String? = null
    private var mServingsNum: Int? = null


    override fun getItemCount(): Int {
        return imageList?.size ?: 0
    }

    fun swapData(data: List<Images>?) {
        this.imageList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item_layout, parent, false)
        return IngredientRowViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewType = getItemViewType(position)

        val viewHolder = holder as IngredientRowViewHolder
        val ingredient = imageList!![position - 1] // deduce header index

        val checked = ingredient.getChecked() != null && ingredient.getChecked() === 1
        viewHolder.checkBox.setChecked(checked)
        viewHolder.ingredientName.setText(capitalize(ingredient.getIngredient()))
        viewHolder.quantity.setText(ingredient.getQuantity() + " " + ingredient.getMeasure())

        animateItemViewSlideFromBottom(holder.itemView, 50 * position)

    }

    interface ImageClickListener {
        fun onImageClicked(image: Images?)
    }

    internal inner class HeaderIngredientViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val recipeNameTV: TextView
        val servingSizeTV: TextView
        val ingredientsNum: TextView

        init {
            recipeNameTV = itemView.findViewById(R.id.recipe_name)
            servingSizeTV = itemView.findViewById(R.id.servings_num)
            ingredientsNum = itemView.findViewById(R.id.ingredients_num)
        }
    }

    inner class IngredientRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val ingredientName: TextView
        internal val quantity: TextView
        internal val checkBox: android.support.v7.widget.AppCompatCheckBox

        init {
            ingredientName = itemView.findViewById(R.id.ingredient_row)
            quantity = itemView.findViewById(R.id.quantity_row)
            checkBox = itemView.findViewById(R.id.ingredient_checkBox)

            checkBox.setOnClickListener {
                imageClickListener.onImageClicked(imageList?.get(adapterPosition))
            }

        }
    }

    companion object {
        private val TYPE_HEADER = 0
        private val TYPE_INGREDIENT = 1
    }
}