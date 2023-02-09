package ru.netology.diploma.adapter

interface OnInteractionListener<T> {
    fun onLike(item: T) {}
    fun onEdit(item: T) {}
    fun onRemove(item: T) {}
    fun onShare(item: T) {}
    fun onUserIds(item: T) {}
    fun onUnauthorized(item: T) {}
    fun onChoose(item: T) {}
}