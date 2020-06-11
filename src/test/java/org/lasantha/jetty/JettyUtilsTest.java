package org.lasantha.jetty;

import java.time.LocalDateTime;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.google.common.base.MoreObjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JettyUtilsTest {

    @Test
    void createObjectMapper() {
        final TestClass objActual = new TestClass();
        objActual.setName("Hello World!");
        objActual.setAge(25);
        objActual.setDate(LocalDateTime.of(2020, 6, 9, 13, 44, 28, 899999999));

        final TestClass objExpected = new TestClass();
        objExpected.setName("Hello World!");
        objExpected.setAge(25);
        objExpected.setDate(LocalDateTime.of(2020, 6, 9, 13, 44, 28)); // nano seconds are truncated

        assertEquals(objExpected, JettyUtils.fromJsonString(JettyUtils.toJsonString(objActual), TestClass.class));
    }

    @Test
    void extractSubTarget() {
        assertEquals("foo", JettyUtils.extractSubTarget("foo/"));
        assertEquals("foo", JettyUtils.extractSubTarget("foo/abc"));
        assertEquals("foo", JettyUtils.extractSubTarget("foo/?a=b"));

        assertEquals("Bar", JettyUtils.extractSubTarget("Bar#"));
        assertEquals("Bar", JettyUtils.extractSubTarget("Bar#abc"));
        assertEquals("Bar", JettyUtils.extractSubTarget("Bar#?a=b"));

        assertEquals("FOO", JettyUtils.extractSubTarget("FOO"));
        assertEquals("FOO", JettyUtils.extractSubTarget("FOO?abc"));
        assertEquals("FOO", JettyUtils.extractSubTarget("FOO?a=b"));

        assertEquals("amn Dkoa93 $16(R89763 2% LJKF S8\\921😀",
                     JettyUtils.extractSubTarget("amn Dkoa93 $16(R89763 2% LJKF S8\\921😀"));
    }

    @Test
    void shortenString() {
        assertEquals("Hello World!", JettyUtils.shortenString("Hello World!", 100));
        assertEquals("0123456789...", JettyUtils.shortenString("0123456789ABCDEFG", 10));
        assertEquals("", JettyUtils.shortenString("", 10));
        assertNull(JettyUtils.shortenString(null, 50));
    }

    private static class TestClass {

        private String name;

        private int age;

        private LocalDateTime date;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(final int age) {
            this.age = age;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(final LocalDateTime date) {
            this.date = date;
        }

        @Override public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (!(o instanceof TestClass))
                return false;
            final TestClass testClass = (TestClass) o;
            return age == testClass.age &&
                   Objects.equals(name, testClass.name) &&
                   Objects.equals(date, testClass.date);
        }

        @Override public int hashCode() {
            return Objects.hash(name, age, date);
        }

        @Override public String toString() {
            return MoreObjects.toStringHelper(this)
                              .add("name", name)
                              .add("age", age)
                              .add("date", date)
                              .toString();
        }
    }

}
