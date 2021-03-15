package com.dmytroandriichuk.cmediacal.fragments.review

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.dmytroandriichuk.cmediacal.R

class FormAdapter(private var dataSet: MutableList<FormItem>,
                  private var spinnerSelector: Map<String, List<String>>,
                  private val changeDataSetListener: ChangeDataSetListener):
        RecyclerView.Adapter<FormAdapter.BaseViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        if (position == dataSet.size) { return 0 }
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate( R.layout.add_image, parent, false)
            ViewHolderAdd(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate( R.layout.form_item, parent, false)
            ViewHolderWithSlider(view)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder.itemViewType == 1) {
            val item = dataSet[position]
            (holder as ViewHolderWithSlider).deleteBtn.setOnClickListener {
                changeDataSetListener.removeFormItem(position)
            }

            ArrayAdapter(holder.view.context, android.R.layout.simple_spinner_item, spinnerSelector.keys.toList())
                    .also { arrayAdapter ->
                        // Specify the layout to use when the list of choices appears
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        holder.formCategorySpinner.apply {
                            adapter = arrayAdapter
                            setSelection(item.positionCategory)
                        }
                    }

            val categoryName = holder.formCategorySpinner.selectedItem as String
            ArrayAdapter(holder.view.context, android.R.layout.simple_spinner_item, spinnerSelector[categoryName]!!.toList())
                    .also {  arrayAdapter ->
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        holder.formSubcategorySpinner.apply {
                            adapter = adapter
                            setSelection(item.positionSubcategory)
                        }
                    }

            holder.formCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    dataSet[position].positionCategory = pos
                    val newCategoryName = holder.formCategorySpinner.selectedItem as String
                    ArrayAdapter(holder.view.context, android.R.layout.simple_spinner_item, spinnerSelector[newCategoryName]!!.toList())
                            .also { arrayAdapter ->
                                // Specify the layout to use when the list of choices appears
                                dataSet[position].positionSubcategory = 0
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                holder.formSubcategorySpinner.apply {
                                    adapter = arrayAdapter
                                    setSelection(dataSet[position].positionSubcategory)
                                }
                            }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            holder.formSubcategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos2: Int, id: Long) {
                    dataSet[position].positionSubcategory = pos2
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            holder.price.setText(dataSet[position].price.toString())

            // todo proper price formating
            holder.price.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val priceString = holder.price.text.toString()
                    if (priceString != "") {
                        dataSet[position].price = priceString.toFloat()
                    }
                }

            })

        } else {
            (holder as ViewHolderAdd).addBtn.setOnClickListener {
                changeDataSetListener.addFormItem()
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size + 1

    fun onItemRemoved(position: Int) {
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, dataSet.size)
    }

    fun onItemAdded() {
        notifyItemInserted(dataSet.size - 1)
    }

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    class ViewHolderWithSlider(val view: View) : BaseViewHolder(view) {
        val formCategorySpinner: Spinner = view.findViewById(R.id.formCategorySpinner)
        val formSubcategorySpinner: Spinner = view.findViewById(R.id.formSubcategorySpinner)
        val deleteBtn: ImageButton = view.findViewById(R.id.formCancelButton)
        val price: EditText = view.findViewById(R.id.formPriceET)
    }

    class ViewHolderAdd(val view: View) : BaseViewHolder(view) {
        val addBtn: ImageButton = view.findViewById(R.id.addBtn)
    }

    interface ChangeDataSetListener {
        fun removeFormItem(position: Int)
        fun addFormItem()
    }

    class FormItem(var positionCategory: Int=0, var positionSubcategory: Int=0, var price: Float = 0f)

}
