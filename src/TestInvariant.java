import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.uwm.cs351.util.RobustQueue;
import edu.uwm.cs351.util.RobustQueue.Spy.Node;


import junit.framework.TestCase;

public class TestInvariant extends TestCase{
	
	protected RobustQueue.Spy<String> spy;
	protected int reports;
	protected RobustQueue<String> r;
	protected Iterator<String> it;
    
    protected void assertReporting(boolean expected, Supplier<Boolean> test) {
            reports = 0;
            Consumer<String> savedReporter = spy.getReporter();
            try {
                    spy.setReporter((String message) -> {
                            ++reports;
                            if (message == null || message.trim().isEmpty()) {
                                    assertFalse("Uninformative report is not acceptable", true);
                            }
                            if (expected) {
                                    assertFalse("Reported error incorrectly: " + message, true);
                            }
                    });
                    assertEquals(expected, test.get().booleanValue());
                    if (!expected) {
                            assertEquals("Expected exactly one invariant error to be reported", 1, reports);
                    }
                    spy.setReporter(null);
            } finally {
                    spy.setReporter(savedReporter);
            }
    }
    
    protected void assertWellFormed(boolean expected, RobustQueue<String> r) {
		assertReporting(expected, () -> spy.wellFormed(r));
	}
    protected void assertWellFormed(boolean expected, Iterator<String> it) {
		assertReporting(expected, () -> spy.wellFormed(it));
	}
    
    protected RobustQueue.Spy.Node<String> d;
    
    @Override //implementation
    protected void setUp() {
		spy = new RobustQueue.Spy<String>();
		d = spy.newNode();
	}
    
    //Ax: Set up and null tests 
    public void testA00() {
		r = spy.newInstance(null, 0);
		assertWellFormed(false, r);
	}
	
	public void testA01() {
		d = null;
		r = spy.newInstance(d, 0);
		assertWellFormed(false, r);
	}
	
	public void testA02() {
		r = spy.newInstance(d, 0);
		spy.setNext(d, d);
		assertWellFormed(false, r);
	}
	
	public void testA03() {
		r = spy.newInstance(d, 0);
		spy.setNext(d, d);
		spy.setPrev(d, d);
		assertWellFormed(true, r);
	}
	
	public void testA04() {
		r = spy.newInstance(d, 0);
		spy.setNext(d, null);
		spy.setPrev(d, d);
		assertWellFormed(false, r);
	}
	
	//Bx: prev and next field accuracy
	public void testB00() {
		Node<String> n1 = spy.newNode("one", null, null);
		r = spy.newInstance(n1, 0);
		spy.setNext(n1, n1);
		spy.setPrev(n1, n1);
		assertWellFormed(false, r);
	}
	
	public void testB01() {
		r = spy.newInstance(d, 1);
		Node<String> n1 = spy.newNode(null, null, null);
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		spy.setNext(n1, d);
		spy.setPrev(n1, d);
		assertWellFormed(false, r);
	}
	
	public void testB02() {
		r = spy.newInstance(d, 1);
		Node<String> n1 = spy.newNode("one", null, null);
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		spy.setNext(n1, d);
		spy.setPrev(n1, d);
		assertWellFormed(true, r);
	}
	
	public void testB03() {
		r = spy.newInstance(d, 1);
		Node<String> n1 = spy.newNode("one", d, d);
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		assertWellFormed(true, r);
	}
	
	public void testB04() {
		r = spy.newInstance(d, 2);
		Node<String> n2 = spy.newNode("two", d, d);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		assertWellFormed(false, r);
	}
	
	public void testB05() {
		r = spy.newInstance(d, 2);
		Node<String> n2 = spy.newNode("two", d, d);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		assertWellFormed(false, r);
	}
	
