package com.example.plantapp.network.repository.article

import android.util.Log
import com.example.plantapp.data.model.Article
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ArticleRepository {
    private val TAG = "ArticleRepository"

    private val _db = Firebase.firestore

    fun getArticlesRealTime(
        onSuccess: ((articles: List<Article?>) -> Unit)? = null,
        onError: ((exception: Exception) -> Unit)? = null
    ) {
        val docRef = _db.collection("article")
        docRef.addSnapshotListener {
                snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot == null) {
                Log.d(TAG, "No such document")
                onError?.invoke(Exception("No such document"))
                return@addSnapshotListener
            }

            if (snapshot.isEmpty) {
                onSuccess?.invoke(mutableListOf())
                return@addSnapshotListener
            }

            val articles = snapshot.documents.map {
                it.toObject(Article::class.java)
            }
            onSuccess?.invoke(articles)
        }
    }
    fun getArticles(
        onSuccess: ((articles: List<Article?>) -> Unit)? = null,
        onError: ((exception: Exception) -> Unit)? = null
    ) {
        val docRef = _db.collection("article")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document == null) {
                    Log.d(TAG, "No such document")
                    onError?.invoke(Exception("No such document"))
                    return@addOnSuccessListener
                }

                if (document.isEmpty) {
                    onSuccess?.invoke(mutableListOf())
                    return@addOnSuccessListener
                }

                val articles = document.documents.map {
                    it.toObject(Article::class.java)
                }
                onSuccess?.invoke(articles)
            }
            .addOnFailureListener {
                onError?.invoke(it)
            }
    }

    fun likedArticle(
        articleId: String,
        isLiked: Boolean,
        onSuccess: ((Article) -> Unit)? = null,
        onError: ((exception: Exception) -> Unit)? = null
    ) {
        getArticle(
            id = articleId,
            onSuccess = {
                if (it == null) {
                    onError?.invoke(Exception("Article: [$articleId] is null"))
                }
                val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
                it?.let { ar: Article ->
                    if (isLiked && !ar.liked.contains(email)) {
                        ar.liked.add(email)
                    } else {
                        ar.liked.remove(email)
                    }

                    // Update lại article
                    setArticle(
                        article = ar,
                        onSuccess = onSuccess,
                        onError = onError
                    )
                }
            },
            onError = onError
        )
    }

    fun getArticle(
        id: String,
        onSuccess: ((articles: Article?) -> Unit)? = null,
        onError: ((exception: Exception) -> Unit)? = null
    ) {
        val docRef = _db.collection("article").document(id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document == null) {
                    Log.d(TAG, "No such document")
                    onError?.invoke(Exception("No such document"))
                    return@addOnSuccessListener
                }
                onSuccess?.invoke(document.toObject(Article::class.java))
            }
            .addOnFailureListener {
                onError?.invoke(it)
            }
    }

    fun setArticle(
        article: Article,
        onSuccess: ((articles: Article) -> Unit)? = null,
        onError: ((exception: Exception) -> Unit)? = null
    ) {
        val docRef = _db.collection("article").document(article.id)
        docRef.set(article)
            .addOnSuccessListener {
                onSuccess?.invoke(article)
            }
            .addOnFailureListener {
                onError?.invoke(it)
            }
    }
}