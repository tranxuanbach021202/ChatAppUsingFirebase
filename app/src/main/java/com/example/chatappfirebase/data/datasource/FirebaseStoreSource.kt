package com.example.chatappfirebase.data.datasource
import android.net.Uri
import com.example.chatappfirebase.utils.RepoResult
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseStoreSource(
    private val storageRef: StorageReference
) {

    suspend fun uploadImageToFirebase(uri: Uri) : RepoResult<String> {
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}")
        val uploadTask = imagesRef.putFile(uri)

        return suspendCoroutine { continuation ->
            uploadTask.addOnCompleteListener { task->
                if (task.isSuccessful){
                    imagesRef.downloadUrl.addOnSuccessListener {
                        continuation.resume(RepoResult.Success(it.toString()))
                    }
                        .addOnFailureListener {
                            continuation.resume(RepoResult.Error(it))
                        }
                } else {
                    continuation.resume(RepoResult.Error(task.exception ?: IllegalStateException("Upload failed")))
                }
            }

        }
    }

}