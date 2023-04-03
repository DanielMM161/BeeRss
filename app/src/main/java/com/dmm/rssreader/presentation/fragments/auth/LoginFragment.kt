package com.dmm.rssreader.presentation.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.LoginFragmentBinding
import com.dmm.rssreader.domain.extension.gone
import com.dmm.rssreader.domain.extension.show
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.presentation.activities.AuthActivity
import com.dmm.rssreader.presentation.activities.MainActivity
import com.dmm.rssreader.presentation.viewModel.AuthViewModel
import com.dmm.rssreader.utils.Constants
import com.dmm.rssreader.utils.Resource
import com.dmm.rssreader.utils.Utils.Companion.alertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {

  private lateinit var authViewModel: AuthViewModel
  private lateinit var binding: LoginFragmentBinding
  private lateinit var googleClient: GoogleSignInClient
  private val GOOGLE_SIGN_IN = 1000

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = LoginFragmentBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

    goToRegister()
    validateField()
    loginEmailPassword()
    initGoogleSignInClient()
    logginWithGoogle()
    goToForgetPassword()
  }


  private fun validateField() {
    val editTextUserName = binding.username.editText
    editTextUserName?.setOnFocusChangeListener { _, focus ->
      if (!focus) {
        val email = editTextUserName.text.toString()
        val result = authViewModel.validateEmail(email)
        if (!result.successful) {
          binding.username.error = getString(result.resId!!)
        }
      } else {
        binding.username.error = null
      }
    }
  }

  private fun signInGoogleAuthCredential(authCredential: AuthCredential) {
    binding.progressBar.show()
    authViewModel.signInWithGoogle(authCredential)
    authViewModel.authUser.observe(this) { authUser ->
      if (authUser.isNew) {
        createUserDocument(authUser)
      } else {
        getUserDocument(authUser.email)
      }
    }
  }

  private fun loginEmailPassword() {
    binding.loginBtn.setOnClickListener {
      authViewModel.logEvent("Email&Password")
      binding.progressBar.show()
      val email = binding.username.editText?.text.toString()
      val password = binding.password.editText?.text.toString()
      authViewModel.signInEmailPassword(email, password).observe(viewLifecycleOwner) {
        when (it) {
          is Resource.Success -> {
            var user = authViewModel.userShare
            if (user != null) {
              goToMainActivity(user)
            } else {
              getUserDocument(email)
            }
          }
          is Resource.Error -> {
            handleAlterDialog(
              title = getString(R.string.title_email_verification),
              message = it.message
            )
          }
          is Resource.ErrorCaught -> {
            handleAlterDialog(
              message = it.asString(context),
              title = getString(R.string.title_email_verification),
            )
          }
          else -> {}
        }
      }
    }
  }

  private fun getUserDocument(documentPath: String) {
    authViewModel.getUserDocument(documentPath)
    authViewModel.currentUser.observe(viewLifecycleOwner) {
      when (it) {
        is Resource.Success -> {
          binding.progressBar.gone()
          val user = it.data
          if (user != null) {
            goToMainActivity(user)
          }
        }
        is Resource.ErrorCaught -> {
          handleAlterDialog(
            message = it.asString(context),
            title = getString(R.string.error_title_dialog)
          )
        }
        is Resource.Error -> {
          handleAlterDialog(
            message = it.message,
            title = getString(R.string.error_title_dialog)
          )
        }
        else -> {}
      }
    }
  }

  private fun createUserDocument(user: UserProfile) {
    authViewModel.createUserDocument(user)
    authViewModel.currentUser.observe(this) {
      when (it) {
        is Resource.Success -> {
          binding.progressBar.gone()
          if (it.data != null) {
            goToMainActivity(it.data)
          }
        }
        is Resource.Error -> {
          binding.progressBar.gone()
          handleAlterDialog(
            message = it.message,
            title = getString(R.string.error_title_dialog)
          )
        }
      }
    }
  }

  private fun handleAlterDialog(
    message: String,
    title: String = "",
  ) {
    binding.progressBar.gone()
    alertDialog(
      context = context,
      title = title,
      message = message,
      textPositiveButton = getString(R.string.accept)
    ) { dialogInterface ->
        dialogInterface.cancel()
    }
  }

  private fun goToRegister() {
    binding.signupBtn.setOnClickListener {
      navigate(R.id.action_loginFragment_to_registerFragment)
    }
  }

  private fun goToForgetPassword() {
    binding.forgetPassword.setOnClickListener {
      navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
    }
  }

  private fun navigate(id: Int) {
    findNavController().navigate(id)
  }

  private fun goToMainActivity(user: UserProfile) {
    val intent = Intent(activity, MainActivity::class.java)
    intent.putExtra(Constants.USER_KEY, user)
    startActivity(intent)
    (activity as AuthActivity?)?.finish()
  }

  private fun initGoogleSignInClient() {
    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build()
    activity?.let {
      googleClient = GoogleSignIn.getClient(it, googleSignInOptions)
      googleClient.signOut()
    }
  }

  private fun getGoogleAuthCredential(googleSignInAccount: GoogleSignInAccount) {
    val token = googleSignInAccount.idToken
    val authCredential = GoogleAuthProvider.getCredential(token, null)
    signInGoogleAuthCredential(authCredential)
  }

  private fun logginWithGoogle() {
    binding.googleIcon.setOnClickListener {
      binding.progressBar.show()
      authViewModel.logEvent("Google")
      startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == GOOGLE_SIGN_IN) {
      val task = GoogleSignIn.getSignedInAccountFromIntent(data)
      try {
        val googleSignInAccount = task.getResult(ApiException::class.java)
        if (googleSignInAccount != null) {
          getGoogleAuthCredential(googleSignInAccount)
        }
      } catch (e: ApiException) {
        binding.progressBar.gone()
      }
    }
  }

}