package com.example.mds

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*


class ComplaintActivity : AppCompatActivity(), LocationListener {
    private  lateinit var title:EditText
    private  lateinit var description:EditText
    private lateinit var institutionName: Spinner
    private lateinit var myImage:ImageView
    private lateinit var date: String
    private lateinit var locationLatitude: String
    private lateinit var locationLongitude: String
    private var locationAddress: String? = null
    private lateinit var cameraBtn:Button
    private lateinit var createBtn:Button
    private lateinit var getLocation:Button
    private val cameraRequestId  = 1222
    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private val locationPermissionCode = 99
    private lateinit var mAuth: FirebaseAuth
    private var filepath: Uri? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaint)

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference()
        mAuth = FirebaseAuth.getInstance()
        cameraBtn = findViewById(R.id.cameraBtn)
        createBtn = findViewById(R.id.create)
        myImage = findViewById(R.id.myImage)
        institutionName = findViewById(R.id.static_spinner)
        title = findViewById(R.id.editTitlu)
        description = findViewById(R.id.editDescriere)
        //getLocation = findViewById(R.id.getLocation)
        databaseReference = FirebaseDatabase.getInstance().getReference("Complaints")
        myImage.setVisibility(View.GONE);

        getLocation()

        cameraBtn.setOnClickListener {

            val cameraInt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraInt,cameraRequestId)
            myImage.setVisibility(View.VISIBLE);

        }

        getInstitutionsName(institutionName)

        var institutionList: ArrayList<String> = arrayListOf()

        var adapter = customSpinnerAdapter(this, institutionList)
        institutionName.adapter = adapter


        createBtn.setOnClickListener {
            if(TextUtils.isEmpty(title.text.toString())){
                title.setError("Please enter title")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty((description.text.toString()))) {
                description.setError("Please enter description")
                 return@setOnClickListener
            }
            if(filepath == null)
            {
                Toast.makeText(this@ComplaintActivity, "You need to take a photo", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (locationAddress == null) {
                Toast.makeText(this@ComplaintActivity, "You must wait until we get your location", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            else {
                uploadToFirebase(filepath!!)
                startActivity(Intent(this@ComplaintActivity, MainActivity::class.java))
                finish()
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraRequestId){
            /**save to Image In layout*/
            val images: Bitmap = data?.extras?.get("data") as Bitmap
            var imageUri:Uri?

            val result = WeakReference(Bitmap.createScaledBitmap(images, images.height, images.width,false).copy(Bitmap.Config.RGB_565,true))
            val x : Bitmap? = result.get()
            imageUri = saveImage(x,this@ComplaintActivity)
            myImage.setImageURI(imageUri)
            if (imageUri != null) {
                filepath = imageUri
            }

        }
    }
    private fun saveImage(image: Bitmap?, context:Context): Uri? {
        val imagesFolder: File = File(context.getCacheDir(),"images")
        var uri: Uri? = null
        try{
            imagesFolder.mkdir()
            val file: File = File(imagesFolder,"captured_image.jpg")
            val stream: FileOutputStream = FileOutputStream(file)
            if (image != null) {
                image.compress(Bitmap.CompressFormat.JPEG,100,stream)
            }
            stream.flush()
            stream.close()
            uri =  FileProvider.getUriForFile(context.applicationContext,"com.example.mds"+".provider",file)
        }catch (e: FileNotFoundException)
        {
            e.printStackTrace()
        }catch (e: IOException)
        {
            e.printStackTrace()
        }
        return uri
    }

    private fun uploadToFirebase(uri: Uri) {
        val fileRef: StorageReference = storageReference.child("images/"+ System.currentTimeMillis().toString() + "." + getFileExtension(uri))
        fileRef.putFile(uri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                //creationdate
                date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                //userId
                val user = mAuth.currentUser
                val userId = user?.uid.toString()
                //status
                val status = true
                var complaint : ComplaintModel = ComplaintModel(
                title.text.toString(),
                description.text.toString(),
                status,
                institutionName.selectedItem.toString(),
                userId,
                locationAddress,
                locationLatitude,
                locationLongitude,
                uri.toString(),
                date

            )

                val complaintId: String? = databaseReference.push().getKey()
                if (complaintId != null) {
                    databaseReference.child(complaintId).setValue(complaint)
                }
                Toast.makeText(this@ComplaintActivity, "Uploaded Successfully", Toast.LENGTH_SHORT)
                    .show()

            }
        }.addOnFailureListener {

            Toast.makeText(this@ComplaintActivity, "Uploading Failed !!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getFileExtension(mUri: Uri): String? {
        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(mUri))
    }


    ////Location

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
        tvGpsLocation = findViewById(R.id.textView)
        var addresses = getCompleteAddressString(location.latitude, location.longitude);

        locationLatitude = location.latitude.toString()
        locationLongitude = location.longitude.toString()
        locationAddress = addresses.toString()

        tvGpsLocation.text = "Address: " + addresses
    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String? {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.getMaxAddressLineIndex()) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("Current loction address", strReturnedAddress.toString())
            } else {
                Log.w("Current loction address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("Current loction address", "Canont get Address!")
        }
        return strAdd
    }
    //Institution
    private fun getInstitutionsName(listView: Spinner){

        var institutionList:ArrayList<String> = arrayListOf<String>()

        var dbref:DatabaseReference
        dbref = FirebaseDatabase.getInstance().getReference("Users")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(user in snapshot.children){

                        val aux = user.getValue(User::class.java)
                        if(aux?.role.toString() == "institution")
                        {
                            institutionList.add(aux?.username.toString())
                        }
                    }
                    val adapter = customSpinnerAdapter(this@ComplaintActivity, institutionList)
                    listView.adapter = adapter
                }


            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ComplaintActivity, "Failed", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@ComplaintActivity, MainActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right)
        finish()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

}