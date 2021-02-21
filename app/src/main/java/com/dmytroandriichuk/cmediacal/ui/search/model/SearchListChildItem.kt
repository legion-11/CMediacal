package com.dmytroandriichuk.cmediacal.ui.search.model

data class SearchListChildItem(
        val name: String,
        val price: Float
) {
    override fun toString(): String {
        return "SearchListChildItem(name='$name', price=$price)"
    }
}