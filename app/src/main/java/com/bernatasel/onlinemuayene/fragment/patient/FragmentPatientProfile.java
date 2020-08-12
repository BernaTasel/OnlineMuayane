package com.bernatasel.onlinemuayene.fragment.patient;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bernatasel.onlinemuayene.Constants;
import com.bernatasel.onlinemuayene.FSOps;
import com.bernatasel.onlinemuayene.R;
import com.bernatasel.onlinemuayene.fragment.FragmentBase;
import com.bernatasel.onlinemuayene.pojo.firestore.user.FSPatient;
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

public class FragmentPatientProfile extends FragmentBase {
    public static FragmentPatientProfile newInstance() {
        Bundle args = new Bundle();
        FragmentPatientProfile fragment = new FragmentPatientProfile();
        fragment.setArguments(args);
        return fragment;
    }

    FSUser user = getApp().getLoggedInPerson();

    static final int GALLERY_REQUEST_CODE = 1;
    static final int CAMERA_REQUEST_CODE = 2;

    private Uri imageUri;
    private CircleImageView ivProfilePhoto;
    private TextView tvNameSurname, tvEmail, tvPhone, tvCity, tvGender, tvBirthdate, tvBmi, tvIsSmoke, tvIsAlcohol, tvIsAllergic, tvIsChoronic;

    private FSPatient patient;

