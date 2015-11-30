package huffman;

public class Node {
	private String chars;
	private String code;
	private Node left;
	private Node right;
	private Integer count;

	Node() {
		chars = "";
		left = null;
		right = null;
		code = "";
		count = 0;

	}

	Node(String s) {
		chars = s;
		left = null;
		right = null;
		count = 1;
	}

	Node(String s, int p) {
		chars = s;
		left = null;
		right = null;
		count = p;
	}

	public Node(Node n) {
		this.chars = n.chars;
		this.left = n.left;
		this.right = n.right;
		this.count = n.count;
	}

	public void setRight(Node n) {
		this.right = n;
	}

	public void setLeft(Node n) {
		this.left = n;
	}

	public void addNode(Node n) {
		count +=n.count;
		chars += n.chars;
	}

	public String toString() {
		String r = "";

		if (left != null)
			r += left+ " < ";//.toString() + "<";
		r += chars;
		if (right != null)
			r += " > " + right;//.toString();
		return r ;//+ "=" +this.count + "=";
//		return "(" + chars + "-" + count + ")";
	}

	public int compareTo(Node n) {
		return -1*(this.count - n.getCount());
	}

	public String getChars() {
		return chars;
	}

	public void setChars(String chars) {
		this.chars = chars;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Node getLeft() {
		return left;
	}

	public Node getRight() {
		return right;
	}

}
