package com.feedbacktower.payment.models

import com.google.gson.annotations.SerializedName

data class PayUResponseFailure(
    @SerializedName("errorCode")
    val errorCode: Any?,
    @SerializedName("message")
    val message: String,
    @SerializedName("responseCode")
    val responseCode: Any?,
    @SerializedName("result")
    val result: Result,
    @SerializedName("status")
    val status: Int
) {
    data class Result(
        @SerializedName("addedon")
        val addedon: String,
        @SerializedName("additionalCharges")
        val additionalCharges: String,
        @SerializedName("additional_param")
        val additionalParam: String,
        @SerializedName("address1")
        val address1: String,
        @SerializedName("address2")
        val address2: String,
        @SerializedName("amount")
        val amount: String,
        @SerializedName("amount_split")
        val amountSplit: String,
        @SerializedName("bank_ref_num")
        val bankRefNum: String,
        @SerializedName("bankcode")
        val bankcode: String,
        @SerializedName("baseUrl")
        val baseUrl: Any?,
        @SerializedName("calledStatus")
        val calledStatus: Boolean,
        @SerializedName("cardToken")
        val cardToken: String,
        @SerializedName("card_merchant_param")
        val cardMerchantParam: Any?,
        @SerializedName("card_type")
        val cardType: String,
        @SerializedName("cardhash")
        val cardhash: String,
        @SerializedName("cardnum")
        val cardnum: String,
        @SerializedName("city")
        val city: String,
        @SerializedName("country")
        val country: String,
        @SerializedName("createdOn")
        val createdOn: Long,
        @SerializedName("discount")
        val discount: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("encryptedPaymentId")
        val encryptedPaymentId: Any?,
        @SerializedName("error")
        val error: String,
        @SerializedName("error_Message")
        val errorMessage: String,
        @SerializedName("fetchAPI")
        val fetchAPI: Any?,
        @SerializedName("field1")
        val field1: String,
        @SerializedName("field2")
        val field2: String,
        @SerializedName("field3")
        val field3: String,
        @SerializedName("field4")
        val field4: String,
        @SerializedName("field5")
        val field5: String,
        @SerializedName("field6")
        val field6: String,
        @SerializedName("field7")
        val field7: String,
        @SerializedName("field8")
        val field8: String,
        @SerializedName("field9")
        val field9: String,
        @SerializedName("firstname")
        val firstname: String,
        @SerializedName("furl")
        val furl: Any?,
        @SerializedName("hash")
        val hash: String,
        @SerializedName("id")
        val id: Any?,
        @SerializedName("isConsentPayment")
        val isConsentPayment: Int,
        @SerializedName("key")
        val key: String,
        @SerializedName("lastname")
        val lastname: String,
        @SerializedName("meCode")
        val meCode: String,
        @SerializedName("merchantid")
        val merchantid: Any?,
        @SerializedName("mihpayid")
        val mihpayid: String,
        @SerializedName("mode")
        val mode: String,
        @SerializedName("name_on_card")
        val nameOnCard: String,
        @SerializedName("net_amount_debit")
        val netAmountDebit: String,
        @SerializedName("offer_availed")
        val offerAvailed: String,
        @SerializedName("offer_failure_reason")
        val offerFailureReason: String,
        @SerializedName("offer_key")
        val offerKey: String,
        @SerializedName("offer_type")
        val offerType: String,
        @SerializedName("paisa_mecode")
        val paisaMecode: String,
        @SerializedName("paymentId")
        val paymentId: Int,
        @SerializedName("payment_source")
        val paymentSource: Any?,
        @SerializedName("payuMoneyId")
        val payuMoneyId: String,
        @SerializedName("pg_TYPE")
        val pgTYPE: String,
        @SerializedName("pg_ref_no")
        val pgRefNo: String,
        @SerializedName("phone")
        val phone: String,
        @SerializedName("postBackParamId")
        val postBackParamId: Int,
        @SerializedName("postUrl")
        val postUrl: String,
        @SerializedName("productinfo")
        val productinfo: String,
        @SerializedName("retryCount")
        val retryCount: Int,
        @SerializedName("state")
        val state: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("surl")
        val surl: Any?,
        @SerializedName("txnid")
        val txnid: String,
        @SerializedName("udf1")
        val udf1: String,
        @SerializedName("udf10")
        val udf10: String,
        @SerializedName("udf2")
        val udf2: String,
        @SerializedName("udf3")
        val udf3: String,
        @SerializedName("udf4")
        val udf4: String,
        @SerializedName("udf5")
        val udf5: String,
        @SerializedName("udf6")
        val udf6: String,
        @SerializedName("udf7")
        val udf7: String,
        @SerializedName("udf8")
        val udf8: String,
        @SerializedName("udf9")
        val udf9: String,
        @SerializedName("unmappedstatus")
        val unmappedstatus: String,
        @SerializedName("version")
        val version: String,
        @SerializedName("zipcode")
        val zipcode: String
    )
}