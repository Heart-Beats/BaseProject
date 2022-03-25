package com.youma.pay.alipay.util

import android.util.Base64
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec

object SignUtils {
    private const val ALGORITHM = "RSA"
    private const val SIGN_ALGORITHMS = "SHA1WithRSA"
    private const val SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA"
    private const val DEFAULT_CHARSET = "UTF-8"
    private fun getAlgorithms(rsa2: Boolean): String {
        return if (rsa2) SIGN_SHA256RSA_ALGORITHMS else SIGN_ALGORITHMS
    }

    fun sign(content: String, privateKey: String?, rsa2: Boolean): String {
        try {
            val priPKCS8 = PKCS8EncodedKeySpec(
                Base64.decode(privateKey, Base64.DEFAULT)
            )
            val keyf = KeyFactory.getInstance(ALGORITHM)
            val priKey = keyf.generatePrivate(priPKCS8)
            val signature = Signature
                .getInstance(getAlgorithms(rsa2))
            signature.initSign(priKey)
            signature.update(content.toByteArray(charset(DEFAULT_CHARSET)))
            val signed = signature.sign()
            return String(Base64.encode(signed, Base64.DEFAULT))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}