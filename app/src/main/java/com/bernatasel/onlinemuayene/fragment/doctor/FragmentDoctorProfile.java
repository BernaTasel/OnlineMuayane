package com.bernatasel.onlinemuayene.fragment.doctor;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSDoctor;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSUser;
import com.bernatasel.onlinemuayene.utils.UtilsAndroid;
import com.bernatasel.onlinemuayene.utils.UtilsDate;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentDoctorProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDoctorProfile extends FragmentBase {

    public static FragmentDoctorProfile newInstance() {
        FragmentDoctorProfile fragment = new FragmentDoctorProfile();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    static final int GALLERY_REQUEST_CODE = 1;
    static final int CAMERA_REQUEST_CODE = 2;

    FSUser user = getApp().getLoggedInPerson();

    FSDoctor doctor;

    private Uri imageUri;
    private String[] cities;
    private String[] professions;
    private int checkedItem = 0;
    HashMap<String, Object> doctorInfo = new HashMap<String, Object>();

    private CircleImageView ivDrProfilePhoto;
    private TextView tvDrNameSurname, tvDrEmail, tvDrProfession, tvDrPhone, tvDrCity, tvDrGender, tvDrBirthdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);
        setHeaderTitle("PROFİLİM");
        initView(view);

        editProfile();

        return view;
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
        String profilePhoto;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    Bundle bundle = data.getExtras();
                    Bitmap bitmapCamera = (Bitmap) bundle.get("data");
                    ivDrProfilePhoto.setImageBitmap(bitmapCamera);
                    doctorInfo.put("profilePhoto", UtilsAndroid.bitmapToBase64(bitmapCamera));
                    break;
                case GALLERY_REQUEST_CODE:
                    imageUri = data.getData();
                    ivDrProfilePhoto.setImageURI(imageUri);
                    try {
                        Bitmap bitmapGallery = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        doctorInfo.put("profilePhoto", UtilsAndroid.bitmapToBase64(bitmapGallery));
                    }
                    catch (Exception e){
                        showToast("Dosya bulunamadı");
                    }
                    break;
            }
            FSOps.getInstance().updateUser(user.getEmail(), doctorInfo);
        }
    }

    private void editProfile() {
        ivDrProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] option = {"Kamera", "Galeri"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getMA(), android.R.layout.select_dialog_item, option);
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());
                builder.setTitle("");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if(UtilsPermissions.checkPermissionCameraElseAsk(getActivity())){
                                pickFromCamera();
                            }
                        } else {
                            pickFromGallery();
                        }
                    }
                });
                builder.create().show();
            }
        });

        tvDrPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());
                builder.setTitle("Telefon numaranız: ");
                builder.setIcon(R.drawable.phone);
                final EditText input = new EditText(getMA());
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                input.setTextSize(14);
                builder.setView(input);
                input.setHint("05055055050");
                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

                builder.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvDrPhone.setText(input.getText().toString());
                        doctorInfo.put("phone",input.getText().toString());
                        FSOps.getInstance().updateUser(user.getEmail(), doctorInfo);

                    }
                });
                builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });

        cities = getResources().getStringArray(R.array.cities_array);
        tvDrCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());
                builder.setTitle("İl Seçiniz: ");
                builder.setIcon(R.drawable.city);
                builder.setSingleChoiceItems(cities, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem = which;
                    }
                });

                builder.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvDrCity.setText(cities[checkedItem]);
                        doctorInfo.put("city", cities[checkedItem]);
                        FSOps.getInstance().updateUser(user.getEmail(), doctorInfo);
                    }
                });
                builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();

            }
        });

        professions = getResources().getStringArray(R.array.professions_array);
        tvDrProfession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());
                builder.setTitle("Uzmanlık Alanı Seçiniz: ");
                builder.setIcon(R.drawable.hospital);
                builder.setSingleChoiceItems(professions, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem = which;
                    }
                });

                builder.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvDrProfession.setText(professions[checkedItem]);
                        doctorInfo.put("profession", professions[checkedItem]);
                        FSOps.getInstance().updateUser(user.getEmail(), doctorInfo);
                    }
                });
                builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });

        String[] genders = {"Kadın", "Erkek"};
        tvDrGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());
                builder.setTitle("Cinsiyet Seçiniz: ");
                builder.setIcon(R.drawable.gender);
                builder.setSingleChoiceItems(genders, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem = which;
                    }
                });

                builder.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvDrGender.setText(genders[checkedItem]);
                        doctorInfo.put("gender", genders[checkedItem]);
                        FSOps.getInstance().updateUser(user.getEmail(), doctorInfo);
                    }
                });
                builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });

        tvDrBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker;
                datePicker = new DatePickerDialog(getMA(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String birthday = dayOfMonth + "/" + month + "/" + year;
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        try {
                            Date date = formatter.parse(birthday);
                            Timestamp birthdate = new Timestamp(date);
                            tvDrBirthdate.setText(UtilsDate.timestampToHumanReadable_DD_MM_YYYY(birthdate.toDate().getTime()));
                            doctorInfo.put("birthdate", birthdate);
                            FSOps.getInstance().updateUser(user.getEmail(), doctorInfo);
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }

                    }
                }, year, month, day);

                datePicker.setTitle("Tarih Seçiniz");
                datePicker.setButton(DialogInterface.BUTTON_POSITIVE, "Tamam", datePicker);
                datePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "İptal", datePicker);
                datePicker.show();
            }
        });

    }

    private void initView(View view){
        ivDrProfilePhoto = view.findViewById(R.id.ivDrProfilePhoto);
        tvDrNameSurname = view.findViewById(R.id.tvDrNameSurname);
        tvDrEmail = view.findViewById(R.id.tvDrEmail);
        tvDrProfession = view.findViewById(R.id.tvDrProfession);
        tvDrPhone = view.findViewById(R.id.tvDrPhone);
        tvDrCity = view.findViewById(R.id.tvDrCity);
        tvDrGender = view.findViewById(R.id.tvDrGender);
        tvDrBirthdate = view.findViewById(R.id.tvDrBirthdate);

        FSOps.getInstance().getUserByEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    getMA().showHideLoading(true);
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        doctor = qds.toObject(FSDoctor.class);
                        if(doctor.getProfilePhoto().equals("default")){
                            ivDrProfilePhoto.setImageDrawable(getResources().getDrawable(R.drawable.doctor_icon_white));
                        }
                        else {
                            ivDrProfilePhoto.setImageBitmap(UtilsAndroid.base64ToBitmap(doctor.getProfilePhoto()));
                        }
                        tvDrNameSurname.setText(doctor.getName() + " " +doctor.getSurname());
                        tvDrEmail.setText(doctor.getEmail());
                        tvDrPhone.setText(doctor.getPhone());
                        tvDrCity.setText(doctor.getCity());
                        tvDrGender.setText(doctor.getGender());
                        tvDrProfession.setText(doctor.getProfession());
                        tvDrBirthdate.setText(UtilsDate.timestampToHumanReadable_DD_MM_YYYY(doctor.getBirthdate().toDate().getTime()));
                    }
                }
                getMA().showHideLoading(false);
            }
        });
    }
}
