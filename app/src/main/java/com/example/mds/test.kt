package com.example.mds

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class test : AppCompatActivity(){
    private lateinit var appExecutors: AppExecutors
    private lateinit var submit: Button
    private lateinit var addressEmail: EditText
    private lateinit var mailDescription: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appExecutors = AppExecutors()
        setContentView(R.layout.activity_test)

        submit = findViewById(R.id.submit)
        addressEmail = findViewById(R.id.mailTitle)
        mailDescription = findViewById(R.id.mailDescription)

        submit.setOnClickListener {
            sendEmail()
        }
    }
    private fun sendEmail(){
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
                val emailAddress = addressEmail.text.toString()
                message.setFrom(InternetAddress(Credentials.EMAIL))
                message.addRecipient(Message.RecipientType.TO,
                    InternetAddress(emailAddress))
                message.subject = "Complaint solved!"
                message.setText(mailDescription.text.toString())
                Transport.send(message)
                appExecutors.mainThread().execute {

                }

            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }
    }
}
/*
    private lateinit var addressEmail: EditText
    private lateinit var mailDescription: EditText
    private lateinit var submit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        addressEmail = findViewById(R.id.mailTitle)
        mailDescription = findViewById(R.id.mailDescription)
        submit = findViewById(R.id.submit)

        submit.setOnClickListener {
            val username = "civicguardmds@gmail.com"
            val password = "@CivicGuardMds244"
            val messageToSend:String = mailDescription.text.toString()
            val properties = Properties()
            properties?.put("mail.smtp.auth", "true")
            properties?.put("mail.smtp.starttls.enable", "true")
            properties?.put("mail.smtp.host", "smtp.gmail.com")
            properties?.put("mail.smtp.port", "587")


            val session = Session.getInstance(properties,
                object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(username, password)
                    }
                })

            try {
                val message: Message = MimeMessage(session)

                message.setFrom(InternetAddress(username))
                message.addRecipient(Message.RecipientType.TO, InternetAddress(addressEmail.text.toString()));
                message.setSubject("Complaint solved!")
                message.setText(messageToSend)
                Transport.send(message)
                Toast.makeText(this, "email send successfully",Toast.LENGTH_LONG).show()

            }catch (e :MessagingException)
            {
                e.printStackTrace()
            }


            val policy:StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()

            StrictMode.setThreadPolicy(policy)

        }


    }



}
*/


