package com.kings.base.juc;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

class MapTest {
    @Test
    void currentMap() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("key", "value");
        map.forEach(System.out::printf);
        Assertions.assertThat(map.get("key")).isNotEmpty();
    }

    @Test
    void map() {
        ConcurrentHashMap<Person, String> map = new ConcurrentHashMap<>();
        map.put(new Person("zs", 18), "1");
        map.put(new Person("ls", 19), "2");
        map.forEach((k, v) -> System.out.println(k));
        map.searchValues(-1, s -> Assertions.assertThat(s).isNotEmpty());
    }

    static class Person {
        private final String name;
        private final int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj instanceof Person) {
                Person that = (Person) obj;
                return Objects.equals(this.name, that.name) && (this.age ^ that.age) == 0;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
