package com.lucifer.personal;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

// Imports the Google Cloud client library
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class TranslateText extends AsyncTask<String, Context, String> {

    @Override
    protected String doInBackground(String... strings) {

        Translate translate = TranslateOptions.getDefaultInstance().getService();

        // The text to translate
        //String text = "Hello, world!";

        // Translates some text into Russian
        Translation translation =
                translate.translate(
                        strings[0],
                        TranslateOption.sourceLanguage("en"),
                        TranslateOption.targetLanguage("ru"));

        //Toast.makeText(NewsDetailActivity.this,translation.getTranslatedText(),Toast.LENGTH_LONG).show();
        return translation.getTranslatedText();
    }

    protected void onPostExecute(String text) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}