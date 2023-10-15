package com.example.firebase_personelkaydet

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import androidx.compose.material3.AlertDialog as AlertDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MyViewModel){
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var selectedItemIndex by remember{ mutableStateOf(0)}


    Surface(
        color = Color.Black,
        modifier = Modifier.fillMaxSize()
    ) {
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(MaterialTheme.colorScheme.inversePrimary)
                        ,
                        contentAlignment = Alignment.Center
                    ){
                        if(viewModel.firebaseAuth.currentUser?.email != ""){
                            Row {
                                Text(text =  viewModel.firebaseAuth.currentUser?.email.toString(), fontWeight = FontWeight.ExtraBold)
                            }

                        }
                    }

                    NavigationDrawerItem(
                        label = { Text(text = "Personel List") },
                        selected = 0 == selectedItemIndex ,
                        onClick = {
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            navController.navigate("personel_liste")
                            selectedItemIndex = 0
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Personel Ekle") },
                        selected = 1 == selectedItemIndex ,
                        onClick = {
                            navController.navigate("personel_ekle/{pno}/{name}/{salary}")
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            selectedItemIndex = 1
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Log out") },
                        selected = 2 == selectedItemIndex ,
                        onClick = {
                            selectedItemIndex = 2
                            navController.navigate("sign_in")
                            viewModel.firebaseAuth.signOut()
                            coroutineScope.launch {
                                drawerState.close()
                            }
                        }
                    )
                }
            },
            drawerState = drawerState
        ) {

            Scaffold(
                topBar = {
                    when(navController.currentBackStackEntryAsState().value?.destination?.route){
                        "personel_liste","personel_ekle/{pno}/{name}/{salary}"->{
                            TopAppBar(
                                title = { Text(text ="")},
                                navigationIcon = {
                                    IconButton(onClick = {coroutineScope.launch { drawerState.open() } }) {
                                        Icon(imageVector = Icons.Default.Menu, contentDescription = "menu")
                                    }

                                },
                                actions ={
                                    IconButton(onClick = { viewModel.isDark.value = !viewModel.isDark.value}) {

                                        Icon(painter =
                                                if (viewModel.isDark.value){
                                                    painterResource(id = R.drawable.darkbulb)
                                                }else{
                                                     painterResource(id = R.drawable.lightbulb)
                                                }
                                            , contentDescription = "dark or light theme button ")
                                    }
                                }


                            )
                        }
                        else->{
                            null
                        }
                    }
                },
                bottomBar = {
                    when (navController.currentBackStackEntryAsState().value?.destination?.route) {
                        "personel_ekle/{pno}/{name}/{salary}", "personel_liste" -> {
                            BottomBar(navController)
                        }

                        else -> {
                            null // personel_ekle ve personel_liste sayfaları dışındaki sayfalarda alt çubuk yok
                        }
                    }
                }
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                ){
                    NavHost(navController = navController, startDestination = "sign_in" , modifier = Modifier.fillMaxSize()){
                        composable("personel_ekle/{pno}/{name}/{salary}")
                        { backStackEntry ->
                            PersonelEkle(
                                viewModel,
                                pnoa = backStackEntry.arguments?.getString("pno")?:"",
                                namea = backStackEntry.arguments?.getString("name")?:"",
                                salarya = backStackEntry.arguments?.getString("salary")?:""
                            )
                        }
                        composable("personel_liste"){PersonelListe(viewModel,navController)}
                        composable("sign_up"){ SignUp(viewModel,navController)}
                        composable("sign_in"){SignIn(viewModel,navController)}

                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUp(viewModel: MyViewModel, navController: NavController){
    val context = LocalContext.current
    var email by remember{ mutableStateOf("")}
    var password by remember{ mutableStateOf("")}
    var isValidPassword by remember {
        mutableStateOf(true)
    }
    var isPasswordViseble by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(
            text = "Sign up with email",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(30.dp))
        TextField(
            value = email,
            onValueChange = {email = it},
            label = { Text(text = "Email")},

        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = password,
            onValueChange = {password = it},
            label = { Text(text = "Password")},
            visualTransformation = if (isPasswordViseble) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (isPasswordViseble){
                    painterResource(id =R.drawable.visibility)}
                else {
                    painterResource(id = R.drawable.visible)
                }
                IconButton(
                    onClick = {
                        isPasswordViseble = !isPasswordViseble
                    }
                ) {
                    Icon(painter = icon, contentDescription = "visiblety", modifier = Modifier.run { width(20.dp).height(20.dp) })
                }
            }
        )
        Spacer(modifier = Modifier.height(10.dp))

        
        Button(onClick = {
            if (password.length>8 && containsUpperCaseLetter(password)&& containsSpecialCharacter(password)){

                viewModel.firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener{
                        if (it.isSuccessful){
                            navController.navigate("personel_liste")
                        }else{
                            Toast.makeText(context,"Lütfen geçerli bir mail adresi giriniz",Toast.LENGTH_SHORT).show()
                            Log.d("signuphata",it.exception.toString())
                        }
                    }
            }
            else{
                 isValidPassword = false
                
            }
            
        }) {
            Text(text = "SignUp")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Text(text = "Already have an account?")
            Text(
                text = "Sign in",
                color = Color.Green,
                modifier = Modifier
                    .clickable { navController.navigate("sign_in")}

            )
        }
        if (isValidPassword == false){
            Column {
                Text(text = "Şifreniz en az 8 karakter olmalıdır", color = Color.Red)
                Text(text = "Şifreniz özel karakterler içermelidir", color = Color.Red)
                Text(text = "Şifreniz en az bir büyük harf içermelidir",color = Color.Red)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignIn(viewModel: MyViewModel, navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordViseble by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome back.", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(30.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text(text = "Email") })
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            visualTransformation = if (isPasswordViseble) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (isPasswordViseble) {
                    painterResource(id = R.drawable.visibility)
                } else {
                    painterResource(id = R.drawable.visible)
                }
                IconButton(
                    onClick = {
                        isPasswordViseble = !isPasswordViseble
                    }
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = "visiblety",
                        modifier = Modifier.run { width(20.dp).height(20.dp) })
                }
            }
        )
        if (showAlertDialog == true) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Text(
                        text = "tamam",
                        modifier = Modifier.clickable {
                            showAlertDialog = false
                        }
                    )

                },
                title = { Text(text = "Uyarı") },
                text = { Text(text = "Email veya Şifre Hatalı") }

            )

        }
        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                viewModel.firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            navController.navigate("personel_liste")
                            Toast.makeText(context, "Giriş Başarılı", Toast.LENGTH_SHORT).show()
                        } else {
                            showAlertDialog = true
                        }
                    }
            },

        ){
            Text(text = "Sign In")

        }
        Row {
            Text(text = "No Account?")
            Text(
                text = "Create one.",
                modifier = Modifier.clickable { navController.navigate("sign_up") },
                color = Color.Green
            )
        }
    }

}

