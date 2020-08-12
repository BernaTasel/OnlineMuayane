package com.bernatasel.onlinemuayene.fragment.patient;

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
import android.widget.CompoundButton;
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

public class FragmentPatientSignUp extends FragmentBase {
    public static FragmentPatientSignUp newInstance(String email) {
        Bundle args = new Bundle();
        FragmentPatientSignUp fragment = new FragmentPatientSignUp();
        fragment.email = email;
        fragment.setArguments(args);
        return fragment;
    }


    private String email;

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private ImageView ivProfilePhoto;
    private String profilePhoto = "default";
    private TextView tvNameSurname, tvEmail, tvBmi;
    private EditText etPhone, etBirthday, etHeight, etWeight, etChronicInfo, etAllergyInfo;
    private EditText etCity, etGender;
    private CheckBox cbIsSmoke, cbIsAlcohol, cbIsAllergy, cbIsChronic;
    private Button btnRegister, btnBmi;

    private int selectedGender;
    private int selectedCity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_patient_sign_up, container, false);
        setHeaderTitle("KAYIT OL");
        initView(view);

        //FOTOĞRAF
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


        //BIRTHDAY DATEPICKER
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

        //BMI
        btnBmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMI();
            }
        });

        //ALERJİ VE KRONİK HASTALIK İÇİN BİLGİ ALMA SATIRLARINI GÖRÜNÜR YAPMA
        cbIsAllergy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etAllergyInfo.setVisibility(View.VISIBLE);
                } else {
                    etAllergyInfo.setVisibility(View.INVISIBLE);
                }
            }
        });

        cbIsChronic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etChronicInfo.setVisibility(View.VISIBLE);
                } else {
                    etChronicInfo.setVisibility(View.INVISIBLE);
                }
            }
        });

        btnRegister.setOnClickListener(v -> {
            getMA().showHideLoading(true);
            if (checkUser()) {
                HashMap<String, Object> patientInfo = new HashMap<>();

                patientInfo.put("profilePhoto", profilePhoto);
                patientInfo.put("gender", etGender.getText().toString());
                patientInfo.put("phone", etPhone.getText().toString());
                patientInfo.put("city", etCity.getText().toString());
                patientInfo.put("bmi", calculateBMI());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                try {
                    Date date = formatter.parse(etBirthday.getText().toString());
                    Timestamp birthdate = new Timestamp(date);
                    patientInfo.put("birthdate", birthdate);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                patientInfo.put("alcohol", cbIsAlcohol.isChecked());
                patientInfo.put("smoke", cbIsSmoke.isChecked());
                patientInfo.put("allergy", cbIsAllergy.isChecked());
                patientInfo.put("choronic", cbIsChronic.isChecked());
                patientInfo.put("allergyInfo", etAllergyInfo.getText().toString());
                patientInfo.put("choronicInfo", etChronicInfo.getText().toString());

                FSOps.getInstance().updateUser(email, patientInfo);

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
        etHeight = view.findViewById(R.id.etHeight);
        etWeight = view.findViewById(R.id.etWeight);
        cbIsSmoke = view.findViewById(R.id.cbIsSmoke);
        cbIsAlcohol = view.findViewById(R.id.cbIAlcohol);
        cbIsAllergy = view.findViewById(R.id.cbIsAllergy);
        cbIsChronic = view.findViewById(R.id.cbIsChronic);
        btnBmi = view.findViewById(R.id.btnIsAllergic);
        tvBmi = view.findViewById(R.id.tvBmi);
        etChronicInfo = view.findViewById(R.id.etChronicInfo);
        etAllergyInfo = view.findViewById(R.id.etAllergyInfo);
        btnRegister = view.findViewById(R.id.btnPatientRegister);

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
                    showToast("Hasta çekilirken hata Oluştu");
                }
            }
        });

    }

    //KAMERADAN FOTOĞRAF ALMA
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
                    ivProfilePhoto.setImageURI(imageUri);
                    try {
                        Bitmap bitmapGallery = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        profilePhoto = UtilsAndroid.bitmapToBase64(bitmapGallery);
                    } catch (Exception e) {
                        showToast("Dosya bulunamadı");
                    }

                    break;
            }
        }
    }

    //Edit Text boş mu
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

        //BOY KONTROLÜ
        if (isEmptyEditText(etHeight)) {
            etError(etHeight, "Boy boş bırakılamaz!");
            isValid = false;
        } else {
            String str_height = etHeight.getText().toString();
            double double_height = Double.parseDouble(str_height);
            if (double_height < 100 && double_height > 210) {
                etError(etHeight, "Geçerli boy aralığı: 100cm - 210 cm");
                isValid = false;
            }
        }

        //KİLO KONTROLÜ
        if (isEmptyEditText(etWeight)) {
            etError(etWeight, "Kilo boş bırakılamaz!");
            isValid = false;
        } else {
            String str_weight = etWeight.getText().toString();
            double double_weight = Double.parseDouble(str_weight);
            if (double_weight < 40 && double_weight > 210) {
                etError(etWeight, "Geçerli kilo aralığı: 40 kg- 210 kg");
                isValid = false;
            }
        }

        return isValid;
    }

    //BMI HESAPLA
    public int calculateBMI() {
        double BMI = 0.0;
        double height, weight;
        if (!isEmptyEditText(etHeight) && !isEmptyEditText(etWeight)) {
            height = Double.parseDouble(etHeight.getText().toString());
            weight = Double.parseDouble(etWeight.getText().toString());
            height = height / 100;
            BMI = weight / (height * height);
            tvBmi.setText("Boy kütle indeksiniz: " + String.valueOf((int) BMI));
            tvBmi.setVisibility(View.VISIBLE);

        } else {
            if (isEmptyEditText(etHeight) && isEmptyEditText(etWeight)) {
                tvBmi.setText("Boy ve kilo bilgisini boş bırakmayınız");
                tvBmi.setVisibility(View.VISIBLE);

            } else {
                if (isEmptyEditText(etHeight)) {
                    tvBmi.setText("Boy bilgisini boş bırakmayınız");
                    tvBmi.setVisibility(View.VISIBLE);
                } else {
                    tvBmi.setText("Kilo bilgisini boş bırakmayınız");
                    tvBmi.setVisibility(View.VISIBLE);
                }
            }
        }
        return (int) Math.round(BMI);
    }
}
