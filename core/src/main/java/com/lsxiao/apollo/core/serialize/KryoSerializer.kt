package com.lsxiao.apollo.core.serialize

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-05-17 17:25
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

class KryoSerializer : Serializable {
    private val mKryo = com.esotericsoftware.kryo.Kryo()

    init {
        mKryo.references = false
    }

    override fun serialize(obj: Any): ByteArray {
        val outputStream = java.io.ByteArrayOutputStream()
        val output = com.esotericsoftware.kryo.io.Output(outputStream)
        mKryo.writeObject(output, obj)
        output.flush()
        output.close()

        val b = outputStream.toByteArray()
        try {
            outputStream.flush()
            outputStream.close()
        } catch (e: java.io.IOException) {
            e.printStackTrace()
        }

        return b
    }

    override fun <T> deserialize(data: ByteArray, clazz: Class<T>): T {
        val byteArrayInputStream = java.io.ByteArrayInputStream(data)
        val input = com.esotericsoftware.kryo.io.Input(byteArrayInputStream)
        val t = mKryo.readObject(input, clazz)
        try {
            input.close()
        } catch (e: com.esotericsoftware.kryo.KryoException) {
            e.printStackTrace()
        }

        return t
    }
}
