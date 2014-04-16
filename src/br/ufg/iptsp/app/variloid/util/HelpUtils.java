/*
O * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.ufg.iptsp.app.variloid.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import br.ufg.iptsp.app.variloid.R;

/**
 * This is a set of helper methods for showing contextual help information in the app.
 */
public class HelpUtils {
    public static void showAbout(FragmentActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog_about");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        new AboutDialog().show(ft, "dialog_about");
    }

    public static class AboutDialog extends DialogFragment {

        private static final String VERSION_UNAVAILABLE = "N/A";

        public AboutDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get app version
            PackageManager pm = getActivity().getPackageManager();
            String packageName = getActivity().getPackageName();
            String versionName;
            try {
                PackageInfo info = pm.getPackageInfo(packageName, 0);
                versionName = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                versionName = VERSION_UNAVAILABLE;
            }

            // Build the about body view and append the link to see OSS licenses
            SpannableStringBuilder aboutBody = new SpannableStringBuilder();
            aboutBody.append(Html.fromHtml(getString(R.string.string_corpo_sobre, versionName)));

//            SpannableString licensesLink = new SpannableString(getString(R.string.sobre_empresa));
//            licensesLink.setSpan(new ClickableSpan() {
//                @Override
//                public void onClick(View view) {
//                    HelpUtils.showOpenSourceLicenses(getActivity());
//                }
//            }, 0, licensesLink.length(), 0);
//            aboutBody.append("\n\n");
//            aboutBody.append(licensesLink);

            SpannableString eulaLink = new SpannableString(getString(R.string.string_sobre_aplicativo));
            eulaLink.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    HelpUtils.showEula(getActivity());
                }
            }, 0, eulaLink.length(), 0);
            aboutBody.append("\n");
            aboutBody.append(eulaLink);
            aboutBody.append("\n");
            aboutBody.append(Html.fromHtml(getString(R.string.string_sobre_empresa)));
            
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            TextView aboutBodyView = (TextView) layoutInflater.inflate(R.layout.dialog_about, null);
            aboutBodyView.setText(aboutBody);
            aboutBodyView.setMovementMethod(new LinkMovementMethod());

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.string_title_about)
                    .setView(aboutBodyView)
                    .setPositiveButton(R.string.btn_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .create();
        }
    }

//    public static void showOpenSourceLicenses(FragmentActivity activity) {
//        FragmentManager fm = activity.getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        Fragment prev = fm.findFragmentByTag("dialog_licenses");
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);
//
//        new OpenSourceLicensesDialog().show(ft, "dialog_licenses");
//    }

//    public static class OpenSourceLicensesDialog extends DialogFragment {
//
//        public OpenSourceLicensesDialog() {
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            WebView webView = new WebView(getActivity());
//            webView.loadUrl("file:///android_asset/licenses.html");
//
//            return new AlertDialog.Builder(getActivity())
//                    .setTitle(R.string.sobre_empresa)
//                    .setView(webView)
//                    .setPositiveButton(R.string.ok,
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    dialog.dismiss();
//                                }
//                            }
//                    )
//                    .create();
//        }
//    }

    public static void showEula(FragmentActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog_eula");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        new EulaDialog().show(ft, "dialog_eula");
    }

    public static class EulaDialog extends DialogFragment {

        public EulaDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int padding = getResources().getDimensionPixelSize(R.dimen.content_padding_normal);

            TextView eulaTextView = new TextView(getActivity());
            eulaTextView.setText(Html.fromHtml(getString(R.string.sobre_text)));
            eulaTextView.setMovementMethod(LinkMovementMethod.getInstance());
            eulaTextView.setPadding(padding, padding, padding, padding);

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.string_sobre_aplicativo)
                    .setView(eulaTextView)
                    .setPositiveButton(R.string.btn_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .create();
        }
    }
    
}