package com.example.chatappfirebase.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.chatappfirebase.R
import com.example.chatappfirebase.constants.Constants
import com.example.chatappfirebase.constants.Constants.NO_AVATAR
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.data.models.UserStatus
import com.example.chatappfirebase.databinding.ActivityRegisterProfileBinding
import com.example.chatappfirebase.ui.home.HomeActivity
import com.example.chatappfirebase.utils.RepoResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity(){

    private val TAG = "ProfileActivity"
    private lateinit var binding: ActivityRegisterProfileBinding
    @Inject
    lateinit var auth: FirebaseAuth

    private var urlImgAvatar : String? = null
    private val viewModel : ProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupObseverCreateProfile()
        setupObseverUploadProfile()
    }



    private fun setupView(){
        binding.imaviewAvatar.setOnClickListener {
            photoPicker()
        }

        binding.btnNext.setOnClickListener {
            if(urlImgAvatar == null) {
                val urlAvatar = Constants.NO_AVATAR
                createProfileUser(urlAvatar)
            } else {
                uploadImageAvatar(Uri.parse(urlImgAvatar))
            }
        }

    }

    private fun uploadImageAvatar(uri: Uri){
        viewModel.uploadImageAvatar(uri)
    }


    // create profile
    private fun createProfileUser(url: String) {
        val uid = auth.currentUser?.uid
        val name = binding.edName.text.toString()
        val radioGroup : RadioGroup = findViewById(R.id.rg_gender)
        val idGender = radioGroup.checkedRadioButtonId
        var gender : String?
        if (idGender != -1) {
            val radioBt : RadioButton = findViewById(idGender)
            gender = radioBt.text.toString()
        } else {
            gender = "Orther"
        }
        val phoneNumber = binding.edPhone.text.toString()
        val address = binding.edAddress.text.toString()
        val status = UserStatus.ONLINE.toString()

        val user = User(uid, name, url, gender, phoneNumber, address, status)

        viewModel.createProfileUser(user)

    }

    private fun setupObseverCreateProfile() {
        viewModel.createProfileResult.observe(this, Observer { data ->
            if (data == true) {
                Toast.makeText(this, "Create profile success", Toast.LENGTH_SHORT).show()
                navigationToScreen()
            } else {
                Toast.makeText(this, "Create Profile failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // screen navigation
    private fun navigationToScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    // pick image
   private fun photoPicker(){
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
   }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            urlImgAvatar = uri.toString()
            displayImage(uri.toString())
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

   // show the image
    private fun displayImage(imageUrl: String) {
        val imageAvatar = binding.imaviewAvatar
        Glide.with(this)
            .load(imageUrl)
            .into(imageAvatar)
    }


    private fun setupObseverUploadProfile() {
        viewModel.uploadImageAvatarResult.observe(this, Observer { data ->
            when (data) {
                is RepoResult.Success -> {
                    createProfileUser(data.data)
                }
                is RepoResult.Error -> {
                    Toast.makeText(this, data.exception.toString(), Toast.LENGTH_SHORT).show()
                }
                is RepoResult.Loading -> {
                }
            }
        })
    }
}