package com.example.birdview

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.birdview.databinding.ActivityAllBirdsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllBirds : AppCompatActivity() {
    private lateinit var binding:ActivityAllBirdsBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var birdlist : ArrayList<SpecieViewModel>
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var birdAdapter: SpecieViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllBirdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading...")
        progressDialog.setCanceledOnTouchOutside(false)
        showAllBirds()
        /*
        Navigation Drawer and Drawer Layout in Kotlin
        This code was sourced from Medium
        Reyhaneh
        https://androidgeek.co/
        https://androidgeek.co/navigation-drawer-and-drawer-layout-in-kotlin-in-depth-guide-103ce411416d
        * */
        toggle = ActionBarDrawerToggle(this@AllBirds, binding.drawerLayouts, 0, 0)
        binding.drawerLayouts.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navViews.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.Map -> {
                    val map = Intent(this, MapV2::class.java)
                    startActivity(map)
                }
                R.id.Entry -> {
                    val entry = Intent(this, BirdEntry::class.java)
                    startActivity(entry)
                }
                R.id.Homepage ->{
                    startActivity(Intent(this, HomePage::class.java))
                }
                R.id.Category ->{
                    val category = Intent(this, SpecieCatgeory::class.java)
                    startActivity(category)
                }
                R.id.Settings ->startActivity(Intent(this, Settings::class.java))
                R.id.logout -> startActivity(Intent(this,Login::class.java))
            }
            true
        }
    }

    private fun showAllBirds(){
        progressDialog.show()
        birdlist = ArrayList()

        var userID = FirebaseAuth.getInstance().currentUser?.uid
        val reference = FirebaseDatabase.getInstance().getReference("Bird")
        reference.orderByChild("uid").equalTo(userID).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.dismiss()

                val latitudeList = ArrayList<Double>()
                val longitudeList = ArrayList<Double>()

                for (i in snapshot.children) {
                    val model = i.getValue(SpecieViewModel::class.java)
                    birdlist.add(model!!)

                    // Assuming Location is stored as "latitude,longitude" format
                    val locationParts = model?.Location?.split(",") ?: listOf()
                    if (locationParts.size == 2) {
                        val latitude = locationParts[0].toDoubleOrNull()
                        val longitude = locationParts[1].toDoubleOrNull()

                        if (latitude != null && longitude != null) {
                            latitudeList.add(latitude)
                            longitudeList.add(longitude)
                        }
                    }
                }

                // Now latitudeList and longitudeList contain the latitude and longitude for all items

                // Example: Store the first latitude and longitude in variables
                if (latitudeList.isNotEmpty() && longitudeList.isNotEmpty()) {
                    val storedLatitude = latitudeList[0]
                    val storedLongitude = longitudeList[0]

                    // Use storedLatitude and storedLongitude as needed
                }

                birdAdapter = SpecieViewAdapter(this@AllBirds, birdlist)
                binding.SpecieList.adapter = birdAdapter
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)
    }
}