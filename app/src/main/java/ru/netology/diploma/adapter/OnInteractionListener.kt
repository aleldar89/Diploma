package ru.netology.diploma.adapter

interface OnInteractionListener<T> {
    fun onLike(item: T) {}
    fun onParticipate(item: T) {}
    fun onEdit(item: T) {}
    fun onRemove(item: T) {}
    fun onShare(item: T) {}
    fun onUnauthorized(item: T) {}
    fun onSelect(item: T) {}
    fun onAuthor(item: T) {}
}

interface OnUserIdsListener {
    fun onUserIds(list: List<Int>) {}
}