package com.dsm.eventos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        val btnIngresar :Button=findViewById(R.id.btnIngresar)
        val email:TextView=findViewById(R.id.mail)
        val pass:TextView=findViewById(R.id.pass)
        val btngGoogle:Button=findViewById(R.id.btnGoogle);
        val btngRegistrar:Button=findViewById(R.id.btnRegister);
        btngRegistrar.setOnClickListener()
        {
            val i=Intent(this,RegisterActivity::class.java)
            startActivity(i);
        }
        btnIngresar.setOnClickListener()
            {
                if((!email.text.isEmpty() || !email.text.isBlank()) && (!pass.text.isEmpty() || !pass.text.isBlank()))
                {
                    sigIn(email.text.toString(),pass.text.toString())
                }else{
                    Toast.makeText(baseContext,"Error correo o contraseña vacio",Toast.LENGTH_SHORT).show()
                }
            }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // ID configurado en google-services.json
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        btngGoogle.setOnClickListener()
        {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result->
        if(result.resultCode==Activity.RESULT_OK)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task);
        }
        else
        {
            Toast.makeText(baseContext,"Ha ocurrido un error",Toast.LENGTH_SHORT).show()
        }
    }
    private fun handleResults(task: Task<GoogleSignInAccount>)
    {
        if(task.isSuccessful)
        {
            val account:GoogleSignInAccount?=task.result
            if (account!=null)
            {
                updateUI(account)
            }
        }
        else
        {
            Toast.makeText(baseContext,"Ha ocurrido un Error ",Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential=GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful)
            {
                Toast.makeText(baseContext,"Iniciando",Toast.LENGTH_SHORT).show()
                val i=Intent(this,EventosActivity::class.java)
                startActivity(i);
            }
            else
            {
                Toast.makeText(baseContext,"Error "+it.exception.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sigIn(email: String,pass:String)
    {
        try {
            Log.d("email",email)
            firebaseAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this) { task->
                    if(task.isSuccessful)
                    {
                        val user=firebaseAuth.currentUser
                        Toast.makeText(baseContext,user?.uid.toString(),Toast.LENGTH_SHORT).show()
                        val i=Intent(baseContext,EventosActivity::class.java)
                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(baseContext,"Credenciales invalidas",Toast.LENGTH_SHORT).show()
                    }
                }
        }catch (e:Exception)
        {
            Log.e("Eror",e.toString())
            Toast.makeText(baseContext,"Error "+e,Toast.LENGTH_LONG).show()
        }
    }
}