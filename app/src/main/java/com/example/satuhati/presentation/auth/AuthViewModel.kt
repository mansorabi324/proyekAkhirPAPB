package com.example.satuhati.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.satuhati.core.common.Resource
import com.example.satuhati.domain.model.Couple
import com.example.satuhati.domain.model.CoupleStatus
import com.example.satuhati.domain.model.User
import com.example.satuhati.domain.model.UserRole
import com.example.satuhati.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class AuthUiState(
    val currentUser: User? = null,
    val currentCouple: Couple? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val emailLinkSent: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val isRegisterSuccess: Boolean = false,
    val isPairingSuccess: Boolean = false
)

class AuthViewModel(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        authRepository.getCurrentUser()
            .onEach { user ->
                _uiState.value = _uiState.value.copy(currentUser = user)
                user?.coupleId?.let { coupleId ->
                    observeCouple(coupleId)
                }
            }
            .launchIn(viewModelScope)
    }

    fun observeCouple(coupleId: String) {
        authRepository.observeCouple(coupleId)
            .onEach { couple ->
                _uiState.value = _uiState.value.copy(
                    currentCouple = couple,
                    isPairingSuccess = couple?.status == CoupleStatus.CONNECTED
                )
            }
            .launchIn(viewModelScope)
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email dan kata sandi wajib diisi.")
            return
        }

        authRepository.loginWithEmail(email.trim(), pass.trim())
            .onEach { res ->
                when (res) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            currentUser = res.data,
                            isLoginSuccess = true
                        )
                        res.data.coupleId?.let { observeCouple(it) }
                    }
                    is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.message)
                }
            }
            .launchIn(viewModelScope)
    }

    fun sendEmailLink(email: String) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email wajib diisi.")
            return
        }

        authRepository.sendSignInLinkToEmail(email.trim())
            .onEach { res ->
                when (res) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                    is Resource.Success -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        emailLinkSent = true,
                        successMessage = "Tautan masuk telah dikirim ke $email"
                    )
                    is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.message)
                }
            }
            .launchIn(viewModelScope)
    }

    fun register(name: String, email: String, pass: String, confirmPass: String, role: UserRole) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Semua kolom wajib diisi.")
            return
        }
        if (pass.length < 8) {
            _uiState.value = _uiState.value.copy(errorMessage = "Kata sandi minimal 8 karakter.")
            return
        }
        if (pass != confirmPass) {
            _uiState.value = _uiState.value.copy(errorMessage = "Konfirmasi kata sandi tidak cocok.")
            return
        }

        authRepository.registerWithEmail(name.trim(), email.trim(), pass.trim(), role)
            .onEach { res ->
                when (res) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                    is Resource.Success -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = res.data,
                        isRegisterSuccess = true
                    )
                    is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.message)
                }
            }
            .launchIn(viewModelScope)
    }

    fun generatePairingCode() {
        val user = _uiState.value.currentUser ?: return
        authRepository.generatePairingCode(user)
            .onEach { res ->
                when (res) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            currentCouple = res.data
                        )
                        observeCouple(res.data.id)
                    }
                    is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.message)
                }
            }
            .launchIn(viewModelScope)
    }

    fun submitPairingCode(code: String) {
        val user = _uiState.value.currentUser ?: return
        if (code.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Masukkan kode undangan.")
            return
        }

        authRepository.requestLinkWithCode(user, code.trim())
            .onEach { res ->
                when (res) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            currentCouple = res.data,
                            successMessage = "Permintaan berhasil dikirim ke istri."
                        )
                        observeCouple(res.data.id)
                    }
                    is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.message)
                }
            }
            .launchIn(viewModelScope)
    }

    fun acceptRequest() {
        val coupleId = _uiState.value.currentCouple?.id ?: return
        authRepository.acceptLinkRequest(coupleId)
            .onEach { res ->
                when (res) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            currentCouple = res.data,
                            isPairingSuccess = true,
                            successMessage = "Akun berhasil dihubungkan!"
                        )
                    }
                    is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.message)
                }
            }
            .launchIn(viewModelScope)
    }

    fun rejectRequest() {
        val coupleId = _uiState.value.currentCouple?.id ?: return
        authRepository.rejectLinkRequest(coupleId)
            .onEach { res ->
                when (res) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is Resource.Success -> _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Permintaan ditolak.")
                    is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.message)
                }
            }
            .launchIn(viewModelScope)
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState()
        }
    }
}
