package com.example.musicalinstrumentsaggregator
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.musicalinstrumentsaggregator.R
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val TAG = "FirestoreDebug"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Get Firestore instance
        val db = FirebaseFirestore.getInstance()

        // Check if Firestore is initialized
        if (db != null) {
            Log.d(TAG, "Firestore initialized successfully!")
        } else {
            Log.e(TAG, "Firestore failed to initialize!")
            return
        }

        // Fetch data from Firestore
        db.collection("/musical_instruments")
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "Success! Found ${documents.size()} documents.")
                if (documents.isEmpty) {
                    Log.d(TAG, "No documents in 'musical_instruments' collection.")
                } else {
                    for (document in documents) {
                        Log.d(TAG, "Document ID: ${document.id}")
                        Log.d(TAG, "Data: ${document.data}")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Firestore Error: ", exception)
            }
        // Add a test document
//        val testDoc = hashMapOf(
//            "Name" to "Test Piano",
//            "Category" to "Keyboard"
//        )
//        db.collection("musical_instruments")
//            .add(testDoc)
//            .addOnSuccessListener {
//                Log.d(TAG, "Test document added!")
//            }
//            .addOnFailureListener { e ->
//                Log.e(TAG, "Error adding test doc", e)
//            }

    }
}
