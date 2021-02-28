package com.dmytroandriichuk.cmediacal.ui.search.model

//POJO item of SearchListChildAdapter
data class SearchListChildItem(
        val name: String,
        val price: Double
) {
    override fun toString(): String {
        return "SearchListChildItem(name='$name', price=$price)"
    }
}