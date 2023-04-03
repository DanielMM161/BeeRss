package com.dmm.rssreader.presentation.fragments.auth

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.RegisterFragmentBinding
import com.dmm.rssreader.domain.extension.gone
import com.dmm.rssreader.domain.extension.show
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.presentation.viewModel.AuthViewModel
import com.dmm.rssreader.utils.Resource
import com.dmm.rssreader.utils.Utils
import com.dmm.rssreader.utils.ValidationResult
import com.google.android.material.textfield.TextInputLayout

class RegisterFragment : Fragment() {

  private lateinit var authViewModel: AuthViewModel
  private lateinit var binding: RegisterFragmentBinding

  private lateinit var fullNameET: EditText
  private lateinit var emailET: EditText
  private lateinit var passwordET: EditText
  private lateinit var passwordRepeatET: EditText


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = RegisterFragmentBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

    fullNameET = binding.fullnameLayout.editText!!
    emailET = binding.emailLayout.editText!!
    passwordET = binding.passwordRegisterLayout.editText!!
    passwordRepeatET = binding.repeatPasswordLayout.editText!!

    validateFields()
    registerUser()
    alreadyHaveAccount()
  }

  private fun registerUser() {
    binding.register.setOnClickListener {
      val email = emailET.text.toString()
      val password = passwordET.text.toString()
      var fails = 0
      val listValidationResult = mapOf(
        binding.fullnameLayout to authViewModel.validateFullName(fullNameET.text.toString()),
        binding.emailLayout to authViewModel.validateEmail(email),
        binding.passwordRegisterLayout to authViewModel.validatePassword(password),
        binding.repeatPasswordLayout to authViewModel.validateRepeatPassword(
          passwordRepeatET.text.toString(),
          password
        )
      )

      listValidationResult.forEach { (textInputLayout, validationResult) ->
        if (!validationResult.successful) {
          fails++
          textInputLayout.error = getString(validationResult.resId!!)
        }
      }

      if (fails == 0) {
        createUserEmailPassword(email, password)
      }
    }
  }

  private fun createUserEmailPassword(email: String, password: String) {
    binding.progressBar.show()
    authViewModel.createUserEmailPassword(email, password).observe(viewLifecycleOwner) {
      when (it) {
        is Resource.Success -> {
          val user = it.data
          if (user != null) {
            createUserDocument(user.copy(fullName = fullNameET.text.toString()))
          }
        }
        is Resource.Error -> {
          binding.progressBar.gone()
          handleAlterDialog(
            message = it.message,
            title = getString(R.string.title_error_creating_user)
          ) { dialogInterface ->
            dialogInterface.cancel()
          }
        }
        else -> {}
      }
    }
  }

  private fun createUserDocument(user: UserProfile) {
    authViewModel.createUserDocument(user)
    authViewModel.currentUser.observe(viewLifecycleOwner) {
      when (it) {
        is Resource.Success -> {
          binding.progressBar.gone()
          val user = it.data
          if (user != null) {
            authViewModel.userShare = user
            sendEmailVerification()
          }
        }
        is Resource.Error -> {
          binding.progressBar.gone()
          handleAlterDialog(
            title = getString(R.string.error_occurred),
            message = it.message
          ) { dialogInterface ->
            dialogInterface.cancel()
          }
        }
        else -> {}
      }
    }
  }

  private fun sendEmailVerification() {
    authViewModel.sendEmailVerification().observe(viewLifecycleOwner) {
      when (it) {
        is Resource.Success -> {
          binding.progressBar.gone()
          handleAlterDialog(
            title = getString(R.string.title_email_verification),
            message = getString(R.string.email_sent_succesfully)
          ) { dialogInterface ->
            dialogInterface.cancel()
            goLoginFragment()
          }
        }
        is Resource.Error -> {
          binding.progressBar.gone()
          handleAlterDialog(
            title = getString(R.string.error_occurred),
            message = it.message
          ) { dialogInterface ->
            dialogInterface.cancel()
          }
        }
        else -> {}
      }
    }
  }

  private fun handleAlterDialog(
    message: String,
    title: String = "",
    callback: (DialogInterface) -> Unit
  ) {
    Utils.alertDialog(
      context = context,
      title = title,
      message = message,
      textPositiveButton = getString(R.string.accept)
    ) {
      callback(it)
    }
  }

  private fun validateFields() {
    fullNameET.onFocusChangeListener = onFocusChangeListener(fullNameET) {
      authViewModel.validateFullName(it)
    }

    emailET.onFocusChangeListener = onFocusChangeListener(emailET) {
      authViewModel.validateEmail(it)
    }

    passwordET.onFocusChangeListener = onFocusChangeListener(passwordET) {
      authViewModel.validatePassword(it)
    }

    passwordRepeatET.onFocusChangeListener = onFocusChangeListener(passwordRepeatET) {
      authViewModel.validateRepeatPassword(it, passwordET.text.toString())
    }
  }

  private fun onFocusChangeListener(
    editText: EditText,
    function: ((String) -> ValidationResult)
  ): View.OnFocusChangeListener {
    return View.OnFocusChangeListener { _, focus ->
      val inputLayout = editText.parent.parent as TextInputLayout
      if (!focus) {
        val textString = editText.text.toString()
        val result = function.invoke(textString)
        if (!result.successful) {
          inputLayout.error = getString(result.resId!!)
        }
      } else {
        inputLayout.error = null
      }
    }
  }

  private fun alreadyHaveAccount() {
    binding.alreadyAccount.setOnClickListener {
      goLoginFragment()
    }
  }

  private fun goLoginFragment() {
    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
  }
}