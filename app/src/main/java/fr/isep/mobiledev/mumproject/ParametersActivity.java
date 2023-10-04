package fr.isep.mobiledev.mumproject;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ParametersActivity extends AppCompatActivity {

    private static final Logger LOGGER = Logger.getLogger(ParametersActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);

        SharedPreferences sharedPreferences = getSharedPreferences("fr.isep.mobiledev.mumproject", MODE_PRIVATE);

        EditText phone = findViewById(R.id.editTextPhone);
        phone.setText(sharedPreferences.getString("phone", ""));
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                sharedPreferences.edit().putString("phone", phone.getText().toString()).apply();
            }
        });

        ActivityResultLauncher<String> mLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(),
            uris -> {
                if (uris != null) {
                    File destinationFolder = new File(getDataDir(), "/images/");
                    Arrays.stream(destinationFolder.listFiles()).forEach(File::delete);
                    uris.forEach(uri -> {
                        String path = getPath(uri);
                        File src = new File(path);
                        File destination = new File(destinationFolder, src.getName());

                        try (FileInputStream fis = new FileInputStream(src); FileOutputStream fos = new FileOutputStream(destination)){
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                fos.write(buffer, 0, length);
                            }
                        } catch (Exception e) {
                            LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        }
                    });
                }
            }
        );

        Button chooseFileButton = findViewById(R.id.chooseFileButton);
        chooseFileButton.setOnClickListener(v -> {
            mLauncher.launch("image/*");
        });
    }

    public String getPath(Uri uri) {

        String path = null;
        String[] projection = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if(cursor == null){
            path = uri.getPath();
        }
        else{
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }

}