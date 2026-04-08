package com.tandau.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tandau.data.local.entities.Interest
import com.tandau.data.local.entities.Strength
import com.tandau.data.repository.UserRepository
import com.tandau.navigation.Screen
import com.tandau.viewmodel.OnboardingViewModel
import com.tandau.viewmodel.OnboardingViewModelFactory

@Composable
fun OnboardingScreen(navController: NavHostController, userRepository: UserRepository) {
    val viewModel: OnboardingViewModel = viewModel(factory = OnboardingViewModelFactory(userRepository))

    val interests by viewModel.interests.collectAsState()
    val strengths by viewModel.strengths.collectAsState()
    val selectedInterests by viewModel.selectedInterests.collectAsState()
    val selectedStrengths by viewModel.selectedStrengths.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val savedSuccess by viewModel.savedSuccess.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(savedSuccess) {
        if (savedSuccess) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Onboarding.route) { inclusive = true }
            }
        }
    }

    var currentStep by remember { mutableStateOf(0) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Progress indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                if (index <= currentStep) MaterialTheme.colorScheme.primary 
                                else Color.DarkGray,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                    }.using(SizeTransform(clip = false))
                }, label = "OnboardingStepAnimation"
            ) { step ->
                when (step) {
                    0 -> OnboardingStepDetails(
                        email = email,
                        password = password,
                        username = username,
                        city = city,
                        age = age,
                        onEmailChange = { email = it; validationError = null },
                        onPasswordChange = { password = it; validationError = null },
                        onUsernameChange = { username = it; validationError = null },
                        onCityChange = { city = it; validationError = null },
                        onAgeChange = { age = it; validationError = null },
                        error = validationError ?: error,
                        onNext = {
                            val err = viewModel.validateDetails(email, password, username, city)
                            if (err == null) {
                                currentStep = 1
                            } else {
                                validationError = err
                            }
                        }
                    )
                    1 -> OnboardingStepInterests(
                        interests = interests,
                        selectedInterests = selectedInterests,
                        onToggle = { id, checked ->
                            val newSelected = if (checked) selectedInterests + id else selectedInterests - id
                            viewModel.updateSelectedInterests(newSelected)
                        },
                        onBack = { currentStep = 0 },
                        onNext = { if (selectedInterests.isNotEmpty()) currentStep = 2 }
                    )
                    2 -> OnboardingStepStrengths(
                        strengths = strengths,
                        selectedStrengths = selectedStrengths,
                        isLoading = isLoading,
                        onToggle = { id, checked ->
                            val newSelected = if (checked) selectedStrengths + id else selectedStrengths - id
                            viewModel.updateSelectedStrengths(newSelected)
                        },
                        onBack = { currentStep = 1 },
                        onFinish = {
                            viewModel.saveUserProfile(email, password, username, city, age.toIntOrNull() ?: 0)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingStepDetails(
    email: String,
    password: String,
    username: String,
    city: String,
    age: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    error: String?,
    onNext: () -> Unit
) {
    Column {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Join Tandau and start your journey",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        OnboardingTextField(
            value = email, 
            onValueChange = onEmailChange, 
            label = "Email Address",
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        var passwordVisible by remember { mutableStateOf(false) }
        OnboardingTextField(
            value = password, 
            onValueChange = onPasswordChange, 
            label = "Password",
            leadingIcon = Icons.Default.Lock,
            keyboardType = KeyboardType.Password,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, contentDescription = null, tint = Color.Gray)
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        OnboardingTextField(
            value = username, 
            onValueChange = onUsernameChange, 
            label = "Username",
            leadingIcon = Icons.Default.Person
        )
        Spacer(modifier = Modifier.height(12.dp))
        OnboardingTextField(
            value = city, 
            onValueChange = onCityChange, 
            label = "City",
            leadingIcon = Icons.Default.LocationOn
        )
        Spacer(modifier = Modifier.height(12.dp))
        OnboardingTextField(
            value = age, 
            onValueChange = onAgeChange, 
            label = "Age (Optional)",
            leadingIcon = Icons.Default.DateRange,
            keyboardType = KeyboardType.Number
        )
        
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TandauButton(text = "Continue", onClick = onNext)
    }
}

@Composable
private fun OnboardingStepInterests(
    interests: List<Interest>,
    selectedInterests: Set<Long>,
    onToggle: (Long, Boolean) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column {
        Text(
            text = "Your Interests",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Select what excites you (Select at least one)",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(interests) { interest ->
                SelectableCard(
                    text = interest.name,
                    isSelected = selectedInterests.contains(interest.id),
                    onSelectedChange = { onToggle(interest.id, it) }
                )
            }
        }

        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TandauButton(text = "Back", onClick = onBack, modifier = Modifier.weight(0.4f), containerColor = Color.Transparent, contentColor = Color.White)
            TandauButton(text = "Next", onClick = onNext, modifier = Modifier.weight(0.6f), enabled = selectedInterests.isNotEmpty())
        }
    }
}

@Composable
private fun OnboardingStepStrengths(
    strengths: List<Strength>,
    selectedStrengths: Set<Long>,
    isLoading: Boolean,
    onToggle: (Long, Boolean) -> Unit,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    Column {
        Text(
            text = "Your Strengths",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "What are you good at? (Select at least one)",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(strengths) { strength ->
                SelectableCard(
                    text = strength.name,
                    isSelected = selectedStrengths.contains(strength.id),
                    onSelectedChange = { onToggle(strength.id, it) }
                )
            }
        }

        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TandauButton(text = "Back", onClick = onBack, modifier = Modifier.weight(0.4f), containerColor = Color.Transparent, contentColor = Color.White)
            TandauButton(
                text = if (isLoading) "Saving..." else "Complete",
                onClick = onFinish,
                modifier = Modifier.weight(0.6f),
                enabled = !isLoading && selectedStrengths.isNotEmpty()
            )
        }
    }
}

@Composable
fun OnboardingTextField(
    value: String, 
    onValueChange: (String) -> Unit, 
    label: String,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.DarkGray,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = Color.Gray,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            unfocusedLeadingIconColor = Color.Gray,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun TandauButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = Color.Black
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = Color.DarkGray,
            disabledContentColor = Color.Gray
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.titleLarge, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SelectableCard(text: String, isSelected: Boolean, onSelectedChange: (Boolean) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable { onSelectedChange(!isSelected) },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF1E1E1E),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
            Text(
                text = text,
                color = if (isSelected) Color.Black else Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}
