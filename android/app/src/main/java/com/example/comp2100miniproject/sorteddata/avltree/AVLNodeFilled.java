package com.example.comp2100miniproject.sorteddata.avltree;

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

		// TODO: Overwrite the following line to correctly compute the tree's balance factor
		this.balance = left.height() - right.height();
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
		// TODO: Complete this method
        int comp = comparator.compare(value, element);
        if (comp == 0) return this;

        AVLNode<T> newLeft = this.left;
        AVLNode<T> newRight = this.right;

        if (comp > 0) {
            newLeft = this.left.insert(element);
        } else {
            newRight = this.right.insert(element);
        }

        AVLNodeFilled<T> node = new AVLNodeFilled<>(comparator, this.value, newLeft, newRight);

		int balance = node.balanceFactor();
        if (balance > 1) {
            if (newLeft.balanceFactor() >= 0) {
                return node.rightRotate();
            } else {
                AVLNodeFilled<T> rotatedLeft = ((AVLNodeFilled<T>) newLeft).leftRotate();
                return new AVLNodeFilled<>(comparator, node.value, rotatedLeft, node.right)
                        .rightRotate();
            }
        }

        if (balance < -1) {
            if (newRight.balanceFactor() <= 0) {
                return node.leftRotate();
            } else {
                AVLNodeFilled<T> rotatedRight = ((AVLNodeFilled<T>) newRight).rightRotate();
                return new AVLNodeFilled<>(comparator, node.value, node.left, rotatedRight)
                        .leftRotate();
            }
        }

        return node;
	}

	/**
	 * Executes a left rotation on the current node, as defined
	 * by the AVL Tree algorithm.
	 * @return the new node taking this node's place after rotation
	 */
	private AVLNodeFilled<T> leftRotate() {
		// TODO: Complete this method
        if (right instanceof AVLNodeFilled<T> rightNode) {
            AVLNode<T> newLeft = new AVLNodeFilled<>(comparator, this.value, this.left, rightNode.left);
            return new AVLNodeFilled<>(comparator, rightNode.value, newLeft, rightNode.right);
        }
		return this;
	}

	/**
	 * Executes a right rotation on the current node, as defined
	 * by the AVL Tree algorithm.
	 * @return the new node taking this node's place after rotation
	 */
	private AVLNodeFilled<T> rightRotate() {
		// TODO: Complete this method
        if (left instanceof AVLNodeFilled<T> leftNode) {
            AVLNode<T> newRight = new AVLNodeFilled<>(comparator, this.value, leftNode.right, this.right);
            return new AVLNodeFilled<>(comparator, leftNode.value, leftNode.left, newRight);
        }
		return this;
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
