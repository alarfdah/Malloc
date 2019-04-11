package malloc;

public class FreeNode {
	private FreeNode next;
	private FreeNode prev;
	
	public FreeNode getNext() {
		return next;
	}
	public void setNext(FreeNode next) {
		this.next = next;
	}
	public FreeNode getPrev() {
		return prev;
	}
	public void setPrev(FreeNode prev) {
		this.prev = prev;
	}
	
	
}
