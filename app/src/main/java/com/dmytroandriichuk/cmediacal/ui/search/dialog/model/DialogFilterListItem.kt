package com.dmytroandriichuk.cmediacal.ui.search.dialog.model

data class DialogFilterListItem(
    val title: String,
    val listOfFilters: List<String>
){
    override fun toString(): String {
        return "FilterListParentItem(title='$title', listOfFilters=$listOfFilters)"
    }
}
