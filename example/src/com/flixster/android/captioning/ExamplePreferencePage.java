package com.flixster.android.captioning;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.captioning.R;
import com.flixster.android.captioning.CaptionPreferences;
import com.flixster.android.captioning.CaptionView;

/** Example app page for selecting caption preferences, and starting an example video with captions */
public class ExamplePreferencePage extends Activity {

    CaptionView example;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_preference_layout);

        // example caption view for displaying changes
        example = (CaptionView) findViewById(R.id.example_caption);

        // setting up the preference selectors
        setupLanguageSpinner();
        setupFontSpinner();
        setupSizeSpinner();
        setupStyleSpinner();
        setupEdgeSpinner();
        setupTextColorSpinner();
        setupBgColorSpinner();
        setupEdgeColorSpinner();
        setupTextOpacitySpinner();
        setupBgOpacitySpinner();

        setupButtons();

        // turn captions on
        CaptionPreferences.instance().setCaptionsEnabled(true);
    }

    /** Set up the "Restore Defaults" and "Play Video" buttons */
    public void setupButtons() {
        Button defaultsButton = (Button) findViewById(R.id.defaults_button);
        Button playButton = (Button) findViewById(R.id.play_button);

        defaultsButton.setText("Restore Defaults");
        defaultsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                CaptionPreferences.instance().restoreDefaults();
                example.applyPreferences();
            }
        });

        playButton.setText("Play Video");
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExamplePreferencePage.this, ExamplePlayer.class);
                Bundle b = new Bundle();
                String videoUri = ((EditText) findViewById(R.id.video_uri)).getText().toString();
                String captionsUri = ((EditText) findViewById(R.id.captions_uri)).getText().toString();
                b.putString("video", videoUri);
                b.putString("captions", captionsUri);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    /** Set up the language options (This will not affect the caption language for the video) */
    public void setupLanguageSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.language_spinner);
        List<String> options = new ArrayList<String>();
        options.add("English");
        options.add("Spanish");
        options.add("French");
        options.add("German");
        options.add("Portuguese");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                CaptionPreferences.instance().setLanguage(pos);
                switch (pos) {
                    case CaptionPreferences.LANGUAGE_ENGLISH:
                        example.setText("[ CAPTION ]");
                        break;
                    case CaptionPreferences.LANGUAGE_SPANISH:
                        example.setText("[ SUBTÍTULO ]");
                        break;
                    case CaptionPreferences.LANGUAGE_FRENCH:
                        example.setText("[ SOUS-TITRE ]");
                        break;
                    case CaptionPreferences.LANGUAGE_GERMAN:
                        example.setText("[ UNTERTITEL ]");
                        break;
                    case CaptionPreferences.LANGUAGE_PORTUGUESE:
                        example.setText("[ SUBTÍTULO ]");
                        break;
                }
                example.applyPreferences();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    /** Set up the font options */
    public void setupFontSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.font_spinner);
        List<String> options = new ArrayList<String>();
        options.add("Monospace");
        options.add("Sans-Serif");
        options.add("Serif");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                CaptionPreferences.instance().setFontType(pos);
                example.applyPreferences();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        spinner.setSelection(1);
    }

    /** Set up the size options */
    public void setupSizeSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.size_spinner);
        List<String> options = new ArrayList<String>();
        options.add("Small");
        options.add("Medium");
        options.add("Large");
        options.add("Huge");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int size = CaptionPreferences.TEXT_SIZE_MEDIUM;
                switch (pos) {
                    case 0:
                        size = CaptionPreferences.TEXT_SIZE_SMALL;
                        break;
                    case 1:
                        break;
                    case 2:
                        size = CaptionPreferences.TEXT_SIZE_LARGE;
                        break;
                    case 3:
                        size = CaptionPreferences.TEXT_SIZE_HUGE;
                        break;
                }
                CaptionPreferences.instance().setTextSize(size);
                example.applyPreferences();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        spinner.setSelection(1);
    }

    /** Set up the text style options */
    public void setupStyleSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.style_spinner);
        List<String> options = new ArrayList<String>();
        options.add("Normal");
        options.add("Bold");
        options.add("Italic");
        options.add("Underline");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                CaptionPreferences.instance().setTextStyle(pos);
                example.applyPreferences();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    /** Set up the edge style options */
    public void setupEdgeSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.edge_spinner);
        List<String> options = new ArrayList<String>();
        options.add("None");
        options.add("Drop Shadow");
        options.add("Raised");
        options.add("Depressed");
        options.add("Uniform");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                CaptionPreferences.instance().setTextEdgeStyle(pos);
                example.applyPreferences();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    /** Set up the text color options */
    public void setupTextColorSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.text_color_spinner);
        List<String> options = new ArrayList<String>();
        options.add("White");
        options.add("Black");
        options.add("Red");
        options.add("Magenta");
        options.add("Yellow");
        options.add("Green");
        options.add("Cyan");
        options.add("Blue");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int color = Color.WHITE;
                switch (pos) {
                    case 0:
                        break;
                    case 1:
                        color = Color.BLACK;
                        break;
                    case 2:
                        color = Color.RED;
                        break;
                    case 3:
                        color = Color.MAGENTA;
                        break;
                    case 4:
                        color = Color.YELLOW;
                        break;
                    case 5:
                        color = Color.GREEN;
                        break;
                    case 6:
                        color = Color.CYAN;
                        break;
                    case 7:
                        color = Color.BLUE;
                        break;
                }
                CaptionPreferences.instance().setTextColor(color);
                example.applyPreferences();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    /** Set up the background color options */
    public void setupBgColorSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.bg_color_spinner);
        List<String> options = new ArrayList<String>();
        options.add("Black");
        options.add("White");
        options.add("Red");
        options.add("Magenta");
        options.add("Yellow");
        options.add("Green");
        options.add("Cyan");
        options.add("Blue");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int color = Color.BLACK;
                switch (pos) {
                    case 0:
                        break;
                    case 1:
                        color = Color.WHITE;
                        break;
                    case 2:
                        color = Color.RED;
                        break;
                    case 3:
                        color = Color.MAGENTA;
                        break;
                    case 4:
                        color = Color.YELLOW;
                        break;
                    case 5:
                        color = Color.GREEN;
                        break;
                    case 6:
                        color = Color.CYAN;
                        break;
                    case 7:
                        color = Color.BLUE;
                        break;
                }
                CaptionPreferences.instance().setBgColor(color);
                example.applyPreferences();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    /** Set up the shadow/outline color options */
    public void setupEdgeColorSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.edge_color_spinner);
        List<String> options = new ArrayList<String>();
        options.add("Black");
        options.add("White");
        options.add("Red");
        options.add("Magenta");
        options.add("Yellow");
        options.add("Green");
        options.add("Cyan");
        options.add("Blue");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int color = Color.BLACK;
                switch (pos) {
                    case 0:
                        break;
                    case 1:
                        color = Color.WHITE;
                        break;
                    case 2:
                        color = Color.RED;
                        break;
                    case 3:
                        color = Color.MAGENTA;
                        break;
                    case 4:
                        color = Color.YELLOW;
                        break;
                    case 5:
                        color = Color.GREEN;
                        break;
                    case 6:
                        color = Color.CYAN;
                        break;
                    case 7:
                        color = Color.BLUE;
                        break;
                }
                CaptionPreferences.instance().setTextEdgeColor(color);
                example.applyPreferences();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    /** Set up the text opacity options */
    public void setupTextOpacitySpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.text_opacity_spinner);
        List<String> options = new ArrayList<String>();
        options.add("100%");
        options.add("75%");
        options.add("50%");
        options.add("25%");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int opacity = 100;
                switch (pos) {
                    case 0:
                        break;
                    case 1:
                        opacity = 75;
                        break;
                    case 2:
                        opacity = 50;
                        break;
                    case 3:
                        opacity = 25;
                        break;
                }
                CaptionPreferences.instance().setTextOpacity(opacity);
                example.applyPreferences();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    /** Set up the background opacity options */
    public void setupBgOpacitySpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.bg_opacity_spinner);
        List<String> options = new ArrayList<String>();
        options.add("100%");
        options.add("75%");
        options.add("50%");
        options.add("25%");
        options.add("0%");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int opacity = 100;
                switch (pos) {
                    case 0:
                        break;
                    case 1:
                        opacity = 75;
                        break;
                    case 2:
                        opacity = 50;
                        break;
                    case 3:
                        opacity = 25;
                        break;
                    case 4:
                        opacity = 0;
                        break;
                }
                CaptionPreferences.instance().setBgOpacity(opacity);
                example.applyPreferences();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }
}
