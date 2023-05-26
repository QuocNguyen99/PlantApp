package com.example.plantapp.network.repository.article

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.camera.core.SurfaceRequest.Result.ResultCode
import com.example.plantapp.data.model.Article
import com.example.plantapp.data.response.DefaultImage
import com.example.plantapp.data.response.Plant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Date


class FireStoreRepository {
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

    fun setPlant(
        plant: Plant,
        bitmap: Bitmap,
        onSuccess: ((plant: Plant) -> Unit)? = null,
        onError: ((exception: Exception) -> Unit)? = null
    ) {
        updatePlantImage(bitmap = bitmap, onError = onError, onSuccess = {
            val defaultImage = DefaultImage()
            defaultImage.license_name = it
            defaultImage.license_url = it
            defaultImage.medium_url = it
            defaultImage.original_url = it
            defaultImage.regular_url = it
            defaultImage.small_url = it
            defaultImage.thumbnail = it
            defaultImage.license = 2
            plant.default_image = defaultImage
            val docRef = _db.collection("species").document(Date().time.toString())
            docRef.set(plant)
                .addOnSuccessListener {
                    onSuccess?.invoke(plant)
                }
                .addOnFailureListener {
                    onError?.invoke(it)
                }
        })
    }

    private fun updatePlantImage(
        bitmap: Bitmap,
        onSuccess: ((url: String) -> Unit)? = null,
        onError: ((exception: Exception) -> Unit)? = null
    ) {

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val data = baos.toByteArray()

        val imageName = Date().time.toString() + ".jpg"
        val storageRef: StorageReference = Firebase.storage.reference
        val imagesRef: StorageReference = storageRef.child(imageName)
        imagesRef.putBytes(data)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    onError?.invoke(Exception("Error upload image"))
                }
                imagesRef.downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri: Uri = task.result
                    // Create a reference to a file from a Google Cloud Storage URI
                    onSuccess?.invoke(downloadUri.toString())
                    Log.d(TAG, downloadUri.toString())
                } else {
                    onError?.invoke(Exception(task.exception.toString()))
                }
            }

    }
}