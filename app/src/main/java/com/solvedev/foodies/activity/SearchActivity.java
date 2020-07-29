package com.solvedev.foodies.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.solvedev.foodies.R;

public class SearchActivity extends AppCompatActivity {

    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edtSearch = findViewById(R.id.edt_search);

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    Intent intentPromosi = new Intent(SearchActivity.this, ProdukStatusActivity.class);
                    intentPromosi.putExtra("search",edtSearch.getText().toString());
                    startActivity(intentPromosi);

                    handled = true;
                }
                return handled;
            }
        });
    }
}
