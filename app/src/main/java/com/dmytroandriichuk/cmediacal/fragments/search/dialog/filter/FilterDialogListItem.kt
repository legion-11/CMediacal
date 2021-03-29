package com.dmytroandriichuk.cmediacal.fragments.search.dialog.filter

data class FilterDialogListItem(
    //filter category
    val title: String,
    // list of filters in category (small, medium, ...)
    val listOfFilters: List<String>
){
    override fun toString(): String {
        return "FilterListParentItem(title='$title', listOfFilters=$listOfFilters)"
    }
}