@Composable
fun PersonelListe(viewModel: MyViewModel, navController: NavController) {
    val personelListState = viewModel.personelList



    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {
        items(personelListState.value) { personel ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "ID: ${personel.pNo}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Name-Surname: ${personel.pNameSurname}",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Salary: ${personel.pSalary}",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Position: ${personel.pPosition}",
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        onClick = {
                            val route = "personel_ekle/${personel.pNo}/${personel.pNameSurname}/${personel.pSalary}"
                            navController.navigate(route)
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.Blue // Renk özelleştirmesi
                        )
                    }
                    IconButton(
                        onClick = {
                                  viewModel.deletePersonel(personel.pNo)
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red // Renk özelleştirmesi
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonelEkle(viewModel: MyViewModel, pnoa: String, namea: String, salarya: String) {
    val context = LocalContext.current
    val radioOptions = listOf("Yönetici", "Mühendis", "Muhasebeci", "Temizlik-Güvenlik Personeli")
    var selectedOption by remember { mutableStateOf(radioOptions[0]) }
    var alertDialogState:MutableState<Boolean> = remember { mutableStateOf(false)}

    var pNo by remember {
        mutableStateOf(
            if (pnoa == "{pno}") {
                ""
            } else {
                pnoa
            }
        )
    }
    var pNameSurname by remember {
        mutableStateOf(
            if (namea == "{name}") {
                ""
            } else {
                namea
            }
        )
    }
    var pSalary by remember {
        mutableStateOf(
            if (salarya == "{salary}") {
                ""
            } else {
                salarya
            }
        )
    }
    MyAlertDialog(alertDialogState){

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {



        OutlinedTextField(
            value = pNo,
            onValueChange = { pNo = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            label = { Text(text = "Personel No") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)

        )

        OutlinedTextField(
            value = pNameSurname,
            onValueChange = { pNameSurname = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            label = { Text(text = "Personel Ad-Soyad") },

        )

        OutlinedTextField(
            value = pSalary,
            onValueChange = { pSalary = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            label = { Text(text = "Personel Maaş") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        RadioGroup(radioOptions, selectedOption) { option ->
            selectedOption = option
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if ((pNo != "") || (pNameSurname != "") || (pSalary != "")){
                    viewModel.writeNewPersonel(pNo, pNameSurname, pSalary, selectedOption)
                    pNo = ""
                    pNameSurname = ""
                    pSalary = ""
                    Toast.makeText(context, "Kayıt İşlemi Başarılı", Toast.LENGTH_SHORT).show()
                }else{
                    alertDialogState.value = true
                }
            },

        ) {
            Text(text = "Save")
        }
    }
}
@Composable
fun MyAlertDialog(
    isShowAlertDialog:MutableState<Boolean>,
    onDismiss: ()-> Unit
){
    if (isShowAlertDialog.value == true){
        AlertDialog(
            onDismissRequest = {
            },
            confirmButton = {
                Button(
                    onClick = { 
                        isShowAlertDialog.value = false
                    }

                ) {
                    Text(text = "Tamam")
                }
            },
            title = {
                Text(text = "Uyarı")
            },
            text = {
                Text(text = "Lütfen boş alanları doldurun")
            }
        )
    }

}

@Composable
fun RadioGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(Modifier.selectableGroup()) {
        options.forEach { text ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton,
                        enabled = true
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = (text == selectedOption), onClick = { })
                Text(
                    text = text,
                    modifier = Modifier.padding(start = 16.dp),
                    fontSize = 18.sp
                )
            }
        }
    }
}


@Composable
fun BottomBar(navController: NavController) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationBar {
        NavigationBarItem(
            selected = backStackEntry.value?.destination?.route == "personel_liste",
            onClick = { navController.navigate("personel_liste") },
            icon = {
                Icon(imageVector = Icons.Default.List, contentDescription = "personel_list")
            }
        )
        NavigationBarItem(
            selected = backStackEntry.value?.destination?.route == "personel_ekle",
            onClick = { navController.navigate("personel_ekle/{pno}/{name}/{salary}") },
            icon = {
                Icon(imageVector = Icons.Default.Add, contentDescription = "personel_ekle")
            }
        )
    }
}

fun containsUpperCaseLetter(password:String):Boolean{
    for (char in password){
        if (char.isUpperCase()){
            return true
        }

    }
    return false
}
fun containsSpecialCharacter(password: String):Boolean{
    val specialCharcter = "!@#$%^&*()_-+=<>?{}[]|"
    for (i in specialCharcter){
        for (j in password){
            if (j.equals(j)){
                return true
            }
        }
    }
    return false
}



