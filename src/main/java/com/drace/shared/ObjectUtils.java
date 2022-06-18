package com.drace.shared;

import com.google.common.collect.Lists;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;

public class ObjectUtils {

    public static String serializeObject(Object object) {

        if (object == null) return "null";

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(object);
            dataOutput.close();

            return Base64Coder.encodeLines(outputStream.toByteArray());

        }

        catch (Exception exception) { exception.printStackTrace(); return "null"; }

    }

    public static Object deserializeObject(String string) {

        if (string == null ||
                string.isEmpty())
            return null;

        try {

            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(string));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Object object = dataInput.readObject(); dataInput.close();

            return object;

        }

        catch (Exception exception) { exception.printStackTrace(); return null; }

    }

}
