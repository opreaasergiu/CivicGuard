package com.example.mds

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.coroutines.DelicateCoroutinesApi
import org.json.JSONException
import org.json.JSONObject
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class SingleComplaintActivity : AppCompatActivity() {
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var status: TextView
    private lateinit var address: TextView
    private lateinit var date: TextView
    private lateinit var institutionAux: TextView
    private lateinit var image: ImageView
    private lateinit var changeStatus: Button
    private lateinit var changeInstitution: Button
    private lateinit var dbref : DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private var roleUser: String? = null
    private var aux:String? = null
    private var complaintId: String? = null
    private lateinit var dialog: Dialog
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAA-yQ1gsU:APA91bG1E4eEaNAJJMnYHkWjwqeJFRuI_nbtgLJ77AO_BE5V2ljaUtL4YVNCzNy6XtMjCspP29Q44QuI7-oHKLpqijpD534jzLG0HMEj9xL_exWf_8Puf3FtmmuVk1l2ILU-jWFsNHNN"
    private val contentType = "application/json"
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this.applicationContext)
    }
    private lateinit var displayMap: View
    private lateinit var appExecutors: AppExecutors
    private lateinit var emailAddress: String
    private lateinit var adapter: customSpinnerAdapter


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_complaint)
        title = findViewById(R.id.titleComplaint)
        description = findViewById(R.id.descriptionComplaint)
        status = findViewById(R.id.statusComplaint)
        address = findViewById(R.id.addressComplaint)
        date = findViewById(R.id.creationDateComplaint)
        institutionAux = findViewById(R.id.institutionNameComplaint)
        image = findViewById(R.id.imageComplaint)
        changeStatus = findViewById(R.id.statusButton)
        changeInstitution = findViewById(R.id.institutionButton)
        displayMap = findViewById(R.id.displayOnMap)

        appExecutors = AppExecutors()

        mAuth = FirebaseAuth.getInstance()

        val complaint: ComplaintModel = intent.getSerializableExtra("complaint") as ComplaintModel

        title.text = "Title: " + complaint.title
        description.text = "Description: " + complaint.description
        if (complaint.status == true) {
            status.text = "Status: Unsolved"
        } else {
            status.text = "Status: Solved"

        }

        address.text = "Address: " + complaint.locationAddress
        date.text = "Creation Date: " + complaint.creationDate
        institutionAux.text = "Institution Name: " + complaint.institutionName
        changeStatus.setVisibility(View.GONE)
        changeInstitution.setVisibility(View.GONE)
        Picasso.get().load(complaint.imageUrl).into(image)

        val user = mAuth.currentUser
        dbref = FirebaseDatabase.getInstance().getReference("Users")
        dbref.child(user?.uid.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    roleUser = snapshot.child("role").value.toString()
                    if (snapshot.child("role").value.toString() == "user") {
                        if (complaint.status == true && complaint.userId == user?.uid.toString())
                            changeStatus.setVisibility(View.VISIBLE)

                    } else if (snapshot.child("role").value.toString() == "institution") {
                        aux = snapshot.child("username").value.toString()
                        if (complaint.institutionName.toString() == aux.toString()) {
                            if(complaint.status == true )
                            {
                                changeStatus.setVisibility(View.VISIBLE)
                            }
                            changeInstitution.setVisibility(View.VISIBLE)
                        }
                        else if(complaint.status == true && complaint.userId == user?.uid.toString())
                        {
                            changeStatus.setVisibility(View.VISIBLE)}
                        }


                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SingleComplaintActivity, "User not found", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        changeStatus.setOnClickListener {
            showChangeStatusWarning(it)


        }
        changeInstitution.setOnClickListener {
            dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_searchable_spinner)
            dialog.window?.setLayout(650, 800)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            val editText: EditText = dialog.findViewById(R.id.edit_text)
            val listView: ListView = dialog.findViewById(R.id.list_view)

            getInstitutionsName(listView)
            var institutionList: ArrayList<String> = arrayListOf()
            adapter = customSpinnerAdapter(this, institutionList)
            listView.adapter = adapter

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    adapter?.getFilter()?.filter(s)
                }
            })
            listView.setOnItemClickListener(object : AdapterView.OnItemClickListener{
                override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    institutionAux.setText("Institution Name: " + adapter?.getItem(p2))
                    updateInstitutionName(adapter.getItem(p2).toString())
                    dialog.dismiss()
                }
            })

        }
        displayMap.setOnClickListener{
            val intent = Intent(this@SingleComplaintActivity, MapsActivity::class.java)
            var complaintList = arrayListOf<ComplaintModel>()
            complaintList.add(complaint)
            intent.putExtra(EXTRA_USER_MAP, complaintList)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);

        }
        getEmail()

    }
    private fun generateNotification()
    {
        val user = mAuth.currentUser
        val complaint: ComplaintModel = intent.getSerializableExtra("complaint") as ComplaintModel
        val db = FirebaseDatabase.getInstance().getReference("Users").child(complaint.userId.toString()).child("deviceToken")
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val topic = snapshot.value.toString()
                    val notification = JSONObject()
                    val notifcationBody = JSONObject()

                    try {
                        notifcationBody.put("title", "Complaint Solved")
                        notifcationBody.put("body", "Your complaint: "+ complaint.title + ", has been solved")
                        notification.put("to", topic)
                        notification.put("notification", notifcationBody)
                        Log.e("TAG", "try")
                    } catch (e: JSONException) {
                        Log.e("TAG", "onCreate: " + e.message)
                    }

                    sendNotification(notification)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SingleComplaintActivity, "Failed", Toast.LENGTH_SHORT)
                    .show()
            }
        })


    }
    private fun sendNotification(notification: JSONObject) {
        Log.e("TAG", "sendNotification")

        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("TAG", "onResponse: $response")
                Log.i("TAG", "$notification")
            },
            Response.ErrorListener {
                Toast.makeText(this@SingleComplaintActivity, "Request error", Toast.LENGTH_LONG).show()
                Log.i("TAG", "onErrorResponse: Didn't work")
            }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }
    private fun showChangeStatusWarning(view: View)
    {
        MaterialAlertDialogBuilder(this)
            .setTitle("Warning")
            .setMessage("You can change the status only once. Do you want to continue?")
            .setNegativeButton("No"){
                dialog, which ->

                Toast.makeText(this,"Status not changed", Toast.LENGTH_SHORT).show()
            }
            .setPositiveButton("Yes"){
                dialog, which ->
                updateStatus()
                sendEmail()
                generateNotification()
                changeStatus.setVisibility(View.GONE)
                Toast.makeText(this,"Status changed", Toast.LENGTH_SHORT).show()
            }
            .show()

    }
    private fun updateInstitutionName(name: String)
    {
        if(complaintId == null) {

            val complaint: ComplaintModel =
                intent.getSerializableExtra("complaint") as ComplaintModel

            dbref = FirebaseDatabase.getInstance().getReference("Complaints")
            dbref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (complaintsSnapshot in snapshot.children) {

                            val complaintDB =
                                complaintsSnapshot.getValue(ComplaintModel::class.java)
                            if (complaintDB == complaint) {
                                complaintId = complaintsSnapshot.key.toString()
                                dbref.child(complaintsSnapshot.key.toString())
                                    .child("institutionName").setValue(name)
                                complaint.institutionName = name
                                Toast.makeText(
                                    this@SingleComplaintActivity,
                                    "Institution Name Updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SingleComplaintActivity, "Failed", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
        else{
            dbref = FirebaseDatabase.getInstance().getReference("Complaints")
            dbref.child(complaintId.toString())
                .child("institutionName").setValue(name)
            Toast.makeText(
                this@SingleComplaintActivity,
                "Institution Name Updated",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private  fun updateStatus() {

            val complaint: ComplaintModel =
                intent.getSerializableExtra("complaint") as ComplaintModel

            dbref = FirebaseDatabase.getInstance().getReference("Complaints")
            dbref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (complaintsSnapshot in snapshot.children) {

                            val complaintDB =
                                complaintsSnapshot.getValue(ComplaintModel::class.java)
                            if (complaintDB == complaint) {
                                dbref.child(complaintsSnapshot.key.toString()).child("status")
                                    .setValue(false)
                                status.text = "Status: Solved"
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SingleComplaintActivity, "Failed", Toast.LENGTH_SHORT)
                        .show()
                }
            })

    }

    private fun getInstitutionsName(listView: ListView){

        var institutionList:ArrayList<String> = arrayListOf<String>()

        dbref = FirebaseDatabase.getInstance().getReference("Users")
        dbref.addValueEventListener(object :ValueEventListener{
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
                    adapter = customSpinnerAdapter(this@SingleComplaintActivity, institutionList)
                    listView.adapter = adapter
                }


                }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SingleComplaintActivity, "Failed", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun sendEmail(){
        val complaint: ComplaintModel = intent.getSerializableExtra("complaint") as ComplaintModel
        val user = mAuth.currentUser

        appExecutors.diskIO().execute {
            val props = System.getProperties()
            props.put("mail.smtp.host", "smtp.gmail.com")
            props.put("mail.smtp.socketFactory.port", "465")
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.port", "465")

            val session =  Session.getInstance(props,
                object : javax.mail.Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(Credentials.EMAIL, Credentials.PASSWORD)
                    }
                })

            try {

                val message = MimeMessage(session)
                message.setFrom(InternetAddress(Credentials.EMAIL))
                message.addRecipient(
                    Message.RecipientType.TO,
                    InternetAddress(emailAddress)
                )
                message.subject = "Complaint solved!"
                message.setText("Your Complaint with title : \" " + complaint.title.toString()+" \" has been solved")
                Transport.send(message)
                appExecutors.mainThread().execute {

                }


            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }
    }

    private fun getEmail(){
        val complaint: ComplaintModel = intent.getSerializableExtra("complaint") as ComplaintModel
        val db = FirebaseDatabase.getInstance().getReference().child("Users").child(complaint.userId.toString()).child("email")
        db.addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists())
                        {
                             emailAddress = snapshot.value.toString()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@SingleComplaintActivity, "Failed to send email", Toast.LENGTH_SHORT).show()
                    }
                })

    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@SingleComplaintActivity, MainActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);
        finish()
    }
}