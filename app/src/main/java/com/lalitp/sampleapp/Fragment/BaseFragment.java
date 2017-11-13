package com.lalitp.sampleapp.Fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lalitp.sampleapp.Activity.MainActivity;
import com.lalitp.sampleapp.Utils.AppConstant;
import com.lalitp.sampleapp.BuildConfig;
import com.lalitp.sampleapp.Utils.PermissionDialogView;
import com.lalitp.sampleapp.R;
import com.lalitp.sampleapp.Utils.VersionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import permission.auron.com.marshmallowpermissionhelper.FragmentManagePermission;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

import static com.lalitp.sampleapp.SampleApp.getAppContext;

public abstract class BaseFragment extends FragmentManagePermission {
    Toolbar mToolbar;
    public static int REQUEST_CAMERA = 1;
    public static int SELECT_FILE = 2;

    public MainActivity getDrawerActivity(){
        return ((MainActivity) super.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolbar(view);
    }

    protected void setToolbar(View view) {
        if(!hasCustomToolbar()) return;
        mToolbar = ButterKnife.findById(view,getToolbarId());
        mToolbar.setTitle(getTitle());
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

    }

    protected @IdRes
    int getToolbarId(){
        return R.id.toolbar;
    }

    public boolean hasCustomToolbar(){
        return false;
    }

    protected @StringRes
    int getTitle(){
        return R.string.app_name;
    }

    protected abstract  @LayoutRes
    int getLayout();

    public Toolbar getToolbar(){
        return mToolbar;
    }

    @Override
    public void onResume() {
        super.onResume();
        String strName = this.getClass().getSimpleName();
        //KareWellnessApp.getInstance().trackScreenView(strName);
    }

    public boolean getLocationPermissionInfo() {
        final boolean[] flag = new boolean[1];

        askCompactPermissions(new String[]{PermissionUtils.Manifest_ACCESS_COARSE_LOCATION
                , PermissionUtils.Manifest_ACCESS_FINE_LOCATION}, new PermissionResult() {

            @Override
            public void permissionGranted() {
                flag[0] = true;
            }

            @Override
            public void permissionDenied() {
                flag[0] = false;
                PermissionDialogView.gotoSettingsDialog(getActivity(), PermissionDialogView.LOCATION_PERMISSION);
            }

            @Override
            public void permissionForeverDienid() {
                flag[0] = false;
                PermissionDialogView.gotoSettingsDialog(getActivity(), PermissionDialogView.LOCATION_PERMISSION);
            }
        });

        return flag[0];
    }

    public void getPermissionInfo() {

        askCompactPermissions(new String[]{
                PermissionUtils.Manifest_CAMERA,
                PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE

        }, new PermissionResult() {

            @Override
            public void permissionGranted() {
              selectImage_dialog();
            }

            @Override
            public void permissionDenied() {
                PermissionDialogView.gotoSettingsDialog(getActivity(), PermissionDialogView.PHONE_PERMISSION);
            }

            @Override
            public void permissionForeverDienid() {
                PermissionDialogView.gotoSettingsDialog(getActivity(), PermissionDialogView.PHONE_PERMISSION);
            }
        });
    }

    private void selectImage_dialog() {
        ListView list_view;

        String[] items = new String[]{"Camera", "Photo Gallery"};

        AlertDialog.Builder _builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
        _builder.setTitle("Choose an image source");
        LayoutInflater layoutinflater = LayoutInflater.from(getActivity());
        View promptView = layoutinflater.inflate(
                R.layout.custom_dialog_setcamera, null);
        _builder.setView(promptView);

        list_view = (ListView) promptView.findViewById(R.id.listview_camera);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
        list_view.setAdapter(adapter);


        _builder.setNegativeButton("dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        _builder.setCancelable(false);
        final AlertDialog dialog = _builder.create();
        dialog.show();


        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent;

                switch (position) {
                    case 0:
                        openCamera(dialog);
                        break;
                    case 1:
                        openGallery(dialog);
                        break;
                }

            }
        });
    }

    private void openCamera(Dialog dialog) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageTempPath(AppConstant.ProfileImagePathTag));
        startActivityForResult(intent, REQUEST_CAMERA);
        dialog.dismiss();

        intent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                getImageTempPath(AppConstant.ProfileImagePathTag));
        List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            getActivity().grantUriPermission(packageName, getImageTempPath(AppConstant.ProfileImagePathTag), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private void openGallery(Dialog dialog) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select Image"),
                SELECT_FILE);
        dialog.dismiss();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Uri getImageTempPath(String from) {
        Uri uri = null;
        try {
            File file = null;
                file = createImageFile();

            if (file != null) {
                if (VersionUtils.isAfter24()) {
                    uri = FileProvider.getUriForFile(getAppContext(),
                            BuildConfig.APPLICATION_ID + ".provider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
            }
            // Log.d("Image Path: ", uri.getPath());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return uri;
    }

    public File createImageFile() {
        String imgpath = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + getResources().getString(R.string.app_name)
                + File.separator
                + "Media"
                + File.separator + "Images" + File.separator + "Temp";
        File dir = new File(imgpath);
        if (!dir.exists())
            dir.mkdirs();

        String filename = "/default"+ String.valueOf(System.currentTimeMillis()) + ".jpg";

        File nomedia = new File(dir, ".nomedia");
        try {
            nomedia.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(dir, filename);

        return file;
    }


}
