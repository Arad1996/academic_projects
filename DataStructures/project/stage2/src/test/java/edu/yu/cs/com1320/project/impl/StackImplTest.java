package edu.yu.cs.com1320.project.impl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StackImplTest {
    @Test
    public void testStack() {
        StackImpl<Integer> stack = new StackImpl<>();
        stack.push(1);
        stack.push(2);
        stack.push(2);
        stack.push(3);
        stack.push(5);
        assertEquals(5, stack.size());
        assertEquals(5, stack.peek());
        Integer item = stack.pop();
        assertEquals(5, item);
        assertEquals(4, stack.size());
    }
}