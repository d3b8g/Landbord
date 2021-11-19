package net.d3b8g.landbord.customComponentsUI

import androidx.lifecycle.MutableLiveData
import com.anjlab.android.iab.v3.BillingProcessor


/*
Copyright (c) 2021 github.com/d3b8g
All Rights Reserved
 
This product is protected by copyright and distributed under
licenses restricting copying, distribution and decompilation.

Use this code only for non commercial purpose.
*/

object PaymentsDetails {

    val isSponsor = MutableLiveData<Boolean>().apply { postValue(false) }

    var billingProcessor: BillingProcessor? = null

}