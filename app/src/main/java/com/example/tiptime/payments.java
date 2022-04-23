package com.example.tiptime;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dev.shreyaspatil.easyupipayment.EasyUpiPayment;
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener;
import dev.shreyaspatil.easyupipayment.model.PaymentApp;
import dev.shreyaspatil.easyupipayment.model.TransactionDetails;

public class payments extends AppCompatActivity implements PaymentStatusListener{

    EditText noteEt,upiIdEt,nameEt,codeEt;
    RadioGroup radioAppChoice;
    TextView tid, statusView;
    Button send;
    String transactionId,name,note,upiId,amount,merchantcode;
    ScrollView scrollView;
    EasyUpiPayment easyUpiPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        initializeViews();
        Intent intent=getIntent();
        amount=intent.getStringExtra("amount");
        transactionId = "TID" + System.currentTimeMillis();
        tid.setText(transactionId);

        send.setOnClickListener(view -> {
            if(isConnectionAvailable(payments.this)) pay();
            else{
                Toast.makeText(payments.this, "Oops..No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @SuppressLint("NonConstantResourceId")
    private void pay() {
        note = noteEt.getText().toString();
        name = nameEt.getText().toString();
        upiId = upiIdEt.getText().toString();
        merchantcode = codeEt.getText().toString();
        if(upiId.equals("")||name.equals("")||note.equals("")||merchantcode.equals("")){
            Toast.makeText(this, "Enter all the details", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton paymentAppChoice = findViewById(radioAppChoice.getCheckedRadioButtonId());
        radioAppChoice.setVisibility(View.INVISIBLE);
        scrollView.setVisibility(View.INVISIBLE);
        PaymentApp paymentApp;
        switch (paymentAppChoice.getId()) {
            case R.id.app_amazonpay:
                paymentApp = PaymentApp.AMAZON_PAY;
                break;
            case R.id.app_bhim_upi:
                paymentApp = PaymentApp.BHIM_UPI;
                break;
            case R.id.app_google_pay:
                paymentApp = PaymentApp.GOOGLE_PAY;
                break;
            case R.id.app_phonepe:
                paymentApp = PaymentApp.PHONE_PE;
                break;
            case R.id.app_paytm:
                paymentApp = PaymentApp.PAYTM;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + paymentAppChoice.getId());
        }


        EasyUpiPayment.Builder builder = new EasyUpiPayment.Builder(payments.this)
                .with(paymentApp)
                .setPayeeVpa(upiId)
                .setPayeeName(name)
                .setTransactionId(transactionId)
                .setTransactionRefId(transactionId)
                .setPayeeMerchantCode(merchantcode)
                .setDescription(note)
                .setAmount(amount);
        try {
            easyUpiPayment = builder.build();
            easyUpiPayment.setPaymentStatusListener(this);
            easyUpiPayment.startPayment();

        } catch (Exception exception) {
            exception.printStackTrace();
            Toast.makeText(this, "Error : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onTransactionCompleted(@NonNull TransactionDetails transactionDetails) {

        Log.d("TransactionDetails", transactionDetails.toString());
        statusView.setText(transactionDetails.toString());

        switch (transactionDetails.getTransactionStatus()) {
            case SUCCESS:
                onTransactionSuccess();
                break;
            case FAILURE:
                onTransactionFailed();
                break;
            case SUBMITTED:
                onTransactionSubmitted();
                break;
        }
    }
    @Override
    public void onTransactionCancelled() {
        Toast.makeText(this, "Transaction cancelled..", Toast.LENGTH_SHORT).show();
    }
    private void onTransactionSubmitted() {
        Log.e("TAG", "TRANSACTION SUBMITTED");
    }
    private void onTransactionSuccess() {
        Toast.makeText(this, "Transaction successfully completed..", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(payments.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void onTransactionFailed(){
        Toast.makeText(this, "Failed to complete transaction", Toast.LENGTH_SHORT).show();
        scrollView.setVisibility(View.VISIBLE);
        radioAppChoice.setVisibility(View.VISIBLE);
    }

    private void initializeViews() {
        send = findViewById(R.id.pay);
        noteEt = findViewById(R.id.note);
        nameEt = findViewById(R.id.name);
        upiIdEt = findViewById(R.id.upi_id);
        codeEt=findViewById(R.id.code);
        tid=findViewById(R.id.tid);
        statusView=findViewById(R.id.status);
        scrollView=findViewById(R.id.scroll);
        radioAppChoice = findViewById(R.id.radioAppChoice);
    }

    public static boolean isConnectionAvailable(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable();
        }
        return false;
    }
}