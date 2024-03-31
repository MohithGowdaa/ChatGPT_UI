package com.example.gpt_app;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGPTTask extends AsyncTask<String, Void, String> {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final String secretToken; // Your secret token
    private final OkHttpClient httpClient = new OkHttpClient();
    private final AsyncTaskResponse delegate;

    public interface AsyncTaskResponse {
        void postResult(String result);
    }

    public ChatGPTTask(String secretToken, AsyncTaskResponse delegate) {
        this.secretToken = secretToken;
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... prompts) {
        try {
            String prompt = prompts[0];
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "gpt-3.5-turbo");
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "user").put("content", prompt));
            jsonBody.put("messages", messages);
            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + secretToken)
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null && delegate != null) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    String content = message.getString("content");
                    delegate.postResult(content);
                } else {
                    delegate.postResult("No response found.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                delegate.postResult("Error parsing the response.");
            }
        } else {
            delegate.postResult("Received null response from API.");
        }
    }
}
