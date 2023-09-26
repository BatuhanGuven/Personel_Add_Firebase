package com.example.firebase_personelkaydet

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: myViewModel){
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var selectedItemIndex by remember{ mutableStateOf(0)}
    ModalNavigationDrawer(
        drawerContent = {
           ModalDrawerSheet {
               Box(
                   modifier = Modifier
                       .fillMaxWidth()
                       .height(200.dp)
                       .background(Color.LightGray),
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
                   navController.navigate("personel_ekle")
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
                    "personel_ekle","personel_liste"->{
                        TopAppBar(
                            title = { Text(text ="")},
                            navigationIcon = {
                                IconButton(onClick = {coroutineScope.launch { drawerState.open() } }) {
                                    Icon(imageVector = Icons.Default.Menu, contentDescription = "menu")
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
                    "personel_ekle", "personel_liste" -> {
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
                    composable("personel_ekle"){ PersonelEkle(viewModel) }
                    composable("personel_liste"){PersonelListe(viewModel)}
                    composable("sign_up"){ SignUp(viewModel,navController)}
                    composable("sign_in"){SignIn(viewModel,navController)}
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUp(viewModel: myViewModel,navController: NavController){
    val context = LocalContext.current
    var email by remember{ mutableStateOf("")}
    var password by remember{ mutableStateOf("")}
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(
            text = "Sign up with email",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(30.dp))
        TextField(value = email, onValueChange = {email = it}, label = { Text(text = "Email")})
        Spacer(modifier = Modifier.height(10.dp))
        TextField(value = password, onValueChange = {password = it}, label = { Text(text = "Password")})
        Spacer(modifier = Modifier.height(10.dp))
        
        Button(onClick = {
            viewModel.firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                if (it.isSuccessful){
                    navController.navigate("personel_liste")
                }else{
                    Toast.makeText(context,it.exception.toString(),Toast.LENGTH_SHORT).show()
                    Log.d("signuphata",it.exception.toString())
                }
            }
            
        }) {
            Text(text = "SignUp")
        }
        Spacer(modifier = Modifier.height(100.dp))
        Row {
            Text(text = "Already have an account?")
            Text(
                text = "Sign in",
                color = Color.Green,
                modifier = Modifier
                    .clickable { navController.navigate("sign_in")}

            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignIn(viewModel: myViewModel,navController: NavController){
    val context = LocalContext.current
    var email by remember{ mutableStateOf("")}
    var password by remember{ mutableStateOf("")}

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = "Welcome back.", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(30.dp))
        TextField(value = email, onValueChange = {email = it}, label = { Text(text = "Email")})
        Spacer(modifier = Modifier.height(10.dp))
        TextField(value = password, onValueChange = {password = it}, label = { Text(text = "Password")})
        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            viewModel.firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                if (it.isSuccessful){
                    navController.navigate("personel_liste")
                    Toast.makeText(context,"Giriş Başarılı",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,it.exception.toString(),Toast.LENGTH_SHORT).show()
                }
            }

        }) {
            Text(text = "Sign In")
        }
        Spacer(modifier = Modifier.height(100.dp))

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
fun PersonelListe(viewModel: myViewModel) {
    val listState = remember{ mutableStateOf(emptyList<Personel>())}
    val context = LocalContext.current
    listState.value = viewModel.personelList

    LazyColumn(Modifier.fillMaxSize()){
        items(listState.value){

            Card (
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
            ){
                Row(
                    modifier = Modifier.padding(5.dp)
                ) {

                    Text(text =it.pNo )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = it.pNameSurname)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = it.pSalary)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonelEkle(viewModel: myViewModel) {
    val context = LocalContext.current
    var pNo by remember{ mutableStateOf("") }
    var pNameSurname by remember{ mutableStateOf("") }
    var pSalary  by remember{ mutableStateOf("") }
    Box(modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {


            Spacer(modifier = Modifier.height(10.dp))
            TextField(value = pNo, onValueChange ={pNo = it}, label = { Text(text = "Personel No") })
            Spacer(modifier = Modifier.height(10.dp))
            TextField(value = pNameSurname, onValueChange ={pNameSurname=it}, label = { Text(text = "Personel Ad-Soyad") })
            Spacer(modifier = Modifier.height(10.dp))
            TextField(value = pSalary, onValueChange ={pSalary=it}, label = { Text(text = "Personel Maaş") })
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                viewModel.writeNewPersonel(pNo, pNameSurname, pSalary)
                pNo = ""
                pNameSurname  =""
                pSalary = ""
                Toast.makeText(context,"Kayıt İşlemi Başarılı",Toast.LENGTH_SHORT).show()
            }) {
                Text(text = "Save")
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavController){
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
            onClick = { navController.navigate("personel_ekle") },
            icon = {
                Icon(imageVector = Icons.Default.Add, contentDescription = "personel_ekle")
            }
        )
    }
}


