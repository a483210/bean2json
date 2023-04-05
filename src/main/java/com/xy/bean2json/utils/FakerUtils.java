package com.xy.bean2json.utils;

import net.datafaker.Faker;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.entertainment.EntertainmentProviders;
import net.datafaker.providers.food.FoodProviders;
import net.datafaker.providers.sport.SportProviders;
import net.datafaker.providers.videogame.VideoGameProviders;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.*;
import java.util.*;

/**
 * FakerUtils
 *
 * @author Created by gold on 2023/4/4 17:43
 * @since 1.0.0
 */
public final class FakerUtils {
    private FakerUtils() {
    }

    public static final Faker FAKER = new Faker(Locale.getDefault());

    //强制匹配生产者
    private static final Map<String, String> FORCE_MATCH_PROVIDER = Map.of(
            "PhoneNumber", "CellPhone"
    );

    /**
     * 创建假数据生成器
     *
     * @return 假数据生成器
     */
    public static Map<String, ProviderInfo> createProviderMap() {
        Map<String, ProviderInfo> providerMap = new HashMap<>();

        List<Pair<Method, Class<?>>> providerClasses = resolveProvider();
        List<Map<String, ProviderInfo>> methodInfos = new ArrayList<>();
        for (Pair<Method, Class<?>> pair : providerClasses) {
            Method method = pair.getKey();
            Class<?> clazz = pair.getValue();

            //获取生成器对象
            Object provider;
            try {
                provider = method.invoke(FAKER);
            } catch (IllegalAccessException | InvocationTargetException ignore) {
                continue;
            }

            List<String> classNames = splitName(clazz, clazz.getSimpleName());

            Method[] childMethods = clazz.getMethods();
            Method firstMethod = null;
            Map<String, ProviderInfo> childMethodMap = new HashMap<>();
            for (int i = 0; i < childMethods.length; i++) {
                Method childMethod = childMethods[i];
                if (!isValidMethod(childMethod)) {
                    continue;
                }

                if (i == 0) {
                    firstMethod = childMethod;
                }

                String methodKey = StringUtils.capitalize(childMethod.getName());

                childMethodMap.put(methodKey, new ProviderInfo(provider, childMethod));
            }

            Trie trie = Trie.builder()
                    .ignoreCase()
                    .addKeywords(childMethodMap.keySet())
                    .build();

            //先填充类的
            for (String name : classNames) {
                String key = StringUtils.capitalize(name);

                String matchName = matchWordsWithAC(trie, key);
                if (matchName != null) {
                    fillProvider(providerMap, key, childMethodMap.get(matchName));
                } else {
                    if (firstMethod != null) {
                        fillProvider(providerMap, key, provider, firstMethod);
                    }
                }
            }

            methodInfos.add(childMethodMap);
        }

        //然后填充方法的
        for (Map<String, ProviderInfo> methodInfo : methodInfos) {
            for (Map.Entry<String, ProviderInfo> entry : methodInfo.entrySet()) {
                String key = entry.getKey();
                ProviderInfo value = entry.getValue();

                fillProvider(providerMap, key, value);
            }
        }

        //强制替换方法
        for (Map.Entry<String, String> entry : FORCE_MATCH_PROVIDER.entrySet()) {
            String src = entry.getKey();
            String dest = entry.getValue();

            ProviderInfo provider = providerMap.get(dest);
            if (provider != null && providerMap.containsKey(src)) {
                providerMap.put(src, provider);
            }
        }

        return providerMap;
    }

    //填充生产者
    private static void fillProvider(Map<String, ProviderInfo> providerMap, String name, Object provider, Method method) {
        fillProvider(providerMap, name, new ProviderInfo(provider, method));
    }

    private static void fillProvider(Map<String, ProviderInfo> providerMap, String name, ProviderInfo provider) {
        if (providerMap.containsKey(name)) {
            return;
        }

        providerMap.put(name, provider);
    }

