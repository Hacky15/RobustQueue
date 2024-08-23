import java.util.Queue;

import edu.uwm.cs351.util.RobustQueue;

public class TestRobustQueue extends TestCollection<String> {

	protected Queue<String> q;
	
	@Override
	protected void initCollections() {
		permitNulls = false;
		failFast = false;
		robust = true;
		c = q = new RobustQueue<String>();
		e = new String[] { "zero", "one", "two", "three", "four","five", 
				"six", "seven", "eight", "noine", "ten" };
	}

	
	/// Locked tests
	
	public void test() {
		// First some tests demonstrating the queue concept:
		q.offer("A");
		q.offer("B");
		q.offer("C");
		q.offer("D");
		assertEquals(Ts(123802754),q.peek());
		assertEquals(Ts(1816796532),q.poll());
		assertEquals(Ts(746722821),q.poll());
		assertEquals(Ts(1438260821),q.peek());
		q.clear();
		testCont(false);
	}
	
	private void testCont(boolean ignored) {
		// now testing starting with an empty queue again
		// give the string of the result, or name of exception
		assertEquals("NullPointerException", toString(() -> q.offer(null)));
		assertEquals(Ts(20428308),toString(() -> q.poll()));
		assertEquals(Ts(537350955), toString(() -> q.element()));
		assertEquals(Ts(2073941131),toString(() -> q.add("hello")));
		assertEquals(Ts(2093075176),toString( () -> q.remove()));
		assertEquals(Ts(1998051885),toString(() -> q.peek()));
		testRobust(true);
	}
	
	private void testRobust(boolean ignored) {
		// now testing robust iterators with an empty queue.
		// give the string of the result, or name of the exception
		it = q.iterator();
		q.add("hello");
		assertEquals(Ts(1763260561),toString(() -> it.next()));
		q.add("bye");
		assertEquals(Ts(1178238337),toString(() -> it.hasNext()));
		q.poll();
		assertEquals(Ts(80770157),toString(() -> it.next()));
		q.poll();
		assertEquals(Ts(1967447783),toString(() -> it.next()));
	}
	
	public void testQ0() {
		assertNull(q.peek());
	}
	
	public void testQ1() {
		assertNull(q.poll());
	}
	
	public void testQ2() {
		assertTrue(q.offer("bread"));
		assertEquals(1,q.size());
	}
	
	public void testQ3() {
		q.offer("apples");
		assertEquals("apples",q.peek());
		assertEquals(1,q.size());
	}
	
	public void testQ4() {
		q.offer("apples");
		assertEquals("apples",q.poll());
		assertEquals(0,q.size());
	}
	
	public void testQ5() {
		q.offer("carrots");
		assertTrue(q.offer("bread"));
		assertEquals(2,q.size());
		assertEquals("carrots",q.peek());
		assertEquals(2,q.size());
	}
	
	public void testQ6() {
		q.offer("dates");
		q.offer("eggs");
		assertEquals("dates",q.poll());
		assertEquals(1,q.size());
		assertEquals("eggs",q.peek());
	}
	
	public void testQ7() {
		q.offer("Q");
		q.offer("7");
		assertEquals("Q",q.poll());
		q.offer("test");
		assertEquals("7",q.poll());
		q.offer("four");
		assertEquals("test",q.peek());
		q.offer("ten");
		assertEquals("test",q.poll());
		assertEquals("four",q.peek());
		assertEquals(2,q.size());
	}
	
	public void testQ8() {
		q.offer("Q");
		q.offer("8");
		q.poll();
		q.poll();
		assertNull(q.peek());
		assertNull(q.poll());
		q.offer("next");
		assertEquals("next",q.peek());
	}
	
	public void testQ9() {
		it = q.iterator();
		q.offer("Q");
		q.offer("9");
		assertEquals("Q",it.next());
	}
	
	
	/// testRN: testing robust iterators and queue operations.
	
	public void testR0() {
		it = q.iterator();
		q.offer("R");
		assertTrue(it.hasNext());
		it.next();
		it.remove();
		q.offer("0");
		assertTrue(it.hasNext());
		assertEquals("0",it.next());
	}
	
