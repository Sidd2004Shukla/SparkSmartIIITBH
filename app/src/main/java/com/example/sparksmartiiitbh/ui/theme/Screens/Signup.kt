package com.example.sparksmartiiitbh.ui.theme.Screens
import androidx.compose.runtime.Composable
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sparksmartiiitbh.ui.theme.Model.AuthState
import com.example.sparksmartiiitbh.ui.theme.viewModel.AuthViewModel
@Composable
fun Signup(modifier: Modifier, navController: NavController, authViewModel: AuthViewModel = hiltViewModel())
{
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    val authState=authViewModel.authState.observeAsState()
    val context= LocalContext.current
    LaunchedEffect(authState.value) {
        when(authState.value)
        {
            is AuthState.Admin->{
                navController.navigate("admin")
            }
            is AuthState.Worker->{
                navController.navigate("worker")
            }
            is AuthState.General->{
                navController.navigate("general")
            }
            is AuthState.Error->{
                Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else->Unit
        }
    }
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
        Text(text = "Signup Page", fontSize = 32.sp)
        Spacer(modifier =Modifier.height(16.dp))
        OutlinedTextField(value = email,
            onValueChange = {
                email=it.replace(" ","") },
            label={
                Text(text = "Email")
            },maxLines = 1,
            singleLine = true
            , modifier = Modifier.height(60.dp)
                .width(280.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
        Spacer(modifier =Modifier.height(32.dp))
        OutlinedTextField(value = password,
            onValueChange = {
                password=it.replace(" ","") },
            label={
                Text(text = "Password")
            },
            maxLines = 1,
            singleLine = true
            , modifier = Modifier.height(60.dp)
                .width(280.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )
        Spacer(modifier =Modifier.height(32.dp))
        Button(onClick = {
            authViewModel.signup(email,password,"general")
        },enabled = authState.value!=AuthState.Loading)
        {
            Text(text = "Create Account")
        }
        Spacer(modifier =Modifier.height(8.dp))
        TextButton(onClick = {
            navController.navigate("login")
        }) {
            Text(text = "Already have an account? Login")
        }




    }
}