	public void testB06() {
		r = spy.newInstance(d, 2);
		Node<String> n2 = spy.newNode("two", d, d);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n2);
		assertWellFormed(true, r);
	}
	
	public void testB07() {
		r = spy.newInstance(d, 3);
		Node<String> n3 = spy.newNode("three", d, d);
		Node<String> n2 = spy.newNode("two", d, n3);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n3, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n3);
		assertWellFormed(true, r);
	}
	
	public void testB08() {
		r = spy.newInstance(d, 3);
		Node<String> n3 = spy.newNode("three", d, d);
		Node<String> n2 = spy.newNode("two", d, n3);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n3, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n3);
		assertWellFormed(true, r);
	}
	
	public void testB09() {
		r = spy.newInstance(d, 3);
		Node<String> n3 = spy.newNode("three", d, d);
		Node<String> n2 = spy.newNode("two", d, n3);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n3, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n2);
		spy.setPrev(d, n3);
		assertWellFormed(false, r);
	}
	
	public void testB10() {
		r = spy.newInstance(d, 3);
		Node<String> n3 = spy.newNode("three", d, d);
		Node<String> n2 = spy.newNode("two", d, d);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n3, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n3);
		assertWellFormed(false, r);
	}
	
	public void testB11() {
		r = spy.newInstance(d, 3);
		Node<String> n3 = spy.newNode("three", d, d);
		Node<String> n2 = spy.newNode("two", d, n3);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n3, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n2);
		assertWellFormed(false, r);
	}
	
	public void testB12() {
		r = spy.newInstance(d, 3);
		Node<String> n3 = spy.newNode("three", d, d);
		Node<String> n2 = spy.newNode(null, d, n3);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n3, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n3);
		assertWellFormed(false, r);
	}
	
	public void testB13() {
		r = spy.newInstance(d, 3);
		Node<String> n3 = spy.newNode("three", d, d);
		Node<String> n2a = spy.newNode("two", d, n3);
		Node<String> n2 = spy.newNode("two", d, n3);
		Node<String> n1 = spy.newNode("one", d, n2a);
		spy.setPrev(n3, n2);
		spy.setPrev(n2, n1);
		spy.setPrev(n2a, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n3);
		assertWellFormed(false, r);
	}
	
	public void testB14() {
		r = spy.newInstance(d, 3);
		Node<String> n3 = spy.newNode("three", d, d);
		Node<String> n2a = spy.newNode("two", d, n3);
		Node<String> n2 = spy.newNode("two", d, n3);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n3, n2a);
		spy.setPrev(n2, n1);
		spy.setPrev(n2a, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n3);
		assertWellFormed(false, r);
	}
	
	public void testB15() {
		r = spy.newInstance(d, 4);
		Node<String> n4 = spy.newNode("B15", d, d);
		Node<String> n3 = spy.newNode("B15", d, n4);
		Node<String> n2 = spy.newNode("B15", d, n3);
		Node<String> n1 = spy.newNode("B15", d, n2);
		spy.setNext(d, n1);
		spy.setPrev(d, n4);
		spy.setPrev(n4, n3);
		spy.setPrev(n3, n2);
		spy.setPrev(n2, n1);
		assertWellFormed(true, r);
	}
	
	public void testB16() {
		r = spy.newInstance(d, 4);
		Node<String> n4 = spy.newNode("B15", d, d);
		Node<String> n3 = spy.newNode("B15", d, n4);
		Node<String> n2 = spy.newNode("B15", d, n3);
		Node<String> n1 = spy.newNode("B15", d, n2);
		spy.setNext(d, n1);
		spy.setPrev(d, n4);
		spy.setPrev(n4, n3);
		spy.setPrev(n3, n2);
		spy.setPrev(n2, n1);
		spy.setNext(n3, n1);
		assertWellFormed(false, r);
	}
	
	//Cx: Size
	public void testC00() {
		r = spy.newInstance(d, 1);
		spy.setNext(d, d);
		spy.setPrev(d, d);
		assertWellFormed(false, r);
	}
	
	public void testC01() {
		r = spy.newInstance(d, 0);
		Node<String> n1 = spy.newNode("one", d, d);
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		assertWellFormed(false, r);
	}
	
	public void testC02() {
		r = spy.newInstance(d, 1);
		Node<String> n2 = spy.newNode("two", d, d);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n2);
		assertWellFormed(false, r);
	}
	
	public void testC03() {
		r = spy.newInstance(d, 2);
		Node<String> n3 = spy.newNode("three", d, d);
		Node<String> n2 = spy.newNode("two", d, n3);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n3, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n3);
		assertWellFormed(false, r);
	}
	
	public void testC04() {
		r = spy.newInstance(d, 3);
		Node<String> n3 = spy.newNode("three", d, d);
		Node<String> n2a = spy.newNode("two", d, n3);
		Node<String> n2 = spy.newNode("two", d, n2a);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n3, n2a);
		spy.setPrev(n2a, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n3);
		assertWellFormed(false, r);
	}
	
	public void testC05() {
		r = spy.newInstance(d, 4);
		Node<String> n3 = spy.newNode("three", d, d);
		Node<String> n2a = spy.newNode("two", d, n3);
		Node<String> n2 = spy.newNode("two", d, n2a);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n3, n2a);
		spy.setPrev(n2a, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n3);
		assertWellFormed(true, r);
	}
	
	//Dx: Outer invariant
	public void testD00() {
		r = spy.newInstance(null, 0);
		it = spy.newIterator(r, d);
		assertWellFormed(false, it);
	}
	 
	public void testD01() {
		r = spy.newInstance(d, 0);
		spy.setNext(d, null);
		spy.setPrev(d, d);
		it = spy.newIterator(r, d);
		assertWellFormed(false, it);
	}
	
	public void testD02() {
		r = spy.newInstance(d, 1);
		Node<String> n1 = spy.newNode(null, null, null);
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		spy.setNext(n1, d);
		spy.setPrev(n1, d);
		it = spy.newIterator(r, d);
		assertWellFormed(false, it);
	}
	
	public void testD03() {
		r = spy.newInstance(d, 3);
		Node<String> n3 = spy.newNode("three", d, d);
		Node<String> n2 = spy.newNode("two", d, d);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n3, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n3);
		it = spy.newIterator(r, d);
		assertWellFormed(false, it);
	}
	
	public void testD04() {
		r = spy.newInstance(d, 4);
		Node<String> n3 = spy.newNode("three", d, d);
		Node<String> n2a = spy.newNode("two", d, n3);
		Node<String> n2 = spy.newNode("two", d, n2a);
		Node<String> n1 = spy.newNode("one", d, n2);
		spy.setPrev(n3, n2a);
		spy.setPrev(n2a, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n3);
		it = spy.newIterator(r, d);
		assertWellFormed(true, it);
	}
	
	//Ex: Iterator and Cursor
	
	public void testE00() {
		r = spy.newInstance(d, 0);
		spy.setPrev(d, d);
		spy.setNext(d, d);
		it = spy.newIterator(r, d);
		assertWellFormed(true, it);
	}
	
	public void testE01() {
		r = spy.newInstance(d, 0);
		spy.setPrev(d, d);
		spy.setNext(d, d);
		it = spy.newIterator(r, null);
		assertWellFormed(false, it);
	}
	
	public void testE02() {
		r = spy.newInstance(d, 1);
		Node<String> n1 = spy.newNode("one", d, d);
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		it = spy.newIterator(r, n1);
		assertWellFormed(true, it);
	}
	
	public void testE03() {
		r = spy.newInstance(d, 1);
		Node<String> n1 = spy.newNode("one", d, d);
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		Node<String> n0 = spy.newNode(null, d, d);
		it = spy.newIterator(r, n0);
		assertWellFormed(true, it);
	}
	
	public void testE04() {
		r = spy.newInstance(d, 1);
		Node<String> n1 = spy.newNode("one", d, d);
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		Node<String> i1 = spy.newNode("one", d, d);
		it = spy.newIterator(r, i1);
		assertWellFormed(false, it);
	}
	
	
	/// testFxx: current is further from real list
	
	public void testF00() {
		r = spy.newInstance(d, 0);
		Node<String> n1 = spy.newNode(null, null, null);
		spy.setNext(d, d);
		spy.setPrev(d, d);
		it = spy.newIterator(r, n1);
		assertWellFormed(false, it);
	}
	
	public void testF01() {
		r = spy.newInstance(d, 0);
		Node<String> n1 = spy.newNode(null, d, null);
		spy.setNext(d, d);
		spy.setPrev(d, d);
		it = spy.newIterator(r, n1);
		assertWellFormed(true, it);
	}
	
	public void testF02() {
		r = spy.newInstance(d, 0);
		Node<String> n1 = spy.newNode(null, d, d);
		spy.setNext(d, d);
		spy.setPrev(d, d);
		it = spy.newIterator(r, n1);
		assertWellFormed(true, it);
	}
	
	public void testF03() {
		r = spy.newInstance(d, 0);
		Node<String> n1 = spy.newNode("one", d, null);
		spy.setNext(d, d);
		spy.setPrev(d, d);
		it = spy.newIterator(r, n1);
		assertWellFormed(false, it);
	}
	
	public void testF04() {
		r = spy.newInstance(d, 1);
		Node<String> n2 = spy.newNode("two", d, d);
		Node<String> n1 = spy.newNode(null, d, null);
		spy.setNext(d, n2);
		spy.setPrev(d, n2);
		it = spy.newIterator(r, n1);
		assertWellFormed(true, it);
	}
	
	public void testF05() {
		r = spy.newInstance(d, 1);
		Node<String> n2 = spy.newNode("two", d, d);
		Node<String> n1 = spy.newNode(null, d, n2);
		spy.setNext(d, n2);
		spy.setPrev(d, n2);
		it = spy.newIterator(r, n1);
		assertWellFormed(true, it);
	}
	
	public void testF06() {
		r = spy.newInstance(d, 1);
		Node<String> n2 = spy.newNode("two", d, d);
		Node<String> n1 = spy.newNode(null, d, null);
		spy.setNext(d, n2);
		spy.setPrev(d, n2);
		spy.setPrev(n2, n1);
		it = spy.newIterator(r, n1);
		assertWellFormed(false, it);
	}
	
	public void testF07() {
		r = spy.newInstance(d, 1);
		Node<String> n2 = spy.newNode("two", d, d);
		Node<String> n1 = spy.newNode(null, d, null);
		Node<String> n0 = spy.newNode(null, n2, n1);
		spy.setNext(d, n2);
		spy.setPrev(d, n2);
		it = spy.newIterator(r, n0);
		assertWellFormed(true, it);
	}
	
	public void testF08() {
		r = spy.newInstance(d, 1);
		Node<String> n2 = spy.newNode("two", d, d);
		Node<String> n1 = spy.newNode(null, d, null);
		Node<String> n0 = spy.newNode("zero", n2, n1);
		spy.setNext(d, n2);
		spy.setPrev(d, n2);
		it = spy.newIterator(r, n0);
		assertWellFormed(false, it);
	}
	
	public void testF09() {
		r = spy.newInstance(d, 1);
		Node<String> n3 = spy.newNode(null, null, null);
		Node<String> n2 = spy.newNode(null, null, n3);
		Node<String> n1 = spy.newNode("one", d, d);
		spy.setPrev(n3, n2);
		spy.setPrev(n2, n1);
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		it = spy.newIterator(r, n3);
		assertWellFormed(true, it);
	}
	
	public void testF10() {
		r = spy.newInstance(d, 1);
		Node<String> n3 = spy.newNode(null, null, null);
		Node<String> n2 = spy.newNode(null, null, n3);
		Node<String> n1 = spy.newNode("one", d, d);
		spy.setPrev(n3, n2);
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		it = spy.newIterator(r, n3);
		assertWellFormed(false, it);
	}
	
	public void testF11() {
		r = spy.newInstance(d, 1);
		Node<String> n1 = spy.newNode("F11", d, d);
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		Node<String> n2 = spy.newNode("F11", d, n1);
		Node<String> n3 = spy.newNode(null, n2, n1);
		it = spy.newIterator(r, n3);
		assertWellFormed(false, it);
	}
	
	public void testF12() {
		r = spy.newInstance(d, 3);
		Node<String> n1 = spy.newNode("one", d, null); spy.setNext(d, n1);
		Node<String> n2 = spy.newNode("two", n1, null); spy.setNext(n1, n2);
		Node<String> n3 = spy.newNode("three", n2, d); spy.setNext(n2, n3);
		spy.setPrev(d, n3);
		Node<String> d1 = spy.newNode(null, n2, n3);
		Node<String> d2 = spy.newNode(null, d1, null);
		Node<String> d3 = spy.newNode(null, d2, d2);
		it = spy.newIterator(r, d3);
		assertWellFormed(true, it);
	}
	
	public void testF13() {
		r = spy.newInstance(d, 3);
		Node<String> n1 = spy.newNode("one", d, null); spy.setNext(d, n1);
		Node<String> n2 = spy.newNode("two", n1, null); spy.setNext(n1, n2);
		Node<String> n3 = spy.newNode("three", n2, d); spy.setNext(n2, n3);
		spy.setPrev(d, n3);
		Node<String> d1 = spy.newNode("three", n2, n3);
		Node<String> d2 = spy.newNode(null, d1, null);
		Node<String> d3 = spy.newNode(null, d2, d2);
		it = spy.newIterator(r, d3);
		assertWellFormed(false, it);
	}

	public void testF14() {
		r = spy.newInstance(d, 3);
		Node<String> n1 = spy.newNode("one", d, null); spy.setNext(d, n1);
		Node<String> n2 = spy.newNode("two", n1, null); spy.setNext(n1, n2);
		Node<String> n3 = spy.newNode("three", n2, d); spy.setNext(n2, n3);
		spy.setPrev(d, n3);
		Node<String> d1 = spy.newNode(null, n2, n3);
		Node<String> d2 = spy.newNode(null, d1, n3);
		Node<String> d3 = spy.newNode(null, d2, n3);
		Node<String> d4 = spy.newNode(null, d3, n3);
		Node<String> d5 = spy.newNode(null, d4, n3);
		Node<String> d6 = spy.newNode(null, d5, n3);
		Node<String> d7 = spy.newNode(null, d6, n3);
		Node<String> d8 = spy.newNode(null, d7, n3);
		it = spy.newIterator(r, d8);
		assertWellFormed(true, it);
	}
	
	public void testF15() {
		r = spy.newInstance(d, 3);
		Node<String> n1 = spy.newNode("one", d, null); spy.setNext(d, n1);
		Node<String> n2 = spy.newNode("two", n1, null); spy.setNext(n1, n2);
		Node<String> n3 = spy.newNode("three", n2, d); spy.setNext(n2, n3);
		spy.setPrev(d, n3);
		Node<String> i2 = spy.newNode("two", n1, n3);
		Node<String> d1 = spy.newNode(null, i2, n3);
		Node<String> d2 = spy.newNode(null, d1, n3);
		Node<String> d3 = spy.newNode(null, d2, n3);
		Node<String> d4 = spy.newNode(null, d3, n3);
		Node<String> d5 = spy.newNode(null, d4, n3);
		Node<String> d6 = spy.newNode(null, d5, n3);
		Node<String> d7 = spy.newNode(null, d6, n3);
		Node<String> d8 = spy.newNode(null, d7, n3);
		it = spy.newIterator(r, d8);
		assertWellFormed(false, it);
	}
	
	public void testF16() {
		r = spy.newInstance(d, 2);
		Node<String> n1 = spy.newNode("one",d,null);
		Node<String> n2 = spy.newNode("two",n1,d);
		spy.setNext(n1, n2);
		spy.setNext(d, n1);
		spy.setPrev(d, n2);
		Node<String> r2 = spy.newNode(null,d,n1);
		Node<String> r1 = spy.newNode(null,n2,n1);//60
		
		it = spy.newIterator(r, r1);
		assertWellFormed(true, it);		
 
	}

	
	/// testGxx: cycles
	
	public void testG00() {
		r = spy.newInstance(d, 0);
		Node<String> n1 = spy.newNode(null, d, null);
		spy.setPrev(n1, n1);
		spy.setNext(d, d);
		spy.setPrev(d, d);
		it = spy.newIterator(r, n1);
		assertWellFormed(false, it);
	}
	
	public void testG01() {
		r = spy.newInstance(d, 1);
		Node<String> n2 = spy.newNode(null, d, null);
		Node<String> n1 = spy.newNode("one", d, d);
		spy.setPrev(n2, n2);
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		it = spy.newIterator(r, n2);
		assertWellFormed(false, it);
	}
	
	
	public void testG02() {
		r = spy.newInstance(d, 1);
		Node<String> n3 = spy.newNode(null, null, null);
		Node<String> n2 = spy.newNode(null, null, n3);
		Node<String> n1 = spy.newNode("one", d, d);
		spy.setPrev(n3, n2);
		spy.setPrev(n2, n3);
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		it = spy.newIterator(r, n3);
		assertWellFormed(false, it);
	}
	
	public void testG03() {
		r = spy.newInstance(d, 1);
		Node<String> n3 = spy.newNode(null, null, null);
		Node<String> n2 = spy.newNode(null, null, n3);
		Node<String> n1 = spy.newNode("one", d, d);
		spy.setPrev(n3, n2);
		spy.setPrev(n2, n1);
		spy.setNext(n3, n2); //Cycle in next fields here doesn't matter
		spy.setNext(d, n1);
		spy.setPrev(d, n1);
		it = spy.newIterator(r, n3);
		assertWellFormed(true, it);
	}
	
	public void testG04() {
		r = spy.newInstance(d, 2);
		Node<String> n4 = spy.newNode(null, null, d);
		Node<String> n3 = spy.newNode("three", null, d);
		Node<String> n2 = spy.newNode(null, null, n3);
		Node<String> n1 = spy.newNode("one", d, n3);
		spy.setPrev(n4, n3);
		spy.setPrev(n4, n2);
		spy.setPrev(n3, n1);
		spy.setPrev(n2, n4);
		spy.setNext(d, n1);
		spy.setPrev(d, n3);
		it = spy.newIterator(r, n2);
		assertWellFormed(false, it);
	}
	
	public void testG05() {
		r = spy.newInstance(d, 3);
		Node<String> n1 = spy.newNode("one", d, null); spy.setNext(d, n1);
		Node<String> n2 = spy.newNode("two", n1, null); spy.setNext(n1, n2);
		Node<String> n3 = spy.newNode("three", n2, d); spy.setNext(n2, n3);
		spy.setPrev(d, n3);
		Node<String> d1 = spy.newNode(null, null, n3);
		Node<String> d2 = spy.newNode(null, d1, null);
		Node<String> d3 = spy.newNode(null, d2, d2);
		spy.setPrev(d1, d2);
		it = spy.newIterator(r, d3);
		assertWellFormed(false, it);	
	}
	
	public void testG06() {
		r = spy.newInstance(d, 3);
		Node<String> n1 = spy.newNode("one", d, null); spy.setNext(d, n1);
		Node<String> n2 = spy.newNode("two", n1, null); spy.setNext(n1, n2);
		Node<String> n3 = spy.newNode("three", n2, d); spy.setNext(n2, n3);
		spy.setPrev(d, n3);
		Node<String> d1 = spy.newNode(null, null, n3);
		Node<String> d2 = spy.newNode(null, d1, null);
		Node<String> d3 = spy.newNode(null, d2, d2);
		spy.setPrev(d1, d3);
		it = spy.newIterator(r, d3);
		assertWellFormed(false, it);	
	}
	
	public void testG07() {
		r = spy.newInstance(d, 3);
		Node<String> n1 = spy.newNode("one", d, null); spy.setNext(d, n1);
		Node<String> n2 = spy.newNode("two", n1, null); spy.setNext(n1, n2);
		Node<String> n3 = spy.newNode("three", n2, d); spy.setNext(n2, n3);
		spy.setPrev(d, n3);
		Node<String> d1 = spy.newNode(null, null, n3);
		Node<String> d2 = spy.newNode(null, d1, null);
		Node<String> d3 = spy.newNode(null, d2, d2);
		Node<String> d4 = spy.newNode(null, d3, d);
		spy.setPrev(d1, d3);
		it = spy.newIterator(r, d4);
		assertWellFormed(false, it);	
	}
		
	public void testG08() {
		r = spy.newInstance(d, 3);
		Node<String> n1 = spy.newNode("one", d, null); spy.setNext(d, n1);
		Node<String> n2 = spy.newNode("two", n1, null); spy.setNext(n1, n2);
		Node<String> n3 = spy.newNode("three", n2, d); spy.setNext(n2, n3);
		spy.setPrev(d, n3);
		Node<String> d1 = spy.newNode(null, n2, n3);
		Node<String> d2 = spy.newNode(null, d1, n3);
		Node<String> d3 = spy.newNode(null, d2, n3);
		Node<String> d4 = spy.newNode(null, d3, n3);
		Node<String> d5 = spy.newNode(null, d4, n3);
		Node<String> d6 = spy.newNode(null, d5, n3);
		Node<String> d7 = spy.newNode(null, d6, n3);
		Node<String> d8 = spy.newNode(null, d7, n3);
		spy.setPrev(d1, d5);
		it = spy.newIterator(r, d8);
		assertWellFormed(false, it);
	}
}