	public void testR1() {
		it = q.iterator();
		q.offer("R");
		assertTrue(it.hasNext());
		it.next();
		q.poll();
		assertFalse(it.hasNext());
		q.offer("1");
		assertEquals("1",it.next());
	}
	
	public void testR2() {
		it = q.iterator();
		q.offer("R");
		assertTrue(it.hasNext());
		q.poll();
		assertFalse(it.hasNext());
		q.offer("2");
		assertTrue(it.hasNext());
	}
	
	public void testR3() {
		q.offer("R");
		it = q.iterator();
		it.next();
		q.offer("3");
		assertTrue(it.hasNext());
		q.poll();
		assertTrue(it.hasNext());
		q.poll();
		assertFalse(it.hasNext());
		q.offer("next");
		assertTrue(it.hasNext());
		assertEquals("next",it.next());
	}

	public void testR4() {
		q.offer("R");
		it = q.iterator();
		it.next();
		q.offer("4");
		assertTrue(it.hasNext());
		q.poll();
		assertEquals("4",it.next());
		it.remove();
		assertFalse(it.hasNext());
		q.offer("next");
		assertEquals("next",it.next());
	}

	public void testR5() {
		q.offer("R");
		it = q.iterator();
		it.next();
		q.offer("5");
		assertTrue(it.hasNext());
		q.poll();
		assertException(IllegalStateException.class, () -> it.remove());
	}
	
	public void testR6() {
		q.offer("R");
		it = q.iterator();
		it.next();
		q.offer("6");
		assertEquals("6",it.next());
		assertFalse(it.hasNext());
		q.poll();
		assertFalse(it.hasNext());
		q.add("next");
		assertTrue(it.hasNext());
		assertEquals("next",it.next());
	}
	
	public void testR7() {
		it = c.iterator();
		q.offer("R");
		it.next();
		q.offer("1");
		q.offer("2");
		q.offer("3");
		q.offer("4");
		q.offer("5");
		q.offer("6");
		assertEquals("R",q.poll());
		assertEquals("1",q.poll());
		assertEquals("2",q.poll());
		assertEquals("3",q.poll());
		assertEquals("4",q.poll());
		assertEquals("5",q.poll());
		assertTrue(it.hasNext());
		q.offer("7");
		assertEquals("6",it.next());
		assertTrue(it.hasNext());
	}
	
	public void testR8() {
		it = c.iterator();
		q.offer("R");
		q.offer("1");
		q.offer("2");
		q.offer("3");
		q.offer("4");
		q.offer("5");
		q.offer("6");
		q.offer("7");
		assertEquals("R",q.poll());
		assertEquals("1",q.poll());
		assertEquals("2",q.poll());
		assertEquals("3",q.poll());
		assertEquals("4",q.poll());
		assertEquals("5",q.poll());
		assertEquals("6",q.poll());
		assertTrue(it.hasNext());
		assertEquals("7",q.poll());
		assertFalse(it.hasNext());
		q.offer("8");
		assertTrue(it.hasNext());
	}
	
	public void testR9() {
		it = c.iterator();
		q.offer("R");
		q.offer("1");
		q.offer("2");
		q.offer("3");
		q.offer("4");
		q.offer("5");
		q.offer("6");
		q.offer("7");
		q.offer("8");
		assertEquals("R",q.poll());
		assertEquals("1",it.next());
		assertEquals("2",it.next());
		assertEquals("1",q.poll());
		assertEquals("3",it.next());
		assertEquals("4",it.next());
		assertEquals("2",q.poll());
		assertEquals("5",it.next());
		assertEquals("3",q.poll());
		assertEquals("4",q.poll());
		assertEquals("5",q.poll());
		assertEquals("6",q.poll());
		assertTrue(it.hasNext());
		assertEquals("7",it.next());
		assertTrue(it.hasNext());
		assertEquals("7",q.poll());
		assertEquals("8",q.poll());
		assertFalse(it.hasNext());
		q.offer("9");
		assertTrue(it.hasNext());
		assertEquals("9",it.next());
	}
}
