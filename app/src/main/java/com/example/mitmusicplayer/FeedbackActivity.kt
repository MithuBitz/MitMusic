package com.example.mitmusicplayer

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mitmusicplayer.databinding.ActivityFeedbackBinding
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class FeedbackActivity : AppCompatActivity() {

    lateinit var binding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Feedback"

        binding.sendFeedbackFA.setOnClickListener {
            val feedbackMsg = binding.feedbackMsgFA.text.toString() + "\n" + binding.emailFA.text.toString()
            val subject = binding.topicFA.text.toString()

            //Mail address where you want to send the feedback
            val username = "mithubortamuly@gmail.com"
            val pass = "Omadomsd1988"
            //Check the mobile network is available or not to send the mail
            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as  ConnectivityManager
            if (feedbackMsg.isNotEmpty() && subject.isNotEmpty() && (cm.activeNetworkInfo?.isConnectedOrConnecting == true)) {
                Thread{
                    try {
                        val properties = Properties()
                        properties["mail.smtp.auth"] = "true"
                        properties["mail.smtp.starttls.enable"] = "true"
                        properties["mail.smtp.host"] = "smtp.gmail.com"
                        properties["mail.smtp.port"] = "587"

                        val session = Session.getInstance(properties, object : Authenticator() {
                            override fun getPasswordAuthentication(): PasswordAuthentication {
                                return PasswordAuthentication(username, pass)
                            }
                        })
                        val mail = MimeMessage(session)
                        mail.subject =subject
                        mail.setText(feedbackMsg)
                        mail.setFrom(InternetAddress(username))
                        mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(username))
                        Transport.send(mail)
                    }catch (e : Exception) { Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()}
                }.start()
                Toast.makeText(this, "Thanks for Feedback", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
            }


        }

    }
}