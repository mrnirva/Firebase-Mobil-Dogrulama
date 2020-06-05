package com.harun.firebasemobildogrulama;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

// https://harun.xyz/

public class MainActivity extends AppCompatActivity {

    // Tanımlamalar
    private EditText telefonNo, dogrulamaKodu;
    private Button koduGonder, koduDogrula;

    // Doğrulama işlemleri için kullanacağımız değişken
    private String mVerificationId;

    // Firebase kimlik doğrulama nesnemiz
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tanımlama
        mAuth = FirebaseAuth.getInstance();

        // Bağlama İşlemleri
        telefonNo = findViewById(R.id.telefonNo);
        dogrulamaKodu = findViewById(R.id.dogrulamaKodu);

        koduGonder = findViewById(R.id.koduGonder);
        koduDogrula = findViewById(R.id.koduDogrula);

        // Kodu gönder butonuna basılma anında çalışacak kısım
        koduGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Kolaylık olsun diye telefon numarasını alıp bir değişkene atıyoruz
                String telefon_no = telefonNo.getText().toString();

                // Telefon no boş değilse
                if(!telefon_no.isEmpty()){

                    dogrulamaKoduGonder(telefon_no);

                }else{

                    Toast.makeText(MainActivity.this,"Telefon No Boş Olamaz!",Toast.LENGTH_SHORT).show();

                }

            }
        });


        // Kodu Doğrula butonuna basılınca çalışacak kısım
        koduDogrula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Kolaylık olması için dogrulama kodunu bir değişkene atıyorum
                String dogrulama_kodu = dogrulamaKodu.getText().toString();

                // Doğrulama kodu boş değilse koduDogrula metodumuza gider
                if(!dogrulama_kodu.isEmpty()){

                    koduDogrula(dogrulama_kodu);

                }else{

                    Toast.makeText(MainActivity.this,"Doğrulama Kodu Boş Olamaz!",Toast.LENGTH_SHORT).show();

                }

            }
        });



    }

    // Doğrulama kodu gönderen metodumuz
    private void dogrulamaKoduGonder(String numara){

        // Dinleyici metotlarımız
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

                // Tamamlanma anında çalışan yer
                Log.e("hata", "Verification Complete");
                Toast.makeText(MainActivity.this,"Doğrulama Tamamlandı",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                // Hata anında çalışan yer
                Log.e("hata", "Verification Failed: "+e.getMessage());
                Toast.makeText(MainActivity.this,"Doğrulama Hatası: "+e.getMessage(),Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                // Kod gönderildiği zaman çalışan yer

                Log.e("hata", "Code Sent");
                mVerificationId = verificationId;

                Toast.makeText(MainActivity.this,"Doğrulama Kodu Gönderildi",Toast.LENGTH_LONG).show();

            }
        };


        /*

            Kod gönderme işlemi yapan kısım
            İlk parametre numaramız
            İkinci parametre ikinci kod için bekleme süresi
            Üçüncü paramtere beklemenin türü biz dakikayı seçtik yani 2 dakika bekleyecek
            Dördüncü parametre işlemlerin hangi Activity üzerinde yüreyeceğini söylediğimiz yer
            Son parametreyse dinleme işlemi yapan hemen yukarıda tanımladığımız OnVerificationStateChanged call backimiz

        */

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                numara,
                2,
                TimeUnit.MINUTES,
                this,
                mCallbacks);


    }


    // Kodun doğrulandığı kısım
    private void koduDogrula(String dogrulama_kodum){

        // Kimlik nesnemizi oluştuduğumuz kısım
        // İlk parametre daha önce sabitlediğimiz VerificationId ikinci parametre telefonumuza gelen doğrulama kodumuz
        final PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, dogrulama_kodum);

        // Firebase mAuth nesnemizle kontrol işlemini yapıyoruz
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Eğer task başarılı dönerse kod doğrulanmış demektir
                        if (task.isSuccessful()) {

                            Log.e("hata","Verification Success");

                            Toast.makeText(MainActivity.this
                                    ,"Kod Başarıyla Doğrulandı :)"
                                    ,Toast.LENGTH_LONG).show();

                            // Telefon doğrulaması yapıldıktan sonra yapılacak işlemler bu metot içerisinde yer alacak
                            dogrulamaSonrasiIslemler();

                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                Log.e("hata","Verification Failed, Invalid credentials");
                                Toast.makeText(MainActivity.this
                                        ,"Hatalı Doğrulama Kodu!"
                                        ,Toast.LENGTH_LONG).show();

                            }

                        }

                    }
                });

    }


    private void dogrulamaSonrasiIslemler(){

        // Doğrulama sonrasında yapılacak işlemler

    }

}
