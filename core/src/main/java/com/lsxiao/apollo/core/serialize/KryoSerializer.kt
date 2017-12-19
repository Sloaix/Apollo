package com.lsxiao.apollo.core.serialize

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.KryoException
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-05-17 17:25
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

class KryoSerializer : Serializable {
    private val mKryo = Kryo()

    init {
        mKryo.references = false
    }

    override fun serialize(obj: Any): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val output = Output(outputStream)
        mKryo.writeObject(output, obj)
        output.flush()
        output.close()

        val b = outputStream.toByteArray()
        try {
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return b
    }

    override fun <T> deserialize(data: ByteArray, clazz: Class<T>): T {
        val byteArrayInputStream = ByteArrayInputStream(data)
        val input = Input(byteArrayInputStream)
        val t = mKryo.readObject(input, clazz)
        try {
            input.close()
        } catch (e: KryoException) {
            e.printStackTrace()
        }

        return t
    }
}
