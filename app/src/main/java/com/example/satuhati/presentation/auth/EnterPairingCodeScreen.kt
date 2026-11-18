package com.example.satuhati.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.satuhati.domain.model.CoupleStatus
import com.example.satuhati.ui.theme.BorderNeutral
import com.example.satuhati.ui.theme.InputBackground
import com.example.satuhati.ui.theme.PrimaryBlue
import com.example.satuhati.ui.theme.PrimarySoftBlue
import com.example.satuhati.ui.theme.ScreenBackground
import com.example.satuhati.ui.theme.TextDark
import com.example.satuhati.ui.theme.TextMuted

@Composable
fun EnterPairingCodeScreen(
    uiState: AuthUiState,
    onSubmitCode: (String) -> Unit,
    onConnectedNavigate: () -> Unit
) {
    var code by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isPairingSuccess) {
        if (uiState.isPairingSuccess) {
            onConnectedNavigate()
        }
    }

    val couple = uiState.currentCouple
    val isPendingApproval = couple?.status == CoupleStatus.PENDING_APPROVAL

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(horizontal = 32.dp, vertical = 48.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(PrimarySoftBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "SATUHATI Logo",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = "SATUHATI",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Hubungkan Istri",
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        uiState.errorMessage?.let { msg ->
            Text(
                text = msg,
                color = Color.Red,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        uiState.successMessage?.let { msg ->
            Text(
                text = msg,
                color = PrimaryBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (isPendingApproval) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFF), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Menunggu Persetujuan Istri",
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Permintaan telah dikirim ke akun istri (${couple?.wifeName ?: ""}). Mohon minta istri untuk menekan tombol 'Terima' pada layar aplikasinya.",
                    color = TextMuted,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                CircularProgressIndicator(
                    color = PrimaryBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Kode Istri",
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase() },
                    placeholder = { Text("Masukkan kode istri", color = Color(0xFF94A3B8)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBackground,
                        unfocusedContainerColor = InputBackground,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = BorderNeutral
                    ),
                    singleLine = true
                )
            }

            Button(
                onClick = { onSubmitCode(code) },
                enabled = !uiState.isLoading && code.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Kirim",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
