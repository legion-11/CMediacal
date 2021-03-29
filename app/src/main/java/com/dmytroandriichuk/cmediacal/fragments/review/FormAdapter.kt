package com.dmytroandriichuk.cmediacal.fragments.review

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
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
            val textWatcher = CustomTextWatcher()
            ViewHolderWithSlider(view, textWatcher)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder.itemViewType == 1) {
            val item = dataSet[position]
            (holder as ViewHolderWithSlider).deleteBtn.setOnClickListener {
                changeDataSetListener.removeFormItem(position)
            }
            createAdapters(holder.formCategorySpinner, spinnerSelector.keys.toList(), holder.view.context, item.positionCategory)

            val categoryName = holder.formCategorySpinner.selectedItem as String
            createAdapters(holder.formSubcategorySpinner, spinnerSelector[categoryName]!!.toList(), holder.view.context, item.positionSubcategory)


            holder.formCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    dataSet[position].positionCategory = pos
                    val newCategoryName = holder.formCategorySpinner.selectedItem as String
                    ArrayAdapter(holder.view.context, android.R.layout.simple_spinner_item, spinnerSelector[newCategoryName]!!.toList())
                            .also { arrayAdapter ->
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
            holder.textWatcher.position = position
            holder.price.setText("$ ${dataSet[position].price}")

        } else {
            (holder as ViewHolderAdd).addBtn.setOnClickListener {
                changeDataSetListener.addFormItem()
            }
        }
    }


    private fun createAdapters(spinner: Spinner, list: List<String>, context: Context, position: Int){
        ArrayAdapter(context, android.R.layout.simple_spinner_item, list)
                .also { arrayAdapter ->
                    // Specify the layout to use when the list of choices appears
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.apply {
                        adapter = arrayAdapter
                        setSelection(position)
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

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ViewHolderWithSlider(val view: View, val textWatcher: CustomTextWatcher) : BaseViewHolder(view) {
        val formCategorySpinner: Spinner = view.findViewById(R.id.formCategorySpinner)
        val formSubcategorySpinner: Spinner = view.findViewById(R.id.formSubcategorySpinner)
        val deleteBtn: ImageButton = view.findViewById(R.id.formCancelButton)
        val price: EditText = view.findViewById(R.id.formPriceET)
        init {
            price.addTextChangedListener(textWatcher)
            price.movementMethod = ScrollingMovementMethod()
        }
    }

    class ViewHolderAdd(val view: View) : BaseViewHolder(view) {
        val addBtn: ImageButton = view.findViewById(R.id.addBtn)
    }

    inner class CustomTextWatcher: TextWatcher {
        var position = 0
        private var _ignore = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            Log.d("TAG", "afterTextChanged: 11111")
            if (_ignore) { return }
            _ignore = true
            var str = s.toString().replace(Regex("[$ ]"), "")
            val split = str.split(".", limit = 2)
            var afterDot = ""
            if (split.size == 2) {
                afterDot = split[1].replace(".", "")
                afterDot = "." + afterDot.substring(0, if (afterDot.length > 2) 2 else afterDot.length)
            }
            val beforeDot = if (split[0].length>1) split[0].trimStart('0') else split[0]
            str = beforeDot + afterDot
            dataSet[position].price = if (str != "")  str.toFloat() else .0f
            val newText =  if (str=="") "" else "$ $str"
            s.replace(0, s.length, newText, 0, newText.length)
            _ignore = false
        }
    }

    interface ChangeDataSetListener {
        fun removeFormItem(position: Int)
        fun addFormItem()
    }

    class FormItem(var positionCategory: Int=0, var positionSubcategory: Int=0, var price: Float = 0f)

}