    private String[] cities;
    private int checkedItem = 0;
    private String[] booleanArr = {"Evet", "Hayır"};
    HashMap<String, Object> patientInfo = new HashMap<String, Object>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_patient_profile, container, false);
        setHeaderTitle("PROFİLİM");
        initView(view);

        editProfile();

        return view;
    }

    private void editProfile() {
        ivProfilePhoto.setOnClickListener(new View.OnClickListener() {
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
                            if (UtilsPermissions.checkPermissionCameraElseAsk(getActivity())) {
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

        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());
                builder.setTitle("Telefon numaranız: ");
                builder.setIcon(R.drawable.phone);
                final EditText input = new EditText(getMA());
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                input.setTextSize(14);
                builder.setView(input);
                input.setHint("Örn: 05055055050");
                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                builder.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        patient.setPhone(input.getText().toString());
                        tvPhone.setText(patient.getPhone());
                        patientInfo.put("phone", patient.getPhone());
                        FSOps.getInstance().updateUser(user.getEmail(), patientInfo);

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
        tvCity.setOnClickListener(new View.OnClickListener() {
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
                        patient.setCity(cities[checkedItem]);
                        tvCity.setText(patient.getCity());
                        patientInfo.put("city", patient.getCity());
                        FSOps.getInstance().updateUser(user.getEmail(), patientInfo);
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

        String[] genders = {Constants.GENDER.FEMALE.getDescription(), Constants.GENDER.MALE.getDescription()};
        tvGender.setOnClickListener(new View.OnClickListener() {
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
                        tvGender.setText(genders[checkedItem]);
                        patientInfo.put("gender", genders[checkedItem]);
                        FSOps.getInstance().updateUser(user.getEmail(), patientInfo);
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

        tvBirthdate.setOnClickListener(new View.OnClickListener() {
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
                            patient.setBirthdate(new Timestamp(date));
                            tvBirthdate.setText(UtilsDate.timestampToHumanReadable_DD_MM_YYYY(patient.getBirthdate().toDate().getTime()));
                            tvBmi.setText(calculateBmiRange(patient.getBmi(), patient.getAge()));
                            patientInfo.put("birthdate", patient.getBirthdate());
                            FSOps.getInstance().updateUser(user.getEmail(), patientInfo);
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

        tvBmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder madb = new MaterialAlertDialogBuilder(getMA());
                madb.setCancelable(true);
                madb.setTitle("Boy/Kilo İndeksi");
                madb.setIcon(R.drawable.bmi);
                View viewInfo = LayoutInflater.from(getMA()).inflate(R.layout.dialog_bmi, null);
                EditText etWeight = viewInfo.findViewById(R.id.etWeight);
                etWeight.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                EditText etHeight = viewInfo.findViewById(R.id.etHeight);
                etHeight.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

                madb.setView(viewInfo);

                madb.setPositiveButton(R.string.guncelle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!etHeight.getText().toString().equals(null) && !etWeight.getText().toString().equals(null)) {
                            patient.setBmi(calculateBMI(Double.parseDouble(etHeight.getText().toString()), Double.parseDouble(etWeight.getText().toString())));
                            tvBmi.setText(calculateBmiRange(patient.getBmi(), patient.getAge()));
                            patientInfo.put("bmi", patient.getBmi());
                            FSOps.getInstance().updateUser(user.getEmail(), patientInfo);
                        } else {
                            etError(etHeight, "Lütfen boş bırakmayınız!");
                            etError(etWeight, "Lütfen boş bırakmayınız!");
                        }
                    }
                });
                madb.setNegativeButton(R.string.iptal, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etClearError(etHeight, etWeight);
                        dialog.dismiss();
                    }
                });

                madb.show();
            }
        });

        tvIsAlcohol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());
                builder.setTitle("Alkol kullanıyor musunuz? ");
                builder.setIcon(R.drawable.drink);
                builder.setSingleChoiceItems(booleanArr, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem = which;
                    }
                });

                builder.setPositiveButton(R.string.guncelle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkedItem == 0) {
                            patientInfo.put("alcohol", true);
                            tvIsAlcohol.setText("Alkol kullanıyorum.");
                        } else {
                            patientInfo.put("alcohol", false);
                            tvIsAlcohol.setText("Alkol kullanmıyorum.");
                        }
                        FSOps.getInstance().updateUser(user.getEmail(), patientInfo);
                    }
                });
                builder.setNegativeButton(R.string.iptal, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });

        tvIsSmoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());
                builder.setTitle("Sigara kullanıyor musunuz? ");
                builder.setIcon(R.drawable.smoke);
                builder.setSingleChoiceItems(booleanArr, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem = which;
                    }
                });

                builder.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkedItem == 0) {
                            patientInfo.put("smoke", true);
                            tvIsSmoke.setText("Sigara kullanıyorum.");
                        } else {
                            patientInfo.put("smoke", false);
                            tvIsSmoke.setText("Sigara kullanmıyorum.");
                        }
                        FSOps.getInstance().updateUser(user.getEmail(), patientInfo);
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

        tvIsAllergic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());
                builder.setTitle("Alerjiniz var mı?");
                builder.setIcon(R.drawable.hospital);
                EditText input = new EditText(getMA());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                input.setHint("Örn: Polen Alerjisi");
                input.setVisibility(View.INVISIBLE);
                boolean isAllergic = patient.isAllergy();
                if (isAllergic) {
                    checkedItem = 0;
                    input.setText(patient.getAllergyInfo());
                    input.setVisibility(View.VISIBLE);
                } else {
                    checkedItem = 1;
                }
                builder.setSingleChoiceItems(booleanArr, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem = which;
                        if (checkedItem == 0) {
                            input.setVisibility(View.VISIBLE);
                        } else {
                            input.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                builder.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkedItem == 0) {
                            patientInfo.put("allergy", true);
                            patientInfo.put("allergyInfo", input.getText().toString());
                            tvIsAllergic.setText("Alerjim var. " + input.getText().toString());
                        } else {
                            patientInfo.put("allergy", false);
                            tvIsAllergic.setText("Alerjim yok.");
                            patientInfo.put("allergyInfo", "");
                        }
                        FSOps.getInstance().updateUser(user.getEmail(), patientInfo);
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

        tvIsChoronic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getMA());
                builder.setTitle("Kronik bir hastalığınız var mı?");
                builder.setIcon(R.drawable.hospital);
                EditText input = new EditText(getMA());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                input.setHint("Örn: Astım, Hipertansiyon");
                input.setVisibility(View.INVISIBLE);
                boolean isChoronic = patient.isChoronic();
                if (isChoronic) {
                    checkedItem = 0;
                    input.setText(patient.getChoronicInfo());
                    input.setVisibility(View.VISIBLE);
                } else {
                    checkedItem = 1;
                }
                builder.setSingleChoiceItems(booleanArr, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem = which;
                        if (checkedItem == 0) {
                            input.setVisibility(View.VISIBLE);
                        } else {
                            input.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                builder.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkedItem == 0) {
                            patientInfo.put("choronic", true);
                            patientInfo.put("choronicInfo", input.getText().toString());
                            tvIsChoronic.setText("Kronik hastalığım var. " + input.getText().toString());
                        } else {
                            patientInfo.put("choronic", false);
                            tvIsChoronic.setText("Kronik hastalığım yok.");
                            patientInfo.put("choronicInfo", "");
                        }
                        FSOps.getInstance().updateUser(user.getEmail(), patientInfo);
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
    }

    private void initView(View view) {
        ivProfilePhoto = view.findViewById(R.id.ivProfilePhoto);
        tvNameSurname = view.findViewById(R.id.tvNameSurname);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvCity = view.findViewById(R.id.tvCity);
        tvGender = view.findViewById(R.id.tvGender);
        tvBirthdate = view.findViewById(R.id.tvBirthdate);
        tvBmi = view.findViewById(R.id.tvBmi);
        tvIsSmoke = view.findViewById(R.id.tvIsSmoke);
        tvIsAlcohol = view.findViewById(R.id.tvIsAlcohol);
        tvIsAllergic = view.findViewById(R.id.tvIsAllergic);
        tvIsChoronic = view.findViewById(R.id.tvIsChoronic);
        FSOps.getInstance().getUserByEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                getMA().showHideLoading(true);
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        patient = qds.toObject(FSPatient.class);
                        if (patient.getProfilePhoto().equals("default")) {
                            ivProfilePhoto.setImageDrawable(getResources().getDrawable(R.drawable.profile));
                        } else {
                            ivProfilePhoto.setImageBitmap(UtilsAndroid.base64ToBitmap(patient.getProfilePhoto()));
                        }

                        tvNameSurname.setText(patient.getName() + " " + patient.getSurname());
                        tvEmail.setText(patient.getEmail());
                        tvPhone.setText(patient.getPhone());
                        tvCity.setText(patient.getCity());
                        tvGender.setText(patient.getGender());
                        tvBmi.setText(calculateBmiRange(patient.getBmi(), patient.getAge()));

                        if (patient.getBirthdate() != null)
                            tvBirthdate.setText(UtilsDate.timestampToHumanReadable_DD_MM_YYYY(patient.getBirthdate().toDate().getTime()));

                        if (patient.isSmoke()) {
                            tvIsSmoke.setText("Sigara kullanıyorum.");
                        } else {
                            tvIsSmoke.setText("Sigara kullanmıyorum.");
                        }

                        if (patient.isAlcohol()) {
                            tvIsAlcohol.setText("Alkol kullanıyorum.");
                        } else {
                            tvIsAlcohol.setText("Alkol kullanmıyorum.");
                        }

                        if (patient.isAllergy()) {
                            tvIsAllergic.setText("Alerjim var. " + patient.getAllergyInfo());
                        } else {
                            tvIsAllergic.setText("Alerjim yok.");
                        }

                        if (patient.isChoronic()) {
                            tvIsChoronic.setText("Kronik hastalığım var. " + patient.getChoronicInfo());
                        } else {
                            tvIsChoronic.setText("Kronik hastalığım yok.");
                        }
                    }
                }
                getMA().showHideLoading(false);
            }
        });
    }

    private String calculateBmiRange(int bmi, int age) {
        String strBmiRange = "";
        if (age >= 18 && age <= 24) {
            if (bmi < 19) {
                strBmiRange = String.valueOf(bmi) + " (Normalden zayıf)";
            } else if (bmi >= 19 && bmi <= 24) {
                strBmiRange = String.valueOf(bmi) + " (Normal)";
            } else {
                strBmiRange = String.valueOf(bmi) + " (Normalden şişman)";
            }
        } else if (age >= 25 && age <= 34) {
            if (bmi < 20) {
                strBmiRange = String.valueOf(bmi) + " (Normalden zayıf)";
            } else if (bmi >= 20 && bmi <= 25) {
                strBmiRange = String.valueOf(bmi) + " (Normal)";
            } else {
                strBmiRange = String.valueOf(bmi) + " (Normalden şişman)";
            }
        } else if (age >= 35 && age <= 44) {
            if (bmi < 21) {
                strBmiRange = String.valueOf(bmi) + " (Normalden zayıf)";
            } else if (bmi >= 21 && bmi <= 26) {
                strBmiRange = String.valueOf(bmi) + " (Normal)";
            } else {
                strBmiRange = String.valueOf(bmi) + " (Normalden şişman)";
            }
        } else if (age >= 45 && age <= 54) {
            if (bmi < 22) {
                strBmiRange = String.valueOf(bmi) + " (Normalden zayıf)";
            } else if (bmi >= 22 && bmi <= 27) {
                strBmiRange = String.valueOf(bmi) + " (Normal)";
            } else {
                strBmiRange = String.valueOf(bmi) + " (Normalden şişman)";
            }
        } else if (age >= 55 && age <= 64) {
            if (bmi < 23) {
                strBmiRange = String.valueOf(bmi) + " (Normalden zayıf)";
            } else if (bmi >= 23 && bmi <= 28) {
                strBmiRange = String.valueOf(bmi) + " (Normal)";
            } else {
                strBmiRange = String.valueOf(bmi) + " (Normalden şişman)";
            }
        } else {
            if (bmi < 24) {
                strBmiRange = String.valueOf(bmi) + " (Normalden zayıf)";
            } else if (bmi >= 24 && bmi <= 29) {
                strBmiRange = String.valueOf(bmi) + " (Normal)";
            } else {
                strBmiRange = String.valueOf(bmi) + " (Normalden şişman)";
            }
        }
        return strBmiRange;
    }

    public int calculateBMI(double height, double weight) {
        if (height == 0) {
            return 0;
        }
        double BMI = 0.0;
        height = height / 100;
        BMI = weight / (height * height);
        return (int) Math.round(BMI);
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
                    ivProfilePhoto.setImageBitmap(bitmapCamera);
                    patientInfo.put("profilePhoto", UtilsAndroid.bitmapToBase64(bitmapCamera));
                    break;
                case GALLERY_REQUEST_CODE:
                    imageUri = data.getData();
                    ivProfilePhoto.setImageURI(imageUri);
                    try {
                        Bitmap bitmapGallery = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        patientInfo.put("profilePhoto", UtilsAndroid.bitmapToBase64(bitmapGallery));
                    } catch (Exception e) {
                        showToast("Dosya bulunamadı");
                    }
                    break;
            }
            FSOps.getInstance().updateUser(user.getEmail(), patientInfo);
        }
    }
}
