package com.example.gpt_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements ChatGPTTask.AsyncTaskResponse {
    private EditText editTextPrompt;
    private TextView textViewResponse;

    // TODO: Securely store and retrieve your secret token. Do not hardcode it in your source code.
    private final String secretToken = "YOUR_API_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPrompt = findViewById(R.id.editTextPrompt);
        Button buttonSend = findViewById(R.id.buttonSend);
        textViewResponse = findViewById(R.id.textViewResponse);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prompt = editTextPrompt.getText().toString();
                new ChatGPTTask(secretToken, MainActivity.this).execute(prompt);
            }
        });


        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextPrompt.setText("");
                textViewResponse.setText("");

            }
        });
    }

    @Override
    public void postResult(String result) {

        textViewResponse.setText(result);
    }
}
