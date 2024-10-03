import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.adminblink.Utils
import com.example.adminblink.model.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow

class AdminViewModel : ViewModel(){

    private val _isImagesUploaded = MutableStateFlow(false)
    val isImagesUploaded: StateFlow<Boolean> = _isImagesUploaded

    private val _downloadUrls = MutableStateFlow<ArrayList<String?>>(arrayListOf())
    val downloadUrls: StateFlow<ArrayList<String?>> = _downloadUrls

    fun saveImageInDB(imageUri: ArrayList<Uri>) {
        val tempDownloadUrls = ArrayList<String?>()

        for (uri in imageUri) {
            val imageRef = FirebaseStorage.getInstance().reference.child(Utils.getCurrentUserId()).child("productImages/${uri.lastPathSegment}").child("UUID.randomUUID().toString()")
            imageRef.putFile(uri).continueWithTask {
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                val url = task.result
                tempDownloadUrls.add(url.toString())

                // Check if all images have been uploaded
                if (tempDownloadUrls.size == imageUri.size) {
                    _downloadUrls.value = tempDownloadUrls // Update the StateFlow with the URLs
                    _isImagesUploaded.value = true         // Set the flag as true when done
                }
            }
        }


    }

    fun saveProduct(product: Product) {
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productRandomID}").setValue(product)
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productRandomID}").setValue(product)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.productType}/${product.productRandomID}").setValue(product)
                            .addOnSuccessListener {
                                _isImagesUploaded.value = true
                            }

                    }

            }

    }
    fun fetchAllProducts(category: String):Flow<List<Product>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    if (category == "All"||prod?.productCategory == category){
                        products.add(prod!!)
                    }


                }
                trySend(products)

            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }

        }
        db.addValueEventListener(eventListener)

        awaitClose{
            db.removeEventListener(eventListener)
        }
    }

    fun SavingUpdateProducts(product: Product) {
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productRandomID}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productRandomID}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.productType}/${product.productRandomID}").setValue(product)
    }



}
