package de.comparus.opensource.longmap;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LongMapImplTest {

    private final LongMap<String> map = new LongMapImpl<>();
    private final String VALUE = "value";
    private final long KEY = 1L;

    @Before
    public void setUp() {
        map.clear();
    }

    @Test
    public void should_put_value() {
        // given && when
        String actual = map.put(KEY, VALUE);

        // then
        assertEquals(actual, VALUE);
    }

    @Test
    public void should_put_value_with_same_key_and_different_value() {
        // given
        map.put(KEY, VALUE);

        //when
        String actual = map.put(KEY, "value2");

        // then
        assertEquals(actual, "value2");
    }

    @Test
    public void should_put_value_with_same_hashcode_but_different_keys() {
        // given
        map.put(3L, VALUE);

        //when
        String actual = map.put(99L, "value99");

        // then
        assertEquals(actual, "value99");
        assertEquals(map.size(), 2);
    }

    @Test
    public void should_get_value() {
        // given
        map.put(KEY, VALUE);

        //when
        String actual = map.get(KEY);

        // then
        assertEquals(actual, VALUE);
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_during_get_not_existed_value() {
        // given && when
        map.put(KEY, VALUE);
        map.get(2L);
    }

    @Test
    public void should_remove_value() {
        // given
        map.put(KEY, VALUE);
        map.put(2L, "value2");

        // when
        String actual = map.remove(KEY);

        //then
        assertEquals(actual, VALUE);
        assertEquals(map.size(), 1);
    }

    @Test
    public void should_check_is_empty() {
        // given
        map.put(KEY, VALUE);

        // when
        boolean actual = map.isEmpty();

        //then
        assertFalse(actual);
    }

    @Test
    public void should_check_is_map_contains_key() {
        // given
        map.put(KEY, VALUE);

        //when
        boolean isEmptyFirst = map.containsKey(KEY);
        boolean isEmptySecond = map.containsKey(2L);

        // then
        assertTrue(isEmptyFirst);
        assertFalse(isEmptySecond);
    }

    @Test
    public void should_check_is_map_contains_value() {
        // given
        map.put(KEY, VALUE);

        //when
        boolean isEmptyFirst = map.containsValue(VALUE);
        boolean isEmptySecond = map.containsValue("anyValue");

        // then
        assertTrue(isEmptyFirst);
        assertFalse(isEmptySecond);
    }

    @Test
    public void should_get_all_keys() {
        // given
        map.put(KEY, VALUE);
        map.put(5L, "value5");
        map.put(17L, "value17");

        long[] expected = {1L, 17L, 5L};

        // when
        long[] actual = map.keys();

        // then
        assertArrayEquals(expected, actual);
    }

    @Test
    public void should_get_all_values() {
        // given
        map.put(KEY, VALUE);
        map.put(5L, "value5");
        map.put(17L, "value17");

        String[] expected = {"value", "value17", "value5"};

        // when
        Object[] actual = map.values();

        // then
        assertArrayEquals(expected, actual);
    }
}