    private static List<String> splitName(Class<?> clazz, String name) {
        //如果是过滤的则直接返回
        if (!isNeedSplitType(clazz)) {
            return Collections.singletonList(name);
        }

        List<String> names = new ArrayList<>();

        String[] splits = StringUtils.splitByCharacterTypeCamelCase(name);
        StringBuilder lastName = new StringBuilder();
        for (int i = 0; i < splits.length; i++) {
            String split = splits[i];
            lastName.append(split);

            if (i == 0) {
                names.add(split);
            } else {
                names.add(lastName.toString());
            }
        }

        return names;
    }

    private static List<Pair<Method, Class<?>>> resolveProvider() {
        List<Pair<Method, Class<?>>> providerClasses = new ArrayList<>();

        Method[] methods = Faker.class.getMethods();
        for (Method method : methods) {
            if (!isValidMethod(method)) {
                continue;
            }

            Class<?> retClass = method.getReturnType();
            if (!AbstractProvider.class.isAssignableFrom(retClass)) {
                continue;
            }

            providerClasses.add(Pair.of(method, retClass));
        }

        return providerClasses;
    }

    private static boolean isNeedSplitType(Class<?> clazz) {
        Type type = resolveParentGenericType(clazz);
        if (!(type instanceof Class<?>)) {
            return false;
        }

        Class<?> genericClass = (Class<?>) type;

        return !EntertainmentProviders.class.isAssignableFrom(genericClass)
                && !FoodProviders.class.isAssignableFrom(genericClass)
                && !VideoGameProviders.class.isAssignableFrom(genericClass)
                && !SportProviders.class.isAssignableFrom(genericClass);
    }

    private static Type resolveParentGenericType(Class<?> clazz) {
        Type parentType = clazz.getGenericSuperclass();
        if (parentType instanceof ParameterizedType) {
            return ((ParameterizedType) parentType).getActualTypeArguments()[0];
        }

        return null;
    }

    private static boolean isValidMethod(Method method) {
        String methodName = method.getName();
        if (methodName.equals("toString")
                || methodName.equals("hashCode")
                || methodName.equals("equals")
                || methodName.equals("getFaker")) {
            return false;
        }

        int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers)
                || Modifier.isNative(modifiers)
                || Modifier.isAbstract(modifiers)
                || !Modifier.isPublic(modifiers)) {
            return false;
        }

        return method.getReturnType() != Object.class
                && method.getReturnType() != void.class
                && method.getParameterTypes().length == 0;
    }

    /**
     * 根据文本匹配关键字
     *
     * @param trie        字典树
     * @param inputString 文本
     * @return 匹配到的关键字
     */
    public static String matchWordsWithAC(Trie trie, String inputString) {
        Collection<Emit> emits = trie.parseText(inputString);
        if (emits.isEmpty()) {
            return null;
        }

        Optional<Emit> optional = emits.stream()
                .max(Comparator.comparingInt(it -> it.getKeyword().length()));
        return optional.map(Emit::getKeyword).orElse(null);
    }

    public static class ProviderInfo {

        /**
         * 生成器
         */
        private final Object provider;
        /**
         * 方法
         */
        private final Method method;
        /**
         * 返回类型
         */
        private final Class<?> retClass;

        public ProviderInfo(Object provider, Method method) {
            this.provider = provider;
            this.method = method;
            this.retClass = method.getReturnType();
        }

        /**
         * 生成数据
         *
         * @param clazz 需要生成的数据类型
         * @return 生成的数据，如果不匹配则返回空
         */
        public Object mockData(Class<?> clazz) {
            try {
                if (clazz == String.class) {
                    Object res = method.invoke(provider);
                    if (retClass == String.class) {
                        return res;
                    } else if (res != null) {
                        return res.toString();
                    }
                }

                if (retClass.isAssignableFrom(clazz)) {
                    return method.invoke(provider);
                }

            } catch (IllegalAccessException | InvocationTargetException ignore) {
            }

            return null;
        }
    }
}
