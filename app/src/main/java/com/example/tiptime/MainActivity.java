package com.example.tiptime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button calc;
    TextView amount,total;
    SwitchCompat aSwitch;
    EditText input;
    RadioGroup radioGroup;
    Button pay;
    double tip= 0.0,amt,cost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calc=findViewById(R.id.calc);
        aSwitch=findViewById(R.id.switchbutton);
        aSwitch.setChecked(false);
        amount=findViewById(R.id.amount);
        total=findViewById(R.id.total);
        input=findViewById(R.id.input);
        radioGroup=findViewById(R.id.tip_options);
        pay=findViewById(R.id.PayButton);
        pay.setVisibility(View.INVISIBLE);
        amount.setText("0.0");
        total.setText("0.0");
        calc.setOnClickListener(view -> {
            calculate();
            if (!total.getText().toString().equalsIgnoreCase("0.0")) {
                pay.setVisibility(View.VISIBLE);
            }
            else{
                Toast.makeText(this, "Enter Valid Data", Toast.LENGTH_SHORT).show();
            }
        });
        pay.setOnClickListener(view1 -> {
            Intent intent=new Intent(MainActivity.this,payments.class);
            intent.putExtra("amount",total.getText().toString());
            startActivity(intent);
        });
    }

    private void calculate() {
        cost=Double.parseDouble(input.getText().toString());
        if(Integer.parseInt(input.getText().toString())==0){
            Toast.makeText(this, "Enter valid Cost of Service", Toast.LENGTH_SHORT).show();
            calc.setVisibility(View.INVISIBLE);
            return;
        }
        RadioButton tipchoice= findViewById(radioGroup.getCheckedRadioButtonId());
        if(tipchoice.getId()==R.id.option1){
            tip=cost*0.20;
        }
        else if(tipchoice.getId()==R.id.option2){
            tip=cost*0.18;
        }
        else{
            tip=cost*0.15;
        }
        SwitchCompat switchCompat=findViewById(R.id.switchbutton);
        boolean switchstate=switchCompat.isChecked();
        if(switchstate){
            tip=Math.round(tip);
        }
        amount.setText(""+tip);
        amt=tip+Double.parseDouble(input.getText().toString());
        total.setText(""+amt);
    }
}