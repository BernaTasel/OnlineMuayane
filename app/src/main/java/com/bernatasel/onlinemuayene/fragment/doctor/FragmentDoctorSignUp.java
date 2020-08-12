package com.bernatasel.onlinemuayene.fragment.doctor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.fragment.general.FragmentLogin;
import com.bernatasel.onlinemuayene.utils.MyFM;
import com.bernatasel.onlinemuayene.utils.UtilsAndroid;
import com.bernatasel.onlinemuayene.utils.UtilsPermissions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class FragmentDoctorSignUp extends FragmentBase {
    public static FragmentDoctorSignUp newInstance(String email) {
        Bundle args = new Bundle();
        FragmentDoctorSignUp fragment = new FragmentDoctorSignUp();
        fragment.email = email;
        fragment.setArguments(args);
        return fragment;
    }

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private String email;

    private String profilePhoto = "default";
    private ImageView ivProfilePhoto;
    private TextView tvNameSurname, tvEmail;
    private EditText etPhone, etBirthday;
    private EditText etProfession, etGender, etCity;
    private CheckBox cbPrivacy, cbKVKK;
    private Button btnRegister;

    private int selectedProfession;
    private int selectedGender;
    private int selectedCity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_doctor_sign_up, container, false);

        setHeaderTitle("KAYIT OL");
        initView(view);

        ivProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] option = {"Kamera", "Galeri"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getMA(), android.R.layout.select_dialog_item, option);
                AlertDialog.Builder builder = new AlertDialog.Builder(getMA());
                builder.setTitle("");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (UtilsPermissions.checkPermissionCameraElseAsk(getActivity())) {
                                pickFromCamera();
                            }
                        } else {
                            pickFromGallery();
                        }
                    }
                });
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        etProfession.setFocusable(false);
        etProfession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());
                builder.setTitle("Uzmanlık Alanı: ");
                builder.setSingleChoiceItems(getResources().getStringArray(R.array.professions_array), selectedProfession, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedProfession = which;
                    }
                });
                builder.setPositiveButton(R.string.tamam, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etProfession.setText(getResources().getStringArray(R.array.professions_array)[selectedProfession]);
                    }
                });
                builder.setNegativeButton(R.string.iptal, null);
                builder.create().show();
            }
        });

        etGender.setFocusable(false);
        etGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());
                builder.setTitle("Cinsiyet: ");
                builder.setIcon(R.drawable.gender);
                builder.setSingleChoiceItems(getResources().getStringArray(R.array.gender_array), selectedGender, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedGender = which;
                    }
                });
                builder.setPositiveButton(R.string.tamam, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etGender.setText(getResources().getStringArray(R.array.gender_array)[selectedGender]);
                    }
                });
                builder.setNegativeButton(R.string.iptal, null);
                builder.create().show();
            }
        });


        etBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker;
                datePicker = new DatePickerDialog(getMA(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etBirthday.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);

                datePicker.setTitle("Tarih Seçiniz");
                datePicker.setButton(DialogInterface.BUTTON_POSITIVE, "Tamam", datePicker);
                datePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "İptal", datePicker);
                datePicker.show();
            }
        });

        etCity.setFocusable(false);
        etCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());
                builder.setTitle("Bulunduğunuz Şehir: ");
                builder.setIcon(R.drawable.city);
                builder.setSingleChoiceItems(getResources().getStringArray(R.array.cities_array), selectedCity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedCity = which;
                    }
                });
                builder.setPositiveButton(R.string.tamam, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etCity.setText(getResources().getStringArray(R.array.cities_array)[selectedCity]);
                    }
                });
                builder.setNegativeButton(R.string.iptal, null);
                builder.create().show();
            }
        });

        cbPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());
                builder.setTitle("Gizlilik ve Üyelik Sözleşmesi");
                builder.setMessage(getResources().getString(R.string.privacy));
                builder.setPositiveButton("Kabul et", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cbPrivacy.setChecked(true);
                    }
                });
                builder.setNegativeButton(R.string.iptal, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cbPrivacy.setChecked(false);
                    }
                });
                builder.show();
            }
        });

        cbKVKK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());

                builder.setTitle("Kişisel Verileri Koruma Kanunu Metni");
                builder.setMessage(getResources().getString(R.string.kvkk));
                builder.setPositiveButton("Kabul et", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cbKVKK.setChecked(true);
                    }
                });
                builder.setNegativeButton(R.string.iptal, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cbKVKK.setChecked(false);
                    }
                });
                builder.show();
            }
        });
        btnRegister.setOnClickListener(v -> {
            getMA().showHideLoading(true);
            if (checkUser()) {
                HashMap<String, Object> doctorInfo = new HashMap<>();

                doctorInfo.put("profilePhoto", profilePhoto);
                doctorInfo.put("gender", etGender.getText().toString());
                doctorInfo.put("phone", etPhone.getText().toString());
                doctorInfo.put("profession", etProfession.getText().toString());
                doctorInfo.put("city", etCity.getText().toString());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                try {
                    Date date = formatter.parse(etBirthday.getText().toString());
                    Timestamp birthdate = new Timestamp(date);
                    doctorInfo.put("birthdate", birthdate);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }

                FSOps.getInstance().updateUser(email, doctorInfo);

                getMA().myFM.clearAll();
                getMA().addFragmentSafe(FragmentLogin.newInstance(), MyFM.ANIM.RIGHT);
            }
            getMA().showHideLoading(false);
        });

        return view;
    }


    private void initView(View view) {
        ivProfilePhoto = view.findViewById(R.id.ivProfilePhoto);
        tvNameSurname = view.findViewById(R.id.tvNameSurname);
        tvEmail = view.findViewById(R.id.tvEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etBirthday = view.findViewById(R.id.etBirthday);
        etCity = view.findViewById(R.id.etCity);
        etGender = view.findViewById(R.id.etGender);
        etProfession = view.findViewById(R.id.etProfession);
        cbPrivacy = view.findViewById(R.id.cbPrivacy);
        cbKVKK = view.findViewById(R.id.cbKVKK);
        btnRegister = view.findViewById(R.id.btnDrRegister);


        tvEmail.setText(email);
        FSOps.getInstance().getUserByEmail(email).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        String name = qds.getString("name");
                        String surname = qds.getString("surname");
                        tvNameSurname.setText(name + " " + surname);
                    }
                } else {
                    showToast("Doktor çekilirken hata Oluştu");
                }
            }
        });
    }

    private void pickFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    //GALERİDEN FOTOĞRAF ALMA
    private void pickFromGallery() {
        //ACTION_GET_CONTENT --> herhangi bir dosya seçebilir
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    //SEÇİLEN FOTOĞRAFI GÖRÜNTÜLEME ve KAYDETME
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    Bundle bundle = data.getExtras();
                    Bitmap bitmapCamera = (Bitmap) bundle.get("data");
                    ivProfilePhoto.setImageBitmap(bitmapCamera);
                    profilePhoto = UtilsAndroid.bitmapToBase64(bitmapCamera);
                    break;
                case GALLERY_REQUEST_CODE:
                    Uri imageUri = data.getData();
                    try {
                        Bitmap bitmapGallery = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        profilePhoto = UtilsAndroid.bitmapToBase64(bitmapGallery);
                    } catch (Exception e) {
                        showToast("Dosya bulunamadı");
                    }
                    ivProfilePhoto.setImageURI(imageUri);
                    break;
            }
        }
    }


    boolean isEmptyEditText(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    public boolean checkUser() {
        boolean isValid = true;

        //TELEFON
        if (isEmptyEditText(etPhone)) {
            etError(etPhone, "Telefon boş bırakılamaz!");
            isValid = false;
        }

        //İL
        if (isEmptyEditText(etCity)) {
            etError(etCity, "Lütfen şehir seçimi yapınız!");
            isValid = false;
        }

        //DOĞUM TARİHİ
        if (isEmptyEditText(etBirthday)) {
            etError(etBirthday, "Doğum tarihi boş bırakılamaz!");
            isValid = false;
        }

        //CİNSİYET
        if (isEmptyEditText(etGender)) {
            etError(etGender, "Lütfen cinsiyet seçimi yapınız!");
            isValid = false;
        }

        //UZMANLIK ALANI
        if (isEmptyEditText(etProfession)) {
            etError(etProfession, "Lütfen uzmanlık alanı seçimi yapınız!");
            isValid = false;
        }

        //SÖZLEŞMELER
        if (!cbKVKK.isChecked() || !cbPrivacy.isChecked()) {
            showToast("Lütfen tüm sözleşmeleri onaylayınız!");
            isValid = false;
        }

        return isValid;
    }
}
