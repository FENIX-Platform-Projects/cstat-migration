package org.fao.ess.cstat.migration.utils.json;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

public class JSONParser {


    private static JsonFactory jsonFactory = new JsonFactory();
    private static ObjectMapper mapper = new ObjectMapper();

    public static String toJSON(Object obj) throws Exception { return mapper.writeValueAsString(obj); }

    public static void toJSON(Iterator<?> objs, Writer writer) throws Exception {
        JsonGenerator generator = jsonFactory.createJsonGenerator(writer);
        while (objs.hasNext())
            mapper.writeValue(generator, objs.next());
        generator.close();
    }
    public static JsonGenerator createGenerator(Writer writer) throws Exception {
        return jsonFactory.createJsonGenerator(writer);
    }
    public static void toJSON(Iterator<?> objs, JsonGenerator generator) throws Exception {
        while (objs.hasNext())
            mapper.writeValue(generator, objs.next());
    }
    public static void toJSON(Object obj, JsonGenerator generator) throws Exception {
        mapper.writeValue(generator, obj);
    }

    public static <T> T toObject(File file, Class<T> objClass) throws Exception { return mapper.readValue(file, objClass); }


    public static <T> T toObject(String json, Class<T> objClass) throws Exception { return mapper.readValue(json, objClass); }
    public static <T> T updateObject(String json, T obj) throws Exception { return mapper.readerForUpdating(obj).readValue(json); }

    public static JsonParser createParser(Reader reader) throws Exception { return jsonFactory.createJsonParser(reader); }
    public static JsonParser createParser(String content) throws Exception { return jsonFactory.createJsonParser(content); }
    public static <T> Iterator<T> toObject(JsonParser parser, Class<T> objClass) throws Exception { return mapper.readValues(parser, objClass); }
    public static <T> Iterator<T> toObject(Reader reader, Class<T> objClass) throws Exception { return mapper.readValues(jsonFactory.createJsonParser(reader), objClass); }

    public static <T> T convertValue(Object obj, Class<T> objClass) throws Exception { return mapper.convertValue(obj, objClass); }

    public static Map<String,Object> toMap(String json) throws Exception { return mapper.readValue(json, new TypeReference<Map<String, Object>>() {
    }); }

    public static Object cloneByJSON (Object obj) throws Exception { return obj!=null ? toObject(toJSON(obj),obj.getClass()) : null; }



    public static <T> T decode(String source, Class<T> beanClass, Type... types) throws Exception {
        TypeFactory factory = mapper.getTypeFactory();

        JavaType jacksonType = null;
        if (types!=null && types.length>0) {
            JavaType[] jacksonTypes = new JavaType[types.length];
            for (int i=0; i<types.length; i++)
                jacksonTypes[i] = factory.constructType(types[i]);
            jacksonType = factory.constructParametricType(beanClass, jacksonTypes);
        } else
            jacksonType = factory.constructType(beanClass);

        //return source!=null ? (T)mapper.reader(jacksonType).without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(source) : null;
        return source!=null ? (T)mapper.reader(jacksonType).readValue(source) : null;
    }
}
