package edu.uwm.cs351.util;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * @author benhackbart
 * A generic extention of the AbstractQueue class that 
 * has iterators that continue to work even if elements are 
 * added or removed from the queue.
 * @param <E> type of the elements
 */
public class RobustQueue<E> extends AbstractQueue<E>{
	
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);

	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}
	
	private Node<E> dummy;
	private int size;
	
	private static class Node<T>{
		Node<T> next, prev;
		T data;
		
		Node(Node<T> p, Node<T> n){
			this(null, p, n);
		}
		
		Node(T d, Node<T> p, Node<T> n){
			data = d;
			prev = p;
			next = n;
		}
	}
	
	/**
	 * checks the integrity of the data structure.
	 * @return true if all conditions are met
	 */
	private boolean wellFormed() {
		// 1. The dummy is not null.
		if(dummy == null) return report("dummy is null");
		// 2. The dummy’s data is null.
		if(dummy.data != null) return report("dummy's data is not null");
		
		int count = 0;
		Node<E> tempNode = dummy;
		
		// 3. Following the next links from the dummy, we always get back to the dummy (a cycle),
		// and the same is true of the prev links.
		do {
			// 5. None of the nodes reachable from the dummy (other than the dummy itself) has null data.
			if(tempNode != dummy && tempNode.data == null) return report("a node's data isnt null");
			
			Node<E> nextNode = tempNode.next;
			// check to make sure a node is not null
			if(nextNode == null) return report("a node has a null next link");
			// 4. If a node in the list starting at the dummy links to another node with its next 
			// field, then that node links back to this node with its prev field.
			if(nextNode.prev != tempNode) return report("a node's next link does not link back correctly");
			
			tempNode = nextNode;
			++count;
			
		} while(tempNode != dummy);
		
		// it ended at dummy, and we cant count dummy as a node in the list. So decrement once.
		--count;
		// 6. The number of nodes reachable from the dummy (other than the dummy itself) is
		// equal to the size field.
		if(size != count) return report("number of nodes (reachable by dummy) is not equal to size");
		
		// if all else passes return true;
		return true;
	}
	
	private RobustQueue(boolean ignored) {} // spy class constructor

	/**
	 * initializes the data structure given zero arguments
	 */
    public RobustQueue() {
    	dummy = new Node<E>(null, null);
    	// dummy points to itself when there are no elements
    	dummy.next = dummy.prev = dummy;
    	size = 0;
    	
    	assert wellFormed() : "invariant broken by constructor";
    }

	@Override // required
	public boolean offer(E e) {
		assert wellFormed() : "invariant broken in offer";
		if(e == null) throw new NullPointerException("data cannot be null");
		
		Node<E> nodeToAdd = new Node<E>(e, null, null);
		// insert it at the end of the list. use dummy.prev to access end
		dummy.prev.next = nodeToAdd;
		nodeToAdd.prev = dummy.prev;
		dummy.prev = nodeToAdd;
		nodeToAdd.next = dummy;
		++size;
		
		assert wellFormed() : "invariant broken by offer";
		return true;
	}

	@Override // required
	public E poll() {
		assert wellFormed() : "invariant broken in poll";
		if(size == 0) return null;
		
		if(isEmpty()) return null;
		E returnData = dummy.next.data; 
		dummy.next.data = null; // set data to null to signify it will no longer be in 'real' list
		// set next and prev values to adjust for removed element
		dummy.next = dummy.next.next; 
		dummy.next.prev = dummy;
		--size;
		
		assert wellFormed() : "invariant broken by poll";
		return returnData;
	}

	@Override // required
	public E peek() {
		if(isEmpty()) return null;
		return dummy.next.data; // returns the head of the queue's data
	}

	@Override // required
	public Iterator<E> iterator() {
		return new MyIterator();
	}

	@Override // required
	public int size() {
		return size;
	}
	
	/**
	 * a class designed to implement the iterator interface
	 * and provide custom features such as its robustness
	 */
	private class MyIterator implements Iterator<E>{

		private Node<E> cur;
		
		MyIterator(boolean ignored) {} // spy class constructor
		
		MyIterator(){
			cur = dummy;
			assert wellFormed() : "invariant broken by iterator's constructor";

		}

		/**
		 * checks the integrity of the data structure.
		 * @return true if all conditions are met
		 */
		private boolean wellFormed() {
		    // check the integrity of the queue structure itself first
			if(!RobustQueue.this.wellFormed()) return false;
			// 1. The current pointer is never null.
			if(cur == null) return report("current should never be null.");
			// 2. If the current pointer’s data field is not null, then it is in the “real” list.
			Node<E> temp = dummy.next;
			boolean foundCur = false;
			while(temp != dummy) {
				if(temp == cur) foundCur = true;
				temp = temp.next;
			}
			if(!foundCur && cur.data != null) return report("cur's data is not null and is not in the 'real' list");
			if(foundCur && cur.data == null) return report("cur's data is null and is in the 'real' list");
			// 3. The current pointer’s node can reach the dummy by traversing “prev” links only.
			// (Checking this will require using something like the “Tortoise and Hare” algorithm to
			// avoid getting caught in a cycle.)
			
			// cur should not exist.
			if(cur.data == null && cur.next == null && cur.prev == null) return report("cur's data, next and prev is null");
		    
			Node<E> slow = cur;
		    Node<E> fast = cur.prev; // Use the "Tortoise and Hare" algorithm to detect a cycle
		    while (fast != dummy && fast.prev != dummy && fast.prev != null && fast.prev.prev != null) {
		        if (fast == slow || fast.prev == slow) {
		            return report("cycle detected: current cannot reach the dummy through 'prev' links without entering a cycle");
		        }
		        slow = slow.prev;
		        fast = fast.prev.prev;
		    }
		    if (fast == null || fast.prev == null) {
		        return report("current cannot reach the dummy: a null 'prev' link found");
		    }
			// 4. Any node reached by “prev” links from the current pointer that has a non-null data
			// field is in the “real” list.
		    temp = cur;
		    while(temp != dummy) {
		        if(temp.data != null) {
		            Node<E> check = dummy.next;
		            boolean isInList = false;
		            while(check != dummy) {
		                if(check == temp) {
		                    isInList = true;
		                    break;
		                }
		                check = check.next;
		            }
		            if(!isInList) return report("a node reached by 'prev' from current with non-null data is not in the real list");
		        }
		        temp = temp.prev;
		    }
			// if all checks pass, return true
			return true;
		}
		
		@Override // implementation
		public void remove() {
			assert wellFormed() : "invariant broken in remove";
			
			if(cur == dummy || cur.data == null) throw new IllegalStateException("cannot remove");
			// removing the cur, and handling its nearby node's fields accordingly.
			cur.prev.next = cur.next;
			cur.next.prev = cur.prev;
			cur.data = null;
			--size;
			
			assert wellFormed() : "invariant broken by remove";
		}
	
		@Override // required
		public boolean hasNext() {
			assert wellFormed() : "invaraint broken in hasNext";
			
		    Node<E> tempCur = cur;

		    // move back to the real list using prev links. only gets called if
		    // cur is pointing to a removed node or is dummy
		    while (tempCur.data == null && tempCur != dummy) {
		        tempCur = tempCur.prev;
		    }

		    // if weve moved to the dummy node using prev links, the real list 
		    // might have extended past the current position. so we check if 
		    // the next node after dummy is not dummy which shows other nodes 
		    // have been added since removal of cur.
		    if (tempCur == dummy) {
				assert wellFormed() : "invaraint broken by hasNext";
		    	return dummy.next != dummy;
		    }
		    else { // default case. make sure the next node is part of the real 
		    	// list and not the dummy node.
				assert wellFormed() : "invaraint broken by hasNext";
		    	return tempCur.next != dummy; 
		    }
		}


		@Override // required
		public E next() {
			assert wellFormed() : "invariant broken in next";
			if(!hasNext()) throw new NoSuchElementException("there is no next");
			
			// here we move back to the 'real' list, just as we did in hasNext. except
			// this time we can use the actual cur pointer because we are changing the
			// value of cur.
			while(cur.data == null && cur != dummy) {
				cur = cur.prev;
			}
			
			// once moved back to the 'real' list, move to the next node.
			cur = cur.next; 
			
			if(cur == dummy || cur.data == null) throw new NoSuchElementException("no next element");
			
			assert wellFormed() : "invariant broken by next";
			return cur.data;
		}
	}
	
	/*
	 * A public version of the data structure's class. Used
	 * for testing.
	 */
	public static class Spy<T> {
		/**
		 * A public version of the data structure's internal node class.
		 * This class is only used for testing.
		 */
		public static class Node<U> extends RobustQueue.Node<U> {
			/**
			 * Create a node with null data, tag, prev, and next fields.
			 */
			public Node() {
				this(null, null, null);
			}
			/**
			 * Create a node with the given values
			 * @param d data for new node, may be null
			 * @param t tag for new node,may be null
			 * @param p prev for new node, may be null
			 * @param n next for new node, may be null
			 */
			public Node(U d, Node<U> p, Node<U> n) {
				super(null, null);
				this.data = d;
				this.prev = p;
				this.next = n;
			}
		}
		
		/**
		 * Create a node for testing.
		 * @param d data for new node, may be null
		 * @param p prev for new node, may be null
		 * @param n next for new node, may be null
		 * @return newly created test node
		 */
		public Node<T> newNode(T d, Node<T> p, Node<T> n) {
			return new Node<T>(d, p, n);
		}
		
		/**
		 * Create a node with all null fields for testing.
		 * @return newly created test node
		 */
		public Node<T> newNode() {
			return new Node<T>();
		}
		
		/**
		 * Change a node's next field
		 * @param n1 node to change, must not be null
		 * @param n2 node to point to, may be null
		 */
		public void setNext(Node<T> n1, Node<T> n2) {
			n1.next = n2;
		}
		
		/**
		 * Change a node's prev field
		 * @param n1 node to change, must not be null
		 * @param n2 node to point to, may be null
		 */
		public void setPrev(Node<T> n1, Node<T> n2) {
			n1.prev = n2;
		}

		/**
		 * Return the sink for invariant error messages
		 * @return current reporter
		 */
		public Consumer<String> getReporter() {
			return reporter;
		}

		/**
		 * Change the sink for invariant error messages.
		 * @param r where to send invariant error messages.
		 */
		public void setReporter(Consumer<String> r) {
			reporter = r;
		}

		/**
		 * Create a testing instance of a linked tag collection with the given
		 * data structure.
		 * @param d the dummy node
		 * @param s the size
		 * @return a new testing linked tag collection with this data structure.
		 */
		public <U> RobustQueue<U> newInstance(Node<U> d, int s) {
			RobustQueue<U> result = new RobustQueue<U>(false);
			result.dummy = d;
			result.size = s;
			return result;
		}
			
		/**
		 * Creates a testing instance of an iterator
		 * @param outer the collection attached to the iterator
		 * @param c the current node
		 */
		public <E> Iterator<E> newIterator(RobustQueue<E> outer, Node<E> c) {
			RobustQueue<E>.MyIterator result = outer.new MyIterator(false);
			result.cur = c;
			return result;
		}
			
		/**
		 * Check the invariant on the given dynamic array robot.
		 * @param r robot to check, must not be null
		 * @return whether the invariant is computed as true
		 */
		public boolean wellFormed(RobustQueue<?> r) {
			return r.wellFormed();
		}
			
		/**
		 * Check the invariant on the given Iterator.
		 * @param it iterator to check, must not be null
		 * @return whether the invariant is computed as true
		 */
		public <E> boolean wellFormed(Iterator<E> it) {
			RobustQueue<E>.MyIterator myIt = (RobustQueue<E>.MyIterator)it;
			return myIt.wellFormed();
		}
	}
}