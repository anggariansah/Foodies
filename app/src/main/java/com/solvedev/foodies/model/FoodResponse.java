package com.solvedev.foodies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FoodResponse {

    @SerializedName("status")
    private int status;

    @SerializedName("error")
    private boolean error;
}
