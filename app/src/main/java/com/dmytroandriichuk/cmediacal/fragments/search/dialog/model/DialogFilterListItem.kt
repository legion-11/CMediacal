package com.dmytroandriichuk.cmediacal.fragments.search.dialog.model

data class DialogFilterListItem(
    //filter category
    val title: String,
    // list of filters in category (small, medium, ...)
    val listOfFilters: List<String>
){
    override fun toString(): String {
        return "FilterListParentItem(title='$title', listOfFilters=$listOfFilters)"
    }
}
