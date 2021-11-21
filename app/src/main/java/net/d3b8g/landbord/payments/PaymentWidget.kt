package net.d3b8g.landbord.payments

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import net.d3b8g.landbord.MainActivity
import net.d3b8g.landbord.R
import net.d3b8g.landbord.components.Converter.getTodayUnix
import net.d3b8g.landbord.payments.PaymentsDetails
import java.util.*

/*
Copyright (c) 2021 github.com/d3b8g
All Rights Reserved
 
This product is protected by copyright and distributed under
licenses restricting copying, distribution and decompilation.

Use this code only for non commercial purpose.
*/

class PaymentWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle), BillingProcessor.IBillingHandler {

    private val daysLeft: Int by lazy { getDaysLeftToAdsView() }

    private val paymentLayout: Int =
        if (daysLeft > 0) R.layout.fragment_trial_widget else R.layout.fragment_adview_widget

    //TrialGone
    private lateinit var adView: AdView

    //TrialLayout
    private lateinit var sponsorTimer: TextView
    private lateinit var offerSponsor: Button


    init {
        inflate(context, paymentLayout, this)

        if (daysLeft > 0) {
            sponsorTimer = findViewById(R.id.sponsor_timer)
            offerSponsor = findViewById(R.id.offer_sponsor)
            trialAction()

        } else {
            adView = findViewById(R.id.adViewMain)
            initAds()
        }
    }

    private fun initAds() {
        MobileAds.initialize(context) {}
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun trialAction() {
        sponsorTimer.text = daysLeft.getDateLeftString()
        offerSponsor.setOnClickListener {
            PaymentsDetails.billingProcessor = BillingProcessor.newBillingProcessor(context,
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmjRCLhbt651MnjdRFjAV6Im8Qd6NvBC3hFC2/obmKzrbf/84PddiykhjshMwoChl+qPLOWEylmZnNBXEY6TQQJfqXWkbXK7CJl28F9si7yBUf47L56NLDjsYOaMBo/rpw4JSfr9cgQV18FcDH+khohqDv3eJkinOUHq25nazxiJEaj+zztIn9RJj2qTYQRotp1DQAmSMVcsm+GHGQxr1vBdNRvnrXMThSNrL3Ck6qIzIFlzWo5G0OWrfTf+RpEnC+R9dc2j37GR8oUcwpw8J5Tw/7SjYMx5O/YQkQ3fu8bUncA54vVXqRe+yKyIefwLZ0ydVqVKY+wKse0Ze3C19EQIDAQAB",this)
            PaymentsDetails.billingProcessor?.initialize()
            PaymentsDetails.billingProcessor?.purchase(context as MainActivity,"4638620513674201751")
        }
    }

    private fun getDaysLeftToAdsView(): Int {
        val adsLimit = PreferenceManager.getDefaultSharedPreferences(context).getLong("ads_limit", getTodayUnix())
        return ((adsLimit - getTodayUnix()) / (86400 * 1000)).toInt()
    }

    private fun Int.getDateLeftString(): String = when(Locale.getDefault().displayLanguage) {
        "русский" -> {
            when(true) {
                this == 1 -> "У тебя осталось $this день без рекламы"
                this in 2..4 -> "У тебя осталось $this дня без рекламы"
                this > 4 || this == 0 -> "У тебя осталось $this дней без рекламы"
                else -> "У тебя осталось $this дня без рекламы"
            }
        }
        else -> "You have left $this days without ads"
    }

    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {

    }

    override fun onPurchaseHistoryRestored() {

    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Snackbar.make(this, resources.getString(R.string.payments_error), Snackbar.LENGTH_LONG).show()
    }

    override fun onBillingInitialized() {

    }
}