package com.codepath.apps.restclienttemplate.models;

import android.text.style.URLSpan;
import android.view.View;

public class DefensiveURLSpan extends URLSpan {
    private String mUrl;

    public DefensiveURLSpan(String url) {
        super(url);
        mUrl = url;
    }

    @Override
    public void onClick(View widget) {
        // openInWebView(widget.getContext(), mUrl); // intercept click event and do something.
        // super.onClick(widget); // or it will do as it is.
    }
}