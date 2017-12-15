package com.mir.legacy.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class Utility {

    
    private final static String TAG = Utility.class.getSimpleName();

    /**
     * Constructor
     *
     * @param contextObj The Context from where the method is called
     * @return none
     */
    public Utility(Context contextObj) {
        context = contextObj;
    }

    /**
     * Show a non-cancelable dialog box with a message, a positive and a
     * negative button.
     *
     * @param context               The context to show this dialog box. Can't be null.
     * @param titleStringId         A valid resource id of the text to be shown in the title bar
     *                              of dialog box. If you don't want to show a title, just pass -1
     *                              here.
     * @param messageStringId       A valid resource id of the message to display.
     * @param positiveButtonLabelId A valid resource id of the text to show on positive button.If
     *                              you don't want to show a positiveButtonLabelId, just pass 0
     *                              here.
     * @param negativeButtonLabelId A valid resource id of the string to show on negative button
     *                              .If you don't want to show a negativeButtonLabelId, just pass 0
     *                              here.
     * @param actionCallback        Callback interface for the positive and negative buttons for
     *                              if you want to perform some action on button clicks. Can be
     *                              null.
     * @throws Resources.NotFoundException if any of the resource not found.
     */
    public static void showAlertDialog(Context context, int titleStringId, int messageStringId,
                                       int positiveButtonLabelId,
                                       int negativeButtonLabelId, final DialogActionCallback
                                               actionCallback) throws Resources.NotFoundException {


        if ((context == null) || (context.getString(messageStringId) == null || context.getString
                (messageStringId).trim().isEmpty())) {

            return;
        }


        String title = null;
        String message = context.getString(messageStringId);
        String positiveButtonLabel = null;
        String negativeButtonLabel = null;
        if (titleStringId > 0) {
            title = context.getString(titleStringId);
        }

        if (positiveButtonLabelId > 0) {
            positiveButtonLabel = context.getString(positiveButtonLabelId);
        }
        if (negativeButtonLabelId > 0) {
            negativeButtonLabel = context.getString(negativeButtonLabelId);
        }


        if ((context == null) || (message == null || message.trim().isEmpty())) {
            return;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        if (title != null && !title.trim().isEmpty()) {
            alertDialog.setTitle(title);
        }

        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(positiveButtonLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                if (actionCallback != null) {
                    actionCallback.doOnPositive();
                }
            }

        });

        alertDialog.setNegativeButton(negativeButtonLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (actionCallback != null) {
                    actionCallback.doOnNegative();
                }
            }
        });

        alertDialog.show();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {

        int currentAPIVersion = Build.VERSION.SDK_INT;

        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission
                    .READ_EXTERNAL_STORAGE) +
                    ContextCompat.checkSelfPermission(context, Manifest.permission
                            .CAMERA)

                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        (ActivityCompat.shouldShowRequestPermissionRationale
                                ((Activity) context, Manifest.permission.CAMERA))) {

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface
                            .OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new
                                            String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest
                                    .permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    // TODO Method Added
    public static String getDeviceId(Activity activity) {

//        TelephonyManager telephonyManager = (TelephonyManager)activity.getSystemService(Context
// .TELEPHONY_SERVICE);
//        return telephonyManager.getDeviceId();

        return Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static void saveDataInSharedPreferences(Context context, String sharedPrefName, int
            mode, String key, String value) {

        SharedPreferences prefsObj = context.getSharedPreferences(sharedPrefName, mode);
        SharedPreferences.Editor editorObj = prefsObj.edit();
        editorObj.putString(key, value);
        editorObj.commit();
    }

    public static void saveDataInSharedPreferences(Context context, String sharedPrefName, int
            mode, String key, boolean value) {

        SharedPreferences prefsObj = context.getSharedPreferences(sharedPrefName, mode);
        SharedPreferences.Editor editorObj = prefsObj.edit();
//        editorObj.putString(key, value);
        editorObj.putBoolean(key, value);
        editorObj.commit();
    }

    public static String readDataFromSharedPreferences(Context context, String sharedPrefName, int
            mode, String key) {

        SharedPreferences prefsObj = context.getSharedPreferences(sharedPrefName, mode);
        return prefsObj.getString(key, "");
    }

    public static boolean readDataFromSharedPreferences(Context context, String sharedPrefName, int
            mode, String key, boolean readBool) {

        SharedPreferences prefsObj = context.getSharedPreferences(sharedPrefName, mode);
//        return prefsObj.getString(key, "");
        return prefsObj.getBoolean(key, false);
    }

    public static void clearSharedPreferences(Context context, String sharedPrefName) {

        context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE).edit().clear().commit();
    }

    public static String getFirstAlphabetCapitalized(String string) {

        if (string == null || string.trim().isEmpty()) {
            return null;
        }

        if (string.length() == 1) {
            return string.toUpperCase();
        }

        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static void printHashKey(Context context) {

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Log.e(TAG, "+++++++");
                Log.d(TAG, Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Log.e(TAG, "+++++++");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }


    

    public static void closeOutputStream(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context
                    .getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    

    public static void hideKeyboard(Context mContext) {

        try {

            IBinder binder = ((Activity) mContext).getWindow().getCurrentFocus()
                    .getWindowToken();

            if (binder != null) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binder, 0);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public static void openPageInBrowser(Context context, String url) {

        if (context == null || url == null || url.trim().isEmpty()) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    

    public static void showMessage(Context context, String message) {

        final MaterialDialog materialDialog = new MaterialDialog(context);

        View view = LayoutInflater.from(context)
                .inflate(R.layout.custom_messagedialog,
                        null);

        TextView infoMessage = (TextView) view.findViewById(R.id.message);
        infoMessage.setMovementMethod(new ScrollingMovementMethod());

        infoMessage.setText(message);
        TextView submit = (TextView) view.findViewById(R.id.sumbit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();

            }
        });

        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.setView(view).show();
    }

    

    /**
     * The method will return current time
     *
     * @return current time
     */
    public String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss a");
        String currentTime = sdf.format(c.getTime());

        return currentTime;
    }

    /**
     * The method will return current date
     *
     * @return current date
     */
    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        String currentDate = Integer.toString(day) + "-" + Integer.toString(month) + "-" +
                Integer.toString(year);
        return currentDate;
    }

    

    /**
     * The method will save the data in shared preferences defined by
     * "sharedPrefName" and the key provided by "key" parameter
     *
     * @param sharedPrefName name of the container
     * @param mode           private
     * @param key            name of the key in which values are saved
     * @param value          data to be saved associated to the particular key
     * @return none
     * @since 2014-08-13
     */
    public void saveDataInSharedPreferences(String sharedPrefName, int mode, String key, String
            value) {
        SharedPreferences prefsObj = context.getSharedPreferences(sharedPrefName, mode);
        SharedPreferences.Editor editorObj = prefsObj.edit();
        editorObj.putString(key, value);
        editorObj.commit();
    }

    /**
     * The method will read the data in shared preferences defined by
     * "sharedPrefName" and the key provided by "key" parameter
     *
     * @param sharedPrefName name of the container
     * @param mode           private
     * @param key            name of the key in which values are saved
     * @return String
     */
    public String readDataInSharedPreferences(String sharedPrefName, int mode, String key) {
        SharedPreferences prefsObj = context.getSharedPreferences(sharedPrefName, mode);
        return prefsObj.getString(key, "");
    }

    /**
     * The method will remove the data stored in shared preferences defined by
     * "sharedPrefName" and the key provided by "key" parameter
     *
     * @param sharedPrefName name of the container
     * @param mode           private
     * @param key            name of the key in which values are saved
     * @param removeAll      if true will remove all the values stored in shared
     *                       preferences else remove as specified by key
     */
    public void removeKeyFromSharedPreferences(String sharedPrefName, int mode, String key,
                                               boolean removeAll) {

        SharedPreferences prefsObj = context.getSharedPreferences(sharedPrefName, mode);
        SharedPreferences.Editor editorObj = prefsObj.edit();
        if (removeAll) {
            editorObj.clear();
        } else {
            editorObj.remove(key);
        }
        editorObj.commit();
    }

    /**
     * show message to user using showToast
     *
     * @param mContext                   contains context of application
     * @param message                    contains text/message to show
     * @param durationForMessageToAppear 1 will show the message for short
     *                                   duration else long duration
     */
    public void showToast(Context mContext, String message, int durationForMessageToAppear) {
        if (durationForMessageToAppear == 1) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * This method will hide virtual keyboard if opened
     *
     * @param mContext contains context of application
     */
    public void hideVirtualKeyboard(Context mContext) {

        try {

            IBinder binder = ((Activity) mContext).getWindow().getCurrentFocus()
                    .getWindowToken();

            if (binder != null) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binder, 0);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    /**
     * This method will show virtual keyboard where ever required
     *
     * @param mContext contains context of application
     */
    public void showVirtualKeyboard(Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    

    public void showCustomDialog(String message) {

        final MaterialDialog materialDialog = new MaterialDialog(context);

        View view = LayoutInflater.from(context)
                .inflate(R.layout.custom_messagedialog,
                        null);

        TextView infoMessage = (TextView) view.findViewById(R.id.message);
        infoMessage.setMovementMethod(new ScrollingMovementMethod());

        infoMessage.setText(message);
        TextView submit = (TextView) view.findViewById(R.id.sumbit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();

            }
        });

        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.setView(view).show();
    }

    

    public static void showCustomToast(Activity activity, int textResId) {

        String text = activity.getString(textResId);
        showCustomToast(activity, text);
    }

    public static void showCustomToast(Activity activity, String text) {

        LayoutInflater inflater =   activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) activity.findViewById(R.id.custom_toast_container));

        TextView textView = (TextView) layout.findViewById(R.id.text);
        textView.setText(text);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0 );
        toast.setDuration(Toast.LENGTH_SHORT );
        toast.setView(layout);
        toast.show();
    }
}
