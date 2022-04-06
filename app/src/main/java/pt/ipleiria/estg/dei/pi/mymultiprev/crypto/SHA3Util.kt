package pt.ipleiria.estg.dei.pi.mymultiprev.crypto

import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3
import org.bouncycastle.util.encoders.Hex
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import java.io.UnsupportedEncodingException


class SHA3Util {
    fun digest(string: String): String {
        val md = DigestSHA3(Constants.SHA3_SIZE)
        try {
            md.update(string.toByteArray(charset("UTF-8")))
        } catch (ex: UnsupportedEncodingException) {
            md.update(string.toByteArray())
        }
        val digest = md.digest()
        return encode(digest)
    }

    private fun encode(bytes: ByteArray) = Hex.toHexString(bytes)
}