package com.example.satuhati.presentation.auth

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.satuhati.domain.model.CoupleStatus
import com.example.satuhati.ui.theme.PrimaryBlue
import com.example.satuhati.ui.theme.PrimarySoftBlue
import com.example.satuhati.ui.theme.ScreenBackground
import com.example.satuhati.ui.theme.TextDark
import com.example.satuhati.ui.theme.TextMuted

@Composable
fun ShowPairingCodeScreen(
    uiState: AuthUiState,
    onGenerateNewCode: () -> Unit,
    onAcceptRequest: () -> Unit,
    onRejectRequest: () -> Unit,
    onConnectedNavigate: () -> Unit
) {
    LaunchedEffect(uiState.isPairingSuccess) {
        if (uiState.isPairingSuccess) {
            onConnectedNavigate()
        }
    }

    LaunchedEffect(Unit) {
        if (uiState.currentCouple == null || uiState.currentCouple.pairingCode.isBlank()) {
            onGenerateNewCode()
        }
    }

    val couple = uiState.currentCouple
    val pairingCode = couple?.pairingCode ?: "MEMUAT..."

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
                    text = "Hubungkan Suami",
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        // Section: Invitation Code
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Kode Undangan",
                color = TextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )

            // Code Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFF), RoundedCornerShape(20.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Berikan kode ini kepada suami agar dia bisa menghubungkan akunnya ke akun Anda.",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                // Code Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Kode undangan",
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = pairingCode,
                        color = TextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        letterSpacing = 4.sp
                    )
                }

                Text(
                    text = "Di halaman lain, suami perlu memasukkan kode ini untuk meminta menghubungkan akunnya ke akun Anda.",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Button(
                onClick = onGenerateNewCode,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = "Buat ulang kode",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Section: Link Request from Husband
        if (couple?.status == CoupleStatus.PENDING_APPROVAL && couple.husbandId != null) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Permintaan Link Akun",
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFF), RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Permintaan dari suami",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = couple.husbandName ?: "Suami Anda",
                            color = TextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        couple.husbandEmail?.let {
                            Text(
                                text = it,
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Text(
                        text = "Suami Anda telah memasukkan kode undangan dan meminta menghubungkan akunnya ke akun Anda.",
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRejectRequest,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color(0xFFD9E2FF)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = "Tolak",
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Button(
                            onClick = onAcceptRequest,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text(
                                text = "Terima",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
