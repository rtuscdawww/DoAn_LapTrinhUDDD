package com.example.vlufind.data

import android.content.Context

object FavoriteManager {

    private const val PREF_NAME = "vlufind_favorites"
    private const val KEY_FAVORITES = "favorite_ids"

    fun getFavorites(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        return prefs.getStringSet(
            KEY_FAVORITES,
            emptySet()
        )?.toSet() ?: emptySet()
    }

    fun isFavorite(context: Context, postId: Int): Boolean {
        return getFavorites(context).contains(postId.toString())
    }

    fun toggleFavorite(context: Context, postId: Int): Boolean {
        val favorites = getFavorites(context).toMutableSet()
        val id = postId.toString()

        val isFavorite = if (favorites.contains(id)) {
            favorites.remove(id)
            false
        } else {
            favorites.add(id)
            true
        }

        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .putStringSet(KEY_FAVORITES, favorites)
            .apply()

        return isFavorite
    }
}