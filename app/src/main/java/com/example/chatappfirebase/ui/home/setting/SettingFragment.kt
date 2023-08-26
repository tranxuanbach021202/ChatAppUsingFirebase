package com.example.chatappfirebase.ui.home.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.chatappfirebase.R
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.utils.RepoResult
import com.example.chatappfirebase.databinding.FragmentSettingBinding
import com.example.chatappfirebase.ui.common.BaseFragment
import com.example.chatappfirebase.ui.login.LoginActivity
import com.example.chatappfirebase.ui.sigup.SigupActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SettingFragment : BaseFragment() {

    private val TAG = "SettingFragment"
    lateinit var binding: FragmentSettingBinding

    private val viewModel: SettingViewModel by viewModels()
    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var urlImageAvatar: String
    private var checkImageAvatar: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
        setupView()
        obseverInfoProfileUser()
        observerUpdateProfile()
        setupObseverUpdateAvatar()
    }

    private fun fetchData() {
        viewModel.fetchInfoUser()
    }





    fun setupView() {
        binding.btnLogout.setOnClickListener{
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        binding.btnUpdate.setOnClickListener{
            updateProfileUser(urlImageAvatar)
        }
        binding.imageviewAvatar.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    fun obseverInfoProfileUser() {
        viewModel.user.observe(viewLifecycleOwner, Observer {
                when(it) {
                    is RepoResult.Success -> {
                        setupInfoUser(it.data)
                        urlImageAvatar = it.data.imgProfile!!
                    }
                    is RepoResult.Loading -> {
                        Log.d(TAG, "Loading")
                    }
                    is RepoResult.Error -> {
                        Log.e(TAG, it.exception.message.toString())
                    }
                }

        })
    }

    private fun setupInfoUser(user: User) {

        binding.user = user

        Glide.with(this)
            .load(user.imgProfile)
            .error(R.drawable.no_avatar)
            .into(binding.imageviewAvatar)
        when(user.gender) {
            "Male" -> {
                binding.rbMale.isChecked = true
            }
            "Famale" -> {
                binding.rbFamale.isChecked = true
            }
            "Orther" -> {
                binding.rbOrther.isChecked = true
            }
        }
    }

    fun updateProfileUser(urlImg: String) {
        val name = binding.edName.text.toString()
        val imgProfile = urlImg
        val phoneNumber = binding.edPhone.text.toString()
        val address = binding.edAddress.text.toString()
        val idGender = binding.rgGender.checkedRadioButtonId
        val radioBt : RadioButton? = view?.findViewById(idGender)
        val gender = radioBt?.text.toString()

        val user = User("", name, imgProfile, gender, phoneNumber, address, "")
        viewModel.updateProfileUser(user)
    }



    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")

            // upload image to firebase storage
            viewModel.updateAvatar(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }


    // show the image
    private fun displayImage(imageUrl: String) {
        val imageAvatar = binding.imageviewAvatar
        Glide.with(this)
            .load(imageUrl)
            .into(imageAvatar)
    }

    private fun setupObseverUpdateAvatar() {
        viewModel.updateAvatarResult.observe(viewLifecycleOwner, Observer{
            when(it) {
                is RepoResult.Success -> {
                    displayImage(it.data)
                    urlImageAvatar = it.data
                }
                is RepoResult.Loading -> {
                    Log.d(TAG, "Loading")
                }
                is RepoResult.Error -> {
                    Log.e(TAG, it.exception.message.toString())
                }
            }
        })
    }

    private fun observerUpdateProfile(){
        viewModel.updateProfileUserResult.observe(viewLifecycleOwner, Observer {
            when(it) {
                is RepoResult.Success -> {
                    if (it.data) {
                        Toast.makeText(requireContext(), "Updated profile successfully", Toast.LENGTH_SHORT).show()
                    }
                }
                is RepoResult.Loading -> {
                    Log.d(TAG, "Loading")
                }
                is RepoResult.Error -> {
                    Log.e(TAG, it.exception.message.toString())


                }
            }
        })
    }



}