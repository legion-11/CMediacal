package com.dmytroandriichuk.cmediacal.ui.search.dialog.dataClasses

data class FilterListParentItem(
    val title: String,
    val listOfFilters: List<String>
){
    override fun toString(): String {
        return "FilterListParentItem(title='$title', listOfFilters=$listOfFilters)"
    }
}
