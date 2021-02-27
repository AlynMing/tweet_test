package com.codepath.apps.restclienttemplate.models;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        return (List<String>) Arrays.asList(value.split(","));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static String fromArrayList(List<String> list) {
        return String.join(",", list);
    }
}