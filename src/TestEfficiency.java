import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import edu.uwm.cs.junit.EfficiencyTestCase;
import edu.uwm.cs351.util.RobustQueue;


public class TestEfficiency extends EfficiencyTestCase {
	Queue<Integer> s;
	Random r;
	
	@Override
	public void setUp() {
		s = new RobustQueue<>();
		r = new Random();
		try {
			assert 1/(int)(s.size()) == 42 : "OK";
			assertTrue(true);
		} catch (ArithmeticException ex) {
			System.err.println("Assertions must NOT be enabled to use this test suite.");
			System.err.println("In Eclipse: remove -ea from the VM Arguments box under Run>Run Configurations>Arguments");
			assertFalse("Assertions must NOT be enabled while running efficiency tests.",true);
		}
		super.setUp();
	}

	private static final int MAX_LENGTH = 2_000_000;
	private static final int SAMPLE = 100;
	
	public void test0() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(i);
		}
		
		int sum = 0;
		Iterator<Integer> it = s.iterator();
		Integer current = it.next();
		for (int j=0; j < SAMPLE; ++j) {
			int n = r.nextInt(MAX_LENGTH/SAMPLE);
			for (int i=0; i < n; ++i) {
				current = it.next();
			}
			sum += n;
			assertEquals(sum,current.intValue());
		}
	}
	
	private static final int MAX_WIDTH = 10_000;
	
	public void test1() {
		@SuppressWarnings("unchecked")
		Queue<Integer>[] a = new Queue[MAX_WIDTH];
		for (int i=0; i < MAX_WIDTH; ++i) {
			a[i] = s = new RobustQueue<Integer>();
			int n = r.nextInt(SAMPLE);
			for (int j=0; j < n; ++j) {
				s.add(j);
			}
		}
		
		for (int j = 0; j < SAMPLE; ++j) {
			int i = r.nextInt(a.length);
			s = a[i];
			if (s.size() == 0) continue;
			int n = r.nextInt(s.size());
			Iterator<Integer> it = s.iterator();
			Integer current = it.next();
			for (int k=0; k < n; ++k) {
				current = it.next();
			}
			assertEquals(n,current.intValue());
		}
	}
	
	public void test2() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(i);
		}
		s.clear();
		assert(s.isEmpty());
	}
	
	public void test3() {
		List<Queue<Integer>> ss = new ArrayList<>();
		ss.add(s);
		int max = 1;
		for (int i=0; i < MAX_LENGTH; ++i) {
			if (r.nextBoolean()) {
				s = new RobustQueue<>();
				s.add(3);
				ss.add(s);
			} else {
				s.addAll(new ArrayList<>(s)); // double size of s
				if (s.size() > max) {
					max = s.size();
					// System.out.println("Reached " + max);
				}
			}
		}
	}
	
	public void test4() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(i);
		}
		int nextIn = MAX_LENGTH;
		int nextOut = 0;
		for (int j=0; j < MAX_LENGTH; ++j) {
			switch (j % 3) {
			case 0:
				assertEquals(nextOut,s.peek().intValue());
				break;
			case 1:
				assertEquals(nextOut++,s.poll().intValue());
				break;
			case 2:
				assertTrue(s.offer(nextIn++));
				break;
			}
		}
	}
	
	public void test5() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.add(i);
		}
		@SuppressWarnings("unchecked")
		Iterator<Integer>[] iterators = new Iterator[SAMPLE];
		for (int j=0; j < SAMPLE; ++j) {
			iterators[j] = s.iterator();
		}
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertEquals(i,iterators[i % SAMPLE].next().intValue());
			assertEquals(i,s.poll().intValue());
		}
	}
}
