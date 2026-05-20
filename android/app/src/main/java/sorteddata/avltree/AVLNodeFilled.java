package sorteddata.avltree;

import java.util.Comparator;

class AVLNodeFilled<T> extends AVLNode<T> {
	final AVLNode<T> left, right;
	final T value;
	private final int height, balance, size;
	public AVLNodeFilled(Comparator<T> comparator, T value, AVLNode<T> left, AVLNode<T> right) {
		super(comparator);
		this.value = value;
		this.left = left;
		this.right = right;
		this.size = left.size() + right.size() + 1;
		this.height = Math.max(left.height(), right.height())+1;
		this.balance = left.height()-right.height();
	}

	public int height() {
		return height;
	}
	public int balanceFactor() {
		return balance;
	}
	public int size() {
		return size;
	}

	public String toString() {
		if (left instanceof AVLNodeEmpty<T> && right instanceof AVLNodeEmpty<T>)
			return value.toString();
		else
			return "%s -> (%s, %s)".formatted(value.toString(), left.toString(), right.toString());
	}

	public AVLNodeFilled<T> insert(T element) {
        int comp = comparator.compare(value, element);
        boolean leftright;
        AVLNodeFilled<T> newTree;
        AVLNodeFilled<T> subtree;
        if (comp==0) {
            return this;
        }
        if (comp > 0) {
            subtree = left.insert(element);
            newTree = new AVLNodeFilled<>(comparator, value, subtree, right);
            leftright=false;
        } else {
            subtree = right.insert(element);
            newTree = new AVLNodeFilled<>(comparator, value, left, subtree);
            leftright = true;
        }
        AVLNodeFilled<T> fixedTree = newTree;
        if (newTree.balance >= 2 || newTree.balance <= -2) {
            if (newTree.balance * subtree.balance < 0) {
                if (subtree.balance < 0) subtree = subtree.leftRotate();
                else if (subtree.balance > 0) subtree = subtree.rightRotate();
            }
            if (leftright) newTree = new AVLNodeFilled<>(comparator, value, left, subtree);
            if (!leftright) newTree = new AVLNodeFilled<>(comparator, value, subtree, right);
            if (newTree.balance < -1) fixedTree = newTree.leftRotate();
            else if (newTree.balance > 1) fixedTree = newTree.rightRotate();
        }
        return fixedTree;
    }





    /**
	 * Executes a left rotation on the current node, as defined
	 * by the AVL Tree algorithm.
	 * @return the new node taking this node's place after rotation
	 */
	private AVLNodeFilled<T> leftRotate() {
        if (right instanceof AVLNodeFilled<T>) {
            AVLNodeFilled<T> newLeft = new AVLNodeFilled<>(comparator, value, left, ((AVLNodeFilled<T>) right).left);
            return new AVLNodeFilled<>(comparator, ((AVLNodeFilled<T>) right).value, newLeft, ((AVLNodeFilled<T>) right).right);
        }
        else {
            AVLNodeFilled<T> newLeft = new AVLNodeFilled<>(comparator, value, left, new AVLNodeEmpty<>(comparator));
            return new AVLNodeFilled<>(comparator, null, newLeft, new AVLNodeEmpty<>(comparator));
        }
	}

	/**
	 * Executes a right rotation on the current node, as defined
	 * by the AVL Tree algorithm.
	 * @return the new node taking this node's place after rotation
	 */
    //test
	private AVLNodeFilled<T> rightRotate() {
        if (left instanceof AVLNodeFilled<T>) {
            AVLNodeFilled<T> newRight = new AVLNodeFilled<>(comparator, value, ((AVLNodeFilled<T>) left).right, right);
            return new AVLNodeFilled<>(comparator, ((AVLNodeFilled<T>) left).value,  ((AVLNodeFilled<T>) left).left,newRight);
        }
        else {
            AVLNodeFilled<T> newRight = new AVLNodeFilled<>(comparator, value, new AVLNodeEmpty<>(comparator), right);
            return new AVLNodeFilled<>(comparator, null, new AVLNodeEmpty<>(comparator), newRight);
        }
	}

	public T getAtIndex(int i) {
		if (i < left.size()) return left.getAtIndex(i);
		else if (i == left.size()) return value;
		return right.getAtIndex(i - left.size() - 1);
	}

	public boolean contains(T element) {
		if (comparator.compare(value, element) < 0) {
			return right.contains(element);
		} else if (comparator.compare(element, value) < 0) {
			return left.contains(element);
		}
		return true;
	}

	public T get(T element) {
		if (comparator.compare(value, element) < 0) {
			return right.get(element);
		} else if (comparator.compare(element, value) < 0) {
			return left.get(element);
		}
		return value;
	}
}
