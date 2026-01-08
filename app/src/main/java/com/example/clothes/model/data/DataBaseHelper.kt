package com.example.clothes.model.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.clothes.model.Cloth
import com.example.clothes.model.Combination
import com.example.clothes.model.Piece
import com.example.clothes.model.Post
import com.example.clothes.model.Profile
import com.example.clothes.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Objects
import java.util.UUID
import android.webkit.URLUtil
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.CompletableFuture

object DataBaseHelper {

    val persistance = FirebaseDatabase.getInstance("https://clothes-11875-default-rtdb.europe-west1.firebasedatabase.app/").setPersistenceEnabled(false)
    val database = FirebaseDatabase.getInstance("https://clothes-11875-default-rtdb.europe-west1.firebasedatabase.app/")
    val fileStorage = FirebaseStorage.getInstance("gs://clothes-11875.appspot.com")

    fun getNewUserKey(): String {

        val newClothesRef = database.getReference("clothes").push()
        return newClothesRef.key.toString()
    }

    fun setCloth(userKey: String, category: String, list: String, image: String?, clothKey: String): CompletableFuture<Boolean>{

        val completableFuture = CompletableFuture<Boolean>()

        if (image != null) {
            if(image.substring(0, 4) == "http"){
                val cloth = Cloth(clothKey, category, list, image)

                database.getReference("clothes").child(userKey).child(clothKey).setValue(cloth).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        completableFuture.complete(true) }
                    else { completableFuture.complete(false) }
                }
            }
            else{
                uploadImage(image) { success, url ->
                    if(success){
                        val cloth = Cloth(clothKey, category, list, url)

                        database.getReference("clothes").child(userKey).child(clothKey).setValue(cloth).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                completableFuture.complete(true) }
                            else { completableFuture.complete(false) }
                        }
                    }
                }
            }
        }
        else{
            val clothUpdate = hashMapOf<String, Any>(
                "category" to category,
                "list" to list
            )
            database.getReference("clothes").child(userKey).child(clothKey).updateChildren(clothUpdate).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    completableFuture.complete(true) }
                else { completableFuture.complete(false) }
            }
        }

        return completableFuture
    }

    fun getCloth(userKey: String, clothKey: String, callback: (Cloth?) -> Unit){

        val pieceRef = database.getReference("clothes").child(userKey).child(clothKey)

        pieceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val cloth = dataSnapshot.getValue(Cloth::class.java)
                callback(cloth)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getClothes(userKey: String, callback: (List<Cloth?>) -> Unit) {

        val clothesRef = database.getReference("clothes").child(userKey)

        clothesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val clothList = mutableListOf<Cloth>()
                for (snapshot in dataSnapshot.children) {
                    val cloth = snapshot.getValue(Cloth::class.java)
                    cloth?.let { clothList.add(it) }
                }

                callback(clothList)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun deleteCloth(userKey: String, clothKey: String): CompletableFuture<Boolean> {

        val completableFuture = CompletableFuture<Boolean>()

        database.getReference("clothes").child(userKey).child(clothKey).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                completableFuture.complete(true) }
            else { completableFuture.complete(false) }
        }

        return completableFuture
    }

    fun setPiece(userKey: String, type: String, brand: String, color: String, size: String, list: String, image: String?, category:String, updateClothKey: String? = null): CompletableFuture<Boolean> {

        val completableFuture = CompletableFuture<Boolean>()

        var clothKey = updateClothKey
        if(clothKey == null)
            clothKey = database.getReference("clothes").child(userKey).push().key.toString()

        setCloth(userKey, category, list, image, clothKey).thenAccept { success ->
            if (success) {
                val piece = Piece(null, null, null, null, type, brand, color, size)
                database.getReference("pieces").child(userKey).child(clothKey).setValue(piece).addOnCompleteListener { task ->
                        if (task.isSuccessful) { completableFuture.complete(true) }
                        else { completableFuture.complete(false) }
                    }
            }
            else{ completableFuture.complete(false) }
        }

        return completableFuture
    }

    fun updatePiece(userKey: String, type: String, brand: String, color: String, size: String, list: String, image: String?, category:String, updateClothKey: String): CompletableFuture<Boolean> {
        return setPiece(userKey, type, brand, color, size, list, image, category, updateClothKey)
    }

    fun getPiece(userKey: String, clothKey: String, callback: (Piece?) -> Unit){

        getCloth(userKey, clothKey) { dbCloth ->

            val pieceRef = database.getReference("pieces").child(userKey).child(clothKey)

            pieceRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val dbPiece = dataSnapshot.getValue(Piece::class.java)
                    val piece = Piece(dbCloth?.id, dbCloth?.list, dbCloth?.category, dbCloth?.image, dbPiece?.type, dbPiece?.brand, dbPiece?.color, dbPiece?.size)
                    callback(piece)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }
    
    fun deletePiece(userKey: String, clothKey: String): CompletableFuture<Boolean> {

        val completableFuture = CompletableFuture<Boolean>()

        deleteCloth(userKey, clothKey).thenAccept { success ->
            if(success) {
                database.getReference("pieces").child(userKey).child(clothKey).removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            completableFuture.complete(true)
                        } else {
                            completableFuture.complete(false)
                        }
                    }
            }
            else{ completableFuture.complete(false) }
        }

        return completableFuture
    }

    fun setCombination(userKey: String, piece1Key: String, piece2Key: String, list: String, category: String, image: String?, updateClothKey: String? = null): CompletableFuture<Boolean> {

        val completableFuture = CompletableFuture<Boolean>()

        var clothKey = updateClothKey
        if(clothKey == null)
            clothKey = database.getReference("clothes").child(userKey).push().key.toString()

        setCloth(userKey, category, list, image, clothKey).thenAccept { success ->
            if (success) {
                val combinationDb = hashMapOf<String, Any>(
                    "piece1id" to piece1Key,
                    "piece2id" to piece2Key
                )
                database.getReference("combinations").child(userKey).child(clothKey).setValue(combinationDb).addOnCompleteListener { task ->
                    if (task.isSuccessful) { completableFuture.complete(true) }
                    else { completableFuture.complete(false) }
                }
            }
            else{ completableFuture.complete(false) }
        }

        return completableFuture
    }

    fun updateCombination(userKey: String, piece1Key: String, piece2Key: String, list: String, category: String, image: String?, clothKey: String): CompletableFuture<Boolean> {
        return setCombination(userKey, piece1Key, piece2Key, list, category, image, clothKey)
    }

    fun getCombination(userKey: String, clothKey: String, callback: (Combination?) -> Unit){

        val combinationRef = database.getReference("combinations").child(userKey).child(clothKey)

        combinationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val piece1key = dataSnapshot.child("piece1id").value.toString()
                val piece2key = dataSnapshot.child("piece2id").value.toString()

                getCloth(userKey, clothKey){ cloth ->
                    getPiece(userKey, piece1key){ piece1 ->
                        getPiece(userKey, piece2key){ piece2 ->
                            val combination = Combination(
                                cloth?.id,
                                cloth?.list,
                                cloth?.category,
                                cloth?.image,
                                piece1,
                                piece2
                            )
                            callback(combination)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun deleteCombination(userKey: String, clothKey: String): CompletableFuture<Boolean> {

        val completableFuture = CompletableFuture<Boolean>()

        deleteCloth(userKey, clothKey).thenAccept { success ->
            if(success) {
                database.getReference("combinations").child(userKey).child(clothKey).removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            completableFuture.complete(true)
                        } else {
                            completableFuture.complete(false)
                        }
                    }
            }
            else{ completableFuture.complete(false) }
        }

        return completableFuture
    }

    fun getProfileInfo(userKey: String, callback: (Profile?) -> Unit){

        Log.d("getProfileInfo userKey", userKey)

        val profilesRef = database.getReference("profiles").child(userKey)
        profilesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val profileInfo = dataSnapshot.getValue(Profile::class.java)
                callback(profileInfo)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

/*
    fun getPosts(userKey: String, callback: (List<Post>?) -> Unit) {

        getProfileInfo(userKey){ profileInfo ->

            val clothesRef = database.getReference("posts").child(userKey)
            clothesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val postsList = mutableListOf<Post>()
                    for (snapshot in dataSnapshot.children) {
                        val image = snapshot.child("image").value.toString()
                        val piece1key = snapshot.child("piece1id").value.toString()
                        val piece2key = snapshot.child("piece2id").value.toString()

                        getPiece(userKey, piece1key){ piece1 ->
                            getPiece(userKey, piece2key){ piece2 ->
                                val post = Post(
                                    profileInfo!!.image,
                                    profileInfo.username,
                                    image,
                                    piece1!!,
                                    piece2!!
                                )
                                postsList.add(post)
                            }
                        }
                    }
                    callback(postsList)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

 */

    fun getPosts(userKey: String, callback: (List<Post>?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val profileInfo = getProfileInfoAsync(userKey)

                val postsRef = database.getReference("posts").child(userKey)
                val dataSnapshot = postsRef.get().await()

                val postsList = mutableListOf<Post>()
                val deferredList = mutableListOf<CompletableDeferred<Piece?>>()

                for (snapshot in dataSnapshot.children) {
                    val piece1key = snapshot.child("piece1id").value.toString()
                    val piece2key = snapshot.child("piece2id").value.toString()

                    val piece1Deferred = CompletableDeferred<Piece?>()
                    val piece2Deferred = CompletableDeferred<Piece?>()

                    launch {
                        val piece1 = getPieceAsync(userKey, piece1key)
                        piece1Deferred.complete(piece1)
                    }

                    launch {
                        val piece2 = getPieceAsync(userKey, piece2key)
                        piece2Deferred.complete(piece2)
                    }

                    deferredList.add(piece1Deferred)
                    deferredList.add(piece2Deferred)
                }

                deferredList.forEach { it.await() }

                for (snapshot in dataSnapshot.children) {
                    val image = snapshot.child("image").value.toString()

                    val piece1 = deferredList.removeAt(0).getCompleted()
                    val piece2 = deferredList.removeAt(0).getCompleted()

                    val post = Post(
                        snapshot.key.toString(),
                        profileInfo!!.image,
                        profileInfo.username,
                        image,
                        piece1!!,
                        piece2!!
                    )
                    postsList.add(post)
                }

                callback(postsList.reversed())
            } catch (e: Exception) { callback(null) }
        }
    }

    suspend fun getProfileInfoAsync(userKey: String): Profile? {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Profile?>()
            getProfileInfo(userKey) { profileInfo ->
                deferred.complete(profileInfo)
            }
            deferred.await()
        }
    }

    suspend fun getPieceAsync(userKey: String, pieceKey: String): Piece? {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Piece?>()
            getPiece(userKey, pieceKey) { piece ->
                deferred.complete(piece)
            }
            deferred.await()
        }
    }

    fun getProfile(userKey: String, callback: (Profile?) -> Unit) {

        getProfileInfo(userKey){ profileInfo ->
            getPosts(userKey){ posts ->
                val profile = Profile(profileInfo!!.image, profileInfo.username, posts)
                callback(profile)
            }
        }
    }

    fun updateProfileImage(userKey: String, image: String){

        uploadImage(image){ sucess, url ->
            if(sucess){
                val update = hashMapOf<String, Any>("image" to url)

                database.getReference("profiles").child(userKey).updateChildren(update)
            }
        }
    }

    fun getProfileByUsername(username: String, callback: (Profile?) -> Unit){
        getUserKey(username){ userKey ->
            getProfile(userKey!!) { profile ->
                callback(profile)
            }
        }
    }

    fun deletePost(userKey: String, postKey: String): CompletableFuture<Boolean> {

        val completableFuture = CompletableFuture<Boolean>()

        database.getReference("posts").child(userKey).child(postKey).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    completableFuture.complete(true)
                } else {
                    completableFuture.complete(false)
                }
            }

        return completableFuture
    }

    fun setPost(userKey: String, image: String, piece1Key: String, piece2Key: String): CompletableFuture<Boolean> {

        val completableFuture = CompletableFuture<Boolean>()

        if(image.substring(0, 4) == "http"){
            val dbPost = hashMapOf<String, Any>(
                "image" to image,
                "piece1id" to piece1Key,
                "piece2id" to piece2Key
            )

            val newPostRef = database.getReference("posts").child(userKey).push()
            newPostRef.setValue(dbPost).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    completableFuture.complete(true)
                } else {
                    completableFuture.complete(false)
                }
            }
        }
        else{
            uploadImage(image) { success, url ->
                if (success) {
                    val dbPost = hashMapOf<String, Any>(
                        "image" to url,
                        "piece1id" to piece1Key,
                        "piece2id" to piece2Key
                    )

                    val newPostRef = database.getReference("posts").child(userKey).push()
                    newPostRef.setValue(dbPost).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            completableFuture.complete(true)
                        } else {
                            completableFuture.complete(false)
                        }
                    }
                }
            }
        }

        return completableFuture
    }

    fun getFollowsPosts(ownUserKey: String, callback: (List<Post>?) -> Unit){

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allPostsList = mutableListOf<Post>()

                val postsRef = database.getReference("posts")
                val userDataSnapshot = postsRef.get().await()
                for(userSnapshot in userDataSnapshot.children){

                    val userKey = userSnapshot.key.toString()
                    if(userKey != ownUserKey) {

                        val profileInfo = getProfileInfoAsync(userKey)

                        val userPostsRef = database.getReference("posts").child(userKey)
                        val dataSnapshot = userPostsRef.get().await()

                        val deferredList = mutableListOf<CompletableDeferred<Piece?>>()

                        for (snapshot in dataSnapshot.children) {
                            val piece1key = snapshot.child("piece1id").value.toString()
                            val piece2key = snapshot.child("piece2id").value.toString()

                            val piece1Deferred = CompletableDeferred<Piece?>()
                            val piece2Deferred = CompletableDeferred<Piece?>()

                            launch {
                                val piece1 = getPieceAsync(userKey, piece1key)
                                piece1Deferred.complete(piece1)
                            }

                            launch {
                                val piece2 = getPieceAsync(userKey, piece2key)
                                piece2Deferred.complete(piece2)
                            }

                            deferredList.add(piece1Deferred)
                            deferredList.add(piece2Deferred)
                        }

                        deferredList.forEach { it.await() }

                        for (snapshot in dataSnapshot.children) {
                            val image = snapshot.child("image").value.toString()

                            val piece1 = deferredList.removeAt(0).getCompleted()
                            val piece2 = deferredList.removeAt(0).getCompleted()

                            val post = Post(
                                snapshot.key.toString(),
                                profileInfo!!.image,
                                profileInfo.username,
                                image,
                                piece1!!,
                                piece2!!
                            )

                            allPostsList.add(post)
                        }
                    }
                }
                callback(allPostsList.reversed())
            } catch (e: Exception) { callback(null) }
        }




    }

    fun getUsernames(callback: (List<String>) -> Unit) {

        val profilesRef = database.getReference("profiles")
        profilesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val usernamesList = mutableListOf<String>()
                for (snapshot in dataSnapshot.children) {
                    val username = snapshot.child("username").value.toString()
                    usernamesList.add(username)
                }
                callback(usernamesList)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun checkLogin(username: String, password: String, callback: (Boolean?) -> Unit) {

        val usersRef = database.getReference("users").child(username)

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dbPassword = dataSnapshot.child("password").value.toString()
                if(password == dbPassword){
                    callback(true)
                }
                else{
                    callback(false)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getUserKey(username: String, callback: (String?) -> Unit) {

        val usersRef = database.getReference("users").child(username)

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userKey = dataSnapshot.child("userKey").value.toString()
                callback(userKey)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun savePieces(userKey: String, piece1: Piece, piece2: Piece): CompletableFuture<Boolean> {

        val completableFuture = CompletableFuture<Boolean>()

        setPiece(userKey, piece1.type!!, piece1.brand!!, piece1.color!!, piece1.size!!, "Wishlist", piece1.image, piece1.category!!).thenAccept { success ->
            if(success){
                setPiece(userKey, piece2.type!!, piece2.brand!!, piece2.color!!, piece2.size!!, "Wishlist", piece2.image, piece2.category!!).thenAccept { success ->
                    if(success){
                        completableFuture.complete(true)
                    }
                    else{
                        completableFuture.complete(false)
                    }
                }
            }
            else{
                completableFuture.complete(false)
            }
        }

        return completableFuture
    }

    fun createAccount(username: String, password: String, userKey: String): CompletableFuture<Boolean> {

        val completableFuture = CompletableFuture<Boolean>()

        val userRef = database.getReference("users").child(username)
        userRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    completableFuture.complete(false)
                }
                else{
                    val user = hashMapOf<String, Any>(
                        "password" to password,
                        "userKey" to userKey
                    )
                    userRef.setValue(user).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val profile = hashMapOf<String, Any>(
                                "username" to username,
                                "image" to "",
                                "follows" to listOf<String>()
                            )
                            database.getReference("profiles").child(userKey).setValue(profile)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        completableFuture.complete(true)
                                    } else {
                                        completableFuture.complete(false)
                                    }
                                }
                        } else {
                            completableFuture.complete(false)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        return completableFuture
    }

    fun uploadImage(image: String, onComplete: (Boolean, String) -> Unit) {
        val fileStorageReference: StorageReference = fileStorage.getReference(image)

        val imageUri = Uri.parse(image)
        fileStorageReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                fileStorageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    onComplete(true, imageUrl)
                }.addOnFailureListener { exception ->
                    onComplete(false, "")
                }
            }
            .addOnFailureListener { exception ->
                onComplete(false, "")
            }
    }
}