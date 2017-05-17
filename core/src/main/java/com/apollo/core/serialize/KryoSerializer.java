package com.apollo.core.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * write with Apollo
 * author:lsxiao
 * date:2017-05-17 17:25
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

public class KryoSerializer implements Serializable {
    private Kryo mKryo = new Kryo();

    public KryoSerializer() {
        mKryo.setReferences(false);
    }

    @Override
    public byte[] serialize(Object obj) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        mKryo.writeObject(output, obj);
        output.flush();
        output.close();

        byte[] b = outputStream.toByteArray();
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return b;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);
        T t = mKryo.readObject(input, clazz);
        try {
            input.close();
        } catch (KryoException e) {
            e.printStackTrace();
        }
        return t;
    }
}
