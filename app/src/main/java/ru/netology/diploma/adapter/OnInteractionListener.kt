package ru.netology.diploma.adapter

interface OnInteractionListener<T> {
    fun onLike(item: T) {}
    fun onEdit(item: T) {}
    fun onRemove(item: T) {}
    fun onShare(item: T) {}
    fun onUserIds(item: T) {} //todo вынести в отдельный интерфейс для передачи списка id
    fun onUnauthorized(item: T) {}
    fun onSelect(item: T) {}
    fun onAuthor(item: T) {}
}

interface OnUserIdsListener {
    fun onUserIds(list: List<Int>) {}
}