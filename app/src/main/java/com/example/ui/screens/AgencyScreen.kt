package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.ui.MainViewModel
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyPrimary

@Composable
fun AgencyScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang by viewModel.language.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // E-Rickshaw Dealership Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.husaini_hero_banner),
                    contentDescription = "E-Rickshaw EV Showroom",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MS HUSAINI ENTERPRISES",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = AppStrings.get(lang, "agency_sub"),
                        fontSize = 12.sp,
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = EmeraldGreen,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "⚡ 100% Electric EV Mobility Dealership",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Owners Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Agency Owners & Contact Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = EmeraldGreen
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Owner 1: Mohammad Anees
                OwnerContactRow(
                    name = "Mohammad Anees",
                    phone = "7668732106",
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:7668732106"))
                        context.startActivity(intent)
                    },
                    onWhatsApp = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=917668732106"))
                        context.startActivity(intent)
                    }
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // Owner 2: Mohammad Suhail
                OwnerContactRow(
                    name = "Mohammad Suhail",
                    phone = "7906599049",
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:7906599049"))
                        context.startActivity(intent)
                    },
                    onWhatsApp = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=917906599049"))
                        context.startActivity(intent)
                    }
                )
            }
        }

        // Features & Capabilities
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "E-Rickshaw Agency Management",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = EmeraldGreen
                )

                FeatureBullet("🛺 1-Click E-Rickshaw Bill & Delivery Slips", "Instant generation of delivery slips with Chassis number, Motor specs, Total Price, Down Payment & EMI calculations.")
                FeatureBullet("🔋 Battery & Guarantee Card System", "Store both E-Rickshaw and Battery guarantee card photos directly with customer records.")
                FeatureBullet("📱 Dual Mobile Live Data Sync", "Mohammad Anees and Mohammad Suhail can share and sync customer records between their phones in 1 tap.")
                FeatureBullet("🔍 Deep Customer & Chassis Search", "Search instantly by Customer Name, Phone, Aadhaar, Chassis Number or Vehicle Number.")
                FeatureBullet("🌐 Hindi ⇋ English Switcher", "Full multi-language support toggleable with a single tap.")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun OwnerContactRow(
    name: String,
    phone: String,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(EmeraldGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalNavyDark
                )
                Text(
                    text = "📞 $phone",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            IconButton(
                onClick = onCall,
                modifier = Modifier
                    .size(36.dp)
                    .background(ElectricBlue.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = "Call", tint = ElectricBlue, modifier = Modifier.size(18.dp))
            }
            IconButton(
                onClick = onWhatsApp,
                modifier = Modifier
                    .size(36.dp)
                    .background(EmeraldGreen.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "WhatsApp", tint = EmeraldGreen, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun FeatureBullet(title: String, desc: String) {
    Column {
        Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalNavyDark)
        Text(text = desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
